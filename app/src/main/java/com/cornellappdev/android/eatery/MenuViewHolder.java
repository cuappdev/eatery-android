package com.cornellappdev.android.eatery;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cornellappdev.android.eatery.databinding.ListViewBodyBinding;
import com.cornellappdev.android.eatery.databinding.ListViewHeaderBinding;
import com.cornellappdev.android.eatery.model.EateryModel;

public abstract class MenuViewHolder extends RecyclerView.ViewHolder {
  public MenuViewHolder(@NonNull View itemView) {
    super(itemView);
  }

  public void bind(EateryModel model, OnItemClickListener<EateryModel> onClickListener) {
  }

  public static class HeaderViewHolder extends MenuViewHolder {
    private ListViewHeaderBinding mBinding;

    public HeaderViewHolder(ListViewHeaderBinding binding) {
      super(binding.getRoot());
      mBinding = binding;
    }

    @Override
    public void bind(EateryModel model, OnItemClickListener<EateryModel> onClickListener) {
      super.bind(model, onClickListener);
      mBinding.setModel(model);
    }
  }

  public static class BodyViewHolder extends MenuViewHolder {
    private ListViewBodyBinding mBinding;

    public BodyViewHolder(ListViewBodyBinding binding) {
      super(binding.getRoot());
      mBinding = binding;
    }

    @Override
    public void bind(EateryModel model, OnItemClickListener<EateryModel> onClickListener) {
      super.bind(model, onClickListener);
      mBinding.setModel(model);
    }
  }
}
