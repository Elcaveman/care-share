package com.example.medicalappv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import firebase.Register;

public class RegisterActivity extends AppCompatActivity {

    EditText mNom, mPrenom, mEmail, mPassword, mNumero;
    Button mRegisterButton;
    TextView mLoginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    Register fRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNom = findViewById(R.id.Nom);
        mPrenom = findViewById(R.id.Prenom);
        mEmail = findViewById(R.id.EmailLogin);
        mPassword = findViewById(R.id.mdp);
        mNumero = findViewById(R.id.numero);
        mRegisterButton = findViewById(R.id.RegisterButton);
        mLoginButton = findViewById(R.id.LoginButton);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();
        fRegister = new Register();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String numero = mNumero.getText().toString().trim();
                String nom = mNom.getText().toString().trim();
                String prenom = mPrenom.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Un email est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mEmail.setError("Un mot de passe est nécessaire");
                    return;
                }
                if (password.length() < 6) {
                    mPassword.setError("Mot de passe doit avoir plus de 6 charactères");
                    return;
                }
                if (TextUtils.isEmpty(numero)) {
                    mEmail.setError("Un numero est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(nom)) {
                    mEmail.setError("Un nom est nécessaire");
                    return;
                }
                if (TextUtils.isEmpty(prenom)) {
                    mEmail.setError("Un prenom est nécessaire");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                fRegister.registerEmailPassword(email, password, nom, prenom, numero, RegisterActivity.this);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}