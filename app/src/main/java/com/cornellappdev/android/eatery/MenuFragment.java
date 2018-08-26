package com.cornellappdev.android.eatery;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.Model.MealModel;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        menus = (ArrayList<MealModel>) getArguments().getSerializable("cafeData");
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        linear = view.findViewById(R.id.linearFragment);

        try {
            position = getArguments().getInt("position");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Checks for unavailable or missing menus
        if (menus.get(position).getMenu().entrySet().isEmpty()) {
            TextView missingMenuText = new TextView(getContext());
            missingMenuText.setText("No menu available");
            missingMenuText.setTextSize(14);
            linear.addView(missingMenuText);
        }

        int counter = 0;
        for (HashMap.Entry<String, ArrayList<String>> entry : menus.get(position).getMenu().entrySet()) {
            // Add subheading for category of food
            String key = entry.getKey();
            List<String> value = entry.getValue();
            TextView categoryText = new TextView(getContext());
            SpannableString str = new SpannableString(key);
            categoryText.setText(str);
            categoryText.setAllCaps(true);
            categoryText.setTextSize(18);
            categoryText.setPadding(0, 60,0, 16);
            linear.addView(categoryText);

            // Add individual meal items to list
            for (int i = 0; i < value.size(); i++) {
                TextView mealItemText = new TextView(getContext());
                mealItemText.setText(value.get(i));
                mealItemText.setTextSize(14);
                mealItemText.setPadding(0, 0, 0, 8);
                if (i == value.size() - 1) {
                    mealItemText.setPadding(0, 0, 0, 60);
                }
                linear.addView(mealItemText);
            }

            // Add horizontal line that separates each category
            if (counter != menus.get(position).getMenu().entrySet().size() - 1) {
                View blank = new View(getContext());
                blank.setBackgroundColor(Color.argb(100, 192,192, 192  ));
                blank.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        6));
                linear.addView(blank);
            }
            counter++;
        }

        // Inflate the layout for this fragment
        return view;
    }
}
