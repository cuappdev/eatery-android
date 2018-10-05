package com.cornellappdev.android.eatery;

import android.content.Context;
import android.text.format.DateFormat;
import com.cornellappdev.android.eatery.model.EateryModel;
import com.cornellappdev.android.eatery.model.EateryModel.Status;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZonedDateTime;

public class EateryStringsUtil {
  public static int getStatusString(Status status) {
    switch (status) {
      case OPEN:
        return R.string.open;
      case CLOSED:
        return R.string.closed;
      case CLOSING_SOON:
        return R.string.closing_soon;
    }
    return 0;
  }

  public static String getOpeningClosingDescription(Context context, EateryModel eatery) {
    if (eatery.isOpen()) {
      ZonedDateTime closeTime = eatery.getCloseTime();
      if (closeTime != null) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(closeTime.toInstant().toEpochMilli());
        String formattedCloseTime = DateFormat.getTimeFormat(context).format(cal.getTime());
        return context.getString(R.string.closing_at_a, formattedCloseTime);
      }
    } else {
      ZonedDateTime nextOpening = eatery.getNextOpening();
      if (nextOpening != null) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(nextOpening.toInstant().toEpochMilli());
        String formattedNextOpening = DateFormat.getTimeFormat(context).format(cal.getTime());
        String formattedNextDate = DateFormat.getDateFormat(context).format(cal.getTime());
        if (nextOpening.toLocalDate().isEqual(LocalDate.now())) {
          return context.getString(R.string.opening_at_a, formattedNextOpening);
        } else if (nextOpening.toLocalDate().isEqual(LocalDate.now().plusDays(1))) {
          return context.getString(R.string.opening_tomorrow_at_a, formattedNextOpening);
        } else {
          return context.getString(
              R.string.opening_on_a_at_b,
              formattedNextDate,
              formattedNextOpening
          );
        }
      }
    }
    return null;
  }
}