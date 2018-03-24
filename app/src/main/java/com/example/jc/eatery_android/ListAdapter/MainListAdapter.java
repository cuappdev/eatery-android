package com.example.jc.eatery_android.ListAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jc.eatery_android.MainActivity;
import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by JC on 2/22/18.
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ListAdapterViewHolder> implements Filterable{

    Context mContext;
    final private ListAdapterOnClickHandler mListAdapterOnClickHandler;
    private int mCount;
    private ArrayList<CafeteriaModel> cafeList;
    private ArrayList<CafeteriaModel> cafeListFiltered;
    private final int TEXT =0;
    private final int IMAGE =1;

    public interface ListAdapterOnClickHandler {
        void onClick(int position,ArrayList<CafeteriaModel> list);
    }

    public MainListAdapter(Context context, ListAdapterOnClickHandler clickHandler, int count, ArrayList<CafeteriaModel> list) {
        mContext = context;
        mListAdapterOnClickHandler = clickHandler;
        mCount = count;
        cafeList = list;
        cafeListFiltered = list;
    }

    public void setList(ArrayList<CafeteriaModel> list, int count){
        mCount = count;
        cafeListFiltered = list;
        notifyDataSetChanged();
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
        holder.cafeName.setText(cafeListFiltered.get(position).getNickName());

        String imageLocation = "@drawable/" + convertName(cafeListFiltered.get(position).getNickName());
        int imageRes = mContext.getResources().getIdentifier(imageLocation, null, mContext.getPackageName());

        Picasso.get().load(imageRes).resize(600, 600).centerCrop()
                .into(holder.cafeImage);
    }

    @Override
    public int getItemViewType(int position){
        if(MainActivity.searchPressed){
            return IMAGE;
        }
        else{
            return TEXT;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    cafeListFiltered = cafeList;
                }
                else {
                    ArrayList<CafeteriaModel> filteredList = new ArrayList<>();
                    for (CafeteriaModel model : cafeListFiltered) {
                        if (model.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    cafeListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = cafeListFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cafeListFiltered  = (ArrayList<CafeteriaModel>) filterResults.values;
                setList((ArrayList<CafeteriaModel>) filterResults.values,cafeListFiltered.size());
                notifyDataSetChanged();
            }
        };
    }



    @Override
    public int getItemCount() {
        return mCount;
    }

    class ListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView cafeName;
        TextView cafeTime;
        ImageView cafeImage;

        public ListAdapterViewHolder(View itemView) {
            super(itemView);
            cafeName =  itemView.findViewById(R.id.cafe_name);
            cafeImage =  itemView.findViewById(R.id.cafe_image);
            cafeTime =  itemView.findViewById(R.id.cafe_time);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
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
