package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.DiningHallMenuModel;
import com.cornellappdev.android.eatery.model.DiningHallModel;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.MealType;
import com.cornellappdev.android.eatery.model.PaymentMethod;
import com.cornellappdev.android.eatery.network.UriUtil;
import com.cornellappdev.android.eatery.util.EateryStringsUtil;
import com.cornellappdev.android.eatery.util.TimeUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

public class MenuActivity extends AppCompatActivity {
  private AppBarLayout appbar;
  private ImageView brbIcon, swipeIcon;
  private SimpleDraweeView cafeImage;
  private TextView cafeIsOpen, cafeLocation, cafeText;
  private CollapsingToolbarLayout collapsingToolbar;
  private LinearLayout linLayout;
  private EateryModel mEatery;
  private TextView menuText;
  private Toolbar toolbar;

  /**
   * Returns scaled size for images NOTE: borrowed from Android Studio reference*
   */
  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
      int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      final int halfHeight = height / 2;
      final int halfWidth = width / 2;
      // Calculate the largest inSampleSize value that is a power of 2 and keeps both
      // height and width larger than the requested height and width.
      while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
        inSampleSize *= 2;
      }
    }
    return inSampleSize;
  }

  // NOTE: borrowed from Android Studio reference
  public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth,
      int reqHeight) {
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
    mEatery = (EateryModel) intent.getSerializableExtra("cafeInfo");

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
        } else if (isShow) {
          collapsingToolbar.setTitle(" ");
          isShow = false;
        }
      }
    });

    // TODO Remove lite lunch for North Star and Becker?
    // Format string for opening/closing time
    cafeIsOpen = findViewById(R.id.ind_open);
    cafeIsOpen.setText(EateryStringsUtil.getStatusString(mEatery.getCurrentStatus()));
    cafeText = findViewById(R.id.ind_time);
    String openingClosingDescription = EateryStringsUtil
        .getOpeningClosingDescription(this, mEatery);
    if (openingClosingDescription != null) {
      cafeText.setText(openingClosingDescription);
    } else {
      cafeText.setVisibility(View.INVISIBLE);
    }
    cafeLocation = findViewById(R.id.ind_loc);
    cafeLocation.setText(mEatery.getBuildingLocation());
    Uri uri = UriUtil.getImageUri(mEatery);
    cafeImage = findViewById(R.id.ind_image);
    cafeImage.setBackgroundColor(0xFFff0000);
    cafeImage.setImageURI(uri);

    brbIcon = findViewById(R.id.brb_icon);

    if (mEatery.hasPaymentMethod(PaymentMethod.BRB)) {
      brbIcon.setVisibility(View.VISIBLE);
    } else {
      brbIcon.setVisibility(View.GONE);
    }
    swipeIcon = findViewById(R.id.swipe_icon);
    if (mEatery instanceof DiningHallModel) {
      swipeIcon.setVisibility(View.VISIBLE);
    } else {
      brbIcon.setVisibility(View.GONE);
    }

    ViewPager customPager = findViewById(R.id.pager);
    TabLayout tabLayout = findViewById(R.id.tabs);
    linLayout = findViewById(R.id.linear);
    // Formatting for when eatery is a cafe
    if (mEatery instanceof CafeModel) {
      CafeModel cafe = (CafeModel) mEatery;
      customPager.setVisibility(View.GONE);
      tabLayout.setVisibility(View.GONE);
      linLayout.setVisibility(View.VISIBLE);
      View blank = new View(this);
      blank.setLayoutParams(
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

      blank.setElevation(-1);
      linLayout.addView(blank);
      float scale = getResources().getDisplayMetrics().density;
      for (int i = 0; i < cafe.getCafeMenu().size(); i++) {
        TextView mealItemText = new TextView(this);
        mealItemText.setText(cafe.getCafeMenu().get(i));
        mealItemText.setTextSize(14);
        mealItemText.setPadding((int) (16 * scale + 0.5f), (int) (8 * scale + 0.5f), 0,
            (int) (8 * scale + 0.5f));
        linLayout.addView(mealItemText);
        // Add divider if text is not the last item in list
        if (i != cafe.getCafeMenu().size() - 1) {
          View divider = new View(this);
          LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, 1);
          dividerParams.setMargins((int) (15.8 * scale + 0.5f), 0, 0, 0);
          divider.setElevation(-1);
          divider.setLayoutParams(dividerParams);
          linLayout.addView(divider);
        }
      }
    } else if (mEatery instanceof DiningHallModel && ((DiningHallModel) mEatery)
        .getMenuForDay(LocalDate.now(TimeUtil.getInstance().getCornellTimeZone()).getDayOfWeek())
        .numberOfMeals() != 0) {
      // Formatting for when eatery is a dining hall and has a menu
      // TODO Why does the second half of this condition exist/should it be
      // refactored?
      // TODO I honestly am unsure of what its intent was... I tried to honor it
      // though.
      menuText = findViewById(R.id.ind_menu);
      menuText.setVisibility(View.GONE);
      customPager.setVisibility(View.VISIBLE);
      tabLayout.setVisibility(View.VISIBLE);
      linLayout.setVisibility(View.GONE);
      ViewPagerAdapter adapter = new ViewPagerAdapter(getApplicationContext(),
          getSupportFragmentManager());
      customPager.setAdapter(adapter);
      tabLayout.setupWithViewPager(customPager);
    }
  }

  class ViewPagerAdapter extends FragmentPagerAdapter {
    private int mCurrentPosition = -1;

    ViewPagerAdapter(Context context, FragmentManager manager) {
      super(manager);
    }

    // Set menu fragment to first MealModel object
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
      super.setPrimaryItem(container, position, object);
      if (position != mCurrentPosition) {
        if (mCurrentPosition == -1) {
          position = 0;
        }
        Fragment fragment = (Fragment) object;
        if (fragment != null && fragment.getView() != null) {
          mCurrentPosition = position;
        }
      }
    }

    @Override
    public Fragment getItem(int position) {
      Bundle b = new Bundle();
      b.putInt("position", position);
      if (mEatery instanceof DiningHallModel) {
        b.putSerializable("mEatery",
            ((DiningHallModel) mEatery).getMenuForDay(
                ZonedDateTime.now().getDayOfWeek()));
      }
      MenuFragment f = new MenuFragment();
      f.setArguments(b);
      return f;
    }

    @Override
    public int getCount() {
      int n = 0;
      try {
        if (mEatery instanceof DiningHallModel) {
          DayOfWeek current = ZonedDateTime.now().getDayOfWeek();
          n = ((DiningHallModel) mEatery).getMenuForDay(current).numberOfMeals();
        }
      } catch (Exception e) {
        n = 0;
      }
      return n;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      if (mEatery instanceof DiningHallModel) {
        DiningHallMenuModel menu = ((DiningHallModel) mEatery)
            .getMenuForDay(ZonedDateTime.now().getDayOfWeek());
        MealType type = menu.getMeal(position).getType();
        switch (type) {
          case BREAKFAST:
            return getString(R.string.breakfast);
          case LITE_LUNCH:
            getString(R.string.lite_lunch);
          case LUNCH:
            getString(R.string.lunch);
          case DINNER:
            getString(R.string.dinner);
          case BRUNCH:
            getString(R.string.brunch);
        }
      }
      return "";
    }
  }
}
