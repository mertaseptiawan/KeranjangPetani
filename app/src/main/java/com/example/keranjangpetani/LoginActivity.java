package com.example.keranjangpetani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView Daftar;
    private EditText etEmail, etPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        progressBar = findViewById(R.id.loginProgresbar);
        mAuth = FirebaseAuth.getInstance();

        Daftar = findViewById(R.id.linkDaftar);
        Daftar.setOnClickListener(this);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linkDaftar:
                startActivity(new Intent(this, SignupActivity.class));
                break;

            case R.id.buttonLogin:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("masukkan email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("email tidak valid");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("masukkan password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("jumlah pasword minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    progressBar.setVisibility(View.VISIBLE);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Gagal", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}