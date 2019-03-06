package com.cornellappdev.android.eatery.util;

public class MoneyUtil {
    public static String toMoneyString(float money){
        String to_string = "$"+money;
        if(money*10%1==0) {
            to_string += "0";
        }
        return to_string;
    }
}
