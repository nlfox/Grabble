package com.example.nlfox.grabble;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.net.URL;

import mbanje.kurt.fabbutton.FabButton;

import static android.os.SystemClock.sleep;


/**
 * Created by nlfox on 16-10-25.
 */


import android.app.Activity;
import android.os.Handler;

import mbanje.kurt.fabbutton.FabButton;

public class InfoDialog extends DialogFragment implements View.OnClickListener {
    private Marker marker;
    private View v;
    private View rootView;
    private FabButton btn;
    private String letter;
    private String title;

    public boolean setMarker(Marker m) {
        marker = m;
        return true;
    }

    public boolean setView(View vm) {
        v = vm;
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.custom_info_contents, container, false);
        getDialog().setCancelable(true);
        getDialog().setTitle(marker.getTitle());
        letter = marker.getTag().toString();
        title = marker.getTitle();
        ImageView markerPic = (ImageView) rootView.findViewById(R.id.marker_pic);
        markerPic.setImageResource(getResources().getIdentifier(
                "letter_" + letter,
                "drawable", "com.example.nlfox.grabble"
        ));
        btn = (FabButton) rootView.findViewById(R.id.buttonCollect);
        btn.setOnClickListener(this);

        return rootView;
    }

    private void complete() {
        marker.setVisible(false);
        btn.setIndeterminate(false);
        btn.setProgress(100);
        //button.showProgress(false);
        //sleep(3000);
        final InfoDialog myself = this;
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                getActivity().getFragmentManager().beginTransaction().remove(myself).commit();
                Snackbar snackbar = Snackbar
                        .make(v, "a new letter collected", Snackbar.LENGTH_SHORT);
                snackbar.setActionTextColor(Color.RED);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.DKGRAY);
                snackbar.show();
            }
        };
        handler.postDelayed(r, 1500);
    }

    @Override
    public void onClick(View view) {
        rootView.findViewById(R.id.marker_pic).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
        btn.setIndeterminate(true);
        btn.showProgress(true);
        CollectLetterTask clt = new CollectLetterTask();
        clt.execute();

    }

    private class CollectLetterTask extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... params) {
            try {

                return GrabbleApplication.getAppContext(getActivity().getApplication()).collectPoint(letter, title);
            } catch (Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Network Error")
                                .setMessage("Network Error")
                                .setPositiveButton("Retry", (dialog, which) -> new CollectLetterTask().execute())
                                .setNegativeButton("Exit", (dialog, which) -> getActivity().finish())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                complete();
        }

    }
}
