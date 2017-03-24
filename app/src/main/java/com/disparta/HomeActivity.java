package com.disparta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;

public class HomeActivity extends AppCompatActivity {

    public Button BtnLogin, BtnRegister;
    private Intent intent;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);



        try {
            LoginActivity.sharedprefrences = getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

            String username, password;



            username = LoginActivity.sharedprefrences.getString("username", null);
            password = LoginActivity.sharedprefrences.getString("password", null);

            RequestParams params = new RequestParams();
            params.put("username", username);
            params.put("password", password);

            GCMRegistrar.checkDevice(this);

            // Make sure the manifest was properly set - comment out this line
            // while developing the app, then uncomment it when it's ready.
            GCMRegistrar.checkManifest(this);

            // BroadcastReceiver mHandleMessageReceiver;
            // registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

            // Get GCM registration id
            final String regId = GCMRegistrar.getRegistrationId(this);

            if (regId.equals("")) {
                // Registration is not present, register now with GCM
                GCMRegistrar.register(this, "71394710589");
            } else {
                // Device is already registered on GCM
                if (GCMRegistrar.isRegisteredOnServer(this)) {
                    // Skips registration.

                } else {

                }
            }
            params.put("deviceID", regId);
            //Toast.makeText(getApplicationContext(), regId, Toast.LENGTH_LONG).show();

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://disparta.com/android/app/login.php", params, new AsyncHttpResponseHandler() {
                // When the response returned by REST has Http
                // response code '200'
                @Override
                public void onSuccess(String response) {


                }

                // When the response returned by REST has Http
                // response code other than '200' such as '404',
                // '500' or '403' etc
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    // Hide Progress Dialog
                    //prgDialog.hide();
                    // When Http response code is '404'
                    if (statusCode == 404) {
                       // Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code is '500'
                    else if (statusCode == 500) {
                      //  Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else {
                       // Toast.makeText(getApplicationContext(), "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : " + statusCode, Toast.LENGTH_LONG).show();
                    }
                }
            });

            // Toast.makeText(getApplicationContext(), username, Toast.LENGTH_SHORT).show();
            if (!username.isEmpty()) {

               // intent = new Intent(getApplicationContext(), MainActivity.class);
               // startActivity(intent);

               // serviceIntent = new Intent(getApplicationContext(), GCMIntentService.class);
                //startService(serviceIntent);


                //serviceIntent = new Intent(getApplicationContext(), SinchService.class);
               // startService(serviceIntent);

               // startSinchClient();

                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            }
        }
        catch (Exception e) {

        }
        BtnLogin = (Button) findViewById(R.id.btnlogin);
        BtnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //finish();
                        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
        );

        BtnRegister = (Button) findViewById(R.id.btnregister);
        BtnRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Snackbar.make(view, "Doesnt Work Yet", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        */
                        Intent i = new Intent(HomeActivity.this, RegisterActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    }
                }
        );


    }

}
