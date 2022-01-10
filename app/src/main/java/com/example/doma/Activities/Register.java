/**
 *
 * Register activity
 * Users can input their data and register themselves into the firebase database
 * all fields in this activity are required.
 *
 */

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

        // referencing the UI elements
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

        // Creating firebase auth instances
        mFirebaseAuth = FirebaseAuth.getInstance();
        // Firebase database instance to refer to data inside database.
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        // storage reference to store image files
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        // clicking this button will open up the users device's photo library and they can choose a photo.
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
        // Get the image data of the user's picked image and load that image in profileImageView UI
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(profileImageView);
        }
    }

    public String[] userInputs () {
        String [] inputs = new String[4];

        // Return user inputs for all fields as an array.

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

    // validate user inputs. If there is any emoty field upon registration, show error
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

    // register user in firebase and call uploadProfileImageToDb function
    public void registerUserInFirebase (String email, String password, String name, String phoneNumber) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            MyCustomToast.createToast(Register.this, "Registration unsuccessful. Please, try again.", true );
                        } else {
                            uploadProfileImageToDb(task, email, name, phoneNumber);
                        }
                    }
                });
        }

        private String getFileExtension(Uri uri) {
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(contentResolver.getType(uri));
        }

        // Store registering user's device token in database. Needed to send notification
        private void storeUserDeviceTokenInDatabase(String uid) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
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

        // Store user's metadata in database after successful image upload.
        private void storeUserDataInDatabase(String uid, Uri uri, Task<AuthResult> task, String email, String name, String phoneNumber) {
            /*
                Store user data in database under userDetails node's child node that is
                this user's uid that is provided by firebase. Database structure userDetails > uID > <data>
                After successful data insertion into the database, go to home activity and pass the
                name, phone, profileImage and email to the home activity
            */
            UserData userData = new UserData(name, phoneNumber, uri.toString(), 50, "");
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
        }

        private void uploadProfileImageToDb(Task<AuthResult> task, String email, String name, String phoneNumber) {
            /*
                If user has selected an image, upload it to storage reference and then make a call to
                storeUserDataInDatabase() and storeUserDeviceTokenInDatabase() functions.
                If user has not selected an image, show toast saying, "No image selected".
             */
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
                                        String uid = task.getResult().getUser().getUid();
                                        storeUserDataInDatabase(uid,uri, task, email, name, phoneNumber);
                                        storeUserDeviceTokenInDatabase(uid);
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