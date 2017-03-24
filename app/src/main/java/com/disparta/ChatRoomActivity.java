package com.disparta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.disparta.R.drawable.nopic;

public class ChatRoomActivity extends AppCompatActivity {

    public static String ToUser;
    private String recipientId;
    ImageView messageImage;


    public static String newMessage, newTitle, newType;
    private SharedPreferences sharedPreferences;

    public static String messageID;
    private MessageChatAdapter messageAdapter;
    private ListView messagesList;
    private EditText messageText;
    MessageFailureInfo failureInfo;
    private String sMessageBuffer;
    Timer timer;
    private MessageClient messageClient = null;
    private static final String TAG = ChatRoomActivity.class.getSimpleName();

    //public List<NameValuePair> nameValuePairs;
    public ProgressDialog dialog = null;
  //  ProgressDialog prgDialog;
    String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    String sCaption = "";
    private static int RESULT_LOAD_IMG = 1;

    private GoogleApiClient client2;
    String wallpaperImg = null;
    LinearLayout layoutCC = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case  R.id.action_attach:
                loadImagefromGallery();
                return true;

        }

        return  super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(ToUser);
        //toolbar.setSubtitle("Last Seen: never");
        toolbar.setCollapsible(true);
        toolbar.hideOverflowMenu();


        Uri url = Uri.parse("http://disparta.com/images/" + ToUser.toLowerCase() + ".jpg");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(Picasso.with(ChatRoomActivity.this).load(url).resize(70, 70).placeholder(R.drawable.nopic).error(R.drawable.nopic).transform(new CircleTransform()).into(getSupportActionBar().get););
        //getSupportActionBar().setBackgroundDrawable(nopic);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //bindService(new Intent(this, MessageService.class), serviceConnection, BIND_AUTO_CREATE);

        layoutCC = (LinearLayout)findViewById(R.id.ChatBack);
        try {

            final int widthImg = 500;
            final int heightImg = 720;
            Bitmap  thumbnail = null;

            wallpaperImg =  getStringProperty("wallpaper");
            thumbnail = (BitmapFactory.decodeFile(wallpaperImg));

            thumbnail = Bitmap.createScaledBitmap(thumbnail, widthImg, heightImg, true);
            thumbnail = Bitmap.createScaledBitmap(thumbnail, widthImg, heightImg, true);

            Drawable drawable = new BitmapDrawable(getBaseContext().getResources(), thumbnail);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layoutCC.setBackground(drawable);
            } else {
                layoutCC.setBackgroundDrawable(drawable);
            }

        }catch(Exception e){
            //Toast.makeText(ChatRoomActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        messageText = (EditText) findViewById(R.id.textMessage);
       // messageImage = (ImageView) findViewById(R.id.messagePic);
/*
        messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });*/


        messagesList = (ListView) findViewById(R.id.messageArea);
        messageAdapter = new MessageChatAdapter(this);
        messagesList.setAdapter(messageAdapter);
        populateMessageHistory();


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        populateMessage();
                    }
                });
            }
        }, 100, 1000);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.sendMessage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fab.setEnabled(false);
                fab.setVisibility(FloatingActionButton.INVISIBLE);

                sendMessage();

                fab.setVisibility(FloatingActionButton.VISIBLE);
                fab.setEnabled(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    public void sendMessage() {

        final String theMessage;

        theMessage = messageText.getText().toString().trim();

        if (theMessage.isEmpty()) {
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        final String user_id, usernames;

        LoginActivity.sharedprefrences = ChatRoomActivity.this.getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        user_id = LoginActivity.sharedprefrences.getString("user_id", null);
        usernames = LoginActivity.sharedprefrences.getString("username", null);

        RequestParams params = new RequestParams();
        params.put("from_user", user_id);
        params.put("to_user", ToUser);
        params.put("message", theMessage);

        client.post("http://disparta.com/android/app/sendMessage.php", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                //prgDialog.hide();

                if (response.contains("result")) {
                    messageText.setText("");
                    messageAdapter.addMessage(theMessage, MessageChatAdapter.DIRECTION_INCOMING, "no");
                    messageAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ChatRoomActivity.this,
                            "Could not send message.",
                            Toast.LENGTH_SHORT).show();
                }


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
                    Toast.makeText(ChatRoomActivity.this,
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(ChatRoomActivity.this,
                            "Something went wrong at server end",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(
                            ChatRoomActivity.this,
                            "Error Occured \n Most Common Error:\n Device not connected to Internet", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void populateMessageHistory() {

        try {
            convertJSON(getStringProperty(ToUser));
        }
        catch(Exception e) {

        }

        AsyncHttpClient client = new AsyncHttpClient();
        final String user_id, usernames;

        LoginActivity.sharedprefrences = ChatRoomActivity.this.getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        user_id = LoginActivity.sharedprefrences.getString("user_id", null);
        usernames = LoginActivity.sharedprefrences.getString("username", null);

        RequestParams params = new RequestParams();
        params.put("me", user_id);
        params.put("friend", ToUser);

        client.post("http://disparta.com/android/app/view.php", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog

              //  Toast.makeText(ChatRoomActivity.this, response, Toast.LENGTH_LONG).show();


                sMessageBuffer = response;
                try {
                    if (getStringProperty(ToUser) != response) {
                        setStringProperty(ToUser, response);
                    }
                } catch (Exception e) {

                }

                if (getStringProperty(ToUser) == response) {
                    convertJSON(getStringProperty(ToUser));

                }

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

                convertJSON(getStringProperty(ToUser));

                if (statusCode == 404) {
                    Toast.makeText(ChatRoomActivity.this,
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(ChatRoomActivity.this,
                            "Something went wrong at server end",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    //Toast.makeText(ChatRoomActivity.this, "Error refreshing", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //save image
    public void saveImage() {
        messageImage.setDrawingCacheEnabled(true);
        Bitmap b = messageImage.getDrawingCache();
        String title, des;
        title = null;

        des = "An image from Disparta";

        MediaStore.Images.Media.insertImage(ChatRoomActivity.this.getContentResolver(), b, title, des);
    }


    public  void imageDownload(Context ctx, String url){
        Picasso.with(ctx) .load("http://blog.concretesolutions.com.br/wp-content/uploads/2015/04/Android1.png") .into(getTarget(url));
    }
    //target to save private static
     Target getTarget(final String url){
         Target target = new Target(){
             @Override public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                 new Thread(new Runnable() {
                     @Override public void run() {
                         File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + url); try {
                             file.createNewFile(); FileOutputStream ostream = new FileOutputStream(file);
                             bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream); ostream.flush();
                             ostream.close();
                         } catch (IOException e) {
                             Log.e("IOException", e.getLocalizedMessage());
                         }
                     }
                 }).start();
             } @Override public void onBitmapFailed(Drawable errorDrawable) {
             } @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
             }
         };
         return target;
     }



    public void populateMessage() {
        try {
            if (newTitle == ToUser) {
                messageAdapter.addMessage(newMessage.trim(), MessageChatAdapter.DIRECTION_OUTGOING, newType);
                messageAdapter.notifyDataSetChanged();
                newTitle = null;
                newMessage = null;
                newType = null;
            }
        } catch (Exception e) {

        }
    }

    public void convertJSON(String response) {

        //Toast.makeText(ChatRoomActivity.this, response, Toast.LENGTH_SHORT).show();

        messageAdapter.removeData();

        final String user_id, usernames;

        LoginActivity.sharedprefrences = ChatRoomActivity.this.getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        user_id = LoginActivity.sharedprefrences.getString("user_id", null);
        usernames = LoginActivity.sharedprefrences.getString("username", null);

        try {
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);

                //chats.add(new Chats(json_data.getString("name"), json_data.getString("message"), json_data.getString("message_id"), "http://disparta.com/images/"+json_data.getString("name").toLowerCase()+".jpg"));
                if (Integer.parseInt(user_id) == json_data.getInt("user_id")) {
                    messageAdapter.addMessage(json_data.getString("message").trim(), MessageChatAdapter.DIRECTION_INCOMING, json_data.getString("file"));
                } else {
                    messageAdapter.addMessage(json_data.getString("message").trim(), MessageChatAdapter.DIRECTION_OUTGOING, json_data.getString("file"));
                }

            }
            messageAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            System.out.println("Error Parsing JSON: " + writer.toString());
            //Toast.makeText(this, writer.toString() , Toast.LENGTH_LONG).show();
        }

    }

    public String getStringProperty(String key) {
        LoginActivity.sharedprefrences = getApplicationContext().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        String res = null;

        try {
            if (LoginActivity.sharedprefrences != null) {
                res = LoginActivity.sharedprefrences.getString(key, null);
            }
        }
        catch(Exception e) {

        }

        return res;
    }

    public void setStringProperty(String key, String value) {
        LoginActivity.sharedprefrences = getApplicationContext().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        if (LoginActivity.sharedprefrences != null) {
            SharedPreferences.Editor editor = LoginActivity.sharedprefrences.edit();
            editor.putString(key, value);
            editor.commit();
        }

        convertJSON(value);

    }


    protected Runnable updateUI = new Runnable() {
        Boolean done = false;

        @Override
        public void run() {
            while (!done) {
                //populateMessageHistory();


            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();

            }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onResume() {
        super.onResume();

        //messageAdt = new messageAdapter(getActivity(), chats);
        //messageView.setAdapter(messageAdt);

        populateMessageHistory();

    }


    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }



    // When Image is selected from Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data


                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView
                //imgView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
                // Get the Image's file name


                String fileNameSegments[] = imgPath.split("/");
                fileName = fileNameSegments[fileNameSegments.length - 1];
                // Put file name in Async Http Post Param which will used in Php web app
                params.put("filename", fileName);
                encodeImagetoString();
                //Toast.makeText(this, imgPath,
                //Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


    // AsyncTask - To convert Image to String
    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            };

            @Override
            protected String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                options.outHeight = 700;
                options.outWidth = 700;
                options.inScaled = true;

                bitmap = BitmapFactory.decodeFile(imgPath, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy

                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
               // prgDialog.setMessage("Calling Upload");
                // Put converted Image string into Async Http Post param
                params.put("image", encodedString);

                final String username;
                LoginActivity.sharedprefrences = ChatRoomActivity.this.getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);
                username = LoginActivity.sharedprefrences.getString("username", null);

                params.put("username", username);
                params.put("to_user", ToUser);
                params.put("picture", imgPath);
                // Trigger Image upload
                triggerImageUpload();
            }
        }.execute(null, null, null);
    }


    public void triggerImageUpload() {
        makeHTTPCall();
    }

    // http://192.168.2.4:9000/imgupload/upload_image.php
    // http://192.168.2.4:9999/ImageUploadWebApp/uploadimg.jsp
    // Make Http call to upload Image to Php server
    public void makeHTTPCall() {
        //prgDialog.setMessage("Uploading...");

        AsyncHttpClient client = new AsyncHttpClient();
        // Don't forget to change the IP address to your LAN address. Port no as well.
        client.post("http://disparta.com/uploadPic.php",
                params, new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        //prgDialog.hide();
                        if (response.contains("Successfully Uploaded Picture")) {
                            Toast.makeText(getApplicationContext(), "Picture Sent", Toast.LENGTH_LONG).show();
                            populateMessageHistory();
                        }
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                       // prgDialog.hide();
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
                                            + statusCode, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }



    @Override
    public void onPause() {
        super.onPause();
        //messageAdapter.removeData();
    }

}
