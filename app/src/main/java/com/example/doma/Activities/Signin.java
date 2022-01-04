package com.example.doma.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.doma.Model.UserData;
import com.example.doma.R;
import com.example.doma.Utils.MyCustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signin extends AppCompatActivity {

    EditText userEmail, userPassword;
    Button signin;
    TextView textViewSignUp;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        userEmail = findViewById(R.id.edittextemailsignin);
        userPassword = findViewById(R.id.edittextpasswordsignin);

        signin = findViewById(R.id.signin_signin);
        textViewSignUp = findViewById(R.id.textviewsignup);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    moveToHome(firebaseUser);
                } else {
                    MyCustomToast.createToast(Signin.this, "Please login.", true);
                }
            }
        };

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] inputs = userInputsSignIn();
                if (inputValidation(inputs[0], inputs[1])) {
                    firebaseAuth.signInWithEmailAndPassword(inputs[0], inputs[1])
                            .addOnCompleteListener(Signin.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        MyCustomToast.createToast(Signin.this, "Sign in error. Please try again.", true);
                                    } else {
                                        moveToHome(task.getResult().getUser());
                                    }
                                }
                            });
                } else {
                    MyCustomToast.createToast(Signin.this, "Fields are empty.", true);
                }
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signin.this, Register.class);
                startActivity(intent);
            }
        });

    }

    public String[] userInputsSignIn () {
        String [] inputs = new String[2];

        final String email = userEmail.getText().toString();
        final String password = userPassword.getText().toString();
        inputs[0] = email;
        inputs[1] = password;

        return inputs;
    }

    public boolean inputValidation (String email, String password) {

        if (email.isEmpty()) {
            userEmail.setError("Please provide an email");
            userEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            userPassword.setError("Please provide a password");
            userPassword.requestFocus();
            return false;
        }

        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void moveToHome(FirebaseUser firebaseUser) {

        firebaseDatabase.getReference().child("userDetails").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = snapshot.getValue(UserData.class);
                String name = userData.getFullName();
                String phone = userData.getPhoneNumber();
                String profileImage = userData.getImageUrl();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("name", name);
                intent.putExtra("phone", phone);
                intent.putExtra("profileImage", profileImage);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}