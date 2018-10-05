package com.cornellappdev.android.eatery.network;

import android.net.Uri;
import com.cornellappdev.android.eatery.model.EateryModel;

public class UriUtil {

  private static final Uri BASE_IMAGE_URI = Uri.parse(
      "https://raw.githubusercontent.com/cuappdev/assets/master/eatery/eatery-images/"
  );

  public static Uri getImageUri(EateryModel eatery) {
    return BASE_IMAGE_URI
        .buildUpon()
        .appendPath(eatery.getSlug() + ".jpg")
        .build();
  }
}
