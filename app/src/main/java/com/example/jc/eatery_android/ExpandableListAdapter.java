package com.example.jc.eatery_android;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.Model.MealModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ningning on 4/20/2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<CafeteriaModel> cafeData;
    private HashMap<CafeteriaModel, MealModel> mealList = new HashMap<>();

    public ExpandableListAdapter(Context context, ArrayList<CafeteriaModel> cafeData, HashMap<CafeteriaModel, MealModel> mealList) {
        this.context = context;
        this.cafeData = cafeData;
        this.mealList = mealList;
    }

    //DONE
    @Override
    public int getGroupCount() {
        return cafeData.size();
    }

    //DONE
    @Override
    public int getChildrenCount(int i) {
        CafeteriaModel m = cafeData.get(i);
        if (mealList.containsKey(m)) {
            Log.d("SIZE", Integer.toString(mealList.get(m).getMenu().size()));
            return m.getWeeklyMenu().get(m.indexOfCurrentDay()).size();
        }
        return 0;
    }

    //DONE
    @Override
    public Object getGroup(int i) {
        return cafeData.get(i);
    }

    //DONE
    @Override
    public Object getChild(int i, int i1) {
        CafeteriaModel m = (CafeteriaModel) getGroup(i);
        if (mealList.containsKey(m)) {
            ArrayList n = new ArrayList(mealList.get(m).getMenu().entrySet());
            return n.get(i1);
        }
        return null;
    }

    //DONE
    @Override
    public long getGroupId(int i)  {
        return i;
    }

    //DONE
    @Override
    public long getChildId(int i, int i1) {
        Log.d("CHILD", Integer.toString(i1));
        return i1;
    }

    //DONE
    @Override
    public boolean hasStableIds() {
        return false;
    }

    //DONE
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_view_header, viewGroup, false);
        }

        CafeteriaModel m = (CafeteriaModel) getGroup(i);
        TextView headertext = view.findViewById(R.id.header);
        TextView timetext = view.findViewById(R.id.time);
        headertext.setText(m.getNickName());
        timetext.setText(m.isOpen());
        return view;
    }

    //DONE
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_view_body, viewGroup, false);
        }
        LinearLayout linear = view.findViewById(R.id.list_view_body);
        //MealModel n = (MealModel) getChild(i, i1);
        int counter = 0;

        HashMap.Entry<String, ArrayList<String>> entry = (HashMap.Entry<String, ArrayList<String>>) getChild(i, i1);
        Log.d("MEAL", "FUCK");

        //add subheading for category of food
        String key = entry.getKey();
        List<String> value = entry.getValue();
        TextView tv = new TextView(context);
        SpannableString str = new SpannableString(key);
        tv.setText(str);
        tv.setAllCaps(true);
        tv.setTextSize(18);
        tv.setPadding(0, 60,0, 16);
        linear.addView(tv);

        //adds individual meal items
        for (int j = 0; j < value.size(); j++) {
            TextView tv2 = new TextView(context);
            tv2.setText(value.get(j));
            tv2.setTextSize(14);
            tv2.setPadding(0, 0, 0, 8);
            if (j == value.size() - 1) {
                tv2.setPadding(0, 0, 0, 60);
            }
            linear.addView(tv2);
        }

        //adds horizontal line
            View blank = new View(context);
            blank.setBackgroundColor(Color.argb(100, 192,192, 192  ));
            blank.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    6));
            linear.addView(blank);

       /* for (HashMap.Entry<String, ArrayList<String>> entry : n.getMenu().entrySet()) {

            //add subheading for category of food
            String key = entry.getKey();
            List<String> value = entry.getValue();
            TextView tv = new TextView(context);
            SpannableString str = new SpannableString(key);
            tv.setText(str);
            tv.setAllCaps(true);
            tv.setTextSize(18);
            tv.setPadding(0, 60,0, 16);
            linear.addView(tv);

            //adds individual meal items
            for (int j = 0; j < value.size(); j++) {
                TextView tv2 = new TextView(context);
                tv2.setText(value.get(j));
                tv2.setTextSize(14);
                tv2.setPadding(0, 0, 0, 8);
                if (j == value.size() - 1) {
                    tv2.setPadding(0, 0, 0, 60);
                }
                linear.addView(tv2);
            }

            //adds horizontal line
            if (counter != n.getMenu().entrySet().size() - 1) {
                View blank = new View(context);
                blank.setBackgroundColor(Color.argb(100, 192,192, 192  ));
                blank.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        6));
                linear.addView(blank);
            }
        }
        if (n.getMenu().entrySet().isEmpty()) {
            TextView tv = new TextView(context);
            tv.setText("No menu available");
            tv.setTextSize(14);
            linear.addView(tv);
        }

        // Inflate the layout for this fragment
        counter++;*/
        return view;
    }

    //DONE
    @Override
    public boolean isChildSelectable(int i, int i1) {
        if (getChildrenCount(i) == 1) {
            return true;
        }
        return false;
    }
}
