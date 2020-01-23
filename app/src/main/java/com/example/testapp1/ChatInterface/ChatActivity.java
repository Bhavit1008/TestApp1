package com.example.testapp1.ChatInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp1.ComplaintDetails.Details;
import com.example.testapp1.MainActivity;
import com.example.testapp1.MapsActivities.MapsActivity;
import com.example.testapp1.MapsActivities.SetLocation;
import com.example.testapp1.R;
import com.example.testapp1.RequestTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.example.testapp1.VerifyPhoneActivity.RequestPermissionCode;

public class ChatActivity extends AppCompatActivity    implements  LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    // Android client
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;

    String latitude,longitude;
    Location currentLocation;

    public FusedLocationProviderClient fusedLocationProviderClient;
    public GoogleApiClient googleApiClient;

    // Java V2
    // private SessionsClient sessionsClient;
    //private SessionName session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        chatLayout = findViewById(R.id.chatLayout);

        ImageView sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                showPopup();
            }
        }, 100);
        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        // Android client
        initChatbot();

        // Java V2
        //initV2Chatbot();
    }

    private void initChatbot() {
        final AIConfiguration config = new AIConfiguration("af9b23e6ebee4863ae2a260c48af4e63",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);// helps to create new session whenever app restarts
        aiRequest = new AIRequest();
    }

    /* private void initV2Chatbot() {
         try {
             InputStream stream = getResources().openRawResource(R.raw.test_agent_credentials);
             GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
             String projectId = ((ServiceAccountCredentials)credentials).getProjectId();

             SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
             SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
             sessionsClient = SessionsClient.create(sessionsSettings);
             session = SessionName.of(projectId, uuid);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
 */
    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.toUpperCase().startsWith("1")){
            Intent i = new Intent(ChatActivity.this,SetLocation.class);
            startActivity(i);
        }

        if (msg.startsWith("y") || msg.startsWith("Y") ){
            Intent i = new Intent(ChatActivity.this, NewActivity.class);
            startActivity(i);
        }

        if (msg.startsWith("2")){
            Intent i = new Intent(ChatActivity.this, Details.class);
            startActivity(i);
        }

        if (msg.startsWith("3")){
            Intent i = new Intent(ChatActivity.this, NewActivity.class);
            startActivity(i);
        }

        if (msg.startsWith("4")){
            showPopup();
        }

        if(msg.toUpperCase().startsWith("HELP") || msg.startsWith("help")){
            String x = String.valueOf(currentLocation.getLatitude());
                                        String y = String.valueOf(currentLocation.getLongitude());
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?&saddr="
                                                        + x
                                                        + ","
                                                        + y
                                                        + "addr=nearby police station"

                                                ));
                                        startActivity(intent);

        }

        if (msg.startsWith("latest") || msg.startsWith("Latest")){
            String url = "https://tweetfeeds.000webhostapp.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }



        if (msg.trim().isEmpty()) {
            Toast.makeText(ChatActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            queryEditText.setText("");
            // Android client
            aiRequest.setQuery(msg);
            RequestTask requestTask = new RequestTask(ChatActivity.this, aiDataService, customAIServiceContext);
            requestTask.execute(aiRequest);

            // Java V2
            //          QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build();
            //        new RequestJavaV2Task(MainActivity.this, session, sessionsClient, queryInput).execute();
        }
    }

    public void callback(AIResponse aiResponse) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            Log.d(TAG, "Bot Reply: " + botReply);
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }

    /*    public void callbackV2(DetectIntentResponse response) {
              if (response != null) {
                // process aiResponse here
                String botReply = response.getQueryResult().getFulfillmentText();
                Log.d(TAG, "V2 Bot Reply: " + botReply);
                showTextView(botReply, BOT);
            } else {
                Log.d(TAG, "Bot Reply: Null");
                showTextView("There was some communication issue. Please Try again!", BOT);
            }
        }
    */
    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
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
                googleApiClient);
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

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this, new
                String[]{ACCESS_FINE_LOCATION}, RequestPermissionCode);
    }


    public void showPopup(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.intro_popup, null, false), 700, 700, true);
        pw.showAtLocation(findViewById(R.id.chat_parent), Gravity.CENTER, 0, 0);
    }

}

