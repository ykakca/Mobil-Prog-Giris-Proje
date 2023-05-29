package com.example.mobiletermproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextName, editTextSurname, editTextEntryYear, editTextFinishYear, editTextEmail, editTextPassword;
    private MaterialButton registerButton, returnToLoginButton;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.name);
        editTextSurname = findViewById(R.id.surname);
        editTextEmail = findViewById(R.id.email_registration);
        editTextPassword = findViewById(R.id.password_registration);
        registerButton = findViewById(R.id.sign_up_button);
        returnToLoginButton = findViewById(R.id.return_to_login);
        returnToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnLoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(returnLoginIntent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, surname, entryYear, finishYear, email, password;
                name = editTextName.getText().toString();
                surname = editTextSurname.getText().toString();
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(RegistrationActivity.this, "İsminizi giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(surname)){
                    Toast.makeText(RegistrationActivity.this, "Soyisminizi giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegistrationActivity.this, "E-mail adresinizi giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegistrationActivity.this, "Şifre giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Geçersiz e-posta adresi");
                    editTextEmail.setFocusable(true);
                }
                else if (password.length() < 6) {
                    editTextPassword.setError("Şifre uzunluğu 6'dan büyük olmalı");
                    editTextPassword.setFocusable(true);
                }
                else {
                    registerUser(email, password, name, surname);
                }
            }

            private void registerUser(String email, final String password, final String name, final String surname) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", name);
                            hashMap.put("surname", surname);
                            hashMap.put("image", "");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);
                            Toast.makeText(RegistrationActivity.this, "Registered User " + user.getEmail(), Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(RegistrationActivity.this, DashboardActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Hata", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, "Hata oluştu", Toast.LENGTH_LONG).show();
                    }
                });
            }


        });
    }
}