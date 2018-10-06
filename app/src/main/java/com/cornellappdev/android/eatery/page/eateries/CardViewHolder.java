package com.cornellappdev.android.eatery.page.eateries;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.cornellappdev.android.eatery.OnItemClickListener;
import com.cornellappdev.android.eatery.databinding.CardItemBinding;
import com.cornellappdev.android.eatery.databinding.CardTextBinding;
import com.cornellappdev.android.eatery.model.EateryModel;

abstract class CardViewHolder extends RecyclerView.ViewHolder {

  private View mModelView;

  CardViewHolder(View modelView) {
    super(modelView);
    mModelView = modelView;
  }

  public void bind(final EateryModel model, OnItemClickListener<EateryModel> onClickListener) {
    mModelView.setOnClickListener(v -> onClickListener.onItemClick(model));
  }

  static class TextCardViewHolder extends CardViewHolder {
    private final CardTextBinding mBinding;

    TextCardViewHolder(CardTextBinding cardItemBinding) {
      super(cardItemBinding.getRoot());
      mBinding = cardItemBinding;
    }

    @Override
    public void bind(EateryModel model, OnItemClickListener<EateryModel> onClickListener) {
      super.bind(model, onClickListener);
      mBinding.setModel(model);
    }
  }

  static class ImageCardViewHolder extends CardViewHolder {
    private final CardItemBinding mBinding;

    ImageCardViewHolder(CardItemBinding cardItemBinding) {
      super(cardItemBinding.getRoot());
      mBinding = cardItemBinding;
    }

    @Override
    public void bind(EateryModel model, OnItemClickListener<EateryModel> onClickListener) {
      super.bind(model, onClickListener);
      mBinding.setModel(model);
    }
  }
}
