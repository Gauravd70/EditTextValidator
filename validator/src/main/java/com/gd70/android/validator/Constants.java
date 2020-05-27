package com.gd70.android.validator;

class Constants {
    //TAG
    static final String TAG="ValidatorEditText";

    //Defaults
    static int DEFAULT_VALUE=-1;
    static int DEFAULT_TIMEOUT=500;
    static int DEFAULT_HELPER_TEXT_SIZE=20;

    //Check types
    static final int ALPHA=0;
    static final int NUMERIC=1;
    static final int DOB=2;
    static final int PHONE=3;
    static final int EMAIL=4;
    static final int ALPHANUMERIC=5;
    static final int USERNAME=6;
    static final int PASSWORD=7;

    //Relative position
    static final int LEFT=0;
    static final int RIGHT=1;

    //Error messages
    static final String REQUIRED="This field cannot be empty";
}
