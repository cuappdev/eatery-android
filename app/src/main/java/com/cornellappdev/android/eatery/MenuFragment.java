package com.cornellappdev.android.eatery;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.MealMenuModel;
import com.cornellappdev.android.eatery.model.MealModel;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    int position;
    private MealMenuModel mMenu;
    private LinearLayout mLinear;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMenu = ((MealModel) getArguments().getSerializable("cafeData")).getMenu();
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mLinear = view.findViewById(R.id.linearFragment);

        try {
            position = getArguments().getInt("position");
        } catch (Exception e) {
            e.printStackTrace();
        }
        float scale = getResources().getDisplayMetrics().density;
        // Checks for unavailable or missing menus
        if (mMenu.getNumberOfCategories() == 0) {
            TextView missingMenuText = new TextView(getContext());
            missingMenuText.setText(R.string.no_menu_text);
            missingMenuText.setTextSize(16);
            missingMenuText.setPadding(0, 96, 0, 0);
            missingMenuText.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.wash));
            missingMenuText.setGravity(Gravity.CENTER_HORIZONTAL);
            mLinear.addView(missingMenuText);
        }

        ArrayList<String> categories = (ArrayList<String>) mMenu.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            // Add subheading for category of food
            TextView categoryText = new TextView(getContext());
            categoryText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            categoryText.setText(categories.get(i));
            categoryText.setTextSize(16);
            categoryText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            categoryText.setPadding(
                    (int) (16 * scale + 0.5f), (int) (12 * scale + 0.5f), 0,
                    (int) (12 * scale + 0.5f));
            mLinear.addView(categoryText);

            View blank = new View(getContext());
            blank.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            blank.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inactive));
            blank.setElevation(-1);
            mLinear.addView(blank);

            ArrayList<String> items = (ArrayList<String>) mMenu.getItems(categories.get(i));
            for (int j = 0; j < items.size(); j++) {
                TextView mealItemText = new TextView(getContext());
                mealItemText.setText(items.get(j));
                mealItemText.setTextSize(14);
                mealItemText.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                mealItemText.setPadding(
                        (int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
                        (int) (8 * scale + 0.5f));
                mLinear.addView(mealItemText);
                if (j != items.size() - 1) {
                    View divider = new View(getContext());
                    divider.setBackgroundColor(
                            ContextCompat.getColor(getContext(), R.color.inactive));
                    LinearLayout.LayoutParams dividerParams =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    1);
                    dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, 0, 0);
                    divider.setElevation(-1);
                    divider.setLayoutParams(dividerParams);
                    mLinear.addView(divider);
                }
            }

            // Add horizontal line that separates each category
            if (i < categories.size() - 1) {
                View divider = new View(getContext());
                divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inactive));
                divider.setLayoutParams(
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
                divider.setElevation(-1);
                mLinear.addView(divider);
            }

            View grey = new View(getContext());
            grey.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.wash));
            grey.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16));
            mLinear.addView(grey);
        }
        return view;
    }
}
