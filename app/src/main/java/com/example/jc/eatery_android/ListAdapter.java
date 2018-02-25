package com.example.jc.eatery_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JC on 2/22/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListAdapterViewHolder>{

    Context mContext;
    final private ListAdapterOnClickHandler mListAdapterOnClickHandler;

    //TODO: needs to implment this method in MainActivity
    public interface ListAdapterOnClickHandler {
        void onClick(int position);
    }

    public ListAdapter(Context context, ListAdapterOnClickHandler clickHandler) {
        mContext = context;
        mListAdapterOnClickHandler = clickHandler;

    }

    @Override
    public ListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //set this to layout of cardview
        int layoutId= 0;


        View view = LayoutInflater.from(mContext).inflate(layoutId,parent,false);
        view.setFocusable(true);



        return new ListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListAdapterViewHolder holder, int position) {

        //set all the textView, ImageView in card view here
        //

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //define all textView + ImageViews in here

        public ListAdapterViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            int adapterPositoin = getAdapterPosition();
            mListAdapterOnClickHandler.onClick(adapterPositoin);

            //TODO: add intent
        }
    }
}
