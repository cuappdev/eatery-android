package com.example.jc.eatery_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jc.eatery_android.Model.CafeteriaModel;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    ImageView cafeImage;
    TextView cafeLoc;
    TextView cafeClosingHours;
    TextView cafeIsOpen;
    LinearLayout linLayout;
    private TabLayout tabLayout;
    private CustomPager customPager;
    ArrayList<CafeteriaModel> cafeList;
    CafeteriaModel cafeData;
    Toolbar toolbar;
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



        //cafeLoc = findViewById(R.id.ind_name);
        //cafeLoc.setText(cafeName);

        cafeName = "@drawable/" + convertName(cafeName);

        cafeList = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("testData");
        cafeData = (CafeteriaModel) intent.getSerializableExtra("cafeInfo");

        cafeIsOpen = findViewById(R.id.ind_open);
        cafeClosingHours = findViewById(R.id.ind_closingHours);

        cafeIsOpen.setText(cafeData.isOpen());
        cafeClosingHours.setText(cafeData.getCloseTime());

        cafeImage = findViewById(R.id.ind_image);
        cafeImage.setBackgroundColor(0xFFff0000);
        int imageRes = getResources().getIdentifier(cafeName, null, getPackageName());
        cafeImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
                imageRes, 400, 400));
        cafeImage.setColorFilter(Color.argb(80, 153, 153, 153));

        customPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        linLayout = findViewById(R.id.linear);

        if (!cafeData.getIs_diningHall()) {
            customPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            linLayout.setVisibility(View.VISIBLE);
            TextView tv2 = new TextView(this);
            tv2.setText("CAFE ITEMS");
            tv2.setTextSize(18);
            tv2.setPadding(0, 0,0, 16);
            linLayout.addView(tv2);
            for (int i = 0; i < cafeData.getCafeInfo().getCafeMenu().size(); i++) {
                TextView tv = new TextView(this);
                tv.setText(cafeData.getCafeInfo().getCafeMenu().get(i));
                tv.setTextSize(14);
                tv.setPadding(0, 0, 0, 8);
                linLayout.addView(tv);
            }
        }

        if (cafeData.getIs_diningHall()) {
            customPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            linLayout.setVisibility(View.GONE);
            setupViewPager(customPager);

            if (cafeData.getIs_diningHall()) {
                tabLayout.setupWithViewPager(customPager);
            }
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
            Log.d("FLOW", "1");
            return f;
        }

        @Override
        public int getCount() {
            int n = 0;
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

    /**Returns scaled size for images**/
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
