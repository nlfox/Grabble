package com.example.nlfox.grabble;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.maps.model.Marker;

import mbanje.kurt.fabbutton.FabButton;

/**
 * Created by nlfox on 16-10-25.
 */

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
                        Utils.buildAlertBox(getActivity(), () -> new CollectLetterTask().execute()).show();
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
