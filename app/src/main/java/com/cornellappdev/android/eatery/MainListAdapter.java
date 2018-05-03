package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cornellappdev.android.eatery.Model.CafeteriaModel;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by JC on 2/22/18.
 */

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;
    final private ListAdapterOnClickHandler mListAdapterOnClickHandler;
    private int mCount;
    private ArrayList<CafeteriaModel> cafeList;
    private ArrayList<CafeteriaModel> cafeListFiltered;
    private final int TEXT =1;
    private final int IMAGE =0;

    public interface ListAdapterOnClickHandler {
        void onClick(int position,ArrayList<CafeteriaModel> list);
    }

    public MainListAdapter(Context context, ListAdapterOnClickHandler clickHandler, int count, ArrayList<CafeteriaModel> list) {
        mContext = context;
        mListAdapterOnClickHandler = clickHandler;
        mCount = count;
        cafeList = list;
        cafeListFiltered = list;
        Fresco.initialize(mContext);
    }

    public void setList(ArrayList<CafeteriaModel> list, int count){
        mCount = count;
        cafeListFiltered = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //set this to layout of cardview
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        int layoutId =0;
        switch(viewType){
            case IMAGE:
                layoutId = R.layout.card_item;
                view = LayoutInflater.from(mContext).inflate(layoutId,parent,false);
                view.setFocusable(true);
                viewHolder = new ListAdapterViewHolder(view);
                break;

            case TEXT:
                layoutId = R.layout.card_text;
                view = LayoutInflater.from(mContext).inflate(layoutId,parent,false);
                viewHolder = new TextAdapterViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder input_holder, int position) {
        switch (input_holder.getItemViewType()){
            case IMAGE:
                ListAdapterViewHolder holder = (ListAdapterViewHolder)input_holder;

                holder.cafeName.setText(cafeListFiltered.get(position).getNickName());

                String imageLocation = "drawable/" + convertName(cafeListFiltered.get(position).getNickName());

                Uri uri = Uri.parse("android.resource://com.cornellappdev.android.eatery/" + imageLocation);
                holder.cafeDrawee.setImageURI(uri);

                SpannableString openString = new SpannableString(cafeListFiltered.get(position).isOpen());
                openString.setSpan(new StyleSpan(Typeface.BOLD), 0, cafeListFiltered.get(position).isOpen().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                Collections.sort(cafeListFiltered);
                if(cafeListFiltered.get(position).getCurrentStatus()==CafeteriaModel.Status.CLOSED){
                    holder.line.setBackgroundColor(Color.parseColor("#FF0000"));
                    holder.cafeOpen.setText("Closed");

                }else if(cafeListFiltered.get(position).getCurrentStatus()==CafeteriaModel.Status.CLOSINGSOON){
                    holder.line.setBackgroundColor(Color.parseColor("#E8F11F"));
                    holder.cafeOpen.setText("Closing Soon");
                }
                else{
                    holder.line.setBackgroundColor(Color.parseColor("#00A350"));
                    holder.cafeOpen.setText("Open");
                }

                holder.cafeTime.setText(cafeListFiltered.get(position).getCloseTime());

                break;
            case TEXT:
                TextAdapterViewHolder holder2 = (TextAdapterViewHolder) input_holder;
                holder2.cafe_name.setText(cafeListFiltered.get(position).getNickName());
                holder2.cafe_time.setText(cafeListFiltered.get(position).isOpen());
                holder2.cafe_time_info.setText(cafeListFiltered.get(position).getCloseTime());
                ArrayList<String> itms = cafeListFiltered.get(position).getSearchedItems();
                Collections.sort(itms);
                String items = itms.toString().substring(1,itms.toString().length()-1);
                holder2.cafe_items.setText(items);



                break;
        }
    }

    @Override
    public int getItemViewType(int position){
        if(!MainActivity.searchPressed){
            return IMAGE;
        }
        else{
            return TEXT;
        }
    }



    @Override
    public int getItemCount() {
        return mCount;
    }

    class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView cafeName;
        TextView cafeTime;
        TextView cafeOpen;
        View line;

        ImageView cafeImage;
        SimpleDraweeView cafeDrawee;

        public ListAdapterViewHolder(View itemView) {
            super(itemView);
            cafeName =  itemView.findViewById(R.id.cafe_name);
            //cafeImage =  itemView.findViewById(R.id.cafe_image);
            cafeTime =  itemView.findViewById(R.id.cafe_time);
            cafeOpen = itemView.findViewById(R.id.cafe_open);
            cafeDrawee = itemView.findViewById(R.id.cafe_image);
            line = itemView.findViewById(R.id.cardviewStatus);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mListAdapterOnClickHandler.onClick(adapterPosition,cafeListFiltered);

        }
    }

    class TextAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView cafe_name;
        TextView cafe_time;
        TextView cafe_time_info;
        TextView cafe_items;


        public TextAdapterViewHolder(View itemView) {
            super(itemView);
            cafe_name = itemView.findViewById(R.id.searchview_name);
            cafe_time = itemView.findViewById(R.id.searchview_open);
            cafe_time_info = itemView.findViewById(R.id.searchview_opentime);
            cafe_items = itemView.findViewById(R.id.searchview_items);



//            cafe_menu = itemView.findViewById(R.id.textview_menu);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            int adapterPosition = getAdapterPosition();
            mListAdapterOnClickHandler.onClick(adapterPosition,cafeListFiltered);
        }

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String convertName(String str) {
        if (str.equals("104West!")) return "west";

        str = str.replaceAll("[&\']", "");
        str = str.replaceAll(" ", "_");
        str = str.replaceAll("é", "e");
        str = str.toLowerCase();
        return str;
    }
}