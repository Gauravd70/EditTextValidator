package com.gd70.android.validator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.Subject;

import static com.gd70.android.validator.Constants.DEFAULT_TIMEOUT;
import static com.gd70.android.validator.Constants.DEFAULT_VALUE;
import static com.gd70.android.validator.Constants.LEFT;
import static com.gd70.android.validator.Constants.REQUIRED;
import static com.gd70.android.validator.Constants.RIGHT;

public class ValidatorEditText extends AppCompatEditText implements Checker.CheckerInterface {
    private String errorMessage,regex;
    private boolean valid,required;
    private int checkType,timeOut,compareToId,drawablePosition;
    private Drawable originalBackground,validBackground,invalidBackground,originalDrawable,validDrawable,invalidDrawable;
    private DrawableState drawableState;
    private Checker checker;
    private Subject<String,String> subject;

    /*
    *
    * returns if the input is valid or invalid
    *
    * */
    public boolean isValid() {
        if(required && getText()!=null) {
            if(getText().toString().isEmpty()) {
                updateErrorText(REQUIRED);
                return false;
            }
        }
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
        required=typedArray.getBoolean(R.styleable.ValidatorEditText_required,false);

        checkType=typedArray.getInt(R.styleable.ValidatorEditText_checkType,DEFAULT_VALUE);
        validBackground=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_validBackground,DEFAULT_VALUE));
        invalidBackground=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_invalidBackground,DEFAULT_VALUE));

        validDrawable=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_validDrawable,DEFAULT_VALUE));
        invalidDrawable=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_invalidDrawable,DEFAULT_VALUE));
        drawablePosition=typedArray.getInt(R.styleable.ValidatorEditText_drawablePosition,RIGHT);

        regex=typedArray.getString(R.styleable.ValidatorEditText_useRegex);
        timeOut=typedArray.getInt(R.styleable.ValidatorEditText_timeOut,DEFAULT_TIMEOUT);

        compareToId=typedArray.getResourceId(R.styleable.ValidatorEditText_compareTo,DEFAULT_VALUE);
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

    /*
    *
    * init helper textView
    *
    * */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        originalBackground=getBackground();
        originalDrawable=getDrawable(R.color.transparent);
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
        this.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        checker.unsubscribe();
    }

    /*
    *
    * checker callback
    *
    * */
    @Override
    public void onStateChanged(boolean state) {
        valid=state;
        if(state)
            updateDrawableState(DrawableState.VALID);
        else {
            updateErrorText(errorMessage);
            updateDrawableState(DrawableState.INVALID);
        }
    }

    /*
    *
    * show toast
    *
    * */
    @Override
    public void showToast(String toast) {
        Observable.just(toast)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s-> Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show());
    }

    /*
    *
    * update helper text
    *
    * */
    private void updateErrorText(String text){
        Observable.just(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setError);
    }

    private void clearCompoundDrawables(){
        addDrawable(null,null,null,null);
    }

    /*
    *
    * update background and drawableState
    *
    * */
    private void updateDrawableState(DrawableState drawableState){
        clearCompoundDrawables();
        this.drawableState=drawableState;
        Drawable[] drawables =new Drawable[2];
        switch (drawableState){
            case VALID: {
                if(validBackground!=null)
                    drawables[0] = validBackground;

                if(validDrawable!=null)
                    drawables[1]=validDrawable;
            }
            break;
            case INVALID: {
                if(invalidBackground!=null)
                    drawables[0] = invalidBackground;

                if(invalidDrawable!=null)
                    drawables[1]=invalidDrawable;
            }
            break;
            default: {
                drawables[0]=originalBackground;
                drawables[1]=originalDrawable;
            }
        }

        if(drawables[0]==null) {
            drawables[0] = originalBackground;
        }
        if(drawables[1]==null) {
            drawables[1] = originalDrawable;
        }

        Observable.just(drawables)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(drawables1 -> {
                    setBackground(drawables1[0]);
                    if(drawablePosition==LEFT)
                        addDrawable(drawables[1],null,null,null);
                    else
                        addDrawable(null,null,drawables[1],null);
                });
    }

    /*
    *
    * add compound drawable
    *
    * */
    private void addDrawable(Drawable start,Drawable top,Drawable end,Drawable bottom){
        Observable.just(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(editText -> editText.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom));
    }

    /*
     *
     * Text watcher for listening to validator edit text change
     *
     * */
    private TextWatcher textWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(drawableState!= DrawableState.ORIGINAL)
                updateDrawableState(DrawableState.ORIGINAL);
            if(subject!=null) {
                subject.onNext(charSequence.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

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

    /*
     *
     * get drawable resource
     *
     * */
    private Drawable getDrawable(int resourceId){
        if(resourceId!=DEFAULT_VALUE){
            return ResourcesCompat.getDrawable(getResources(),resourceId,null);
        }
        return originalBackground;
    }
}
