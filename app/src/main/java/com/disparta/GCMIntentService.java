package com.disparta;

import static com.disparta.CommonUtilities.SENDER_ID;
import static com.disparta.CommonUtilities.displayMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;
import com.google.android.gcm.GCMBaseIntentService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.Timer;

import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


import android.app.Notification;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.content.Context;

import android.content.Intent;

import android.os.PowerManager;

import android.util.Log;


import com.google.android.gcm.GCMBaseIntentService;
import com.squareup.picasso.Picasso;


public class GCMIntentService extends GCMBaseIntentService {

    Target mTarget;
    Bitmap contactPic = null;
    String getOnlinePic = null;

    private static final String TAG = "GCM Tutorial::Service";



    // Use your PROJECT ID from Google API into SENDER_ID

    public static final String SENDER_ID = "71394710589";



    public GCMIntentService() {

        super(SENDER_ID);

    }


    @Override

    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "onRegistered: registrationId=" + registrationId);
    }


    @Override

    protected void onUnregistered(Context context, String registrationId) {


        Log.i(TAG, "onUnregistered: registrationId=" + registrationId);

    }


    @Override

    protected void onMessage(final Context context, Intent data) {

        String message, title, type, Texttype;

        // Message from PHP server
        message = data.getStringExtra("message");
        title = data.getStringExtra("title");
        type  = data.getStringExtra("type");

        Intent intent2 = new Intent(this, ChatRoomActivity.class);
        // Pass data to the new activity
        intent2.putExtra("message", message);
        intent2.putExtra("title", title);

        // Open a new activity called GCMMessageView
        ChatRoomActivity.ToUser = title;

        Intent intent = new Intent(this, ChatRoomActivity.class);

        intent.putExtra("message", message);
        intent.putExtra("title", title);
        //setIntentRedelivery(true);
        ChatRoomActivity.newMessage = message;
        ChatRoomActivity.newTitle = title;
        ChatRoomActivity.newType = type;


        getOnlinePic = "http://disparta.com/images/"+title.toLowerCase()+".jpg";

        try { contactPic = new AsyncTask<Void, Void, Bitmap>() {
            @Override protected Bitmap doInBackground(Void... params) {
                    try {
                        return Picasso.with(context).load(getOnlinePic)
                                .resize(200, 200)
                                .placeholder(R.drawable.nopic)
                                .error(R.drawable.nopic)
                                .centerCrop()
                                .transform(new CircleTransform())
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } return null;
                }
            }.execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        // Pass data to the new activity

        // Starts the activity on notification click
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the notification with a notification builder
        Uri url = Uri.parse("http://disparta.com/images/"+title.toLowerCase()+".jpg");
        Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.nopic);
        Notification notification = null;
        if (contactPic != null) {
            //builder.setLargeIcon(contactPic);

            notification = new Notification.Builder(this)

                    .setSmallIcon(R.mipmap.title_icon)
                    //.setLargeIcon(data.getB)
                    .setWhen(System.currentTimeMillis())
                            //.setSubText(tickerText)
                    .setContentTitle(title)
                    .setOngoing(false)
                    .setNumber(1)
                    //.setPublicVersion(Notification.VISIBILITY_PRIVATE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[1])
                    .setLargeIcon(contactPic)
                    .setContentText(message)
                    .setContentIntent(pIntent)
                    .setDefaults(0)


                    .getNotification();
        }
        else {
            //builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_action_user_purple_light));


        notification = new Notification.Builder(this)

                .setSmallIcon(R.mipmap.title_icon)
                //.setLargeIcon(data.getB)
                .setWhen(System.currentTimeMillis())
                //.setSubText(tickerText)
                .setContentTitle(title)
                .setOngoing(false)
                .setNumber(1)
                //.setPublicVersion(Notification.VISIBILITY_PRIVATE)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[1])
                .setLargeIcon(placeholder)
                .setContentText(message)
                .setContentIntent(pIntent)
                .setDefaults(0)


                .getNotification();
        }
        // Remove the notification on click

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(R.string.app_name, notification);

        {

            // Wake Android Device when notification received

            PowerManager pm = (PowerManager) context

                    .getSystemService(Context.POWER_SERVICE);

            final PowerManager.WakeLock mWakelock = pm.newWakeLock(

                    PowerManager.FULL_WAKE_LOCK

                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");

            mWakelock.acquire();
            // Timer before putting Android Device to sleep mode.
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    mWakelock.release();
                }
            };
            timer.schedule(task, 5000);
        }


    }


    @Override

    protected void onError(Context arg0, String errorId) {


        Log.e(TAG, "onError: errorId=" + errorId);

    }


}