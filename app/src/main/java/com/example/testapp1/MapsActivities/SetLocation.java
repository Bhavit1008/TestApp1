package com.example.testapp1.MapsActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.example.testapp1.ChatInterface.NewActivity;
import com.example.testapp1.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetLocation extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient googleApiClient;
    public FusedLocationProviderClient fusedLocationProviderClient;
    String latitude;
    String longitude;
    Location currentlocation;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Registered_Users").child(user.getUid());
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Toast.makeText(this, "please tap on map to select the location", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latitude =  dataSnapshot.child("latitude").getValue().toString();
                longitude =  dataSnapshot.child("longitude").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //latitude = location.getLatitude();
        //longitude = location.getLongitude();
        currentlocation = location;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        Location myLocation = googleMap.getMyLocation();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                latitude =  dataSnapshot.child("latitude").getValue().toString();
                longitude =  dataSnapshot.child("longitude").getValue().toString();
                LatLng location=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
//                googleMap.addMarker(new MarkerOptions().position(location));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.addMarker(new MarkerOptions().position(latLng));
                String lat = String.valueOf(latLng.latitude);
                String lng = String.valueOf(latLng.longitude);
                //Toast.makeText(MapsActivity.this, lat, Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences("Locations", MODE_PRIVATE).edit();
                editor.putString("lat", lat);
                editor.putString("lng", lng);
                editor.apply();
                Toast.makeText(SetLocation.this, "Location is stored please press the back", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SetLocation.this,NewActivity.class);
                startActivity(intent);
            }
        });
    }
}
