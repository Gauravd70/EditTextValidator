package com.gd70.android.validator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.Subject;

import static com.gd70.android.validator.Constants.DEFAULT_TIMEOUT;
import static com.gd70.android.validator.Constants.DEFAULT_VALUE;

public class ValidatorEditText extends AppCompatEditText implements Checker.CheckerInterface {
    private String errorMessage,regex;
    private boolean state;
    private int checkType,timeOut,compareToId;
    private Drawable original,valid,invalid;
    private DrawableState drawableState;
    private Checker checker;
    private Subject<String,String> subject;
    private View compareTo;

    enum DrawableState{
        ORIGINAL,VALID,INVALID
    }

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

    private void init(Context context,AttributeSet attrs,int defStyleAttr){
        state=false;
        original=getBackground();
        drawableState= DrawableState.ORIGINAL;
        getAttributes(context, attrs, defStyleAttr);
        initChecker();
    }

    private void getAttributes(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.ValidatorEditText,defStyleAttr,0);
        errorMessage=typedArray.getString(R.styleable.ValidatorEditText_errorMessage);
        checkType=typedArray.getInt(R.styleable.ValidatorEditText_checkType,DEFAULT_VALUE);
        valid=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_validBackground,DEFAULT_VALUE));
        invalid=getDrawable(typedArray.getResourceId(R.styleable.ValidatorEditText_invalidBackground,DEFAULT_VALUE));
        regex=typedArray.getString(R.styleable.ValidatorEditText_useRegex);
        timeOut=typedArray.getInt(R.styleable.ValidatorEditText_timeOut,DEFAULT_TIMEOUT);
        compareToId=typedArray.getResourceId(R.styleable.ValidatorEditText_compareTo,DEFAULT_VALUE);
        typedArray.recycle();
    }

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
        if(compareToId!=DEFAULT_VALUE)
            compareTo=((View)getParent()).findViewById(compareToId);
        checker.subscribe();
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
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(drawableState!= DrawableState.ORIGINAL)
            updateDrawableState(DrawableState.ORIGINAL);
        if(subject!=null)
            subject.onNext(text.toString());
    }

    @Override
    public void onStateChanged(boolean state) {
        if(state)
            updateDrawableState(DrawableState.VALID);
        else
            updateDrawableState(DrawableState.INVALID);
    }

    private void updateDrawableState(DrawableState drawableState){
        Drawable drawable;
        switch (drawableState){
            case VALID:
                drawable=valid;
            break;
            case INVALID:
                drawable=invalid;
            break;
            default:
                drawable=original;
        }
        this.drawableState=drawableState;
        Observable.just(drawable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setBackground);
    }

    private Drawable getDrawable(int resourceId){
        if(resourceId!=DEFAULT_VALUE){
            return ResourcesCompat.getDrawable(getResources(),resourceId,null);
        }
        return original;
    }
}
