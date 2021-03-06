package com.cornellappdev.android.eatery;

import static com.cornellappdev.android.eatery.model.EateryBaseModel.Status.CLOSED;
import static com.cornellappdev.android.eatery.model.EateryBaseModel.Status.CLOSINGSOON;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.util.TimeUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private final ListAdapterOnClickHandler mListAdapterOnClickHandler;
    private final int TEXT = 1;
    private final int IMAGE = 0;
    private int mCount;
    private String mQuery;
    private ArrayList<EateryBaseModel> cafeListFiltered;
    private Repository rInstance = Repository.getInstance();

    MainListAdapter(
            Context context,
            ListAdapterOnClickHandler clickHandler,
            int count,
            ArrayList<EateryBaseModel> list) {
        mContext = context;
        mListAdapterOnClickHandler = clickHandler;
        mCount = count;
        cafeListFiltered = list;
    }

    void setList(ArrayList<EateryBaseModel> list, int count, String query) {
        mQuery = query;
        mCount = count;
        cafeListFiltered = list;
        notifyDataSetChanged();
    }

    /**
     * Set view to layout of CardView
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        final int layoutId;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == IMAGE) {
            layoutId = R.layout.card_item;
            view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            view.setFocusable(true);
            viewHolder = new ListAdapterViewHolder(view);
        } else {
            layoutId = R.layout.card_text;
            view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            viewHolder = new TextAdapterViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder input_holder, int position) {
        final EateryBaseModel eateryModel = cafeListFiltered.get(position);
        switch (input_holder.getItemViewType()) {
            case IMAGE:
                ListAdapterViewHolder holder = (ListAdapterViewHolder) input_holder;
                holder.cafeName.setText(eateryModel.getNickName());
                String imageLocation = eateryModel.getImageURL();
                Uri uri = Uri.parse(imageLocation);
                Picasso.get()
                        .load(uri)
                        .noFade()
                        .into(holder.cafeDrawee);

                String openText = eateryModel.getCurrentStatus().toString();
                SpannableString openString = new SpannableString(openText);
                openString.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        0,
                        openText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (eateryModel.getCurrentStatus() == CLOSED) {
                    holder.cafeOpen.setText(R.string.closed);
                    holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    holder.rlayout.setAlpha(.5f);
                } else if (eateryModel.getCurrentStatus() == CLOSINGSOON) {
                    holder.cafeOpen.setText(R.string.closing_soon);
                    holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.yellow));
                    holder.rlayout.setAlpha(1f);
                } else {
                    holder.cafeOpen.setText(R.string.open);
                    holder.cafeOpen.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                    holder.rlayout.setAlpha(1f);
                }

                holder.brb_icon.setVisibility(View.INVISIBLE);
                if (eateryModel.hasPaymentMethod(PaymentMethod.BRB)) {
                    holder.brb_icon.setVisibility(View.VISIBLE);
                }

                holder.swipe_icon.setVisibility(View.INVISIBLE);
                if (eateryModel instanceof DiningHallModel) {
                    holder.swipe_icon.setVisibility(View.VISIBLE);
                }
                holder.cafeTime.setText((TimeUtil.format(eateryModel.getCurrentStatus(),
                        eateryModel.getChangeTime())));
                break;
            case TEXT:
                TextAdapterViewHolder holder2 = (TextAdapterViewHolder) input_holder;
                holder2.cafe_name.setText(eateryModel.getNickName());
                if (eateryModel.getCurrentStatus() == CLOSED) {
                    holder2.cafe_time.setText(R.string.closed);
                    holder2.cafe_time.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                    holder2.cafe_time_info.setText((TimeUtil.format(eateryModel.getCurrentStatus(),
                            eateryModel.getChangeTime())));
                } else if (eateryModel.getCurrentStatus() == CLOSINGSOON) {
                    holder2.cafe_time.setText(R.string.closing_soon);
                    holder2.cafe_time.setTextColor(
                            ContextCompat.getColor(mContext, R.color.yellow));
                    holder2.cafe_time_info.setText((TimeUtil.format(eateryModel.getCurrentStatus(),
                            eateryModel.getChangeTime())));
                } else {
                    holder2.cafe_time.setText(R.string.open);
                    holder2.cafe_time.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                    holder2.cafe_time_info.setText((TimeUtil.format(eateryModel.getCurrentStatus(),
                            eateryModel.getChangeTime())));
                }

                ArrayList<String> itemList = eateryModel.getSearchedItems();
                if (itemList == null) {
                    holder2.cafe_items.setText("");
                    break;
                }
                Collections.sort(itemList);
                String items = itemList.toString().substring(1, itemList.toString().length() - 1);

                if (mQuery != null) {
                    // Fixes conflict with replacing character 'b' after inserting HTML bold tags
                    if (mQuery.equals("B")) mQuery = "b";

                    // Find case-matching instances to bold
                    items = items.replaceAll(mQuery, "<b>" + mQuery + "</b>");

                    // Find instances that don't match the case of the query and bold them
                    int begIndex = items.replaceAll(mQuery, " ").toLowerCase().indexOf(
                            mQuery.toLowerCase());
                    if (begIndex >= 0) {
                        String queryMatchingItemCase = items.substring(begIndex,
                                begIndex + mQuery.length());
                        items = items.replaceAll(queryMatchingItemCase,
                                "<b>" + queryMatchingItemCase + "</b>");
                    }
                }

                holder2.cafe_items.setText(HtmlCompat.fromHtml(items.replace(", ", "<br/>"),
                        HtmlCompat.FROM_HTML_MODE_LEGACY));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (rInstance.getIsSearchPressed()) {
            return TEXT;
        } else {
            return IMAGE;
        }
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public interface ListAdapterOnClickHandler {
        void onClick(int position, ArrayList<EateryBaseModel> list, View sharedImageView);
    }

    class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cafeName;
        TextView cafeTime;
        TextView cafeOpen;
        ImageView cafeDrawee;
        CardView rlayout;
        ImageView swipe_icon;
        ImageView brb_icon;
        View viewHolder;

        ListAdapterViewHolder(View itemView) {
            super(itemView);
            cafeName = itemView.findViewById(R.id.cafe_name);
            cafeTime = itemView.findViewById(R.id.cafe_time);
            cafeOpen = itemView.findViewById(R.id.cafe_open);
            swipe_icon = itemView.findViewById(R.id.card_swipe);
            brb_icon = itemView.findViewById(R.id.card_brb);
            cafeDrawee = itemView.findViewById(R.id.cafe_image);
            rlayout = itemView.findViewById(R.id.cv);
            viewHolder = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mListAdapterOnClickHandler.onClick(adapterPosition, cafeListFiltered, viewHolder);
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
            mListAdapterOnClickHandler.onClick(adapterPosition, cafeListFiltered, null);
        }
    }
}
