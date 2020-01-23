package com.example.testapp1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp1.DataClasses.Users;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

public class VerifyPhoneActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private String verificationId;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText editText;
    Button buttonSignIn,loc;
    public String phoneNumber;
    private FusedLocationProviderClient fusedLocationClient;
    public String latitude, longitude;
    public static final int RequestPermissionCode = 1;
    public static final int RequestPermissionCode1 = 0;
    protected GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    TextView txtloc;
    public String device_id;
    TelephonyManager tm;
    GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    LocationRequest mLocationRequest;
    String lat,lon;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);

        mAuth = FirebaseAuth.getInstance();

        //progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        txtloc = findViewById(R.id.txtloc);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendVerificationCode(phoneNumber);
        loc = findViewById(R.id.mylocation_btn);
        // save phone number
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                device_id = tm.getDeviceId();
            }
        });

       buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();





        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat = String.valueOf(currentLocation.getLatitude());
                lon = String.valueOf(currentLocation.getLongitude());
                if(lat != null){

                }
                else{
                    Toast.makeText(VerifyPhoneActivity.this, "please on your GPS for location", Toast.LENGTH_SHORT).show();
                }
                String code = editText.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveRegisteredUsers();
                            Intent intent = new Intent(VerifyPhoneActivity.this, OkScreen.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
//        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );

       // progressBar.setVisibility(View.GONE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    };

    public void saveRegisteredUsers() {
        //getLocation();
        Users user = new Users(phoneNumber,lat,lon,device_id);

        FirebaseDatabase.getInstance().getReference("Registered_Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(VerifyPhoneActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void requestPermissionIMEI() {
        ActivityCompat.requestPermissions(VerifyPhoneActivity.this, new
                String[]{READ_PHONE_STATE}, RequestPermissionCode1);
    }


    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(currentLocation.getLatitude());
        lon = String.valueOf(currentLocation.getLongitude());
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            // Log.e("x", mLastLocation.getLongitude() + "");
            //Log.e("y", mLastLocation.getLatitude() + "");
            currentLocation = mLastLocation;


        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(5000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(50F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        return;
    }



}
