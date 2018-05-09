package com.cornellappdev.android.eatery;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cornellappdev.android.eatery.Model.CafeteriaModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ningning on 4/20/2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<CafeteriaModel> cafeData = new ArrayList<>();
    private HashMap<CafeteriaModel, ArrayList<String>> mealMap = new HashMap<>();
    private ArrayList<String> mealList = new ArrayList<>();
    private ArrayList<String> test = new ArrayList<String>();
    View line;



    public ExpandableListAdapter(Context context, ArrayList<CafeteriaModel> cafeData, HashMap<CafeteriaModel, ArrayList<String>> mealMap) {
        this.context = context;
        this.mealMap = mealMap;

        for (CafeteriaModel m : mealMap.keySet()) {
            this.cafeData.add(m);
        }
    }

    //DONE
    @Override
    public int getGroupCount() {
        return mealMap.size();
    }

    //DONE
    @Override
    public int getChildrenCount(int i) {
        CafeteriaModel m = cafeData.get(i);
        return mealMap.get(m).size();
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
        return mealMap.get(m).get(i1);
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
        //inflate layout if it does not exist already
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_view_header, viewGroup, false);
        }

        CafeteriaModel m = (CafeteriaModel) getGroup(i);
        TextView headertext = view.findViewById(R.id.header);

        headertext.setText(m.getNickName());

        //TODO: fix isopen function
        //TextView timetext = view.findViewById(R.id.time);
        //timetext.setText(m.isOpen());

        return view;
    }

    //DONE
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        //inflate layout if it does not exist already
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.list_view_body, viewGroup, false);
        }

        TextView tv = view.findViewById(R.id.menu_title);
        line = view.findViewById(R.id.horiline);
        line.setVisibility(View.INVISIBLE);
        String str = (String)getChild(i,i1);

        //if the string is a category
        if (str.charAt(0) == '1') {
            str = str.substring(1);
            SpannableString sstr = new SpannableString(str);
            tv.setText(sstr);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setAllCaps(true);
            tv.setTextSize(18);
        }
        //if the string is a meal item
        else {
            SpannableString sstr = new SpannableString(str);
            tv.setText(sstr);
            tv.setAllCaps(false);
            tv.setTextColor(Color.parseColor("#808080"));
            tv.setTextSize(14);
        }
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
