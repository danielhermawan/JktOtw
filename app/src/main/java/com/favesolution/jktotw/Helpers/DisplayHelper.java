package com.favesolution.jktotw.Helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Daniel on 11/4/2015 for JktOtw project.
 */
public class DisplayHelper {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static String dotString(String longString) {
        if (longString.length() > 33) {
            return longString.substring(0, 30) + "...";
        } else {
            return longString;
        }
    }
}
