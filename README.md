# EditTextValidator
EditTextValidator is a library for Android which helps in input validation using regular expressions.

## Features
* **Easy** to use
* Input validation using regular expressions
* Has basic predefined regular expressions for form validation
* Can be used to **compare one Edittext with another**
* Has **high flexibility** allowing the use of **custom ui components as backgrounds or drawables**  
* Has **Timeout** feature which waits till the desired time before validation

## Prerequisites
1. The library makes use of Java8. Add the following to your app build.gradle if not present.
```
android{
  compileOptions{
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
2. The library is built on AndroidX. So, for successful compilation migrate to AndroidX. Steps of migration :-
  * Click on _Refactor_ on the menubar
  * At the bottom part of the Refactor menu click on _Migrate to AndriodX_
 
## Adding dependency
1. Add this in your root build.gradle 
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency to your app build.gradle
```
dependencies{
    implementation 'com.github.Gauravd70:EditTextValidator:1.0.2'
 }
```

## Additional Attributes of ValidatorEditText
1. **app:checkType=""** - Used to define the type of validation. By default is alpha.
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:checkType="email"/>
```

2. **app:validBackground=""** - Background drawable to be used if input is valid
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:validBackground="@drawable/validBackgroundDrawable"/>
```

3. **app:invalidBackground=""** - Background drawable to be used if input is invalid
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:invalidBackground="@drawable/invalidBackgroundDrawable"/>
```

4. **app:validDrawable=""** - Drawable to be used if input is valid **(This is shown inside the EditText)**
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:validDrawable="@drawable/validDrawable"/>
```

5. **app:invalidDrawable=""** - Drawable to be used if input is invalid **(This is shown inside the EditText)**
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:invalidDrawable="@drawable/invalidDrawable"/>
```

6. **app:drawablePosition=""** - Location of the drawable inside the edittext. By default is **Right(end)** of the edittext
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:drawablePosition="left"/>
```

7. **app:useRegex=""** - provide a custom regex to be used for validation
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:useRegex="[0-9]+"/>
```

8. **app:timeOut=""** - wait time(in milliseconds) before validation is started. By default is **500ms**
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:timeOut="1000"/>
```

9. **app:compareTo=""** - used to compare one edittext with another
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:compareTo="@id/password"/>
```

10. **app:required=""** - mark a field as required
```
<com.gd70.android.validator.ValidatorEditText
                .
                .
                app:required="true"/>
```

11. To check for the validity on form submission you can use the following function
```
ValidatorEditText validatorEditText=findViewById(R.id.validatorText);
if(validatorEditText.isValid()){
  //valid
}
else{
  //invalid
}
```

## How to use

1. Use ValidatorEditText instead of EditText
```
<com.gd70.android.validator.ValidatorEditText
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:layout_constraintTop_toTopOf="parent"
                android:textSize="20sp"
                android:padding="10sp"
                android:hint="@string/username"
                />
```

2. Define the validation type using checkType attribute or provide your own regular expression to the useRegex attribute
  

 





