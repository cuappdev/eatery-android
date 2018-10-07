package com.cornellappdev.android.eatery.page.eateries;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.arch.core.util.Function;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cornellappdev.android.eatery.page.EateryTabFragment;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.CampusArea;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.PaymentMethod;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class EateriesFragment extends EateryTabFragment implements OnQueryTextListener {

  private static final EateryModelFilter<CampusArea> AREA_FILTER = (model, area) -> area == model
      .getArea();
  private static final EateryModelFilter<PaymentMethod> PAYMENT_FILTER = EateryModel::hasPaymentMethod;
  private static final EateryModelFilter<String> SEARCH_FILTER = (model, query) -> {
    String cleanQuery = query.trim().toLowerCase();
    boolean remove = true;
    if (model.getNickName().toLowerCase().contains(cleanQuery)) {
      remove = false;
    }

    List<String> mealItems = model.getMealItems();
    for (String mealItem : mealItems) {
      if (mealItem.toLowerCase().contains(cleanQuery)) {
        remove = false;
      }
    }
    return !remove;
  };
  public static boolean searchPressed = false;
  public ChipGroup campusAreaChipGroup;
  public List<EateryModel> eateries; // holds all cafes
  public EateryRecyclerViewAdapter mRecyclerAdapter;
  public RecyclerView mRecyclerView;
  public ChipGroup paymentChipGroup;
  public ProgressBar progressBar;
  public RelativeLayout splash;
  private String mCurrentQuery;

  public void onDataLoaded(List<EateryModel> eateries) {
    this.eateries = new ArrayList<>(eateries);
    Context context = getContext();
    if (context != null) {
      LinearLayoutManager layoutManager = new LinearLayoutManager(
          context.getApplicationContext(),
          RecyclerView.VERTICAL,
          false
      );
      mRecyclerView.setLayoutManager(layoutManager);
      mRecyclerAdapter = new EateryRecyclerViewAdapter(
          getContext().getApplicationContext(),
          eateries,
          EateryModel::compareTo
      );
      mRecyclerView.setAdapter(mRecyclerAdapter);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final View eateriesFragment = inflater.inflate(R.layout.fragment_eateries, container, false);

    mRecyclerView = eateriesFragment.findViewById(R.id.cafe_list);
    paymentChipGroup = eateriesFragment.findViewById(R.id.paymentChipGroup);
    campusAreaChipGroup = eateriesFragment.findViewById(R.id.campusAreaChipGroup);
    mRecyclerView.setNestedScrollingEnabled(false);
    mRecyclerView.setItemViewCacheSize(25);
    mRecyclerView.setDrawingCacheEnabled(true);
    mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    if (eateries != null) {
      Context context = getContext();

      LinearLayoutManager layoutManager = new LinearLayoutManager(
          context.getApplicationContext(),
          RecyclerView.VERTICAL,
          false
      );
      mRecyclerView.setLayoutManager(layoutManager);
      mRecyclerAdapter = new EateryRecyclerViewAdapter(
          getContext().getApplicationContext(),
          eateries,
          EateryModel::compareTo
      );
      mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    Function<PaymentMethod, Function<Boolean, Void>> paymentListener = (paymentMethod) -> (b) -> {
      if (b) {
        mRecyclerAdapter.removeFilters(PAYMENT_FILTER);
        mRecyclerAdapter.addFilter(paymentMethod, PAYMENT_FILTER);
      } else {
        mRecyclerAdapter.removeFilters(PAYMENT_FILTER);
      }
      mRecyclerAdapter.filter();

      return null;
    };

    Function<CampusArea, Function<Boolean, Void>> areaListener = (area) -> (b) -> {
      if (b) {
        mRecyclerAdapter.removeFilters(AREA_FILTER);
        mRecyclerAdapter.addFilter(area, AREA_FILTER);
      } else {
        mRecyclerAdapter.removeFilters(AREA_FILTER);
      }
      mRecyclerAdapter.filter();

      return null;
    };

    paymentChipGroup.setOnCheckedChangeListener((chipGroup, id) -> {
      int checkedId = chipGroup.getCheckedChipId();

      if (checkedId != View.NO_ID && checkedId == id) {
        switch (id) {
          case R.id.brbButton:
            paymentListener.apply(PaymentMethod.BRB).apply(true);
            break;
          case R.id.swipesButton:
            paymentListener.apply(PaymentMethod.SWIPES).apply(true);
            break;
        }
      } else if (checkedId == View.NO_ID) {
        paymentListener.apply(null).apply(false);
      }
    });

    campusAreaChipGroup.setOnCheckedChangeListener((chipGroup, id) -> {
      int checkedId = chipGroup.getCheckedChipId();

      if (checkedId != View.NO_ID && checkedId == id) {
        switch (id) {
          case R.id.northButton:
            areaListener.apply(CampusArea.NORTH).apply(true);
            break;
          case R.id.westButton:
            areaListener.apply(CampusArea.WEST).apply(true);
            break;
          case R.id.centralButton:
            areaListener.apply(CampusArea.CENTRAL).apply(true);
            break;
        }
      } else if (checkedId == View.NO_ID) {
        areaListener.apply(null).apply(false);
      }
    });

    return eateriesFragment;
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    mCurrentQuery = query;
    mRecyclerAdapter.addFilter(query, SEARCH_FILTER);
    mRecyclerAdapter.filter();
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    mRecyclerAdapter.removeFilters(SEARCH_FILTER);
    return onQueryTextSubmit(newText);
  }

  public class SnackBarListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      Intent browser =
          new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cornellappdev.com/apply/"));
      startActivity(browser);
    }
  }
}