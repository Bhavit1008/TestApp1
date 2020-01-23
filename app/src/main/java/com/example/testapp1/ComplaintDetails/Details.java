package com.example.testapp1.ComplaintDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Details extends Activity {

    FirebaseAuth auth,auth1;
    DatabaseReference refernce,reference1;
    FirebaseUser user,user1;
    TextView result_number,result_name,result_complaint,result_age,result_gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_details);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        refernce = FirebaseDatabase.getInstance().getReference("Registered_Users").child(user.getUid());

        result_number = findViewById(R.id.result_number);
        result_complaint = findViewById(R.id.result_complaint);
        result_age = findViewById(R.id.result_age);
        result_gender = findViewById(R.id.result_gender);
        result_name = findViewById(R.id.result_name);
        auth1 = FirebaseAuth.getInstance();
        user1 = auth1.getCurrentUser();
        reference1 = FirebaseDatabase.getInstance().getReference(user.getUid()).child("Complaints");
        result_number = findViewById(R.id.result_number);

        refernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String number = dataSnapshot.child("phoneNumber").getValue().toString();
                result_number.setText(number);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String complaint = dataSnapshot.child("complaint").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                String age = dataSnapshot.child("age").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                result_complaint.setText(complaint);
                result_name.setText(name);
                result_age.setText(age);
                result_gender.setText(gender);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
