package com.cornellappdev.android.eatery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.HistoryObjectModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryInfoAdapter extends ArrayAdapter<HistoryObjectModel> {

    private static LayoutInflater sInflater = null;
    private static int mLayout;
    private static List<HistoryObjectModel> mHistory;
    public HistoryInfoAdapter(Context context, int layout, List<HistoryObjectModel> objects) {
        super(context, layout, objects);
        sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayout = layout;
        mHistory = objects;
    }

    public static class ViewHolder {
        public TextView display_name;
        public TextView display_timestamp;

    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = sInflater.inflate(mLayout, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) vi.findViewById(R.id.eatery_name);
                holder.display_timestamp = (TextView) vi.findViewById(R.id.eatery_timestamp);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.display_name.setText(mHistory.get(position).getName());
            holder.display_timestamp.setText(mHistory.get(position).getTimestamp());


        } catch (Exception e) {


        }
        return vi;
    }
}
