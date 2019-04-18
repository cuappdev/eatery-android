package com.cornellappdev.android.eatery.loginviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.HistoryObjectModel;
import com.cornellappdev.android.eatery.util.MoneyUtil;

import java.util.List;

/**
 * This listAdapter renders all the purchase history items for the AccountInfoFragment Fragment
 */
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

    public View getView(int position, View convertView, ViewGroup parent) {
        View historyItemView = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                historyItemView = sInflater.inflate(mLayout, null);
                // Making item non-clickable
                historyItemView.setEnabled(false);
                historyItemView.setOnClickListener(null);
                holder = new ViewHolder();

                holder.displayName = (TextView) historyItemView.findViewById(R.id.purchase_eatery);
                holder.displayTimestamp = (TextView) historyItemView.findViewById(
                        R.id.purchase_timestamp);
                holder.displayAmount = (TextView) historyItemView.findViewById(
                        R.id.purchase_amount);

                historyItemView.setTag(holder);
            } else {
                holder = (ViewHolder) historyItemView.getTag();
            }

            holder.displayName.setText(mHistory.get(position).getName());
            holder.displayTimestamp.setText(mHistory.get(position).getTimestamp());
            holder.displayAmount.setText(
                    "- " + MoneyUtil.toMoneyString(mHistory.get(position).getAmount()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return historyItemView;
    }

    // Each cell of the list has a name, a timestamp, and an amount of the purchase
    public static class ViewHolder {
        private TextView displayAmount;
        private TextView displayName;
        private TextView displayTimestamp;
    }
}
