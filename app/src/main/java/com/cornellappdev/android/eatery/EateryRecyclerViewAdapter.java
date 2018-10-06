package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import com.cornellappdev.android.eatery.CardViewHolder.ImageCardViewHolder;
import com.cornellappdev.android.eatery.databinding.CardItemBinding;
import com.cornellappdev.android.eatery.databinding.CardTextBinding;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EateryRecyclerViewAdapter extends RecyclerView.Adapter<CardViewHolder> {
  private static final int IMAGE = 0;
  private static final int TEXT = 1;
  private final Context mContext;
  private final List<EateryModel> mEateries;
  private Comparator<EateryModel> mComparator;
  private final SortedList<EateryModel> mSortedList = new SortedList<>(EateryModel.class,
      new SortedList.Callback<EateryModel>() {
        @Override
        public int compare(EateryModel a, EateryModel b) {
          return mComparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
          notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
          notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
          notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
          notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(EateryModel oldModel, EateryModel newModel) {
          return oldModel.equals(newModel);
        }

        @Override
        public boolean areItemsTheSame(EateryModel a, EateryModel b) {
          return a.getId() == b.getId();
        }
      });
  private Map<EateryModelFilter<?>, List<Object>> mFilterValues = new HashMap<>();

  EateryRecyclerViewAdapter(Context context, List<EateryModel> list,
      Comparator<EateryModel> comparator) {
    mComparator = comparator;
    mContext = context;
    mEateries = new ArrayList<>(list);

    Collections.sort(mEateries, EateryModel::compareTo);
    for (EateryModel m : mEateries) {
      mSortedList.add(m);
    }
    // Logcat for Fresco
    Set<RequestListener> requestListeners = new HashSet<>();
    requestListeners.add(new RequestLoggingListener());
    ImagePipelineConfig config =
        ImagePipelineConfig.newBuilder(context).setRequestListeners(requestListeners).build();
    Fresco.initialize(context, config);
    FLog.setMinimumLoggingLevel(FLog.VERBOSE);
  }

  public <T> void removeFilter(@NonNull T t, EateryModelFilter<T> filter) {
    List<Object> objs = mFilterValues.get(filter);
    if (objs != null) {
      objs.remove(t);
    }
  }

  public <T> void removeFilters(EateryModelFilter<T> filter) {
    mFilterValues.put(filter, new ArrayList<>());
  }

  public <T> void addFilter(@NonNull T t, EateryModelFilter<T> filter) {
    List<Object> objs = mFilterValues.get(filter);
    if (objs == null) {
      objs = new ArrayList<>();
    }
    objs.add(t);
    mFilterValues.put(filter, objs);
  }

  public void filter() {
    List<EateryModel> filtered = new ArrayList<>();
    // TODO Cleanup this raw access.
    for (int i = mEateries.size() - 1; i >= 0; i--) {
      final EateryModel model = mEateries.get(i);
      boolean keep = true;
      for (Entry<EateryModelFilter<?>, List<Object>> retainedFilter : mFilterValues
          .entrySet()) {
        for (Object obj : retainedFilter.getValue()) {
          EateryModelFilter filter = retainedFilter.getKey();
          @SuppressWarnings("unchecked")
          boolean toKeep = filter.filter(model, obj);
          keep = toKeep;
        }

        if (!keep) {
          break;
        }
      }
      if (keep) {
        filtered.add(model);
      }
    }
    replaceAll(filtered);
  }

  public void reset() {
    replaceAll(mEateries);
  }

  public void removeAll(List<EateryModel> models) {
    mSortedList.beginBatchedUpdates();
    for (EateryModel model : models) {
      mSortedList.remove(model);
    }
    mSortedList.endBatchedUpdates();
  }

  public void replaceAll(List<EateryModel> models) {
    mSortedList.beginBatchedUpdates();
    for (int i = mSortedList.size() - 1; i >= 0; i--) {
      final EateryModel model = mSortedList.get(i);
      if (!models.contains(model)) {
        mSortedList.remove(model);
      }
    }
    mSortedList.addAll(models);
    mSortedList.endBatchedUpdates();
  }

  /**
   * Set view to layout of CardView
   */
  @Override
  public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View view;
    final int layoutId;
    CardViewHolder viewHolder = null;
    LayoutInflater inflator = LayoutInflater.from(mContext);

    switch (viewType) {
      case IMAGE:
        layoutId = R.layout.card_item;
        view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        CardItemBinding cardItemBinding = CardItemBinding.inflate(inflator, parent, false);
        viewHolder = new ImageCardViewHolder(cardItemBinding);
        view.setFocusable(true);
        break;
      case TEXT:
        layoutId = R.layout.card_text;
        view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        CardTextBinding cardTextBinding = CardTextBinding.inflate(inflator, parent, false);
        viewHolder = new CardViewHolder.TextCardViewHolder(cardTextBinding);
        view.setFocusable(true);
        break;
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(CardViewHolder inputHolder, int position) {
    final EateryModel eateryModel = mSortedList.get(position);
    inputHolder.bind(eateryModel, eatery -> {
      Intent intent = new Intent(mContext.getApplicationContext(), MenuActivity.class);
      intent.putExtra("cafeInfo", eatery);
      intent.putExtra("locName", eatery.getNickName());
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mContext.getApplicationContext().startActivity(intent);
    });
  }

  @Override
  public int getItemViewType(int position) {
    if (!MainActivity.searchPressed) {
      return IMAGE;
    } else {
      return TEXT;
    }
  }

  @Override
  public int getItemCount() {
    return mSortedList.size();
  }
}
