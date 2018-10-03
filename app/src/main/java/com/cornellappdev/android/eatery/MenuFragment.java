package com.cornellappdev.android.eatery;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cornellappdev.android.eatery.model.MealModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** A simple {@link Fragment} subclass. */
public class MenuFragment extends Fragment {
  int position;
  ArrayList<MealModel> menus;
  LinearLayout linear;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    menus = (ArrayList<MealModel>) getArguments().getSerializable("cafeData");
    View view = inflater.inflate(R.layout.fragment_menu, container, false);
    linear = view.findViewById(R.id.linearFragment);

    try {
      position = getArguments().getInt("position");
    } catch (Exception e) {
      e.printStackTrace();
    }

    float scale = getResources().getDisplayMetrics().density;
    // Checks for unavailable or missing menus
    if (menus.get(position).getMenu().entrySet().isEmpty()) {
      TextView missingMenuText = new TextView(getContext());
      missingMenuText.setText("No menu available");
      missingMenuText.setTextColor(Color.parseColor("#de000000"));
      missingMenuText.setPadding((int) (16 * scale + 0.5f), 0, 0, (int) (12 * scale + 0.5f));
      missingMenuText.setTextSize(14);
      linear.addView(missingMenuText);
    }

    int counter = 0;
    for (HashMap.Entry<String, ArrayList<String>> entry :
        menus.get(position).getMenu().entrySet()) {
      // Add subheading for category of food
      String key = entry.getKey();
      List<String> value = entry.getValue();
      TextView categoryText = new TextView(getContext());
      categoryText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
      categoryText.setText(key);
      categoryText.setTextSize(16);
      categoryText.setTextColor(Color.parseColor("#de000000"));

      // Note(lesley): there's extra padding added to the first category somewhere and I
      // don't know how to fix it
      if (counter == 0) {
        categoryText.setPadding((int) (16 * scale + 0.5f), 0, 0, (int) (12 * scale + 0.5f));
      } else {
        categoryText.setPadding(
            (int) (16 * scale + 0.5f), (int) (12 * scale + 0.5f), 0, (int) (12 * scale + 0.5f));
      }

      linear.addView(categoryText);

      View blank = new View(getContext());
      blank.setLayoutParams(
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
      blank.setBackgroundColor(Color.parseColor("#ccd0d5"));
      blank.setElevation(-1);
      linear.addView(blank);

      // Add individual meal items to list
      for (int i = 0; i < value.size(); i++) {
        TextView mealItemText = new TextView(getContext());
        mealItemText.setText(value.get(i));
        mealItemText.setTextSize(14);
        mealItemText.setTextColor(Color.parseColor("#de000000"));
        mealItemText.setPadding(
            (int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0, (int) (8 * scale + 0.5f));
        linear.addView(mealItemText);

        if (i != value.size() - 1) {
          View divider = new View(getContext());
          divider.setBackgroundColor(Color.parseColor("#ccd0d5"));
          LinearLayout.LayoutParams dividerParams =
              new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
          dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, 0, 0);
          divider.setElevation(-1);
          divider.setLayoutParams(dividerParams);
          linear.addView(divider);
        }
      }

      // Add horizontal line that separates each category
      if (counter != menus.get(position).getMenu().entrySet().size() - 1) {
        View divider = new View(getContext());
        divider.setBackgroundColor(Color.parseColor("#ccd0d5"));
        divider.setLayoutParams(
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setElevation(-1);
        linear.addView(divider);

        View grey = new View(getContext());
        grey.setBackgroundColor(Color.parseColor("#f2f2f2"));
        grey.setLayoutParams(
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16));
        linear.addView(grey);
      }
      counter++;
    }

    // Inflate the layout for this fragment
    return view;
  }
}
