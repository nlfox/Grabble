package com.example.nlfox.grabble;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.Marker;


/**
 * Created by nlfox on 16-10-25.
 */

public class InfoDialog extends DialogFragment implements View.OnClickListener {
    private Marker marker;

    public boolean setMarker(Marker m) {
        marker = m;
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom_info_contents, container, false);
        getDialog().setTitle("Simple Dialog");

        Button btn = (Button) rootView.findViewById(R.id.buttonCollect);
        btn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        marker.setVisible(false);
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }
}
