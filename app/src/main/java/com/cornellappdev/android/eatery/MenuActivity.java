package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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

import com.cornellappdev.android.eatery.Model.CafeteriaModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    TextView cafeText;
    SimpleDraweeView cafeImage;
    TextView cafeLoc;
    TextView cafeIsOpen;
    TextView menuText;
    ImageView swipe_icon;
    ImageView brb_icon;
    LinearLayout linLayout;
    private TabLayout tabLayout;
    private CustomPager customPager;
    ArrayList<CafeteriaModel> cafeList;
    CafeteriaModel cafeData;
    Toolbar toolbar;
    AppBarLayout appbar;
    CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        final String cafeName = (String) intent.getSerializableExtra("locName");
        cafeText = findViewById(R.id.ind_cafe_name);
        cafeText.setText(cafeName);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsingToolbarLayout);

        // Shows/hides title depending on scroll offset
        appbar = findViewById(R.id.appbar);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
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
                } else if(isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

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
        cafeIsOpen.setText(cafeData.isOpen());
        if (cafeData.isOpen().equals("Open")) {
            cafeIsOpen.setTextColor(Color.parseColor("#7dd600"));
        } else {
            cafeIsOpen.setTextColor(Color.parseColor("#d82e41"));
        }

        cafeText = findViewById(R.id.ind_time);
        cafeText.setText(cafeData.getCloseTime());

        cafeLoc = findViewById(R.id.ind_loc);
        cafeLoc.setText(cafeData.getBuildingLocation());

        cafeImage = findViewById(R.id.ind_image);
        cafeImage.setBackgroundColor(0xFFff0000);

        String imageLocation =
                "https://raw.githubusercontent.com/cuappdev/assets/master/eatery/eatery-images/"
                        + convertName(cafeName + ".jpg");
        Uri uri = Uri.parse(imageLocation);
        cafeImage.setImageURI(uri);

//        getDirections = findViewById(R.id.ind_direction);
//        getDirections.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(view.getContext(), MapsActivity.class);
//                intent.putExtra("cafeData", cafeList);
//                startActivity(intent);
//            }
//        });

        brb_icon = findViewById(R.id.brb_icon);
        for (String pay : cafeData.getPay_methods()) {
            if (pay.equalsIgnoreCase("Meal Plan - Debit")) {
                brb_icon.setVisibility(View.VISIBLE);
            }
        }

        swipe_icon = findViewById(R.id.swipe_icon);
        if (cafeData.getIs_diningHall()) {
            swipe_icon.setVisibility(View.VISIBLE);
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
            blank.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            blank.setBackgroundColor(Color.parseColor("#ccd0d5"));
            blank.setElevation(-1);
            linLayout.addView(blank);

            float scale = getResources().getDisplayMetrics().density;
            for (int i = 0; i < cafeData.getCafeInfo().getCafeMenu().size(); i++) {
                TextView mealItemText = new TextView(this);
                mealItemText.setText(cafeData.getCafeInfo().getCafeMenu().get(i));
                mealItemText.setTextSize(14);
                mealItemText.setTextColor(Color.parseColor("#de000000"));
                mealItemText.setPadding((int)(16*scale + 0.5f), (int)(8*scale + 0.5f), 0, (int)(8*scale + 0.5f));
                linLayout.addView(mealItemText);

                // Add divider if text is not the last item in list
                if (i != cafeData.getCafeInfo().getCafeMenu().size()-1) {
                    View divider = new View(this);
                    divider.setBackgroundColor(Color.parseColor("#ccd0d5"));
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1);
                    dividerParams.setMargins((int)(15.8*scale + 0.5f), 0, 0, 0);
                    divider.setElevation(-1);
                    divider.setLayoutParams(dividerParams);
                    linLayout.addView(divider);
                }
            }
        }

        // Formatting for when eatery is a dining hall and has a menu
        else if (cafeData.getIs_diningHall() && !cafeData.getWeeklyMenu().get(0).toString().equals("[]")) {
            menuText = findViewById(R.id.ind_menu);
            menuText.setVisibility(View.GONE);
            customPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            linLayout.setVisibility(View.GONE);
            setupViewPager(customPager);
            tabLayout.setupWithViewPager(customPager);
            tabLayout.setTabTextColors(Color.parseColor("#57000000"), Color.parseColor("#4e80bd"));
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
        if (str.equals("104West!.jpg")) return "104-West.jpg";
        if (str.equals("McCormick's.jpg")) return "mccormicks.jpg";
        if (str.equals("Franny's.jpg")) return "frannys.jpg";
        if (str.equals("Ice Cream Cart.jpg")) return "icecreamcart.jpg";
        if (str.equals("Risley Dining Room.jpg")) return "Risley-Dining.jpg";
        if (str.equals("Martha's Express.jpg")) return "Marthas-Cafe.jpg";
        if (str.equals("Bus Stop Bagels.jpg")) return "Bug-Stop-Bagels.jpg";


        str = str.replaceAll("!", "");
        str = str.replaceAll("[&\']", "");
        str = str.replaceAll(" ", "-");
        str = str.replaceAll("é", "e");
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
