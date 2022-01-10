/**
 * ContactProfile
 * Once a user taps on a contact's profile in home activity, this activity opens with the data
 * of the contact whose profile was tapped on.
 */

package com.example.doma.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
    EditText editThought;
    ProgressBar energyBar;
    Spinner seletctMessage;
    Button send;
    String userToken;
    FirebaseUser firebaseUser;
    DatabaseReference mFirebaseDatabase;
    String thought;
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
        seletctMessage = findViewById(R.id.dropdown_menu);
        editThought = findViewById(R.id.edit_thought);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        // Getting the name of the user, who tapped on this user's profile in home activity. a.k.a: sender.
        String sender = intent.getStringExtra("sender");

        /*
            NotificationsMessagesList contains the default list of messages. Sender can select
            one of these messages, customize them or, send their own message by typing it into the
            text field.
         */
        notificationMessagesList.add(sender + " sent you a thought. That's great. Show some love.");
        notificationMessagesList.add("Your loved one, " + sender + " is thinking about you");
        notificationMessagesList.add("You have received a thought by a loved one of yours");

        // Spinner is the select menu that shows the above default messages.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, notificationMessagesList);

        seletctMessage.setAdapter(adapter); // First message in the list is selected by default.

        editThought.setText(notificationMessagesList.get(0)); // First message in the list is shown in editThought
        // editText field by default. It can be edited

        String Uid = intent.getStringExtra("Contact_Uid"); // Getting uid of the receiver.

        // Getting receiver's data to show in UI.
        UserData userData = (UserData) intent.getSerializableExtra("Contact_Data");
        Picasso.get().load(userData.getImageUrl()).fit().centerCrop().into(contactProfileImage);
        name.setText(userData.getFullName());
        energeyPercentage.setText(userData.getEnergyPercentage() + " %");
        energyBar.setProgress(Integer.valueOf(userData.getEnergyPercentage()));

        seletctMessage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editThought.setText(notificationMessagesList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                thought = notificationMessagesList.get(new Random().nextInt(notificationMessagesList.size() - 1) + 0);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editThought.getText().toString().isEmpty()) {
                    thought = editThought.getText().toString();
                }
                increaseEnergyPoints(Uid, userData);
                sendNotification(Uid,  thought, send);
            }
        });
    }

    /*
     Once sender taps on SEND button, energy point of receiver will be increased by 1 and shown
     in energy progress bar.
     */
    private void increaseEnergyPoints(String uid, UserData userData) {
        FirebaseDatabase.getInstance().getReference().child("userDetails").child(uid).child("energyPoints")
                .setValue(Integer.valueOf(userData.getEnergyPercentage()) + 1);
    }

    // Disable send button for 1 hour after a user sends a thought. This is to avoid notification spamming.
    private void disableButtonTimer(Button send) {
        new CountDownTimer(3600000, 1000) {
            public void onTick(long millisUntilFinished) {
                Long secondsRemaining = millisUntilFinished / 1000;
                String hours = String.valueOf(secondsRemaining / 3600);
                String minutes = String.valueOf((secondsRemaining % 3600) / 60);
                String seconds = String.valueOf(secondsRemaining % 60);
                String timeString = hours + ":" + minutes + ":" + seconds;
                send.setTextSize(14);
                send.setText("wait: " + timeString);
            }
            public void onFinish() {
                send.setText("SEND");
                send.setEnabled(true);
            }
        }.start();
    }

    /*
        Get the receiver's device token from db and send them a notificaion in a separate thread.
        Notification will be sent after a delay of 3 seconds to give enough time for db to return the
        device token.
     */

    private void sendNotification(String uid, String thought, Button send) {
        send.setEnabled(false);
        disableButtonTimer(send);
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
                                thought, getApplicationContext(),
                                ContactProfile.this);

                notificationSender.sendNotifications();
            }
        }, 3000);

    }
}