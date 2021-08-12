package com.example.habitfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {


    private Button signup;
    private EditText name1,email1,password1;
    private FirebaseAuth firebaseAuth;
    String email, name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        signup = findViewById(R.id.btnsignup);
        name1 = findViewById(R.id.namerg);
        email1 = findViewById(R.id.emailrg);
        password1 = findViewById(R.id.passwordrg);

        firebaseAuth = FirebaseAuth.getInstance();
        clickSignUp();

    }

    private Boolean validate() {
        Boolean result = false;
        name = name1.getText().toString();
        password = password1.getText().toString();
        email = email1.getText().toString();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(RegisterActivity.this, "SUCCESSFULLY REGISTWERED, VERIFICATION MAIL SENT", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else
                        Toast.makeText(RegisterActivity.this, "Verification main has not been sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("UserInfo").child(firebaseAuth.getUid());

        UserProfile userProfile = new UserProfile(email, name);
        myRef.setValue(userProfile);
    }

    private boolean validateUsername() {
        String usernameInput = name1.getEditableText().toString().trim();
        if (usernameInput.isEmpty()) {
            name1.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 15) {
            name1.setError("Username too long");
            return false;
        } else {
            name1.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String emailInput = email1.getEditableText().toString().trim();
        if (emailInput.isEmpty()) {
            email1.setError("Field can't be empty");
            return false;
        } else {
            email1.setError(null);
            return true;
        }
    }

    private void clickSignUp(){
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUsername() && validateEmail() && validate()) {
                    //Upload data to database
                    String user_email = email1.getText().toString().trim();
                    String user_password = password1.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                sendEmailVerification();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}