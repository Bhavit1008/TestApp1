package com.example.testapp1.ChatInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.testapp1.DataClasses.ComplaintClass;
import com.example.testapp1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewActivity extends Activity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    Button submit,cancel;
    EditText edtname,edtcomplaint,age,gender;
    String status = "0",AGE,GENDER;
    FirebaseAuth auth,auth1;
    DatabaseReference refernce,reference1;
    FirebaseUser user,user1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_new);
        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel_btn);
        edtname = findViewById(R.id.edit_username);
        edtcomplaint = findViewById(R.id.edit_complaint);
        gender = findViewById(R.id.gender);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        age = findViewById(R.id.age);
//You can change "yyyyMMdd_HHmmss as per your requirement


        String[] items = new String[]{"Male", "Female", "others"};

        String currentDateandTime = sdf.format(new Date());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_new, items);


        final String name = edtname.getText().toString().trim();
        final String complaint = edtcomplaint.getText().toString().trim();
        final String number = "";

        Intent intent = getIntent();
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        if (latitude == null && longitude == null){
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            refernce = FirebaseDatabase.getInstance().getReference("Registered_Users").child(user.getUid());
            refernce.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String number = dataSnapshot.child("phoneNumber").getValue().toString();
                    String lat = dataSnapshot.child("latitude").getValue().toString();
                    String lng = dataSnapshot.child("longitude").getValue().toString();
                    Toast.makeText(NewActivity.this, number, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = edtname.getText().toString().trim();
                final String complaint = edtcomplaint.getText().toString().trim();
                AGE = age.getText().toString();
                GENDER = gender.getText().toString();
                SharedPreferences prefs = getSharedPreferences("Locations", MODE_PRIVATE);
                String lat = prefs.getString("lat", "No defined lat");//"No name defined" is the default value.
                String lng = prefs.getString("lng", "no defined lng");
                final String number = "";
                ComplaintClass c = new ComplaintClass(
                        name,
                        complaint,
                        status,
                        currentDateandTime,
                        AGE,
                        GENDER,
                        lat,
                        lng
                );




                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child(user.getUid()).child("Complaints").setValue(c);
                finish();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
