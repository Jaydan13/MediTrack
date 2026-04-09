package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button gotoRegisterBtn = findViewById(R.id.goToRegisterBtn);
        gotoRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        Button loginBtn = findViewById(R.id.loginBtn);
        EditText emailText = findViewById(R.id.emailText);
        EditText passwordText = findViewById(R.id.passwordText);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        loginBtn.setOnClickListener(view -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(a -> {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, HomePage.class);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
            });
        });

    }
}