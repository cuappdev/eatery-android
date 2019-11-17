package com.cornellappdev.android.eatery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.Category;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.presenter.MainListPresenter;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.cornellappdev.android.eatery.model.enums.CampusArea.CENTRAL;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.NORTH;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.WEST;

public class MainListFragment extends Fragment
        implements MainListAdapter.ListAdapterOnClickHandler, View.OnClickListener {
    private MainListPresenter mListPresenter;
    private MainListAdapter mListAdapter;
    private Map<Integer, Button> mCampusButtons;
    private Map<Integer, Button> mCollegetownButtons;
    private RecyclerView mRecyclerView;
    private Set<Button> mAreaButtonsPressed;
    private Set<Button> mPaymentButtonsPressed;
    private Set<Button> mCategoryButtonsPressed;
    private boolean mNearestFirstButtonPressed;
    private ImageView mCampusPill;
    private ImageView mCollegetownPill;
    private LinearLayout mCampusPillHolder;
    private LinearLayout mCtownPillHolder;
    private LinearLayout mPillHolder;
    public SearchView searchView;
    private boolean mCurrentlyAnimating;
    private boolean mEateryClickable = true;
    private boolean mPillVisible = true;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mRecyclerView = rootView.findViewById(R.id.cafe_list);
        mCampusPill = rootView.findViewById(R.id.pill_campus);
        mCollegetownPill = rootView.findViewById(R.id.pill_collegetown);
        mCampusPillHolder = rootView.findViewById(R.id.pill_campus_holder);
        mCtownPillHolder = rootView.findViewById(R.id.pill_ctown_holder);
        mPillHolder = rootView.findViewById(R.id.pill_holder);

        mListPresenter = new MainListPresenter();
        mAreaButtonsPressed = new HashSet<>();
        mPaymentButtonsPressed = new HashSet<>();
        mCategoryButtonsPressed = new HashSet<>();
        mNearestFirstButtonPressed = false;
        mPillHolder.bringToFront();

        // Set up recyclerView and corresponding listAdapter
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mListAdapter = new MainListAdapter(getContext(), this, mListPresenter
                .getEateryList().size(), mListPresenter.getEateryList());
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // On scroll, if scrolling down at a rate then make the pill invisible, if scrolling
                // up make the pill visiible
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 25) {
                    animatePillInvisible();
                } else if (dy < 0) {
                    animatePillVisible();
                }

            }
        });
        mCampusButtons = new HashMap<>();
        mCollegetownButtons = new HashMap<>();
        initializeCampusEateryButtons(rootView);
        initializeCollegeTownEateryButtons(rootView);

        mCampusPillHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCampusPillPress();
            }
        });
        mCtownPillHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCollegetownPillPress();
            }
        });

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

    private void initializeCollegeTownEateryButtons(View rootView) {
        int[] viewIds = {
                R.id.nearestFirstButton,
                R.id.american,
                R.id.coffee,
                R.id.chinese,
                R.id.desserts,
                R.id.grocery,
                R.id.italian,
                R.id.indian,
                R.id.japanese,
                R.id.korean,
                R.id.mediterranean,
                R.id.thai,
                R.id.vietnamese
        };

        for (int id : viewIds) {
            Button tempButton = rootView.findViewById(id);
            mCollegetownButtons.put(id, tempButton);
            tempButton.setOnClickListener(this);
            changeButtonColor(R.color.blue, R.color.wash, tempButton);
        }
    }

    private void changeButtonVisbility(boolean setCTownButtonVisible) {
        if (setCTownButtonVisible) {
            for (Button button : mCampusButtons.values()) {
                button.setVisibility(View.GONE);
            }
            for (Button button : mCollegetownButtons.values()) {
                button.setVisibility(View.VISIBLE);
            }

        } else {
            for (Button button : mCampusButtons.values()) {
                button.setVisibility(View.VISIBLE);
            }
            for (Button button : mCollegetownButtons.values()) {
                button.setVisibility(View.GONE);
            }
        }
    }

    private void changeButtonColor(int textColor, int backgroundColor, Button button) {
        button.setTextColor(
                ContextCompat.getColor(getActivity().getApplicationContext(), textColor));
        GradientDrawable bgShape = (GradientDrawable) button.getBackground();
        bgShape.setColor(
                ContextCompat.getColor(getActivity().getApplicationContext(), backgroundColor));
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

    private void animatePillInvisible() {
        if (!mCurrentlyAnimating && mPillVisible) {
            mCurrentlyAnimating = true;
            mPillHolder.animate().translationY(300).setDuration(500).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mCurrentlyAnimating = false;
                            mPillVisible = false;
                        }
                    });
        }
    }

    private void animatePillVisible() {
        if (!mCurrentlyAnimating && !mPillVisible) {
            mCurrentlyAnimating = true;
            mPillHolder.animate().translationY(0).setDuration(500).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mCurrentlyAnimating = false;
                            mPillVisible = true;
                        }
                    });
        }
    }

    private void getCurrentCategory() {
        HashSet<Category> categorySet = new HashSet<>();
        for (Button button : mCategoryButtonsPressed) {
            switch (button.getId()) {
                case R.id.american:
                    categorySet.add(Category.American);
                    break;
                case R.id.coffee:
                    categorySet.add(Category.Coffee);
                    break;
                case R.id.chinese:
                    categorySet.add(Category.Chinese);
                    break;
                case R.id.desserts:
                    categorySet.add(Category.Desserts);
                    break;
                case R.id.grocery:
                    categorySet.add(Category.Grocery);
                    break;
                case R.id.italian:
                    categorySet.add(Category.Italian);
                    break;
                case R.id.indian:
                    categorySet.add(Category.Indian);
                    break;
                case R.id.japanese:
                    categorySet.add(Category.Japanese);
                    break;
                case R.id.korean:
                    categorySet.add(Category.Korean);
                    break;
                case R.id.mediterranean:
                    categorySet.add(Category.Mediterranean);
                    break;
                case R.id.thai:
                    categorySet.add(Category.Thai);
                    break;
                case R.id.vietnamese:
                    categorySet.add(Category.Vietnamese);
                    break;
                default:
                    break;
            }
        }
        mListPresenter.setCategorySet(categorySet);
    }

    private void handleCollegetownPillPress() {
        mFirebaseAnalytics.logEvent("collegetown_pill_press", null);
        mListPresenter.setDisplayCTown(true);
        mCollegetownPill.setBackgroundResource(R.drawable.pill_ct_active);
        mCampusPill.setBackgroundResource(R.drawable.pill_campus_inactive);
        changeButtonVisbility(true);
        mListPresenter.setCurrentList(mListPresenter.getCtEateryList());
        searchView.setQuery("", false);
        mListPresenter.filterImageList();
        updateListAdapter();
    }

    private void handleCampusPillPress() {
        mFirebaseAnalytics.logEvent("campus_pill_press", null);
        mListPresenter.setDisplayCTown(false);
        mCollegetownPill.setBackgroundResource(R.drawable.pill_ct_inactive);
        mCampusPill.setBackgroundResource(R.drawable.pill_campus_active);
        changeButtonVisbility(false);
        mListPresenter.setCurrentList(mListPresenter.getEateryList());
        searchView.setQuery("", false);
        mListPresenter.filterImageList();
        updateListAdapter();
    }

    private void handleNearestFirstButtonPress(Button button) {
        if (mNearestFirstButtonPressed) {
            changeButtonColor(R.color.white, R.color.blue, button);
            mListPresenter.sortNearestFirst(getContext());
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

    private void handleCategoryButtonPress(Button button) {
        if (mCategoryButtonsPressed.contains(button)) {
            changeButtonColor(R.color.blue, R.color.wash, button);
            mCategoryButtonsPressed.remove(button);
        } else {
            changeButtonColor(R.color.white, R.color.blue, button);
            mCategoryButtonsPressed.add(button);
        }
        getCurrentCategory();
        mListPresenter.filterImageList();
    }

    public void initializeEateries() {
        mListPresenter.setCurrentList(mListPresenter.getEateryList());
        updateListAdapter();
    }

    private void updateListAdapter() {
        ArrayList<EateryBaseModel> cafesToDisplay = mListPresenter.getCafesToDisplay();
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
        } else {
            mFirebaseAnalytics.logEvent("collegetown_filters_press", null);
            handleCategoryButtonPress(mCollegetownButtons.get(view.getId()));
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
            if (model.isCtEatery()) {
                mFirebaseAnalytics.logEvent("collegetown_eatery_press", null);
                intent = new Intent(getActivity(), CtownMenuActivity.class);
            } else {
                if (model instanceof DiningHallModel) {
                    mFirebaseAnalytics.logEvent("campus_dining_hall_press", null);
                } else {
                    mFirebaseAnalytics.logEvent("campus_cafe_press", null);
                }
                intent = new Intent(getActivity(), CampusMenuActivity.class);
            }
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

            Pair<View, String> imageTransition = Pair.create(sharedCafeImage, ViewCompat.getTransitionName(sharedCafeImage));
            Pair<View, String> nameTransition = Pair.create(sharedCafeName, ViewCompat.getTransitionName(sharedCafeName));
            Pair<View, String> openTransition = Pair.create(sharedCafeOpen, ViewCompat.getTransitionName(sharedCafeOpen));
            Pair<View, String> timeTransition = Pair.create(sharedCafeTime, ViewCompat.getTransitionName(sharedCafeTime));

            Pair<View, String> dollarTransition = Pair.create(sharedDollarIcon, ViewCompat.getTransitionName(sharedDollarIcon));
            Pair<View, String> brbTransition;
            Pair<View, String> swipeTransition;

            if (sharedSwipeIcon != null && sharedSwipeIcon.getVisibility() == View.INVISIBLE) {
                // If the icon isn't visible, don't animate it
                swipeTransition = Pair.create(sharedCafeName, "");
            } else {
                swipeTransition = Pair.create(sharedSwipeIcon, ViewCompat.getTransitionName(sharedSwipeIcon));
            }

            if (sharedBrbIcon != null && sharedBrbIcon.getVisibility() == View.INVISIBLE)
                brbTransition = Pair.create(sharedCafeName, "");
            else {
                brbTransition = Pair.create(sharedBrbIcon, ViewCompat.getTransitionName(sharedBrbIcon));
            }

            // Adds transitions for all the elements Pair<View, String> specified above
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    imageTransition, nameTransition, openTransition, timeTransition,
                    swipeTransition, dollarTransition, brbTransition);
            startActivity(intent, options.toBundle());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                mFirebaseAnalytics.logEvent("homescreen_map_press", null);
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
        searchView = (SearchView) searchItem.getActionView();
        getActivity().setTitle("Eatery");
        AutoCompleteTextView searchTextView =
                searchView.findViewById(R.id.search_src_text);
        searchView.setMaxWidth(2000);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

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
