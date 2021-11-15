package com.bignerdranch.android.testplatflorm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";
    private static final Pattern NAME_ADDR_EMAIL_PATTERN =
            Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");

    private Pattern emailPattern = Patterns.EMAIL_ADDRESS;
    private Context mContext;

    private TextInputLayout mUserInputLayout;
    private TextInputLayout mPwdInputLayout;
    private Button mRegisterButton;
    private Button mLoginButton;
    private LinearLayout mEditLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch);
        init();
    }

    private void init() {

        mUserInputLayout = (TextInputLayout) findViewById(R.id.username_label);
        mPwdInputLayout = (TextInputLayout) findViewById(R.id.password_label);
        mEditLayout = (LinearLayout) findViewById(R.id.edit_label);
        mLoginButton = (Button) findViewById(R.id.login_button);

        TextInputEditText UsereditText = (TextInputEditText) mUserInputLayout.getEditText();
        TextInputEditText PwdeditText = (TextInputEditText) mPwdInputLayout.getEditText();

//        mEditLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    hideKeyboard(v);
//                }
//            }
//        });

        UsereditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !PwdeditText.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });

        PwdeditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !UsereditText.hasFocus()) {
                    hideKeyboard(v);
                }
            }
        });

        UsereditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !isEmailAddress(s.toString())) {
                    UsereditText.setError("Error Address");
                    mUserInputLayout.setErrorEnabled(true);
                } else {
                    UsereditText.setError(null);
                    mUserInputLayout.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        PwdeditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mContext = this;

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, HomePageActivity.class);
                startActivity(i);
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private static String extractAddrSpec(final String address) {
        final Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);

        if (match.matches()) {
            return match.group(2);
        }
        return address;
    }

    private static boolean isEmailAddress(final String address) {
        if (TextUtils.isEmpty(address)) {
            return true;
        }

        final String s = extractAddrSpec(address);
        final Matcher match = Patterns.EMAIL_ADDRESS.matcher(s);
        return match.matches();
    }

//
//    public static void hideKeyboard(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = activity.getCurrentFocus();
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(activity);
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//
//            if (isShouldHideKeyBoard(v, ev)) {
//                hideKeyBoard(v.getWindowToken());
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }

//    private boolean isTouchPointInView(View targetView, int currentX, int currentY) {
//        if (targetView == null) {
//            return false;
//        }
//
//        int[] location = new int[2];
//        targetView.getLocationInWindow(location);
//        int left = location[0];
//        int top = location[1];
//        int right = left + targetView.getWidth();
//        int bottom = top + targetView.getHeight();
//        if (currentY >= top && currentY <= bottom && currentX >= left && currentX <= right) {
//            return true;
//        }
//
//        return false;
//    }


//    private void hideKeyBoard(IBinder windowToken) {
//        if (windowToken != null) {
//            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            im.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
//        }
//    }

//    private boolean isShouldHideKeyBoard(View v, MotionEvent ev) {
//        if (v != null && (v instanceof EditText)) {
//            int[] l = {0, 0};
//            v.getLocationInWindow(l);
//            int left = l[0],
//                    top = l[1],
//                    bottom = top + v.getHeight(),
//                    right = left + v.getWidth();
//
//            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//
//        return false;
//
//        if (v != null && (v instanceof EditText)) {
//            return true;
//        }
//
//        return false;
//    }
}