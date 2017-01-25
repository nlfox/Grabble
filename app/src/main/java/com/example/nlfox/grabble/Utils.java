package com.example.nlfox.grabble;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by nlfox on 1/22/17.
 */

class Utils {
    static AlertDialog.Builder buildAlertBox(Activity c, Runnable a) {
        return new AlertDialog.Builder(c)
                .setCancelable(false)
                .setTitle("Network Error")
                .setMessage("Network Error")
                .setPositiveButton("Retry", (dialog, which) -> a.run())
                .setNegativeButton("Exit", (dialog, which) -> c.finish());
    }

    static String getLetterScore(String c) {
        int[] arr = new int[]{3, 20, 13, 10, 1, 15, 18, 9, 5, 25, 22, 11, 14, 6, 4, 19, 24, 8, 7, 2, 12, 21, 17, 23, 16, 26};
        Character ct = c.charAt(0);
        return Integer.toString((arr[(ct -'a')]));
    }


}
