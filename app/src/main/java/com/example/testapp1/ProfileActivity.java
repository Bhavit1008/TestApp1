package com.example.testapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.testapp1.ChatInterface.ChatActivity;
import com.example.testapp1.MapsActivities.MapsActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity implements LocationListener {

    Button logout,chat,maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logout = findViewById(R.id.logout);
        chat = findViewById(R.id.chat);
        maps = findViewById(R.id.maps_complaint_btn);
             logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
