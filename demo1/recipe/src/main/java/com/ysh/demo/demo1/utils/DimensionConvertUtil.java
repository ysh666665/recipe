package com.ysh.demo.demo1.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by hasee on 2016/11/25.
 */

public class DimensionConvertUtil {
    private DisplayMetrics metric;
    public DimensionConvertUtil(Activity activity){
        metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
    }

    public float convertToSp(int value){
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metric);
    }
    public float convertToDp(int value){
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metric);
    }

}
