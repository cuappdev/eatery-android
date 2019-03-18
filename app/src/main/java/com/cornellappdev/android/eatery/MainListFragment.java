package com.cornellappdev.android.eatery;

import static com.cornellappdev.android.eatery.model.enums.CampusArea.CENTRAL;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.NORTH;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.WEST;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.presenter.MainListPresenter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainListFragment extends Fragment
        implements MainListAdapter.ListAdapterOnClickHandler, View.OnClickListener {
    public MainListPresenter mListPresenter;
    private RecyclerView mRecyclerView;
    private MainListAdapter mListAdapter;
    private Set<Button> mAreaButtonsPressed;
    private Set<Button> mPaymentButtonsPressed;
    private SparseArray<Button> mButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

        getActivity().setTitle("Eateries");
        setHasOptionsMenu(true);

        mRecyclerView = rootView.findViewById(R.id.cafe_list);
        mListPresenter = new MainListPresenter();
        mAreaButtonsPressed = new HashSet<>();
        mPaymentButtonsPressed = new HashSet<>();
        // Set up recyclerView and corresponding listAdapter
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mListAdapter =
                new MainListAdapter(getContext(), this, mListPresenter.getEateryList().size(),
                        mListPresenter.getEateryList());
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);

        mButtons = new SparseArray<>();
        initializeButtons(rootView);

        return rootView;
    }

    public void initializeButtons(View rootView) {
        int[] viewIds = {
                R.id.northButton,
                R.id.westButton,
                R.id.centralButton,
                R.id.swipes,
                R.id.brb};
        for (int id : viewIds) {
            Button tempButton = rootView.findViewById(id);
            mButtons.put(id, tempButton);
            tempButton.setOnClickListener(this);
            changeButtonColor(R.color.blue, R.color.wash, tempButton);
        }
    }

    public void changeButtonColor(int textColor, int backgroundColor, Button button) {
        button.setTextColor(
                ContextCompat.getColor(getActivity().getApplicationContext(), textColor));
        GradientDrawable bgShape = (GradientDrawable) button.getBackground();
        bgShape.setColor(
                ContextCompat.getColor(getActivity().getApplicationContext(), backgroundColor));
    }

    public void getCurrentAreas() {
        HashSet<CampusArea> areaSet = new HashSet<>();
        for (Button button : mAreaButtonsPressed) {
            if (button.getId() == R.id.northButton) {
                areaSet.add(NORTH);
            } else if (button.getId() == R.id.westButton) {
                areaSet.add(WEST);
            } else if (button.getId() == R.id.centralButton) {
                areaSet.add(CENTRAL);
            }
        }
        mListPresenter.setAreaSet(areaSet);
    }

    public void getCurrentPaymentTypes() {
        HashSet<PaymentMethod> paymentSet = new HashSet<>();
        for (Button button : mPaymentButtonsPressed) {
            if (button.getId() == R.id.brb) {
                paymentSet.add(PaymentMethod.BRB);
            } else if (button.getId() == R.id.swipes) {
                paymentSet.add(PaymentMethod.SWIPES);
            }
        }
        mListPresenter.setPaymentSet(paymentSet);
    }

    private void handleAreaButtonPress(Button button) {
        if (mAreaButtonsPressed.contains(button)) {
            changeButtonColor(R.color.blue, R.color.wash, button);
            mAreaButtonsPressed.remove(button);
        } else {
            changeButtonColor(R.color.white, R.color.blue, button);
            mAreaButtonsPressed.add(button);
        }

        getCurrentAreas();
        mListPresenter.filterImageList();
    }

    private void handlePaymentButtonPress(Button button) {
        if (mPaymentButtonsPressed.contains(button)) {
            changeButtonColor(R.color.blue, R.color.wash, button);
            mPaymentButtonsPressed.remove(button);
        } else {
            changeButtonColor(R.color.white, R.color.blue, button);
            mPaymentButtonsPressed.add(button);
        }
        getCurrentPaymentTypes();
        mListPresenter.filterImageList();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.northButton || view.getId() == R.id.centralButton
                || view.getId() == R.id.westButton) {
            handleAreaButtonPress(mButtons.get(view.getId()));
        } else if (view.getId() == R.id.brb || view.getId() == R.id.swipes) {
            handlePaymentButtonPress(mButtons.get(view.getId()));

        }
        ArrayList<EateryBaseModel> cafesToDisplay = mListPresenter.getCafesToDisplay();
        Collections.sort(cafesToDisplay);
        mListAdapter.setList(cafesToDisplay, cafesToDisplay.size(), mListPresenter.getQuery());
    }

    @Override
    public void onClick(int position, ArrayList<EateryBaseModel> list) {
        Intent intent = new Intent(getActivity(), MenuActivity.class);
        intent.putExtra("cafeInfo", list.get(position));
        intent.putExtra("locName", list.get(position).getNickName());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        MapsActivity.class);
                startActivity(intent);
                return true;
            default:
                // The user's action was not recognized, and invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        getActivity().setTitle("Eateries");
        AutoCompleteTextView searchTextView =
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchView.setMaxWidth(2000);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(
                    searchTextView,
                    R.drawable
                            .cursor); // This sets the cursor resource ID to 0 or @null which
          // will make it visible
            // on white background
        } catch (Exception e) {
            // Don't do anything
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mListPresenter.setIsSearchPressed(query.length() > 0);
                mListPresenter.setQuery(query);
                mListPresenter.filterSearchList();
                ArrayList<EateryBaseModel> cafesToDisplay = mListPresenter.getCurrentList();
                mListAdapter.setList(cafesToDisplay, cafesToDisplay.size(), query);
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        mListPresenter.setIsSearchPressed(false);
        super.onDestroyView();
    }
}
