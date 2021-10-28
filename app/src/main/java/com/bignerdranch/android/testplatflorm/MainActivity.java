package com.bignerdranch.android.testplatflorm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";

    private TextInputLayout mUserInputLayout;
    private TextInputLayout mPwdInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch);
        init();
    }

    private void init() {

        mUserInputLayout = (TextInputLayout) findViewById(R.id.username_label);
        mPwdInputLayout = (TextInputLayout) findViewById(R.id.password_label);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyBoard(v, ev)) {
                hideKeyBoard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyBoard(IBinder windowToken) {
        if (windowToken != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isShouldHideKeyBoard(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();

            if (ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}