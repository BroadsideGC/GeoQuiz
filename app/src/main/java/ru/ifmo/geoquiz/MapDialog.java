package ru.ifmo.geoquiz;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapDialog extends DialogFragment {
    private SupportMapFragment fragment;
    private GoogleMap map;
    private LatLng markerCoordinates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Choose your location");
        return inflater.inflate(R.layout.fragment_map_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            map = fragment.getMap();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        if (markerCoordinates != null) {
            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerCoordinates, 5));
                map.addMarker(new MarkerOptions().position(markerCoordinates));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.map)).commit();
    }

    public GoogleMap getMap() {
        return map;
    }

    public SupportMapFragment getFragment() {
        return fragment;
    }

    public LatLng getMarkerCoordinates() {
        return markerCoordinates;
    }

    public void setMarkerCoordinates(LatLng markerCoordinates) {
        this.markerCoordinates = markerCoordinates;
    }
}
