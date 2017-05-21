package com.warden.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.warden.myapplication.Activity.DetailActivity;
import com.warden.myapplication.model.Fruit;
import com.warden.myapplication.R;
import com.warden.myapplication.model.Message;

import java.util.List;


/**
 * Created by Warden on 2017/4/8.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> mMessageList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView messageImage;
        TextView messageTitle;
        TextView messagePreview;
        TextView messageDate;
        TextView messageTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            messageTitle = (TextView)view.findViewById(R.id.message_title);
            messagePreview = (TextView)view.findViewById(R.id.message_preview);
            messageDate =(TextView)view.findViewById(R.id.message_date);
            messageTime = (TextView)view.findViewById(R.id.message_time);
        }
    }

    public MessageAdapter(List<Message> messageList){
        mMessageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Message message = mMessageList.get(position);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.MESSAGE_TITLE, message.getTitle());
                intent.putExtra(DetailActivity.MESSAGE_CONTEXT, message.getContext());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        holder.messageTitle.setText(message.getTitle());
        holder.messagePreview.setText(message.getPreview());
        holder.messageDate.setText(message.getDate());
        holder.messageTime.setText(message.getTime());
        //Glide.with(mContext).load(R.drawable.more).into(holder.messageImage);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
