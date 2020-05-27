package com.gd70.android.validator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.Subject;

import static com.gd70.android.validator.Constants.DEFAULT_HELPER_TEXT_SIZE;
import static com.gd70.android.validator.Constants.DEFAULT_TIMEOUT;
import static com.gd70.android.validator.Constants.DEFAULT_VALUE;

public class ValidatorEditText extends AppCompatEditText implements Checker.CheckerInterface {
    private String errorMessage,regex;
    private boolean valid;
    private int checkType,timeOut,compareToId,helperTextSize,helperTextColor;
    private Drawable originalDrawable,validDrawable,invalidDrawable;
    private DrawableState drawableState;
    private Checker checker;
    private Subject<String,String> subject;

    /*
    *
    * returns if the input is valid or invalid
    *
    * */
    public boolean isValid() {
        if(!valid)
            updateDrawableState(DrawableState.INVALID);
        return valid;
    }

    /*
    *
    * drawable state
    *
    * */
    enum DrawableState{
        ORIGINAL,VALID,INVALID
    }

    /*
    *
    * constructors
    *
    * */

    public ValidatorEditText(Context context) {
        super(context);
        init(context,null,DEFAULT_VALUE);
    }

    public ValidatorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,DEFAULT_VALUE);
    }

    public ValidatorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /*
    *
    * initialize required variables
    *
    * */
    private void init(Context context,AttributeSet attrs,int defStyleAttr){
        valid=false;
        getAttributes(context, attrs, defStyleAttr);
        initChecker();
    }

    /*
    *
    * get attributes defined for the validator EditText
    *
    * */
    private void getAttributes(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.ValidatorEditText,defStyleAttr,0);
        errorMessage=typedArray.getString(R.styleable.ValidatorEditText_errorMessage);
        checkType=typedArray.getInt(R.styleable.ValidatorEditText_checkType,DEFAULT_VALUE);
        validDrawable=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_validBackground,DEFAULT_VALUE));
        invalidDrawable=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_invalidBackground,DEFAULT_VALUE));
        regex=typedArray.getString(R.styleable.ValidatorEditText_useRegex);
        timeOut=typedArray.getInt(R.styleable.ValidatorEditText_timeOut,DEFAULT_TIMEOUT);
        compareToId=typedArray.getResourceId(R.styleable.ValidatorEditText_compareTo,DEFAULT_VALUE);
        helperTextSize=typedArray.getInt(R.styleable.ValidatorEditText_helperTextColor,DEFAULT_HELPER_TEXT_SIZE);
        helperTextColor=typedArray.getResourceId(R.styleable.ValidatorEditText_helperTextColor,getColor(R.color.holo_red_light));
        typedArray.recycle();
    }

    /*
    *
    * initialize checker
    *
    * */
    private void initChecker(){
        if(regex!=null)
            checker=new Checker(this,regex,timeOut);
        else
            checker=new Checker(this,checkType,timeOut);
        subject=checker.getSubject();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        originalDrawable=getBackground();
        drawableState= DrawableState.ORIGINAL;
        if(compareToId!=DEFAULT_VALUE) {
            View compareTo = ((View) getParent()).findViewById(compareToId);
            if(compareTo instanceof EditText)
                ((EditText) compareTo).addTextChangedListener(compareToTextWatcher);
            else if(compareTo instanceof TextView)
                ((TextView)compareTo).addTextChangedListener(compareToTextWatcher);
            checker.subscribe(compareTo);
        }
        else {
            checker.subscribe();
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        checker.unsubscribe();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(drawableState!= DrawableState.ORIGINAL)
            updateDrawableState(DrawableState.ORIGINAL);
        if(subject!=null)
            subject.onNext(text.toString());
    }

    /*
    *
    * checker callback
    *
    * */
    @Override
    public void onStateChanged(boolean state) {
        if(state)
            updateDrawableState(DrawableState.VALID);
        else
            updateDrawableState(DrawableState.INVALID);
    }

    /*
    *
    * update background and drawableState
    *
    * */
    private void updateDrawableState(DrawableState drawableState){
        Drawable drawable;
        switch (drawableState){
            case VALID:
                drawable=validDrawable;
            break;
            case INVALID:
                drawable=invalidDrawable;
            break;
            default:
                drawable=originalDrawable;
        }
        this.drawableState=drawableState;
        Observable.just(drawable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setBackground);
    }

    /*
    *
    * get drawable resource
    *
    * */
    private Drawable getDrawable(int resourceId){
        if(resourceId!=DEFAULT_VALUE){
            return ResourcesCompat.getDrawable(getResources(),resourceId,null);
        }
        return originalDrawable;
    }

    /*
    *
    * get color Resource
    *
    * */
    private int getColor(int resourceId){
        return ResourcesCompat.getColor(getResources(),resourceId,null);
    }

    /*
    *
    * Text watcher for listening to comparing view text change
    *
    * */
    private TextWatcher compareToTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(subject!=null && getText()!=null)
                subject.onNext(getText().toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
