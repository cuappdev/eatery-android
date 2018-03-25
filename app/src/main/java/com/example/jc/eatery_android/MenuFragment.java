package com.example.jc.eatery_android;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
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
        Log.i("TAG", menus.get(position).stringTo());
        int counter = 0;
        for (HashMap.Entry<String, ArrayList<String>> entry : menus.get(position).getMenu().entrySet()) {

            String key = entry.getKey();
            List<String> value = entry.getValue();
            TextView tv = new TextView(getContext());
            SpannableString str = new SpannableString(key);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, key.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(str);
            //tv.setAllCaps(true);
            //tv.setTextSize(18);
            tv.setPadding(0, 60,0, 16);
            linear.addView(tv);

            for (int i = 0; i < value.size(); i++) {
                TextView tv2 = new TextView(getContext());
                tv2.setText(value.get(i));
                tv2.setTextSize(14);
                tv2.setPadding(0, 0, 0, 8);
                if (i == value.size() - 1) {
                    tv2.setPadding(0, 0, 0, 60);
                }
                linear.addView(tv2);
            }
            if (counter != menus.get(position).getMenu().entrySet().size() - 1) {
                View blank = new View(getContext());
                blank.setBackgroundColor(Color.argb(100, 192,192, 192  ));
                blank.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        6));
                linear.addView(blank);
            }
        }
        if (menus.get(position).getMenu().entrySet().isEmpty()) {
            TextView tv = new TextView(getContext());
            tv.setText("No menu available");
            tv.setTextSize(14);
            linear.addView(tv);
        }

        // Inflate the layout for this fragment
        counter++;
        return view;

    }


}
