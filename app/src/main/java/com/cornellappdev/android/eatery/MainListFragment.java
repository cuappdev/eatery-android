package com.cornellappdev.android.eatery;

import static com.cornellappdev.android.eatery.model.enums.CampusArea.CENTRAL;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.NORTH;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.WEST;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.presenter.MainListPresenter;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainListFragment extends Fragment
        implements MainListAdapter.ListAdapterOnClickHandler, View.OnClickListener {
    private MainListPresenter mListPresenter;
    private MainListAdapter mListAdapter;
    private Map<Integer, Button> mCampusButtons;
    private Set<Button> mAreaButtonsPressed;
    private Set<Button> mPaymentButtonsPressed;
    private boolean mNearestFirstButtonPressed;
    private SearchView searchView;
    private boolean mEateryClickable = true;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
        setHasOptionsMenu(true);

        if (getActivity() != null) {
            ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(false);
            }
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.cafe_list);

        mListPresenter = new MainListPresenter();
        if (getContext() != null) {
            mListPresenter.initializeLocationListener(getContext());
        }
        mAreaButtonsPressed = new HashSet<>();
        mPaymentButtonsPressed = new HashSet<>();
        mNearestFirstButtonPressed = false;

        // Set up recyclerView and corresponding listAdapter
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mListAdapter = new MainListAdapter(getContext(), this, mListPresenter
                .getEateryList().size(), mListPresenter.getEateryList());
        recyclerView.setAdapter(mListAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        mCampusButtons = new HashMap<>();
        initializeCampusEateryButtons(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        // Reset ability to tap eateries when fragment is re-focused
        this.mEateryClickable = true;
        super.onResume();
    }

    private void initializeCampusEateryButtons(View rootView) {
        int[] viewIds = {
                R.id.nearestFirstButton,
                R.id.northButton,
                R.id.westButton,
                R.id.centralButton,
                R.id.swipes,
                R.id.brb};
        for (int id : viewIds) {
            Button tempButton = rootView.findViewById(id);
            mCampusButtons.put(id, tempButton);
            tempButton.setOnClickListener(this);
            changeButtonColor(R.color.blue, R.color.wash, tempButton);
        }
    }

    public void requestLocationPermissions() {
        if (getActivity() != null) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void changeButtonColor(int textColor, int backgroundColor, Button button) {
        if (getActivity() != null) {
            button.setTextColor(
                    ContextCompat.getColor(getActivity().getApplicationContext(), textColor));
            GradientDrawable bgShape = (GradientDrawable) button.getBackground();
            bgShape.setColor(
                    ContextCompat.getColor(getActivity().getApplicationContext(), backgroundColor));
        }
    }

    private void getCurrentAreas() {
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

    private void getCurrentPaymentTypes() {
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


    private void handleNearestFirstButtonPress(Button button) {
        if (mNearestFirstButtonPressed) {
            boolean successful = mListPresenter.sortNearestFirst(getContext(), this);
            if (successful) {
                changeButtonColor(R.color.white, R.color.blue, button);
            }
            else {
                mNearestFirstButtonPressed = false;
            }
        } else {
            changeButtonColor(R.color.blue, R.color.wash, button);
            mListPresenter.sortAlphabetical();
        }
        mListPresenter.filterImageList();
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

    public void initializeEateries() {
        mListPresenter.setCurrentList(mListPresenter.getEateryList());
        updateListAdapter();
    }

    private void updateListAdapter() {
        ArrayList<EateryBaseModel> cafesToDisplay = mListPresenter.getCafesToDisplay();
        Log.d("hellooo", String.valueOf(cafesToDisplay.size()));
        mListAdapter.setList(cafesToDisplay, cafesToDisplay.size(), mListPresenter.getQuery());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nearestFirstButton) {
            mFirebaseAnalytics.logEvent("nearest_first_filter_press", null);
            mNearestFirstButtonPressed = !mNearestFirstButtonPressed;
            handleNearestFirstButtonPress(mCampusButtons.get(view.getId()));
        } else if (view.getId() == R.id.northButton || view.getId() == R.id.centralButton
                || view.getId() == R.id.westButton) {
            if (view.getId() == R.id.northButton) {
                mFirebaseAnalytics.logEvent("north_filter_press", null);
            } else if (view.getId() == R.id.centralButton) {
                mFirebaseAnalytics.logEvent("central_filter_press", null);
            } else if (view.getId() == R.id.westButton) {
                mFirebaseAnalytics.logEvent("west_filter_press", null);
            }
            handleAreaButtonPress(mCampusButtons.get(view.getId()));
        } else if (view.getId() == R.id.brb || view.getId() == R.id.swipes) {
            if (view.getId() == R.id.brb) {
                mFirebaseAnalytics.logEvent("brb_filter_press", null);
            } else if (view.getId() == R.id.swipes) {
                mFirebaseAnalytics.logEvent("swipes_filter_press", null);
            }
            handlePaymentButtonPress(mCampusButtons.get(view.getId()));
        }
        updateListAdapter();
    }

    @Override
    public void onClick(int position, ArrayList<EateryBaseModel> list, View viewHolder) {
        if (list.size() <= position) return;
        EateryBaseModel model = list.get(position);
        Intent intent;
        if (this.mEateryClickable) {
            this.mEateryClickable = false;
            if (model instanceof DiningHallModel) {
                mFirebaseAnalytics.logEvent("campus_dining_hall_press", null);
            } else {
                mFirebaseAnalytics.logEvent("campus_cafe_press", null);
            }
            intent = new Intent(getActivity(), CampusMenuActivity.class);
            intent.putExtra("cafeInfo", model);

            if (viewHolder == null) {
                startActivity(intent);
                return;
            }
            View sharedCafeImage = viewHolder.findViewById(R.id.cafe_image);
            View sharedCafeName = viewHolder.findViewById(R.id.cafe_name);
            View sharedCafeOpen = viewHolder.findViewById(R.id.cafe_open);
            View sharedCafeTime = viewHolder.findViewById(R.id.cafe_time);
            View sharedDollarIcon = viewHolder.findViewById(R.id.card_dollar);
            View sharedSwipeIcon = viewHolder.findViewById(R.id.card_swipe);
            View sharedBrbIcon = viewHolder.findViewById(R.id.card_brb);

            Pair<View, String> imageTransition = Pair.create(sharedCafeImage,
                    ViewCompat.getTransitionName(sharedCafeImage));
            Pair<View, String> nameTransition = Pair.create(sharedCafeName,
                    ViewCompat.getTransitionName(sharedCafeName));
            Pair<View, String> openTransition = Pair.create(sharedCafeOpen,
                    ViewCompat.getTransitionName(sharedCafeOpen));
            Pair<View, String> timeTransition = Pair.create(sharedCafeTime,
                    ViewCompat.getTransitionName(sharedCafeTime));

            Pair<View, String> dollarTransition = Pair.create(sharedDollarIcon,
                    ViewCompat.getTransitionName(sharedDollarIcon));
            Pair<View, String> brbTransition = null;
            Pair<View, String> swipeTransition = null;

            if (sharedSwipeIcon != null) {
                if (sharedSwipeIcon.getVisibility() == View.INVISIBLE) {
                    // If the icon isn't visible, don't animate it
                    swipeTransition = Pair.create(sharedCafeName, "");
                } else {
                    swipeTransition = Pair.create(sharedSwipeIcon,
                            ViewCompat.getTransitionName(sharedSwipeIcon));
                }
            }

            if (sharedBrbIcon != null) {
                if (sharedBrbIcon.getVisibility() == View.INVISIBLE) {
                    brbTransition = Pair.create(sharedCafeName, "");
                } else {
                    brbTransition = Pair.create(sharedBrbIcon,
                            ViewCompat.getTransitionName(sharedBrbIcon));
                }
            }
            if (getActivity() != null) {
                // Adds transitions for all the elements Pair<View, String> specified above
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        imageTransition, nameTransition, openTransition, timeTransition,
                        swipeTransition, dollarTransition, brbTransition);
                startActivity(intent, options.toBundle());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (getActivity() == null) {
            return false;
        }
        if (item.getItemId() == R.id.action_map) {
            mFirebaseAnalytics.logEvent("homescreen_map_press", null);
            Intent intent = new Intent(getActivity().getApplicationContext(),
                    MapsActivity.class);
            startActivity(intent);
            return true;
        } else {
            // The user's action was not recognized, and invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            searchView = (SearchView) searchItem.getActionView();
            getActivity().setTitle("Eatery");
            AutoCompleteTextView searchTextView =
                    searchView.findViewById(R.id.search_src_text);
            searchView.setMaxWidth(2000);
            if (getContext() != null) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
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

    }

    @Override
    public void onDestroyView() {
        mListPresenter.setIsSearchPressed(false);
        super.onDestroyView();
    }
}
