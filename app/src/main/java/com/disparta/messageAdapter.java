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

public class messageAdapter extends BaseAdapter {

	private List<Chats> messages;
	private LayoutInflater songInf;
	
	//constructor
	public messageAdapter(Context c, List<Chats> theMessages){
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
						(R.layout.chatlistitem, parent, false);

				ImageView album_art = (ImageView)Lay.findViewById(R.id.profilePic);
				TextView titleView = (TextView)Lay.findViewById(R.id.separatal);
				TextView new_message = (TextView)Lay.findViewById(R.id.latestMessage);
				TextView date = (TextView)Lay.findViewById(R.id.message_date);
				ImageView newMessage = (ImageView)Lay.findViewById(R.id.newMessageIndi);



		//get song using position
				Chats currMessage = messages.get(messageID);
				titleView.setText(currMessage.getName());
				newMessage.setVisibility(View.INVISIBLE);

				if (currMessage.getMessageDate().isEmpty()) {
					date.setVisibility(View.INVISIBLE);
					newMessage.setVisibility(View.INVISIBLE);
				}

				if (currMessage.getNewMessage().contains("yes")) {
//					date.setTextColor(R.color.colorPrimary);
					date.setTextColor(Color.parseColor("#800000"));
					newMessage.setVisibility(View.VISIBLE);
				} else  {

					newMessage.setVisibility(View.INVISIBLE);
				}

				date.setText(currMessage.messageDate);


				new_message.setText(currMessage.getLatestMsg());
				Lay.setId(messageID);

				Context context = album_art.getContext();
				Uri url = Uri.parse("http://disparta.com/images/"+currMessage.getName().toLowerCase()+".jpg");//transform(new CircleTransform()).
				Picasso.with(context)
						.load(url).resize(640, 640)
						.centerCrop()
						.placeholder(R.drawable.nopic)
						.error(R.drawable.nopic)
						.transform(new CircleTransform())
						.into(album_art);


				String to = "";
				/*
				if ( currMessage.getTitle().contains("New Message from") ) {
					titleView.setTextColor(Color.RED);
					//new_message.setVisibility(1);
					to = currMessage.getTitle().substring(currMessage.getTitle().indexOf("New Message from "));
					Lay.setTag("new "+to);
					
				}else {
					titleView.setTextColor(Color.BLACK);
					to = currMessage.getTitle().substring(currMessage.getTitle().indexOf("Convo with "));
					Lay.setTag(to);
					//new_message.setVisibility(0);
				}*/
				
			//	if ( currMessage.getPicture() != "null" ) {
					//String ImgUrl = MainActivity.ImgUrl+"users/"+currMessage.getPicture();
					//new DownloadImageTask(album_art, 1).execute(ImgUrl);
				//}
				
				//set message as tag
				//Lay.setTag(currMessage.getMessageID());
				
				return Lay;
	}

}
