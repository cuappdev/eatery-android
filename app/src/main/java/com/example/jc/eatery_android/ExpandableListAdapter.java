package com.example.jc.eatery_android;

import android.content.Context;
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
import java.util.Set;

/**
 * Created by Ningning on 4/20/2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<CafeteriaModel> cafeData;
    private String mealText = "";
    private HashMap<CafeteriaModel, MealModel> mealList = new HashMap<>();

    public ExpandableListAdapter(Context context, ArrayList<CafeteriaModel> cafeData, HashMap<CafeteriaModel, MealModel> mealList, String mealText) {
        this.context = context;
        this.cafeData = cafeData;
        this.mealList = mealList;
        this.mealText = mealText;
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
            return 1;
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
            return mealList.get(m);
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
        LinearLayout linLayout = view.findViewById(R.id.list_view_body);
        TextView tv = new TextView(context);
        tv.setText("yay!");
        linLayout.addView(tv);
        return view;
    }

    //DONE
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
