package com.example.doma.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.doma.Notifications.FcmNotificationSender;
import com.example.doma.Model.UserData;
import com.example.doma.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactProfile extends AppCompatActivity {

    CircleImageView contactProfileImage;
    TextView name, energeyPercentage;
    ProgressBar energyBar;
    Button send;
    String userToken;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mFirebaseDatabase;

    List<String> notificationMessagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_profile);
        contactProfileImage = findViewById(R.id.contact_profile_image);
        name = findViewById(R.id.contact_name);
        energeyPercentage = findViewById(R.id.energy_percentage);
        energyBar = findViewById(R.id.energy_bar);
        send = findViewById(R.id.send);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String sender = intent.getStringExtra("sender");
        notificationMessagesList.add(sender + " sent you a thought. That's great. Show some love.");
        notificationMessagesList.add("Your loved one, " + sender + " is thinking about you");
        notificationMessagesList.add("You have received a thought by a loved one of yours");
        String Uid = intent.getStringExtra("Contact_Uid");
        UserData userData = (UserData) intent.getSerializableExtra("Contact_Data");
        Picasso.get().load(userData.getImageUrl()).fit().centerCrop().into(contactProfileImage);
        name.setText(userData.getFullName());
        energeyPercentage.setText(userData.getEnergyPercentage() + " %");
//        energyBar.setProgressDrawable();
        energyBar.setProgress(Integer.valueOf(userData.getEnergyPercentage()));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseEnergyPoints(Uid, userData);
                sendNotification(Uid,  sender);

            }
        });

    }

    private void increaseEnergyPoints(String uid, UserData userData) {
        FirebaseDatabase.getInstance().getReference().child("userDetails").child(uid).child("energyPoints")
                .setValue(Integer.valueOf(userData.getEnergyPercentage()) + 1);
    }

    private void sendNotification(String uid, String name) {

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(uid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userToken = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FcmNotificationSender notificationSender =
                        new FcmNotificationSender(userToken, "ThinkingU-Doma",
                                notificationMessagesList.get(new Random().nextInt(notificationMessagesList.size() - 1) + 0), getApplicationContext(),
                                ContactProfile.this);

                notificationSender.sendNotifications();
            }
        }, 3000);

    }
}