package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button registerBTN, loginBTN;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.idLoginEmail);
        password = findViewById(R.id.idLoginPassword);
        registerBTN = findViewById(R.id.idLoginToSignUp);
        loginBTN = findViewById(R.id.idLoginButton);
        checkBox = findViewById(R.id.idLoginCheckBox);
        progressBar = findViewById(R.id.idLoginProgressBar);
        mAuth = FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                    confirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        registerBTN.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        loginBTN.setOnClickListener(view -> {
            String emailText = email.getText().toString();
            String passText = password.getText().toString();
            if(!TextUtils.isEmpty(emailText) || !TextUtils.isEmpty(passText)){
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(emailText,passText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            sendToMain();
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            String error = task.getException().getMessage();
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Please enter all Fields", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void sendToMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}