package com.cornellappdev.android.eatery;

import com.cornellappdev.android.eatery.model.EateryModel;

public interface EateryModelFilter<T> {
  boolean filter(EateryModel model, T t);
}
