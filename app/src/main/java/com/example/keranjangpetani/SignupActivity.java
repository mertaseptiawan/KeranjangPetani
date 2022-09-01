package com.example.keranjangpetani;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "TAG";
    private EditText etUsername, etEmail, etPhone, etPassword, etConfirmPassword;
    private String username, email, phone, password, confirmPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.username);
        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPassword = findViewById(R.id.password);
        etConfirmPassword = findViewById(R.id.confirmPassword);

        btnSignup = findViewById(R.id.signupButton);
        btnSignup.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.signupProgresbar);

        db = FirebaseFirestore.getInstance();

        tvLogin = findViewById(R.id.linkLogin);
        tvLogin.setOnClickListener(this);

        //hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linkLogin:
                login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.signupButton:
                signupButton();
                break;
        }
    }

    private void signupButton() {
        email = etEmail.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean isValid = validate();

        if (!isValid) {
            return;
        }

        register();
    }

    private boolean validate() {
        if (username.isEmpty()) {
            etUsername.setError("masukkan username");
            etUsername.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("masukkan nomor hp");
            etPhone.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("masukkan email");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("email tidak valid");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("masukkan password");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("jumlah password minimal 6 karakter");
            etPassword.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("masukkan konfirmasi password");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;

    }

    private void register() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser user = mAuth.getCurrentUser();

                            Map<String, Object> userPayload = new HashMap<>();
                            userPayload.put("uname", username);
                            userPayload.put("email", email);
                            userPayload.put("phone", phone);

                            DocumentReference documentReference = db.collection("users").document(user.getUid());
                            documentReference.set(userPayload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    Toast.makeText(SignupActivity.this, "Berhasil membuat akun", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.VISIBLE);
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ERROR", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignupActivity.this, "gagal membuat akun", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Log.w("ERROR", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "akun sudah terdaftar", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}