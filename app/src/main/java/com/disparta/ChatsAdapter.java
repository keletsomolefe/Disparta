package com.disparta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.viewHolder>{
    private LayoutInflater inflater;
    List<Chats> data= Collections.emptyList();

    public  ChatsAdapter(Context context, List<Chats> data){
        inflater=LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chats,parent,false);
        viewHolder holder = new viewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        Chats current = data.get(position);
        holder.name.setText(current.name);
       // holder.currMsg.setText(current.LatestMsg);
        holder.image.setImageResource(current.imageID);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, currMsg;
        ImageView image;

        public viewHolder(View itemView) {
            super(itemView);

            name =(TextView)itemView.findViewById(R.id.separatal);
            //currMsg =(TextView)itemView.findViewById(R.id.latestMessage);
            image = (ImageView)itemView.findViewById(R.id.profilePic);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
