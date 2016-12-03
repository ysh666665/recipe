package com.ysh.demo.demo1.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by hasee on 2016/11/23.
 */

public class Toastutil {
    private static Toast toast;

    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_LONG);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
