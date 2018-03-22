package com.example.jc.eatery_android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jc.eatery_android.Model.MealModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    int position;
    ArrayList<MealModel> menus;
    LinearLayout linear;

    public MenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        menus = (ArrayList<MealModel>) getArguments().getSerializable("cafeData");
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        try {
            position = getArguments().getInt("position");
        } catch (Exception e) {
          e.printStackTrace();
        }

        linear = view.findViewById(R.id.linearFragment);
        for (HashMap.Entry<String, ArrayList<String>> entry : menus.get(position).getMenu().entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            TextView tv = new TextView(getContext());
            tv.setText(key);
            tv.setAllCaps(true);
            linear.addView(tv);

            for (int i = 0; i < value.size(); i++) {
                TextView tv2 = new TextView(getContext());
                tv2.setText(value.get(i));
                linear.addView(tv2);
            }

            TextView blank = new TextView(getContext());
            blank.setText(" ");
            linear.addView(blank);
        }

        // Inflate the layout for this fragment
        return view;

    }


}
