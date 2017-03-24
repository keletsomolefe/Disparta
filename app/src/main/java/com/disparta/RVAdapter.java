package com.disparta;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.Visibility;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static java.security.AccessController.getContext;


public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ChatsViewHolder>{

	ViewGroup viewGroup;

	public static class ChatsViewHolder extends RecyclerView.ViewHolder {
		CardView cv;
		TextView chatName;
		TextView chatMessage;
		ImageView chatPic;

		ChatsViewHolder(View itemView) {
			super(itemView);
			//cv = (CardView)itemView.findViewById(R.id.cv);
			chatName = (TextView)itemView.findViewById(R.id.separatal);
			chatPic = (ImageView)itemView.findViewById(R.id.profilePic);
			chatMessage = (TextView)itemView.findViewById(R.id.latestMessage);
		}


	}

	List<Chats> chats;

	RVAdapter(List<Chats> chats) {
		this.chats = chats;
	}

	@Override
	public int getItemCount() {
		return chats.size();
	}

	@Override
	public ChatsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatlistitem, viewGroup, false);
		ChatsViewHolder cvh = new ChatsViewHolder(v);
		return cvh;
	}

	@Override
	public  void onBindViewHolder(ChatsViewHolder chatsViewHolder, int i){
		chatsViewHolder.chatMessage.setText(chats.get(i).LatestMsg);
		chatsViewHolder.chatName.setText(chats.get(i).name);

		Context context = chatsViewHolder.chatPic.getContext();
		Uri url = Uri.parse("http://disparta.com/images/"+chats.get(i).name.toLowerCase()+".jpg");
		//new DownloadImageTask(chatsViewHolder.chatPic, 1).execute("http://disparta.com/images/"+chats.get(i).name.toLowerCase()+".jpg");
		Picasso.with(context).load(url).resize(50, 50).placeholder(R.drawable.nopic).error(R.drawable.nopic).into(chatsViewHolder.chatPic);
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

}
