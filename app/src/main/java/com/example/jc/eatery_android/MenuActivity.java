package com.example.jc.eatery_android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jc.eatery_android.Model.CafeteriaModel;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    ImageView cafeImage;
    TextView cafeLoc;
    TextView cafeClosingHours;
    TextView cafeIsOpen;
    LinearLayout linLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ArrayList<CafeteriaModel> cafeList;
    CafeteriaModel cafeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        String cafeName = (String) intent.getSerializableExtra("locName");
        cafeLoc = (TextView)findViewById(R.id.ind_name);
        cafeLoc.setText(cafeName);

        cafeName = "@drawable/" + convertName(cafeName);

        cafeList = (ArrayList<CafeteriaModel>) intent.getSerializableExtra("testData");
        cafeData = (CafeteriaModel) intent.getSerializableExtra("cafeInfo");

        cafeIsOpen = findViewById(R.id.ind_open);
        cafeClosingHours = findViewById(R.id.ind_closingHours);

        cafeIsOpen.setText(cafeData.isOpen());
        

        cafeImage = (ImageView) findViewById(R.id.ind_image);
        int imageRes = getResources().getIdentifier(cafeName, null, getPackageName());
        cafeImage.setImageBitmap(decodeSampledBitmapFromResource(getResources(),
                imageRes, 400, 400));


        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        linLayout = (LinearLayout) findViewById(R.id.linear);

        if (!cafeData.getIs_diningHall()) {
            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle b = new Bundle();
            Log.i("TAG 5", cafeData.getCafeInfo().toString());
            b.putSerializable("cafeData", cafeData.getCafeInfo());
            MenuFragment f = new MenuFragment();
            f.setArguments(b);
            ft.replace(R.id.pager, f);
            ft.commit();*/
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            linLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < cafeData.getCafeInfo().getCafeMenu().size(); i++) {
                TextView tv = new TextView(this);
                tv.setText(cafeData.getCafeInfo().getCafeMenu().get(i));
                linLayout.addView(tv);
            }
        }

        if (cafeData.getIs_diningHall()) {
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            linLayout.setVisibility(View.GONE);
            setupViewPager(viewPager);

            if (cafeData.getIs_diningHall()) {
                tabLayout.setupWithViewPager(viewPager);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private Context mContext;

        public ViewPagerAdapter(Context context, FragmentManager manager) {
            super(manager);
            mContext = context;
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
            return cafeData.getWeeklyMenu().get(cafeData.indexOfCurrentDay()).size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return cafeData.getWeeklyMenu().get(cafeData.indexOfCurrentDay()).get(position).getType();
        }

    }

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

    public static String convertName(String str) {
        if (str.equals("104West!")) return "west";

        str = str.replaceAll("[&\']", "");
        str = str.replaceAll(" ", "_");
        str = str.replaceAll("é", "e");
        str = str.toLowerCase();
        return str;
    }
}
