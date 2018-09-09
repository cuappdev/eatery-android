package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cornellappdev.android.eatery.Model.CafeteriaModel;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    ImageView cafeImage;
    TextView cafeLoc;
    TextView cafeIsOpen;
    TextView getDirections;
    ImageView swipe_icon;
    LinearLayout linLayout;
    private TabLayout tabLayout;
    private CustomPager customPager;
    ArrayList<CafeteriaModel> cafeList;
    CafeteriaModel cafeData;
    net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        String cafeName = (String) intent.getSerializableExtra("locName");
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(cafeName);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.collapsingToolbarLayoutTitleColor);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayoutTitleColor);

        cafeName = "@drawable/" + convertName(cafeName);

        cafeList = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("testData");
        cafeData = (CafeteriaModel) intent.getSerializableExtra("cafeInfo");

        // TODO(lesley): do this removal on the JSON side, also I'm certain that the logic to remove
        // Becker is broken
        // Remove Lite Lunch for North Star and Becker
        if (cafeData.getNickName().equals("North Star") || cafeData.getNickName().equals("Becker House Dining")) {
                if(cafeData.getWeeklyMenu().get(cafeData.indexOfCurrentDay()).size()>2){
                    cafeData.getWeeklyMenu().get(cafeData.indexOfCurrentDay()).remove(2);
                }
        }

        // Format string for opening/closing time
        cafeIsOpen = findViewById(R.id.ind_open);
        SpannableString openString = new SpannableString(cafeData.isOpen() + "  "
                + cafeData.getCloseTime());
        openString.setSpan(new StyleSpan(Typeface.BOLD), 0, cafeData.isOpen().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        openString.setSpan(new ForegroundColorSpan(Color.parseColor("#4B7FBE")),
                0, cafeData.isOpen().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cafeIsOpen.setText(openString);
        cafeIsOpen.setTextSize(15);

        cafeLoc = findViewById(R.id.ind_loc);
        cafeLoc.setTextSize(15);
        cafeLoc.setText(cafeData.getBuildingLocation());

        cafeImage = findViewById(R.id.ind_image);
        cafeImage.setBackgroundColor(0xFFff0000);
        int imageRes = getResources().getIdentifier(cafeName, null, getPackageName());

        cafeImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
               imageRes, 400, 400));
        cafeImage.setColorFilter(Color.argb(80, 153, 153, 153));

        getDirections = findViewById(R.id.directions);
        getDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                intent.putExtra("cafeData", cafeList);
                startActivity(intent);
            }
        });

        swipe_icon = findViewById(R.id.swipe_icon);
        if (!cafeData.getIs_diningHall()) {
            swipe_icon.setVisibility(View.GONE);
        }

        customPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        linLayout = findViewById(R.id.linear);

        // Formatting for when eatery is a cafe
        if (!cafeData.getIs_diningHall()) {
            customPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            linLayout.setVisibility(View.VISIBLE);

            View blank = new View(this);
            blank.setBackgroundColor(Color.argb(100, 192,192, 192));
            blank.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    6));
            linLayout.addView(blank);

            TextView categoryText = new TextView(this);
            SpannableString str = new SpannableString("CAFE ITEMS");
            categoryText.setText(str);
            categoryText.setTextSize(18);
            categoryText.setPadding(0, 40,0, 16);
            linLayout.addView(categoryText);
            for (int i = 0; i < cafeData.getCafeInfo().getCafeMenu().size(); i++) {
                TextView mealItemText = new TextView(this);
                mealItemText.setText(cafeData.getCafeInfo().getCafeMenu().get(i));
                mealItemText.setTextSize(14);
                mealItemText.setPadding(0, 0, 0, 8);
                linLayout.addView(mealItemText);
            }
        }

        // Formatting for when eatery is a dining hall and has a menu
        else if (cafeData.getIs_diningHall() && !cafeData.getWeeklyMenu().get(0).toString().equals("[]")) {
            customPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            linLayout.setVisibility(View.GONE);
            setupViewPager(customPager);

            tabLayout.setupWithViewPager(customPager);
        }

        // Formatting for when eatery is a dining hall and is missing a menu
        else {
            customPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            linLayout.setVisibility(View.VISIBLE);

            View blank = new View(this);
            blank.setBackgroundColor(Color.argb(100, 192,192, 192));
            blank.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    6));
            linLayout.addView(blank);

            TextView missingMenuText = new TextView(this);
            SpannableString str = new SpannableString("No Menu Available");
            missingMenuText.setText(str);
            missingMenuText.setTextSize(18);
            missingMenuText.setPadding(0, 40,0, 16);
            linLayout.addView(missingMenuText);
        }
    }

    private void setupViewPager(CustomPager customPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        customPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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
            b.putSerializable("cafeData", cafeData.getWeeklyMenu().get(cafeData.indexOfCurrentDay()));
            MenuFragment f = new MenuFragment();
            f.setArguments(b);
            return f;
        }

        @Override
        public int getCount() {
            int n;
            try {
                n = cafeData.getWeeklyMenu().get(cafeData.indexOfCurrentDay()).size();
            } catch (Exception e) {
                n = 0;
            }
            return n;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return cafeData.getWeeklyMenu().get(cafeData.indexOfCurrentDay()).get(position).getType();
        }
    }

    /**Returns scaled size for images
     * NOTE: borrowed from Android Studio reference**/
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    //NOTE: borrowed from Android Studio reference
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /** Gets name of corresponding picture to cafe**/
    public static String convertName(String str) {
        //Android does not allow image names to begin with a number
        if (str.equals("104West!")) return "west";

        str = str.replaceAll("[&\']", "");
        str = str.replaceAll(" ", "_");
        str = str.replaceAll("é", "e");
        str = str.toLowerCase();
        return str;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
