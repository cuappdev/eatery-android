package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CampusMenuActivity extends AppCompatActivity {
    TextView mCafeText;
    TextView mCafeLoc;
    TextView mCafeIsOpen;
    TextView mMenuText;
    ImageView mSwipeIcon;
    ImageView mBrbIcon;
    LinearLayout mLinLayout;
    EateryBaseModel mCafeData;
    Toolbar mToolbar;
    AppBarLayout mAppbar;
    CollapsingToolbarLayout mCollapsingToolbar;
    private TabLayout mTabLayout;
    private CustomPager mCustomPager;
    private BarChart mWaitTimesChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_eatery);

        Intent intent = getIntent();
        mCafeData = (EateryBaseModel) intent.getSerializableExtra("cafeInfo");
        String cafeName = mCafeData.getNickName();
        String imageUrl = EateryBaseModel.getImageURL(mCafeData.getNickName());

        // Load image animation
        Picasso.get()
                .load(imageUrl)
                .noFade()
                .into((ImageView)findViewById(R.id.ind_image));

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
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
            mCafeIsOpen.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
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

        mCustomPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tabs);
        mLinLayout = findViewById(R.id.linear);

        float scale = getResources().getDisplayMetrics().density;

        // Formatting for when eatery is a cafe
        if (mCafeData instanceof CafeModel) {
            mCustomPager.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
            mLinLayout.setVisibility(View.VISIBLE);

            View blank = new View(this);
            blank.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
            blank.setBackgroundColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.inactive));
            blank.setElevation(-1);
            mLinLayout.addView(blank);

            List<String> menu = ((CafeModel) mCafeData).getCafeMenu();
            for (int i = 0; i < menu.size(); i++) {
                TextView mealItemText = new TextView(this);
                mealItemText.setText(menu.get(i));
                mealItemText.setTextSize(14);
                mealItemText.setTextColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.primary));
                mealItemText.setPadding(
                        (int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
                        (int) (8 * scale + 0.5f));
                mLinLayout.addView(mealItemText);

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
                    mLinLayout.addView(divider);
                }
            }
        }
        // Formatting for when eatery is a dining hall and has a menu
        else if (mCafeData instanceof DiningHallModel) {
            mMenuText = findViewById(R.id.ind_menu);
            mCustomPager.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
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
                mCustomPager.setVisibility(View.VISIBLE);
                mTabLayout.setVisibility(View.VISIBLE);
                mLinLayout.setVisibility(View.GONE);
                setupViewPager(mCustomPager);
                mTabLayout.setupWithViewPager(mCustomPager);
                mTabLayout.setTabTextColors(
                        ContextCompat.getColor(getApplicationContext(), R.color.primary),
                        ContextCompat.getColor(getApplicationContext(), R.color.blue));
            }
        }

        setupWaitTimesChart();
    }

    private void setupAxis(BarChart b) {
        // Remove bar chart Y axis - left side.
        YAxis yAxisLeft = b.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawLabels(false);
        yAxisLeft.setEnabled(false);
        yAxisLeft.setAxisMinimum(-0.5f);

        // Remove bar chart Y axis - right side.
        YAxis yAxisRight = b.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setEnabled(false);

        // Customize bar chart X axis.
        XAxis xAxis = b.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set X axis colors and fonts.
        // "xAxis.set[_]Color(R.color.[_])" will use Android's default colors, rather than our custom defined.
        xAxis.setAxisLineColor(getResources().getColor(R.color.inactive));
        xAxis.setTextColor(getResources().getColor(R.color.secondary));
        xAxis.setTextSize(14f);
        xAxis.setAxisLineWidth(2f);
        // Setup X axis labels.
        xAxis.setAxisMaximum(21);
        xAxis.setLabelCount(8);
        xAxis.setTypeface(Typeface.SANS_SERIF);
        xAxis.setYOffset(8f);
        ArrayList<String> xAxisLabels = new ArrayList<String>();
        for(int i = 0; i < 22; i++) {
            int hourNum = (i + 6) % 12;
            if (hourNum == 0) {
                hourNum = 12;
            }
            String ap = i <= 5 || i >= 18 ? "a" : "p";
            xAxisLabels.add(hourNum + ap);
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
    }

    private void setupWaitTimesChart() {
        mWaitTimesChart = (BarChart) findViewById(R.id.wait_time_chart);
        mWaitTimesChart.setDescription(null);
        mWaitTimesChart.getLegend().setEnabled(false);
        mWaitTimesChart.setDoubleTapToZoomEnabled(false);
        mWaitTimesChart.setPinchZoom(false);
        mWaitTimesChart.animateY(2000);
        mWaitTimesChart.setExtraBottomOffset(24f);
        mWaitTimesChart.getRendererXAxis().getPaintAxisLabels().setTextAlign(Paint.Align.LEFT);


        mWaitTimesChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        setupAxis(mWaitTimesChart);

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 2f));
        entries.add(new BarEntry(1f, 30f));
        entries.add(new BarEntry(2f, 10f));
        entries.add(new BarEntry(3f, 20f));
        entries.add(new BarEntry(4f, 30f));
        entries.add(new BarEntry(5f, 40f));
        entries.add(new BarEntry(6f, 50f));
        entries.add(new BarEntry(7f, 35f));
        entries.add(new BarEntry(8f, 50f));
        entries.add(new BarEntry(9f, 30f));
        entries.add(new BarEntry(10f, 2f));
        entries.add(new BarEntry(11f, 5f));
        entries.add(new BarEntry(12f, 10f));
        entries.add(new BarEntry(13f, 60f));
        entries.add(new BarEntry(14f, 40f));
        entries.add(new BarEntry(15f, 20f));
        entries.add(new BarEntry(16f, 2f));
        entries.add(new BarEntry(17f, 2f));
        entries.add(new BarEntry(18f, 10f));
        entries.add(new BarEntry(19f, 2f));
        entries.add(new BarEntry(20f, 2f));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColors(ContextCompat.getColor(getApplicationContext(), R.color.lightblue));
        set.setValueTypeface(Typeface.SANS_SERIF);
//        set.setHighlightEnabled(false);
        set.setHighLightColor(getResources().getColor(R.color.blue));
        set.setHighLightAlpha(255);
        BarData barData = new BarData(set);
        barData.setDrawValues(false);
        mWaitTimesChart.setData(barData);
    }

    private void setupViewPager(CustomPager customPager) {
        ViewPagerAdapter adapter =
                new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        customPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        DiningHallModel dhm = (DiningHallModel) mCafeData;
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
