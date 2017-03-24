package com.disparta;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.disparta.R;
import com.sinch.android.rtc.messaging.WritableMessage;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageChatAdapter extends BaseAdapter {

    public static final int DIRECTION_INCOMING = 0;
    public static final int DIRECTION_OUTGOING = 1;
    public static String Msgtype;
    ImageView messageImage;

    private List<Pair<String, Integer>> messages;
    private LayoutInflater layoutInflater;

    public MessageChatAdapter(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        messages = new ArrayList<Pair<String, Integer>>();
    }

    public void addMessage(String message, int direction, String type) {
        message = message +" ~+ "+ type;
        messages.add(new Pair(message, direction));
        Msgtype = type;

        notifyDataSetChanged();
    }

    public void removeData() {
        messages.clear();
                //.add(new Pair(message, direction));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        return messages.get(i).second;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        int direction = getItemViewType(i);

        //GET mESSAGE

        String message = messages.get(i).first;

        //show message on left or right, depending on if
        //it's incoming or outgoing
        if (convertView == null) {
            int res = 0;
            if (direction == DIRECTION_INCOMING) {
                res = R.layout.message_right;
            } else if (direction == DIRECTION_OUTGOING) {
                res = R.layout.message_left;
            }

            convertView = layoutInflater.inflate(res, viewGroup, false);
        }

        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        messageImage = (ImageView)convertView.findViewById(R.id.messagePic);

        messageImage.setVisibility(View.GONE);//.getTextBody());
        txtMessage.setVisibility(View.GONE);

        messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        try {
            if (message.contains("~+ no")) {

                String[] output = message.split("~+");
                String result = output[0];

                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setText(result);//.getTextBody());
                //messageImage.setVisibility(View.INVISIBLE);

            }
            else {
                String[] output = message.split("~+");
                String result = output[0];


                Random ran = new Random();
                int num1 = ran.nextInt(1000)+999;
                int num2 = ran.nextInt(100)+99;
                int num3 = ran.nextInt(100)+99;

                final String fileName = Integer.toString(num1) +"_"+ Integer.toString(num2)+"_"+Integer.toString(num3)+".jpg";


                messageImage.setVisibility(View.VISIBLE);//.getTextBody());
                Context context = messageImage.getContext();
                Uri url = Uri.parse(result);//transform(new CircleTransform()).
                Picasso.with(context)
                        .load(url).resize(650, 650)
                        .centerCrop()
                        .placeholder(R.drawable.bloading)
                        .error(R.drawable.berror)
                        .into(messageImage);
            }
        }
        catch (Exception e) {

        }
        return convertView;
    }



}

