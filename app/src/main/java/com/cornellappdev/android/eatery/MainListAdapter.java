package com.cornellappdev.android.eatery;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.Model.CafeteriaModel;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final ListAdapterOnClickHandler mListAdapterOnClickHandler;
    private int mCount;
    private String mQuery;
    private ArrayList<CafeteriaModel> cafeListFiltered;
    private final int TEXT = 1;
    private final int IMAGE = 0;

    public interface ListAdapterOnClickHandler {
        void onClick(int position, ArrayList<CafeteriaModel> list);
    }

    MainListAdapter(Context context, ListAdapterOnClickHandler clickHandler, int count, ArrayList<CafeteriaModel> list) {
        mContext = context;
        mListAdapterOnClickHandler = clickHandler;
        mCount = count;
        cafeListFiltered = list;

        // Logcat for Fresco
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(context, config);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
    }

    void setList(ArrayList<CafeteriaModel> list, int count, String query) {
        mQuery = query;
        mCount = count;
        cafeListFiltered = list;
        notifyDataSetChanged();
    }

    /**
     * Set view to layout of CardView
     **/
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        final int layoutId;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case IMAGE:
                layoutId = R.layout.card_item;
                view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
                view.setFocusable(true);
                viewHolder = new ListAdapterViewHolder(view);
                break;
            case TEXT:
                layoutId = R.layout.card_text;
                view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
                viewHolder = new TextAdapterViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder input_holder, int position) {
        switch (input_holder.getItemViewType()) {
            case IMAGE:
                ListAdapterViewHolder holder = (ListAdapterViewHolder) input_holder;

                holder.cafeName.setText(cafeListFiltered.get(position).getNickName());

                String imageLocation =
                        "https://raw.githubusercontent.com/cuappdev/assets/master/eatery/eatery-images/"
                                + convertName(cafeListFiltered.get(position).getNickName() + ".jpg");
                Uri uri = Uri.parse(imageLocation);
                holder.cafeDrawee.setImageURI(uri);

                SpannableString openString = new SpannableString(cafeListFiltered.get(position).isOpen());
                openString.setSpan(new StyleSpan(Typeface.BOLD), 0, cafeListFiltered.get(position).isOpen().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                Collections.sort(cafeListFiltered);
                if(cafeListFiltered.get(position).getCurrentStatus()==CafeteriaModel.Status.CLOSED){
                    holder.cafeOpen.setText("Closed");
                    holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    holder.rlayout.setAlpha(.6f);

                } else if(cafeListFiltered.get(position).getCurrentStatus()==CafeteriaModel.Status.CLOSINGSOON){
                    holder.cafeOpen.setText("Closing Soon");
                    holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                    holder.rlayout.setAlpha(1f);


                } else{
                    holder.cafeOpen.setText("Open");
                    holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                    holder.rlayout.setAlpha(1f);

                }
                int numMethods =0;

                int i=1;
                //This is setting the icons for the different payment methods
                for(String payMethod:cafeListFiltered.get(position).getPay_methods()){
                    int payImage = getPayImage(payMethod);
                    if(payImage!=-1){
                        if(i==1){
                            holder.icon_1.setImageResource(payImage);
                            i++;
                        }
                        else if(i==2){
                            holder.icon_2.setImageResource(payImage);
                            i++;
                        }
                        else if(i==3){
                            holder.icon_3.setImageResource(payImage);
                            i++;

                        }
                    }
                }
                //this is removing any unused payment icons
                while(i!=4){
                    if(i==1){
                        holder.icon_1.setVisibility(View.INVISIBLE);
                        i++;
                    }
                    if(i==2){
                        holder.icon_2.setVisibility(View.INVISIBLE);
                        i++;
                    }
                    if(i==3){
                        holder.icon_3.setVisibility(View.INVISIBLE);
                        i++;

                    }
                }


                holder.cafeTime.setText(cafeListFiltered.get(position).getCloseTime());
                break;
            case TEXT:
                TextAdapterViewHolder holder2 = (TextAdapterViewHolder) input_holder;
                holder2.cafe_name.setText(cafeListFiltered.get(position).getNickName());
                holder2.cafe_time.setText(cafeListFiltered.get(position).isOpen());
                holder2.cafe_time_info.setText(cafeListFiltered.get(position).getCloseTime());

                ArrayList<String> itemList = cafeListFiltered.get(position).getSearchedItems();
                Collections.sort(itemList);
                String items = itemList.toString().substring(1, itemList.toString().length() - 1);

                if (mQuery != null) {
                    // Fixes conflict with replacing character 'b' after inserting HTML bold tags
                    if (mQuery.equals("B")) mQuery = "b";

                    // Find case-matching instances to bold
                    items = items.replaceAll(mQuery, "<b>" + mQuery + "</b>");

                    // Find instances that don't match the case of the query and bold them
                    int begIndex = items.replaceAll(mQuery, " ").toLowerCase().indexOf(mQuery.toLowerCase());
                    if (begIndex >= 0) {
                        String queryMatchingItemCase =
                                items.substring(begIndex, begIndex + mQuery.length());
                        items = items.replaceAll(queryMatchingItemCase,
                                "<b>" + queryMatchingItemCase + "</b>");
                    }
                }

                holder2.cafe_items.setText(Html.fromHtml(items.replace(", ", "<br/>")));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!MainActivity.searchPressed) {
            return IMAGE;
        } else {
            return TEXT;
        }
    }

    public int getPayImage(String payment){
        if(payment.equalsIgnoreCase("Meal Plan - Debit")){
            return R.drawable.brb_icon;
        }
        else if(payment.equalsIgnoreCase("Meal Plan - Swipe")){
            return R.drawable.swipe_icon;
        }
        else if(payment.equalsIgnoreCase("Major Credit Cards")){
            return R.drawable.dollar_icon;
        }
        else return -1;
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cafeName;
        TextView cafeTime;
        TextView cafeOpen;
        ImageView icon_1;
        ImageView icon_2;
        ImageView icon_3;
        SimpleDraweeView cafeDrawee;
        CardView rlayout;

        ListAdapterViewHolder(View itemView) {
            super(itemView);
            cafeName = itemView.findViewById(R.id.cafe_name);
            cafeTime = itemView.findViewById(R.id.cafe_time);
            cafeOpen = itemView.findViewById(R.id.cafe_open);
            cafeDrawee = itemView.findViewById(R.id.cafe_image);
//            line = itemView.findViewById(R.id.cardviewStatus);
            icon_1 = itemView.findViewById(R.id.icon_1);
            icon_2 = itemView.findViewById(R.id.icon_2);
            icon_3 = itemView.findViewById(R.id.icon_3);
            rlayout = itemView.findViewById(R.id.cv);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mListAdapterOnClickHandler.onClick(adapterPosition, cafeListFiltered);

        }
    }

    class TextAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cafe_name;
        TextView cafe_time;
        TextView cafe_time_info;
        TextView cafe_items;


        TextAdapterViewHolder(View itemView) {
            super(itemView);
            cafe_name = itemView.findViewById(R.id.searchview_name);
            cafe_time = itemView.findViewById(R.id.searchview_open);
            cafe_time_info = itemView.findViewById(R.id.searchview_opentime);
            cafe_items = itemView.findViewById(R.id.searchview_items);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mListAdapterOnClickHandler.onClick(adapterPosition, cafeListFiltered);
        }

    }

    private static String convertName(String str) {
        if (str.equals("104West!.jpg")) return "104-West.jpg";
        if (str.equals("McCormick's.jpg")) return "mccormicks.jpg";
        if (str.equals("Franny's.jpg")) return "frannys.jpg";
        if (str.equals("Ice Cream Cart.jpg")) return "icecreamcart.jpg";
        if (str.equals("Risley Dining Room.jpg")) return "Risley-Dining.jpg";
        if (str.equals("Martha's Express.jpg")) return "Marthas-Cafe.jpg";
        if (str.equals("Bus Stop Bagels.jpg")) return "Bug-Stop-Bagels.jpg";


        str = str.replaceAll("!", "");
        str = str.replaceAll("[&\']", "");
        str = str.replaceAll(" ", "-");
        str = str.replaceAll("é", "e");
        return str;
    }
}