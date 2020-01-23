package com.example.testapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText edt;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        edt = findViewById(R.id.editTextMobile);
        btn = findViewById(R.id.buttonContinue);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = edt.getText().toString().trim();

                if(mobile.isEmpty() || mobile.length()<10){
                    edt.setError("enter valid number");
                    edt.requestFocus();
                }

                Intent i = new Intent(MainActivity.this,VerifyPhoneActivity.class);
                i.putExtra("phoneNumber", "+91" +  mobile);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
    }
}
