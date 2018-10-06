package com.cornellappdev.android.eatery.page.eateries;

import com.cornellappdev.android.eatery.model.EateryModel;

public interface EateryModelFilter<T> {
  boolean filter(EateryModel model, T t);
}
