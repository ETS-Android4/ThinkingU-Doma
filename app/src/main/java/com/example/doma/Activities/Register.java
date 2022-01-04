package com.example.doma.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doma.Model.UserData;
import com.example.doma.R;
import com.example.doma.Utils.MyCustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText userEmail, userPassword, userFullName, userPhoneNumber;
    Button profileImage, signUp;
    TextView textViewSignIn, profile;
    CircleImageView profileImageView;
    ProgressBar progressBar;

    private Uri mImageUri;
    private StorageReference storageReference;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.edittextemail);
        userPassword = findViewById(R.id.edittextpassword);
        userFullName = findViewById(R.id.edittextname);
        userPhoneNumber = findViewById(R.id.edittextphone);

        profileImage = findViewById(R.id.chooseimagebutton);
        signUp = findViewById(R.id.signup);

        textViewSignIn = findViewById(R.id.textviewsignin);
        profile = findViewById(R.id.profile);
        profileImageView = findViewById(R.id.profile_image);
        progressBar = findViewById(R.id.register_progress);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] inputs = userInputs();
                if (inputs != null) {
                    if (inputValidation(inputs[0], inputs[1], inputs[2], inputs[3])) {
                        registerUserInFirebase(inputs[0], inputs[1], inputs[2], inputs[3]);
                    }
                } else {
                    MyCustomToast.createToast(Register.this, "An error occurred while retrieving user's entered inputs..", true );
                }
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Signin.class);
                startActivity(intent);
            }
        });

    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(profileImageView);
        }
    }

    public String[] userInputs () {
        String [] inputs = new String[4];

        final String email = userEmail.getText().toString();
        final String password = userPassword.getText().toString();
        final String fullName = userFullName.getText().toString();
        final String phoneNumber = userPhoneNumber.getText().toString();
        inputs[0] = email;
        inputs[1] = password;
        inputs[2] = fullName;
        inputs[3] = phoneNumber;

        return inputs;
    }

    public boolean inputValidation (String email, String password, String fullName, String phoneNumber) {

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

        if (fullName.isEmpty()) {
            userFullName.setError("Please provide your full name");
            userFullName.requestFocus();
            return false;
        }

        if (phoneNumber.isEmpty()) {
            userPhoneNumber.setError("Please provide a phone number");
            userPhoneNumber.requestFocus();
            return false;
        }

        return true;

    }

    public void registerUserInFirebase (String email, String password, String name, String phoneNumber) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            MyCustomToast.createToast(Register.this, "Registration unsuccessful. Please, try again.", true );
                        } else {
                            uploadUserData(task, email, name, phoneNumber);
                        }
                    }
                });
        }

        private String getFileExtension(Uri uri) {
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(contentResolver.getType(uri));
        }

        private void uploadUserData(Task<AuthResult> task, String email, String name, String phoneNumber) {
            if (mImageUri != null) {
                StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                +"."+getFileExtension(mImageUri));
                fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        UserData userData = new UserData(name, phoneNumber, uri.toString(), new Random().nextInt(100) + 1, "");
                                        String uid = task.getResult().getUser().getUid();
                                        mFirebaseDatabase.getReference().child("userDetails").child(uid)
                                                .setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent intent = new Intent(Register.this, Home.class);
                                                intent.putExtra("name", name);
                                                intent.putExtra("phone", phoneNumber);
                                                intent.putExtra("profileImage", uri.toString());
                                                intent.putExtra("email", email);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                MyCustomToast.createToast(Register.this, "An error occurred while storing the data. Please try again.", true );
                                            }
                                        });
                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {
                                                        if (!task.isSuccessful()) {
                                                            //Log.w("TOKEN", "Fetching FCM registration token failed", task.getException());
                                                            Toast.makeText(Register.this, "Token generation unsuccessful.", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        // Get new FCM registration token
                                                        String token = task.getResult();
                                                        FirebaseDatabase.getInstance().getReference("Tokens").child(uid).child("token").setValue(token);
                                                        //FirebaseDatabase
                                                        Log.d("TOKEN GOT in register", token);
                                                    }
                                                });
                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }


}