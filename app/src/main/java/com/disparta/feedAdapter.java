package com.disparta;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class feedAdapter extends BaseAdapter {

	private List<Chats> messages;
	private LayoutInflater songInf;
	
	//constructor
	public feedAdapter(Context c, List<Chats> theMessages){
		messages=theMessages;
		songInf=LayoutInflater.from(c);	
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return messages.size();
	
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int messageID, View arg1, ViewGroup parent) {
		// TODO Auto-generated method stub
		//map to song layout
				LinearLayout Lay = (LinearLayout)songInf.inflate
						(R.layout.feed_text, parent, false);

				ImageView album_art = (ImageView)Lay.findViewById(R.id.feeds_profilePic);
				TextView titleView = (TextView)Lay.findViewById(R.id.feed_separatal);
				TextView new_message = (TextView)Lay.findViewById(R.id.feed_text);
				TextView date = (TextView)Lay.findViewById(R.id.feed_date);

				Chats currMessage = messages.get(messageID);
				titleView.setText(currMessage.getName()+ " wrote a scrap");
				new_message.setText(currMessage.getLatestMsg());
				date.setText(currMessage.getMessageDate());
				Lay.setId(messageID);

				Context context = album_art.getContext();
				Uri url = Uri.parse("http://disparta.com/images/"+currMessage.getName().toLowerCase()+".jpg");
				Picasso.with(context).load(url).resize(640, 640).centerCrop().placeholder(R.drawable.nopic).error(R.drawable.nopic).transform(new CircleTransform()).into(album_art);

				String to = "";

				return Lay;
	}

}
