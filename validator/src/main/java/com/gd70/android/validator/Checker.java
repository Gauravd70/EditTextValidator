package com.gd70.android.validator;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import static com.gd70.android.validator.Constants.ALPHA;
import static com.gd70.android.validator.Constants.ALPHANUMERIC;
import static com.gd70.android.validator.Constants.DEFAULT_VALUE;
import static com.gd70.android.validator.Constants.DOB;
import static com.gd70.android.validator.Constants.EMAIL;
import static com.gd70.android.validator.Constants.NUMERIC;
import static com.gd70.android.validator.Constants.PASSWORD;
import static com.gd70.android.validator.Constants.PHONE;
import static com.gd70.android.validator.Constants.USERNAME;

public class Checker {
    private String regex;
    private int timeOut;
    private SerializedSubject<String,String> inputBus;
    private Subscription checkSubscription;

    public Subject<String,String> getSubject(){
        return inputBus;
    }

    public interface CheckerInterface{
        void onStateChanged(boolean state);
    }
    private CheckerInterface anInterface;

    public Checker(ValidatorEditText editText, String regex, int timeOut) {
        init(editText,DEFAULT_VALUE,regex,timeOut);
    }

    public Checker(ValidatorEditText editText, int checkType, int timeOut) {
        init(editText,checkType,null,timeOut);
    }

    private void init(ValidatorEditText editText,int checkType,String regex,int timeOut){
        this.timeOut=timeOut;
        inputBus=new SerializedSubject<>(PublishSubject.create());
        anInterface= editText;
        if(checkType!=DEFAULT_VALUE){
            switch (checkType)
            {
                case ALPHA:
                {
                    this.regex="[a-zA-Z ]+";
                }
                break;
                case NUMERIC:
                {
                    this.regex="[0-1]?[0-9]?[0-9]|2[0]{2}";
                }
                break;
                case DOB:
                {
                    this.regex="(0[1-9]|1[0-9]|2[0-9]|3[01]).(0[1-9]|1[012]).[0-9]{4}";
                }
                break;
                case PHONE:
                {
                    this.regex="\\d{2}\\d{4}\\d{4}";
                }
                break;
                case EMAIL:
                {
                    this.regex="^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})$";
                }
                break;
                case ALPHANUMERIC:
                {
                    this.regex="[a-zA-Z0-9 ]+";
                }
                break;
                case USERNAME:
                {
                    this.regex="^[a-zA-Z][a-zA-Z0-9-_.]{5,20}$";
                }
                break;
                case PASSWORD:
                {
                    this.regex="(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$";
                }
            }
        }
        else
            this.regex=regex;
    }

    public void subscribe() {
        checkSubscription=inputBus.debounce(timeOut, TimeUnit.MILLISECONDS)
                .subscribe(s1 -> {
                    Pattern pattern=Pattern.compile(regex);
                    Matcher m=pattern.matcher(s1);
                    anInterface.onStateChanged(m.matches());
                });
    }

    public void unsubscribe(){
        if(checkSubscription!=null)
            checkSubscription.unsubscribe();
    }
}
