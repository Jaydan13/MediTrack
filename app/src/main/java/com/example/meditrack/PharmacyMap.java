package com.example.meditrack;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class PharmacyMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    ImageButton backBtn;
    Button loadPharmacyBtn;
    private final String API_KEY = "AIzaSyAyr_f5V_NCpuVgde1xpAQ68_9Tyh0bmKc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_map);

        ThemeHelper.applyTheme(this);

        backBtn = findViewById(R.id.backBtn);
        loadPharmacyBtn = findViewById(R.id.loadPharmacyBtn);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(PharmacyMap.this, Inventory.class);
            startActivity(intent);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        enableUserLocation();

        loadPharmacyBtn.setOnClickListener(view -> {
            loadNearbyPharmacies();
        });
    }

    private void enableUserLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            if (location != null) {

                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
            }
        });
    }

    private void loadNearbyPharmacies() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

            if (location != null) {
                fetchNearbyPharmacies(location);
            }
        });
    }

    private void fetchNearbyPharmacies(Location location) {

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + location.getLatitude() + "," + location.getLongitude()
                + "&radius=3000" + "&type=pharmacy" + "&key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("MAP_URL", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {

                mMap.clear();

                JSONArray results = response.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {

                    JSONObject obj = results.getJSONObject(i);

                    JSONObject geo = obj.getJSONObject("geometry").getJSONObject("location");

                    String name = obj.getString("name");

                    LatLng pos = new LatLng(geo.getDouble("lat"), geo.getDouble("lng"));

                    mMap.addMarker(new MarkerOptions().position(pos).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }

            } catch (Exception e) {

                Log.e("MAP_ERROR", e.getMessage());
            }
        }, error -> Log.e("MAP_ERROR", error.toString()));

        queue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        }
    }
}