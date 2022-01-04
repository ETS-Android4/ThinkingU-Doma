package com.example.doma.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.doma.Adapters.GridAdapter;
import com.example.doma.Model.UserData;
import com.example.doma.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    Button signout;
    GridView gridView;
    CircleImageView userProfileImage;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    List<String> phoneNumsFromDb;
    List<String> phoneNumsFromPhone;
    List<UserData> contactsWithAppInstalled;
    List<String> contactsWithAppInstalledUids;
    List<Drawable> drawablesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        signout = findViewById(R.id.signout);
        userProfileImage = findViewById(R.id.profile_image_home);
        gridView = findViewById(R.id.contacts_installed_app_gridView);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("userDetails");

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    //Log.w("TOKEN", "Fetching FCM registration token failed", task.getException());
                    Toast.makeText(Home.this, "Token generation unsuccessful in home.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid()).child("token").setValue(token);
                //FirebaseDatabase
                Log.d("TOKEN GOT in home", token);
            }
        });

        phoneNumsFromDb = new ArrayList<>();
        String imageUrl = getIntent().getStringExtra("profileImage");
        Picasso.get().load(imageUrl).fit().centerCrop().into(userProfileImage);

        contactsWithAppInstalled = new ArrayList<>();
        contactsWithAppInstalledUids = new ArrayList<>();

        drawablesList = new ArrayList<>();
        drawablesList.add(getResources().getDrawable(R.drawable.progress_drawable_vertical));
        drawablesList.add(getResources().getDrawable(R.drawable.progress_drawable_vertical_second));
        drawablesList.add(getResources().getDrawable(R.drawable.progress_drawable_vertical_third));
        drawablesList.add(getResources().getDrawable(R.drawable.progress_drawable_verttical_fourth));
        drawablesList.add(getResources().getDrawable(R.drawable.progress_drawable_vertical_fifth));
        drawablesList.add(getResources().getDrawable(R.drawable.progress_drawable_vertical_sixth));
        phoneNumsFromPhone = getContactList();

        findContactsWithAppInstalled();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Home.this, ContactProfile.class);
                intent.putExtra("Contact_Data", contactsWithAppInstalled.get(position));
                intent.putExtra("Contact_Uid", contactsWithAppInstalledUids.get(position));
                intent.putExtra("sender", name);
                startActivity(intent);
                //Toast.makeText(Home.this, contactsWithAppInstalled.get(position).getFullName(), Toast.LENGTH_SHORT).show();
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Home.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    public void findContactsWithAppInstalled() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String phoneNum = dataSnapshot.child("phoneNumber").getValue().toString();
                    for (String phoneNumFromPhone : phoneNumsFromPhone) {
                        if (phoneNumFromPhone.contains(phoneNum) || phoneNumFromPhone.equals(phoneNum)) {
                            if (!containsName(contactsWithAppInstalled, phoneNum)) {
                                UserData userData = dataSnapshot.getValue(UserData.class);
                                contactsWithAppInstalledUids.add(dataSnapshot.getKey());
                                contactsWithAppInstalled.add(userData);
                            }
                        }
                    }
                    phoneNumsFromDb.add(dataSnapshot.child("phoneNumber").getValue().toString());
                }
                GridAdapter gridAdapter = new GridAdapter(Home.this, contactsWithAppInstalled, drawablesList);
                gridView.setAdapter(gridAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean containsName(final List<UserData> list, final String phoneNumber){
        return list.stream().map(UserData::getPhoneNumber).filter(phoneNumber::equals).findFirst().isPresent();
    }

    private List<String> getContactList() {
        List<String> phoneNumsFromPhone = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumsFromPhone.add(phoneNo.replaceAll("\\s+", ""));
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        return phoneNumsFromPhone;
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        FirebaseAuth.getInstance().signOut();
//        Intent intent = new Intent(Home.this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
}