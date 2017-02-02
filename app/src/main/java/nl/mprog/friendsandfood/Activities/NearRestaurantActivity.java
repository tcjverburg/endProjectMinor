package nl.mprog.friendsandfood.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import nl.mprog.friendsandfood.AsyncTasks.MyAsyncTask;
import nl.mprog.friendsandfood.R;

import static com.facebook.login.widget.ProfilePictureView.TAG;
import static nl.mprog.friendsandfood.Classes.AppConfig.GEOMETRY;
import static nl.mprog.friendsandfood.Classes.AppConfig.LATITUDE;
import static nl.mprog.friendsandfood.Classes.AppConfig.LOCATION;
import static nl.mprog.friendsandfood.Classes.AppConfig.LONGITUDE;
import static nl.mprog.friendsandfood.Classes.AppConfig.NAME;
import static nl.mprog.friendsandfood.Classes.AppConfig.OK;
import static nl.mprog.friendsandfood.Classes.AppConfig.PLACE_ID;
import static nl.mprog.friendsandfood.Classes.AppConfig.STATUS;
import static nl.mprog.friendsandfood.Classes.AppConfig.ZERO_RESULTS;


/**
 * NearRestaurantActivity.java
 * TomVerburg-OwnProject
 *
 * In this Activity, the user is able to select restaurants near to his current location. By clicking
 * on a marker on the map fragment shown, the user is then directed to SelectedRestaurantsActivivy
 * where he can see more information about the selected restaurant.
 *
 * Source:https://www.androidtutorialpoint.com/intermediate/android-map-app-showing-current-location-android/
 * Source: http://androidmastermind.blogspot.nl/2016/06/android-google-maps-with-nearyby-places.html
 */

public class NearRestaurantActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker mCurrLocationMarker;
    private Map<String, String> restaurantMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_restaurant);

        //Views.
        findViewById(R.id.friends_nav).setOnClickListener(this);
        findViewById(R.id.own_reviews_nav).setOnClickListener(this);
        findViewById(R.id.refresh).setOnClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        setMapFragment();
        setColorButton();
    }

    /** Obtain the SupportMapFragment and get notified when the map is ready to be used. */
    public void setMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /** Sets the color of the navigation button of current activity. */
    public void setColorButton() {
        ImageButton Nav = (ImageButton) findViewById(R.id.restaurants_nav);
        int myColor = getResources().getColor(R.color.colorButtonPressed);
        Nav.setBackgroundColor(myColor);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used. If Google Play services is not
     * installed on the device, the user will be prompted to install it inside the
     * SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    /** Builds API client */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /** Sets the LocationRequest settings after connection is made to the API. */
    @Override
    public void onConnected(Bundle bundle) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * After the user location is found or changed, this method is called. It centers the map on
     * the user and generates a query to the Google Places API to get information about nearby
     * restaurants.
     * */
    @Override
    public void onLocationChanged(Location location) {

        Location mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker.
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //Moves map camera to users current location.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Stops location updates, otherwise quota will be
        getJsonResult("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.getLatitude() + "," + location.getLongitude() +
                "&radius=1000&type=restaurant&key=AIzaSyDMXpcFcn3qN59rHEKWLdA2_dA6FeVEnTU");

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    /** Gets the JSON result from the query. */
    public void getJsonResult(String query) {
        MyAsyncTask asyncTask = new MyAsyncTask();
        try {
            String result = asyncTask.execute(query).get();
            JSONObject json = new JSONObject(result);
            parseLocationResult(json);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /** Gets permission from user to get the user location.*/
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    /** Processes the permissions given by the user, and builds the api client. */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /** Processes the query result, so various markers can be set on the map and information
     * is processed, selected and saved for later use. */
    private void parseLocationResult(JSONObject result) {

        String place_id, placeName = null;
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray("results");
            if (result.getString(STATUS).equalsIgnoreCase(OK)) {
                mMap.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    place_id = place.getString(PLACE_ID);
                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LONGITUDE);
                    addMarkers(placeName, place_id, latitude, longitude);
                }
                Toast.makeText(getBaseContext(), jsonArray.length() + " Restaurants found!",
                        Toast.LENGTH_LONG).show();
            } else if (result.getString(STATUS).equalsIgnoreCase(ZERO_RESULTS)) {
                Toast.makeText(getBaseContext(), "No Restaurants found in 1KM radius!!!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }

    /** Adds markers to the map for all restaurants which were found. */
    public void addMarkers(String placeName, String place_id, double latitude, double longitude) {
        restaurantMap.put(placeName, place_id);
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(latitude, longitude);
        markerOptions.position(latLng);
        markerOptions.title(placeName);
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerClickListener(this);
    }

    /** Opens SelectedRestaurantActivtity when a marker is selected and shows information of
     * the selected restaurant. */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        Intent intent = new Intent(NearRestaurantActivity.this, SelectedRestaurantActivity.class);
        intent.putExtra("restaurantName", marker.getTitle());
        intent.putExtra("restaurantID", restaurantMap.get(marker.getTitle()));
        startActivity(intent);
        return false;
    }

    /** On click method for the navigation bar and other buttons.*/
    @Override
    public void onClick(View v) {
        //On click method for the navigation bar and other buttons.
        int i = v.getId();
        if (i == R.id.own_reviews_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), YourReviewsActivity.class);
            startActivity(getNameScreen);
        } else if (i == R.id.refresh) {
            recreate();
        } else if (i == R.id.friends_nav) {
            Intent getNameScreen = new Intent(getApplicationContext(), FriendsFeedActivity.class);
            startActivity(getNameScreen);
        }
    }

}