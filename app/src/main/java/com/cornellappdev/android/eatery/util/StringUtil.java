package com.cornellappdev.android.eatery.util;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;

public class StringUtil {
  private static StringUtil instance;

  public static StringUtil getInstance() {
    if (instance == null) {
      instance = new StringUtil();
    }
    return instance;
  }

  /**
   * Removes URL underlines in a string by replacing URLSpan occurrences by
   * URLSpanNoUnderline objects.
   *
   * Note(lesley): Code is from https://www.evilcodingmonkey.com/2011/06/07
   * /remove-underline-from-android-textview-link/
   */
  public static void removeUnderlines(Spannable p_Text) {
    URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);

    for(URLSpan span:spans) {
      int start = p_Text.getSpanStart(span);
      int end = p_Text.getSpanEnd(span);
      p_Text.removeSpan(span);
      span = new URLSpanNoUnderline(span.getURL());
      p_Text.setSpan(span, start, end, 0);
    }
  }
}

class URLSpanNoUnderline extends URLSpan {
  public URLSpanNoUnderline(String p_Url) {
    super(p_Url);
  }

  public void updateDrawState(TextPaint p_DrawState) {
    super.updateDrawState(p_DrawState);
    p_DrawState.setUnderlineText(false);
  }
}
