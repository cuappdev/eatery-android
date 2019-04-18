package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.MealModel;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.util.TimeUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class CampusMenuActivity extends AppCompatActivity {
    TextView cafeText;
    SimpleDraweeView cafeImage;
    TextView cafeLoc;
    TextView cafeIsOpen;
    TextView menuText;
    ImageView swipeIcon;
    ImageView brbIcon;
    LinearLayout linLayout;
    EateryBaseModel cafeData;
    Toolbar toolbar;
    AppBarLayout appbar;
    CollapsingToolbarLayout collapsingToolbar;
    private TabLayout tabLayout;
    private CustomPager customPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_eatery);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        final String cafeName = (String) intent.getSerializableExtra("locName");
        cafeText = findViewById(R.id.ind_cafe_name);
        cafeText.setText(cafeName);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayout);

        // Shows/hides title depending on scroll offset
        appbar = findViewById(R.id.appbar);
        appbar.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    boolean isShow = true;
                    int scrollRange = -1;

                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        if (scrollRange == -1) {
                            scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + verticalOffset == 0) {
                            collapsingToolbar.setTitle(cafeName);
                            isShow = true;
                        } else if (isShow) {
                            collapsingToolbar.setTitle(" ");
                            isShow = false;
                        }
                    }
                });

        cafeData = (EateryBaseModel) intent.getSerializableExtra("cafeInfo");

        // Format string for opening/closing time
        cafeIsOpen = findViewById(R.id.ind_open);
        EateryBaseModel.Status currentStatus = cafeData.getCurrentStatus();
        cafeIsOpen.setText(currentStatus.toString());
        if (currentStatus == EateryBaseModel.Status.OPEN) {
            cafeIsOpen.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        } else if (currentStatus == EateryBaseModel.Status.CLOSINGSOON) {
            cafeIsOpen.setTextColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.yellow));
        } else {
            cafeIsOpen.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }

        cafeText = findViewById(R.id.ind_time);
        cafeText.setText(TimeUtil.format(cafeData.getCurrentStatus(), cafeData.getChangeTime()));

        cafeLoc = findViewById(R.id.ind_loc);
        cafeLoc.setText(cafeData.getBuildingLocation());

        cafeImage = findViewById(R.id.ind_image);

        String imageLocation = EateryBaseModel.getImageURL(cafeName);
        Uri uri = Uri.parse(imageLocation);
        cafeImage.setImageURI(uri);

        brbIcon = findViewById(R.id.brb_icon);
        if (cafeData.hasPaymentMethod(PaymentMethod.BRB)) {
            brbIcon.setVisibility(View.VISIBLE);
        }

        swipeIcon = findViewById(R.id.swipe_icon);
        if (cafeData.hasPaymentMethod(PaymentMethod.SWIPES)) {
            brbIcon.setVisibility(View.VISIBLE);
        }

        customPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        linLayout = findViewById(R.id.linear);

        float scale = getResources().getDisplayMetrics().density;

        // Formatting for when eatery is a cafe
        if (cafeData instanceof CafeModel) {
            customPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            linLayout.setVisibility(View.VISIBLE);

            View blank = new View(this);
            blank.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            blank.setBackgroundColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.inactive));
            blank.setElevation(-1);
            linLayout.addView(blank);

            List<String> menu = ((CafeModel) cafeData).getCafeMenu();
            for (int i = 0; i < menu.size(); i++) {
                TextView mealItemText = new TextView(this);
                mealItemText.setText(menu.get(i));
                mealItemText.setTextSize(14);
                mealItemText.setTextColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.primary));
                mealItemText.setPadding(
                        (int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
                        (int) (8 * scale + 0.5f));
                linLayout.addView(mealItemText);

                // Add divider if text is not the last item in list
                if (i != menu.size() - 1) {
                    View divider = new View(this);
                    divider.setBackgroundColor(
                            ContextCompat.getColor(getApplicationContext(), R.color.wash));
                    LinearLayout.LayoutParams dividerParams =
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    1);
                    dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, 0, 0);
                    divider.setElevation(-1);
                    divider.setLayoutParams(dividerParams);
                    linLayout.addView(divider);
                }
            }
        }
        // Formatting for when eatery is a dining hall and has a menu
        else if (cafeData instanceof DiningHallModel) {
            menuText = findViewById(R.id.ind_menu);
            customPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);

            ArrayList<MealModel> mm =
                    ((DiningHallModel) cafeData).getCurrentDayMenu().getAllMeals();
            if (mm.isEmpty() || mm.get(0).getMenu().getNumberOfCategories() == 0) {
                menuText.setText(R.string.no_menu_text);
                menuText.setTextSize(16);
                menuText.setPadding(0, 96, 0, 0);
                menuText.setBackgroundColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.wash));
                menuText.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                menuText.setVisibility(View.GONE);
                customPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                linLayout.setVisibility(View.GONE);
                setupViewPager(customPager);
                tabLayout.setupWithViewPager(customPager);
                tabLayout.setTabTextColors(
                        ContextCompat.getColor(getApplicationContext(), R.color.primary),
                        ContextCompat.getColor(getApplicationContext(), R.color.blue));
            }
        }
    }

    private void setupViewPager(CustomPager customPager) {
        ViewPagerAdapter adapter =
                new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        customPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        DiningHallModel dhm = (DiningHallModel) cafeData;
        private Context mContext;
        private int mCurrentPosition = -1;

        public ViewPagerAdapter(Context context, FragmentManager manager) {
            super(manager);
            mContext = context;
        }

        // Set menu fragment to first MealModel object
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (position != mCurrentPosition) {
                if (mCurrentPosition == -1) position = 0;
                Fragment fragment = (Fragment) object;
                CustomPager pager = (CustomPager) container;
                if (fragment != null && fragment.getView() != null) {
                    mCurrentPosition = position;
                    pager.measureCurrentView(fragment.getView());
                }
            }
        }

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
