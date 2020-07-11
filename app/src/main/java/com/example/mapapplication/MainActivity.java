package com.example.mapapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    private Button loginButton;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;

    Drawable ErrorDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login_button);

        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_password);

        ErrorDrawable = getResources().getDrawable(R.drawable.ic_error);
        ErrorDrawable.setBounds(0, 0, ErrorDrawable.getIntrinsicWidth(), ErrorDrawable.getIntrinsicHeight());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUsername() && validatePassword()) {
                    loginFirebase();
                }
            }
        });

        textInputUsername.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputUsername.getEditText().setTextColor(Color.DKGRAY);

                if (!TextUtils.isEmpty(textInputUsername.getEditText().getText()) && !TextUtils.isEmpty(textInputPassword.getEditText().getText())) {
                    loginButton.setAlpha(1f);
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setAlpha(0.3f);
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textInputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputPassword.getEditText().setTextColor(Color.DKGRAY);

                if (!TextUtils.isEmpty(textInputUsername.getEditText().getText()) && !TextUtils.isEmpty(textInputPassword.getEditText().getText())) {
                    loginButton.setAlpha(1f);
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setAlpha(0.3f);
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        int permission_All = 1;
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE};
        if (!PermissionRequest.hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, permission_All);
        }

        PermissionRequest.checkNetworkConnected(this);
    }

    private void loginFirebase(){

    }

    private boolean validateUsername() {
        String usernameInput = textInputUsername.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputUsername.getEditText().setError("Field can't be empty", ErrorDrawable);
            return false;
        }  else {
            textInputUsername.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}