package com.example.maxapp1.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.maxapp1.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {
    private static final float DEFAULT_ZOOM = 15.0f;
    private GoogleMap myMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(googleMap -> {
                myMap = googleMap;
            });
        }
    }

    public void zoom(double lat, double lon) {
        if (myMap != null) {
            LatLng scoreLatLng = new LatLng(lat, lon);
            myMap.clear();
            myMap.addMarker(new MarkerOptions()
                    .position(scoreLatLng)
                    .title("High score marker"));
            CameraPosition cameraPos = new CameraPosition.Builder()
                    .target(scoreLatLng)
                    .zoom(DEFAULT_ZOOM)
                    .build();
            myMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
        }
    }
}