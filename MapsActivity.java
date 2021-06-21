package com.example.footballapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.telecom.Connection;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.material.snackbar.Snackbar;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnCompleteListener<Void> {

    GoogleMap mMap;
    LatLng mLatLng;

    ArrayList<LatLng> MarkerPoints;
    ArrayList<Geofence> mGeofenceList;

    private GeofencingClient mGeofencingClient;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    CircleOptions stadiumRange;

    String name = "";
    String servicetype = "";
    String location = "";
    String lat = "";
    String lng = "";
    String rating = "";

    int ratingLevel;

    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;
    private Button setRatingLevel;
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    private final String URL_FOR_GEOFENCES= "http://192.168.0.11/services.php";

    private static final String TAG = "MapsActivity";

    final RxPermissions rxPermissions = new RxPermissions(this);
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    final private int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1;

    static HashMap<String, LatLng> FOOTBALL_AREAS = new HashMap<>();
    static ArrayList<Object> FOOTBALLAREAS = new ArrayList();



    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MapsInitializer.initialize(getApplicationContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }
                    , MY_PERMISSION_ACCESS_COARSE_LOCATION);

        MarkerPoints = new ArrayList<>();
        mGeofenceList = new ArrayList<>();


        mAddGeofencesButton = (Button) findViewById(R.id.addGeofencesButton);
        mRemoveGeofencesButton = (Button) findViewById(R.id.removeGeofencesButton);
        setRatingLevel = (Button) findViewById(R.id.setRatingLevel);

        setRatingLevel.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  popupMenu(view);
                                              }
                                          });



        populateGeofenceList();

        mGeofencePendingIntent = null;
        mGeofencingClient = LocationServices.getGeofencingClient(this);

        getLocation();

        loadLocation();

        parseJSON();




        //GetJSON("http://192.168.0.10/test.php");


    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null)
        {
            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }

    private void popupMenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(this, view);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_ratings, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {

                    case R.id.one :

                        Toast.makeText(MapsActivity.this, "1", Toast.LENGTH_SHORT).show();
                        ratingLevel = 1;

                        Log.d("RatingTest", "Rating: " + ratingLevel);
                        break;

                    case R.id.two :

                        Toast.makeText(MapsActivity.this, "2", Toast.LENGTH_SHORT).show();
                        ratingLevel = 2;

                        Log.d("RatingTest", "Rating: " + ratingLevel);
                        break;

                    case R.id.three :

                        Toast.makeText(MapsActivity.this, "3", Toast.LENGTH_SHORT).show();
                        ratingLevel = 3;

                        Log.d("RatingTest", "Rating: " + ratingLevel);
                        break;

                    case R.id.four :

                        Toast.makeText(MapsActivity.this, "4", Toast.LENGTH_SHORT).show();
                        ratingLevel = 4;

                        Log.d("RatingTest", "Rating: " + ratingLevel);
                        break;

                    case R.id.five :

                        Toast.makeText(MapsActivity.this, "5", Toast.LENGTH_SHORT).show();

                        ratingLevel  = 5;

                        Log.d("RatingTest", "Rating: " + ratingLevel);
                        break;

                }
                return false;
            }
        });

        popupMenu.show();
    }








    //  @Override

    private void loadLocation()
    {
        Log.d("TAGasdasdhasd", "chips11");
       new GetJSON().execute();
        Log.d("TAGasdasdhasd", "chips12");
    }







    // public void GetJSON(final String urlWebservice) {


        class GetJSON extends AsyncTask<Void, String, String> {

            private HashMap<String, LatLng> FOOTBALL_AREAS = new HashMap<>();

            @Override
            protected String doInBackground(Void... voids) {
                Log.d("TAGasdasdhasd", "chips2");

                String json = null;
                try {

                    URL url = new URL("http://192.168.0.11/test.php");

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    InputStream inputStream = con.getInputStream();
                    Log.d("TAGasdasdhasd", "chips3");

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                 //   String json;
                      StringBuilder sb = new StringBuilder();


                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "/n");

                        Log.d("JsonTest2", "Test: " + json);



                    }

                    return sb.toString().trim();


                    //  return sb.toString().trim();
                } catch (Exception ex) {
ex.printStackTrace();
                }



                return json;


            }

            protected void onPostExecute(String json) {
                //super.onPostExecute(s);
                // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

        //   processJSON(json);






                }






        }


    private void processJSON(String json) {
        LatLng latLng;


        Log.d("TAGasdasdhasd", "chips");

        String name = "";
        String lat = "";
        String lng = "";

        try {

            JSONArray jsonArray = new JSONArray(json);
            Log.d("JsonTest3", "Test: " + json);
            String[] stadium = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {


                JSONObject obj = jsonArray.getJSONObject(i);
                name = obj.optString("name");
                //Log.d("StadiumTest", "Test: " + name);

                lat = obj.optString("latitude");
                //Log.d("StadiumTest", "Test: " + lat);
                lng = obj.optString("longitude");
                //Log.d("StadiumTest", "Test: " + lng);


                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lng);

                latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng));

                FOOTBALL_AREAS.put(name, new LatLng(latitude, longitude));
                Log.d("HashMapTest", "Test: " + FOOTBALL_AREAS);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON() {



        StringRequest stringRequest = new StringRequest(URL_FOR_GEOFENCES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LatLng latLng;
                Log.d("HashMapTest37", "Test: " + FOOTBALL_AREAS);


                try {
                    Log.d("HashMapTest38", "Test: " + FOOTBALL_AREAS);
                    JSONArray services = new JSONArray(response);
                    Log.d("HashMapTest38.5", "Test: " + FOOTBALL_AREAS);
                  //  JSONArray jsonArray = jsonArray.getJSONArray("services");
                    Log.d("HashMapTest38.7", "Test: " + FOOTBALL_AREAS);
                    for (int i = 0; i < services.length(); i++) {
                        JSONObject obj = services.getJSONObject(i);
                        Log.d("HashMapTest39", "Test: " + FOOTBALL_AREAS);
                        name = obj.optString("name");
                        //Log.d("StadiumTest", "Test: " + name);
                        Log.d("HashMapTest40", "Test: " + FOOTBALL_AREAS);
                        servicetype = obj.optString("servicetype");

                        location = obj.optString("location");

                        lat = obj.optString("latitude");
                        //Log.d("StadiumTest", "Test: " + lat);
                        lng = obj.optString("longitude");
                        //Log.d("StadiumTest", "Test: " + lng);

                        rating = obj.optString("rating");

                        Log.d("HashMapTest41", "Test: " + FOOTBALL_AREAS);

                        double latitude = Double.parseDouble(lat);
                        double longitude = Double.parseDouble(lng);

                        latLng = new LatLng(latitude, longitude);

                        FOOTBALLAREAS.add(name);
                        FOOTBALLAREAS.add(latLng);
                        FOOTBALLAREAS.add(servicetype);
                        FOOTBALLAREAS.add(location);
                        FOOTBALLAREAS.add(rating);

                        mMap.addMarker(new MarkerOptions().position(latLng));

                        FOOTBALL_AREAS.put(name, new LatLng(latitude, longitude));
                        Log.d("HashMapTest45", "Test: " + FOOTBALL_AREAS);
                        Log.d("ArrayListTest45", "Test: " + FOOTBALLAREAS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }





    //   GetJSON getJSON = new GetJSON();
       // getJSON.execute();


   // }

    public void onMapReady(GoogleMap googleMap)  {
        mMap = googleMap;
        displayMarker();


      //  mMap.addMarker(new MarkerOptions().position(mLatLng).title("T"));
        //  new GetJSON().execute();

        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //        .findFragmentById(R.id.map);
        //  mapFragment.getMapAsync(this);

        LatLng Arsenal = new LatLng(51.555034, -0.108482);
         LatLng AstonVilla = new LatLng(52.509236, -1.884815);
         LatLng Bournemouth = new LatLng(50.744747, -1.841174);
         LatLng Brighton = new LatLng(50.860948, -0.081484);
         LatLng Burnley = new LatLng(53.789183, -2.230195);


         googleMap.addMarker(new MarkerOptions().position(Arsenal).title("Emirates Stadium").snippet("This is where Arsenal play").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
         googleMap.addMarker(new MarkerOptions().position(AstonVilla).title("Villa Park"));
         googleMap.addMarker(new MarkerOptions().position(Bournemouth).title("Dean Court"));
         googleMap.addMarker(new MarkerOptions().position(Brighton).title("AMEX stadium"));
         googleMap.addMarker(new MarkerOptions().position(Burnley).title("Turf Moor"));


        CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(51.555034, -0.108482));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Arsenal, 14F));
        googleMap.moveCamera(point);

       // googleMap.setMapType(googleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Already two locations
                if (MarkerPoints.size() > 1) {

                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured



            }
        });
    }



    private void displayMarker()
    {
       // mMap.clear();
        Log.d("TAG57", "chips17");
        if(mMap == null) return;
        if(mLatLng == null) return;

        Log.d("TAG55", "chips18");

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(mLatLng);


        //mMap.addMarker(new MarkerOptions().position(mLatLng).title("T"));


            mMap.addMarker(markerOption);
            mMap.addMarker(new MarkerOptions().position(mLatLng).title(""));




    }


    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    public void addGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
    }


    private void addGeofences() {
        if (!checkPermissions()) {
            showSnackbar("insufficient_permissions");
            return;
        }

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(this);



    }

    public void removeGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
            requestPermissions();
            return;
        }
        removeGeofences();

    }

    private void removeGeofences() {
        if (!checkPermissions()) {
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);



    }

    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }



    private void populateGeofenceList() {

        for (Map.Entry<String, LatLng> entry : Geofences.FOOTBALL_AREAS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Geofences.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Geofences.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Geofences.GEOFENCES_ADDED_KEY, false);
    }

    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Geofences.GEOFENCES_ADDED_KEY, added)
                .apply();
        Log.d("UpdateGeofence: ", Geofences.GEOFENCES_ADDED_KEY);
    }

    private void performPendingGeofenceTask() {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);

        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }



    private void setButtonsEnabledState() {
        if (getGeofencesAdded()) {
            mAddGeofencesButton.setEnabled(false);
            mRemoveGeofencesButton.setEnabled(true);
        } else {
            mAddGeofencesButton.setEnabled(true);
            mRemoveGeofencesButton.setEnabled(false);
        }
    }

    private void setRatingLevel()
    {

    }

    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }










    }

