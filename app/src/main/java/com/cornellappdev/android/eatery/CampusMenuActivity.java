package com.cornellappdev.android.eatery;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cornellappdev.android.eatery.components.CustomPager;
import com.cornellappdev.android.eatery.components.WaitTimesComponent;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.CampusModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.Swipe;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.presenter.MenuPresenter;
import com.cornellappdev.android.eatery.util.TimeUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampusMenuActivity extends AppCompatActivity {
    TextView mCafeText;
    TextView mCafeLoc;
    TextView mCafeIsOpen;
    TextView mMenuText;
    ImageView mExceptionImage;
    ImageView mSwipeIcon;
    ImageView mBrbIcon;
    Button mBottomButton;
    FrameLayout mButtonFrame;
    LinearLayout mLinLayout;
    LinearLayout mLinearMaster;
    EateryBaseModel mCafeData;
    Toolbar mToolbar;
    AppBarLayout mAppbar;
    CollapsingToolbarLayout mCollapsingToolbar;
    NestedScrollView mScrollView;
    private TabLayout mTabLayout;
    private TabLayout mExpandedTabLayout;
    private MenuPresenter mMenuPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_eatery);

        Intent intent = getIntent();
        mCafeData = (CampusModel) intent.getSerializableExtra("cafeInfo");

        mMenuPresenter = new MenuPresenter(mCafeData);
        String cafeName = mCafeData.getNickName();
        String imageUrl = mCafeData.getImageURL();

        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        mLinearMaster = findViewById(R.id.master_container);

        // Load image animation
        Picasso.get()
                .load(imageUrl)
                .noFade()
                .into((ImageView) findViewById(R.id.ind_image));

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        mToolbar.setNavigationOnClickListener((View v) -> finishAfterTransition());

        mExceptionImage = findViewById(R.id.exception_image);
        Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        List<String> exceptions = mCafeData.getExceptions();
        if (exceptions.size() != 0) {
            if (exceptions.get(0).equals("Mobile Order Only")) {
                mExceptionImage.setImageResource(R.drawable.mobile_exception);
            }
            if (mCafeData.getCurrentStatus().toString().equals("Closed")){
                mExceptionImage.setAlpha((float) 0.72);
            }
            mExceptionImage.setVisibility(View.VISIBLE);
            mExceptionImage.startAnimation(fadein);
        }
      
        // Floating button is implemented as transparent button in a Frame Layout for design reasons
        mBottomButton = findViewById(R.id.bottom_button);
        mButtonFrame = findViewById(R.id.button_frame);
        if (mCafeData.getIsGet()) {
            mButtonFrame.setVisibility(View.VISIBLE);
            mBottomButton.setVisibility(View.VISIBLE);
            mBottomButton.setText(getString(R.string.get_button));
        } else if (mCafeData.getReserveUrl() != null) {
            mButtonFrame.setVisibility(View.VISIBLE);
            mBottomButton.setVisibility(View.VISIBLE);
            mBottomButton.setText(getString(R.string.opentable_button));
        }
        mBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCafeData.getIsGet()) {
                    try {
                        Intent i;
                        PackageManager managerclock = getPackageManager();
                        i = managerclock.getLaunchIntentForPackage("com.cbord.get");
                        i.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(i);
                    } catch(Exception e) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("https://get.cbord.com/cornell/full/food_home.php"));
                        startActivity(intent);
                    }
                } else if(mCafeData.getReserveUrl() != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(mCafeData.getReserveUrl()));
                    startActivity(intent);
                }
            }
        });

        mButtonFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCafeData.getIsGet()) {
                    Intent i;
                    PackageManager managerclock = getPackageManager();
                    i = managerclock.getLaunchIntentForPackage("com.cbord.get");
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(i);
                } else if(mCafeData.getReserveUrl() != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(mCafeData.getReserveUrl()));
                    startActivity(intent);
                }
            }
        });

        mCafeText = findViewById(R.id.ind_cafe_name);
        mCafeText.setText(cafeName);
        mCollapsingToolbar = findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle(" ");
        mCollapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayout);

        // Shows/hides title depending on scroll offset
        mAppbar = findViewById(R.id.appbar);
        mAppbar.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = true;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            mCollapsingToolbar.setTitle(cafeName);
                            isShow = true;
                        } else if (isShow) {
                            mCollapsingToolbar.setTitle(" ");
                            isShow = false;
                        }
                    }
                });

        // Format string for opening/closing time
        mCafeIsOpen = findViewById(R.id.ind_open);
        EateryBaseModel.Status currentStatus = mCafeData.getCurrentStatus();
        mCafeIsOpen.setText(currentStatus.toString());
        if (currentStatus == EateryBaseModel.Status.OPEN) {
            mCafeIsOpen.setTextColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.green));
        } else if (currentStatus == EateryBaseModel.Status.CLOSINGSOON) {
            mCafeIsOpen.setTextColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.yellow));
        } else {
            mCafeIsOpen.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }

        mCafeText = findViewById(R.id.ind_time);
        mCafeText.setText(TimeUtil.format(mCafeData.getCurrentStatus(), mCafeData.getChangeTime()));

        mCafeLoc = findViewById(R.id.ind_loc);
        mCafeLoc.setText(mCafeData.getBuildingLocation());

        mBrbIcon = findViewById(R.id.brb_icon);
        if (mCafeData.hasPaymentMethod(PaymentMethod.BRB)) {
            mBrbIcon.setVisibility(View.VISIBLE);
        }

        mSwipeIcon = findViewById(R.id.swipe_icon);
        if (mCafeData.hasPaymentMethod(PaymentMethod.SWIPES)) {
            mSwipeIcon.setVisibility(View.VISIBLE);
        }

        mScrollView = findViewById(R.id.controlled_scroll_view);
        mScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY && mButtonFrame.getVisibility() == View.VISIBLE) {
                    mButtonFrame.setVisibility(View.GONE);
                    mBottomButton.setVisibility(View.GONE);
                }
                if (scrollY < oldScrollY && mButtonFrame.getVisibility() == View.GONE) {
                    mButtonFrame.setVisibility(View.VISIBLE);
                    mBottomButton.setVisibility(View.VISIBLE);
                }
            }
        });

        CustomPager customPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tabs);
        mExpandedTabLayout = findViewById(R.id.expandedTabs);
        mLinLayout = findViewById(R.id.linear);

        float scale = getResources().getDisplayMetrics().density;

        // Formatting for when eatery is a cafe
        if (mCafeData instanceof CafeModel) {
            customPager.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
            mLinLayout.setVisibility(View.VISIBLE);

            View blank = new View(this);
            blank.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            blank.setBackgroundColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.inactive));
            blank.setElevation(-1);
            mLinLayout.addView(blank);


            // Expanded menu
            List<String> menu = ((CafeModel) mCafeData).getExpandedMenuItems();
            List<String> prices = ((CafeModel) mCafeData).getExpandedMenuPrices();
            List<String> stations = ((CafeModel) mCafeData).getExpandedMenuStations();
            List<Integer> sizes = ((CafeModel) mCafeData).getStationSizes();

            // if it has an expanded menu
            if (menu.size() != 0) {
                mExpandedTabLayout.setVisibility(View.VISIBLE);
                mExpandedTabLayout.setupWithViewPager(customPager);
                mExpandedTabLayout.setTabTextColors(
                        ContextCompat.getColor(getApplicationContext(), R.color.primary),
                        ContextCompat.getColor(getApplicationContext(), R.color.blue));

                for (String station : stations) {
                    mExpandedTabLayout.addTab(mExpandedTabLayout.newTab().setText(station));
                }

                mExpandedTabLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((Activity) CampusMenuActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        int widthS = displayMetrics.widthPixels;
                        mExpandedTabLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        int widthT = mExpandedTabLayout.getMeasuredWidth();
                        if (widthS > widthT) {
                            mExpandedTabLayout.setTabMode(TabLayout.MODE_FIXED);
                            mExpandedTabLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
                }});

                List<View> containers = new ArrayList<>();
                for (int i = 0; i < menu.size(); i++) {
                    String name = menu.get(i);
                    String price = prices.get(i);

                    LinearLayout container = new LinearLayout(getApplicationContext());

                    container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    container.setOrientation(LinearLayout.HORIZONTAL);

                    // item text
                    TextView mealItemText = new TextView(getApplicationContext());
                    mealItemText.setText(name);
                    mealItemText.setTextSize(14);
                    mealItemText.setTextColor(
                            ContextCompat.getColor(getApplicationContext(), R.color.primary));
                    mealItemText.setPadding(
                            (int) (18 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
                            (int) (8 * scale + 0.5f));
                    mealItemText.setGravity(Gravity.LEFT);

                    // price text
                    TextView priceText = new TextView(this);
                    priceText.setText(price);
                    priceText.setTextSize(14);
                    priceText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary));
                    priceText.setPadding(
                            0, (int) (8 * scale + 0.5f), 0, (int) (8 * scale + 0.5f));
                    priceText.setGravity(Gravity.RIGHT);

                    mealItemText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mealItemText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0,0, width - mealItemText.getMeasuredWidth() - priceText.getMeasuredWidth() - 100,0);
                            mealItemText.setLayoutParams(params);
                        }
                    });

                    container.addView(mealItemText);
                    container.addView(priceText);
                    containers.add(container);

                    mLinLayout.addView(container);

                    // Dividers
                    View divider = new View(this);
                    divider.setBackgroundColor(
                            ContextCompat.getColor(getApplicationContext(), R.color.wash));
                    LinearLayout.LayoutParams dividerParams =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    4);
                    dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, (int) (15.8 * scale + 0.5f), 0);
                    divider.setElevation(-1);
                    divider.setLayoutParams(dividerParams);
                    mLinLayout.addView(divider);

                    mExpandedTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            int acc = 0;
                            for (int i = 0; i <= tab.getPosition(); i++) {
                                acc += sizes.get(i);
                            }
                            View cell = containers.get(acc-1);
                            cell.getParent().requestChildFocus(cell, cell);
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                        }
                    });
                }
            } else {
                mLinearMaster.removeView(mExpandedTabLayout);
                List<String> legacyMenu = ((CafeModel) mCafeData).getCafeMenu();
                for (int i = 0; i < legacyMenu.size(); i++) {
                    TextView mealItemText = new TextView(this);
                    mealItemText.setText(legacyMenu.get(i));
                    mealItemText.setTextSize(14);
                    mealItemText.setTextColor(
                            ContextCompat.getColor(getApplicationContext(), R.color.primary));
                    mealItemText.setPadding(
                            (int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
                            (int) (8 * scale + 0.5f));
                    mLinLayout.addView(mealItemText);

                    // Add divider if text is not the last item in list
                    if (i != legacyMenu.size() - 1) {
                        View divider = new View(this);
                        divider.setBackgroundColor(
                                ContextCompat.getColor(getApplicationContext(), R.color.wash));
                        LinearLayout.LayoutParams dividerParams =
                                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        1);
                        dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, 0, 0);
                        divider.setElevation(-1);
                        divider.setLayoutParams(dividerParams);
                        mLinLayout.addView(divider);
                    }
            }
            }
        }
        // Formatting for when eatery is a dining hall and has a menu
        else if (mCafeData instanceof DiningHallModel) {
            mMenuText = findViewById(R.id.ind_menu);
            customPager.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
            mExpandedTabLayout.setVisibility(View.GONE);

            ArrayList<MealModel> mm =
                    ((DiningHallModel) mCafeData).getCurrentDayMenu().getAllMeals();

            if (mm.isEmpty()) {
                mMenuText.setText(R.string.no_menu_text);
                mMenuText.setTextSize(16);
                mMenuText.setPadding(0, 96, 0, 0);
                mMenuText.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.wash));
                mMenuText.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                mMenuText.setVisibility(View.GONE);
                customPager.setVisibility(View.VISIBLE);
                mTabLayout.setVisibility(View.VISIBLE);
                mLinLayout.setVisibility(View.GONE);
                setupViewPager(customPager);
                mTabLayout.setupWithViewPager(customPager);
                mTabLayout.setTabTextColors(
                        ContextCompat.getColor(getApplicationContext(), R.color.primary),
                        ContextCompat.getColor(getApplicationContext(), R.color.blue));
            }
        }

        this.setupWaitTimes();
        this.setupDefaultTab();
    }

    // Set up wait times feature.
    private void setupWaitTimes() {
        // Fetch wait time data for this model
        List<Swipe> waitTimes = mMenuPresenter.getWaitTimes();
        if (waitTimes == null) return;
        // Create and load wait times chart.
        WaitTimesComponent waitTimesComponent = new WaitTimesComponent(waitTimes);
        FrameLayout waitTimesHolder = findViewById(R.id.wait_times_frame);
        waitTimesComponent.inflateView(getApplicationContext(), waitTimesHolder, mScrollView);
    }

    // Set up default tab / menu for dining halls.
    private void setupDefaultTab() {
        if (mCafeData instanceof DiningHallModel) {
            DiningHallModel dhm = (DiningHallModel) mCafeData;
            int tabIndex = dhm.getCurrentMealTypeTabIndex();
            TabLayout.Tab defaultTab = mTabLayout.getTabAt(tabIndex);
            if (defaultTab != null) {
                defaultTab.select();
            }
        }
    }

    private void setupViewPager(CustomPager customPager) {
        ViewPagerAdapter adapter =
                new ViewPagerAdapter(getSupportFragmentManager());
        customPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter  {

        DiningHallModel dhm = (DiningHallModel) mCafeData;
        private int mCurrentPosition = -1;

        ViewPagerAdapter(FragmentManager manager) {
            super(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        // Set menu fragment to first MealModel object
        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position,
                @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
            if (position != mCurrentPosition) {
                if (mCurrentPosition == -1) {
                    position = 0;
                }
                Fragment fragment = (Fragment) object;
                CustomPager pager = (CustomPager) container;
                if (fragment.getView() != null) {
                    mCurrentPosition = position;
                    pager.measureCurrentView(fragment.getView());
                }
            }
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Bundle b = new Bundle();
            b.putInt("position", position);
            ArrayList<MealModel> todayMeals = dhm.getCurrentDayMenu().getAllMeals();
            b.putSerializable("cafeData", todayMeals.get(position));
            MenuFragment f = new MenuFragment();
            f.setArguments(b);
            return f;
        }

        @Override
        public int getCount() {
            int n;
            try {
                n = dhm.getCurrentDayMenu().getAllMealTypes().size();
            } catch (Exception e) {
                n = 0;
            }
            return n;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return dhm.getCurrentDayMenu().getAllMealTypes().get(position).toString();
        }
    }
}
