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


}
