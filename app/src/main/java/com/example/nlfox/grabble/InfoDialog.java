package com.example.nlfox.grabble;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.Marker;


/**
 * Created by nlfox on 16-10-25.
 */

public class InfoDialog extends DialogFragment implements View.OnClickListener {
    private Marker marker;
    private View v;

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
        View rootView = inflater.inflate(R.layout.custom_info_contents, container, false);
        getDialog().setTitle(marker.getTitle());

        Button btn = (Button) rootView.findViewById(R.id.buttonCollect);
        btn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        marker.setVisible(false);
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        Snackbar snackbar = Snackbar
                .make(v, "a new letter collected", Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        snackbar.show();
    }
}
