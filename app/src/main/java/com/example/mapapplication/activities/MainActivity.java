package com.example.mapapplication.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.mapapplication.MessageDisplay;
import com.example.mapapplication.PermissionRequest;
import com.example.mapapplication.R;
import com.example.mapapplication.data.Container;
import com.example.mapapplication.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "EmailPassword";

    private Button loginButton;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;

    private ProgressDialog loadingBar;

    Drawable ErrorDrawable;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
//    private FirebaseDatabase myDatabase;

    private List<Container> containers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("users");
//        myDatabase = myRef.getDatabase();

        loginButton = findViewById(R.id.login_button);

        textInputUsername = findViewById(R.id.text_input_username);
        textInputPassword = findViewById(R.id.text_input_password);

        ErrorDrawable = getResources().getDrawable(R.drawable.ic_error);
        ErrorDrawable.setBounds(0, 0, ErrorDrawable.getIntrinsicWidth(), ErrorDrawable.getIntrinsicHeight());

        loadingBar = new ProgressDialog(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUsername() && validatePassword()) {
                    checkSignInFireBase(textInputUsername.getEditText().getText().toString(), textInputPassword.getEditText().getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Email or password error", Toast.LENGTH_SHORT);
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
                textInputUsername.setError(null);
                textInputUsername.getEditText().setCompoundDrawables(null, null, null, null);

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
                textInputPassword.setError(null);

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

        CheckAddNewUser("taha.almokahel@gmail.com", "taha","123456");
    }

    private void CheckAddNewUser(final String email, final String username, final String password) {
        myRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        addNewUser(email, username, password);
                    } else {
                        Toast.makeText(MainActivity.this, "Username already exists. Please try other username.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            }
        );
    }

    private void addNewUser(final String email, final String username, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");

                        FirebaseUser user = mAuth.getCurrentUser();

                        User _user = new User(email, username, password, false);
                        myRef.child(user.getUid()).setValue(_user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void checkSignInFireBase(final String username, final String password) {
        loadingBar.setTitle("Sign In");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        myRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    User user = new User();
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        user = singleSnapshot.getValue(User.class);
                    }
                    signInFireBase(user.email, password);
                } else {
                    MessageDisplay.showToastLong(MainActivity.this, "There is no such username!");
                    textInputUsername.setError("error");
                    if (textInputUsername.getChildCount() == 2) {
                        textInputUsername.getChildAt(1).setVisibility(View.GONE);
                    }
                    textInputUsername.getEditText().setTextColor(Color.RED);
                    textInputUsername.getEditText().setCompoundDrawables(null, null, ErrorDrawable, null);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void signInFireBase(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @RequiresApi(api = Build.VERSION_CODES.P)
                @SuppressLint("ResourceAsColor")
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        startActivity(intent);

                    } else {
                        MessageDisplay.showToastLong(MainActivity.this, "Password Not Correct");
                        textInputPassword.setError("error");
                        if (textInputPassword.getChildCount() == 2) {
                            textInputPassword.getChildAt(1).setVisibility(View.GONE);
                        }
                        textInputPassword.getEditText().setTextColor(Color.RED);
                    }
                    loadingBar.dismiss();
                }
            });
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