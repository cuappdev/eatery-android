package com.example.jc.eatery_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jc.eatery_android.Model.CafeteriaModel;

import java.util.ArrayList;

/**
 * Created by JC on 2/22/18.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListAdapterViewHolder>{

    Context mContext;
    final private ListAdapterOnClickHandler mListAdapterOnClickHandler;
    private int mCount;
    private ArrayList<CafeteriaModel> cafeList;
    //needs to implment this method in MainActivity

    public interface ListAdapterOnClickHandler {
        void onClick(int position);
    }

    public ListAdapter(Context context, ListAdapterOnClickHandler clickHandler, int count, ArrayList<CafeteriaModel> list) {
        mContext = context;
        mListAdapterOnClickHandler = clickHandler;
        mCount = count;
        cafeList = list;
    }

    @Override
    public ListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //set this to layout of cardview
        int layoutId= R.layout.card_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId,parent,false);
        view.setFocusable(true);

        return new ListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListAdapterViewHolder holder, int position) {

        holder.cafeName.setText(cafeList.get(position).getName());

        String imageLocation = "@drawable/" + mContext.getResources().getStringArray(R.array.cafe_loc)[position];

        Log.i("TAG", imageLocation);
        int imageRes = mContext.getResources().getIdentifier(imageLocation, null, mContext.getPackageName());
        Drawable res = mContext.getResources().getDrawable(imageRes);
        //Bitmap image = BitmapFactory.decodeResource(mContext.getResources(), imageRes);
        holder.cafeImage.setImageDrawable(res);
    }

    @Override
    public int getItemCount() {
        Log.i("Tag",""+mCount);
        return mCount;
    }

    class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //define all textView + ImageViews in here

        TextView cafeName;
        ImageView cafeImage;

        public ListAdapterViewHolder(View itemView) {
            super(itemView);
            cafeName = (TextView) itemView.findViewById(R.id.cafe_name);
            cafeImage = (ImageView) itemView.findViewById(R.id.cafe_image);

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
