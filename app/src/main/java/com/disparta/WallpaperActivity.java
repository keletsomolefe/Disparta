package com.disparta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

public class WallpaperActivity extends AppCompatActivity {

    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap;
    String sCaption = "";
    private static int RESULT_LOAD_IMG = 1;
    static String username = null;
    final String usernames = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Chat Background Wallpaper");

        toolbar.setCollapsible(true);
        toolbar.hideOverflowMenu();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            //layoutCC.setBackground(drawable);
           // Snackbar.make(getView, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImagefromGallery();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
            }
        });
    }


    public void setStringProperty(String key, String value) {
        LoginActivity.sharedprefrences = getApplicationContext().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        if (LoginActivity.sharedprefrences != null) {
            SharedPreferences.Editor editor = LoginActivity.sharedprefrences.edit();
            editor.putString(key, value);
            editor.commit();
        }

     //   convertJSON(value);

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

                setStringProperty("wallpaper", imgPath);

                Toast.makeText(this, "Image set as chat background", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


}
