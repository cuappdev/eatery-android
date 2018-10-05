package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cornellappdev.android.eatery.CardViewHolder.ImageCardViewHolder;
import com.cornellappdev.android.eatery.databinding.CardItemBinding;
import com.cornellappdev.android.eatery.databinding.CardTextBinding;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainListAdapter extends RecyclerView.Adapter<CardViewHolder> {
  private final int IMAGE = 0;
  private final int TEXT = 1;
  private final Context mContext;
  private List<EateryModel> cafeListFiltered;
  private int mCount;

  MainListAdapter(Context context, int count, List<EateryModel> list) {
    mContext = context;
    mCount = count;
    cafeListFiltered = list;
    // Logcat for Fresco
    Set<RequestListener> requestListeners = new HashSet<>();
    requestListeners.add(new RequestLoggingListener());
    ImagePipelineConfig config =
        ImagePipelineConfig.newBuilder(context).setRequestListeners(requestListeners).build();
    Fresco.initialize(context, config);
    FLog.setMinimumLoggingLevel(FLog.VERBOSE);
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
    final EateryModel eateryModel = cafeListFiltered.get(position);

    inputHolder.bind(eateryModel, eatery -> {
      Intent intent = new Intent(mContext, MenuActivity.class);
      intent.putExtra("cafeInfo", eatery);
      intent.putExtra("locName", eatery.getNickName());
      mContext.startActivity(intent);
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
    return mCount;
  }

  public interface ListAdapterOnClickHandler {
    void onClick(int position, List<EateryModel> list);
  }

}
