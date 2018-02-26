package com.example.jc.eatery_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by JC on 2/22/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListAdapterViewHolder>{

    Context mContext;
    final private ListAdapterOnClickHandler mListAdapterOnClickHandler;
    private int mCount;
    //needs to implment this method in MainActivity

    public interface ListAdapterOnClickHandler {
        void onClick(int position);
    }

    public ListAdapter(Context context, ListAdapterOnClickHandler clickHandler, int count) {
        mContext = context;
        mListAdapterOnClickHandler = clickHandler;
        mCount = count;

    }

    @Override
    public ListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //set this to layout of cardview
        int layoutId= R.layout.custom_row;

        View view = LayoutInflater.from(mContext).inflate(layoutId,parent,false);
        view.setFocusable(true);


        return new ListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListAdapterViewHolder holder, int position) {

        holder.mText.setText("does it work");
        holder.mText2.setText("Yes it does");
        holder.mImage.setImageResource(R.drawable.sample);


    }

    @Override
    public int getItemCount() {
        Log.i("Tag",""+mCount);
        return mCount;
    }

    class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //define all textView + ImageViews in here

        TextView mText;
        TextView mText2;
        ImageView mImage;

        public ListAdapterViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.cv_text1);
            mText2 = itemView.findViewById(R.id.cv_text2);
            mImage = itemView.findViewById(R.id.cv_image1);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPositoin = getAdapterPosition();
            mListAdapterOnClickHandler.onClick(adapterPositoin);

            //TODO: add intent
        }
    }
}
