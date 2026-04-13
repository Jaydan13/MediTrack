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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText newEmail, newPass, checkNewPass, username;
    Button registerBtn, loginReturnBtn;
    FirebaseAuth mAuth;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        newEmail = findViewById(R.id.newEmail);
        newPass = findViewById(R.id.newPass);
        checkNewPass = findViewById(R.id.checkNewPass);
        username = findViewById(R.id.username);
        registerBtn = findViewById(R.id.registerBtn);
        loginReturnBtn = findViewById(R.id.loginReturnBtn);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginReturnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(view -> registerUser());

    }

    private void registerUser() {

        String email = newEmail.getText().toString().trim();
        String password = newPass.getText().toString().trim();
        String confirmPassword = checkNewPass.getText().toString().trim();
        String user = username.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(user)) {
            Toast.makeText(this, "All Fields Required!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            checkNewPass.setError("Passwords do not match!!!");
            return;
        }

        if (password.length() < 6) {
            newPass.setError("Password must be atleast 6 Characters");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                String userId = mAuth.getCurrentUser().getUid();

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("username", user);
                userMap.put("email", email);

                db.collection("users").document(userId).set(userMap).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

            } else {
                Toast.makeText(this, "Registration Failed: " + task.getException(), Toast.LENGTH_LONG).show();
            }
        });
    }
}