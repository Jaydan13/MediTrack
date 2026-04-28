package com.example.meditrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText emailText, passwordText;
    Button loginBtn, gotoRegisterBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ThemeHelper.applyTheme(this);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        loginBtn = findViewById(R.id.loginBtn);
        gotoRegisterBtn = findViewById(R.id.goToRegisterBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        gotoRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(view -> loginUser());

    }

    private void loginUser() {

        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All Fields Required!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = mAuth.getCurrentUser().getUid();

                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {

                            if (documentSnapshot.exists()) {
                                String username = documentSnapshot.getString("username");

                                Toast.makeText(this, "Welcome " + username, Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(Login.this, HomePage.class));
                                finish();
                            } else {
                                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}