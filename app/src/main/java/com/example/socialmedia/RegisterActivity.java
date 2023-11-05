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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, confirmPass;
    private Button registerBTN, loginBTN;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.idRegisterEmail);
        registerBTN = findViewById(R.id.idRegisterButton);
        loginBTN = findViewById(R.id.idRegisterToLogin);
        password = findViewById(R.id.idRegisterPassword);
        confirmPass = findViewById(R.id.idRegisterConfirmPassword);
        progressBar = findViewById(R.id.idRegisterProgressBar);
        checkBox = findViewById(R.id.idRegisterCheckBox);
        mAuth = FirebaseAuth.getInstance();

        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                confirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        registerBTN.setOnClickListener(view -> {
            String emailText = email.getText().toString();
            String passText = password.getText().toString();
            String confirmPassText = confirmPass.getText().toString();

            if(!TextUtils.isEmpty(emailText) || !TextUtils.isEmpty(passText) || !TextUtils.isEmpty(confirmPassText)){
                if(passText.equals(confirmPassText)){
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(emailText,passText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                sendToMain();
                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                                String error = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Password do not Match", Toast.LENGTH_SHORT).show();
                }
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
        loginBTN.setOnClickListener(view1 -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void sendToMain() {
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            sendToMain();
        }
    }
}