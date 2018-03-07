package com.example.jc.eatery_android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jc.eatery_android.Model.CafeModel;
import com.example.jc.eatery_android.Model.CafeteriaModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    int position;
    CafeModel cafeData = null;
    TextView textView;

    public MenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cafeData = (CafeModel) getArguments().getSerializable("cafeData");
        try {
            position = getArguments().getInt("position");
        } catch (Exception e) {

        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        textView = view.findViewById(R.id.menuTemp);
        Log.i("TAG 2", Integer.toString(position));
        if (position == 0) {

            for (int i = 0; i < cafeData.getCafeMenu().size(); i++) {
                Log.i("TAG 2", cafeData.getCafeMenu().get(i));
                textView.setText(cafeData.getCafeMenu().get(i));
            }
            textView.setText(cafeData.getCafeMenu().toString());
        }
    }
}
