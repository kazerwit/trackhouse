package org.trackhouse.trackhouse;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.provider.Settings;
import java.util.Locale;


public class Home extends AppCompatActivity {

    //TODO: Add location updates, save instance state, add onResume
    //TODO: method to start location updates

    private static final String TAG = "HomeActivity";
    private TextView greeting, locationText, latitudeText, longitudeText;
    private String mLatitudeLabel;
    private String mLongitudeLabel;

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //create location variables
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Represents a geographical location
     */
    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        Log.d(TAG, "onCreate Successful");

        //setup bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        //setup views
        greeting = (TextView) findViewById(R.id.display_username);
        locationText = (TextView) findViewById(R.id.display_location);
        latitudeText = (TextView) findViewById(R.id.display_latitude);
        longitudeText = (TextView) findViewById(R.id.display_longitude);
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);


        //get firebase auth instance and create user variable based on logged in user
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //get fused location provider client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //display logged in user email
        greeting.setText(user.getEmail());

        //handles bottom navigation selection
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        // User selects "Home" item
                        Intent intent = new Intent(Home.this, Home.class);
                        startActivity(intent);
                        break;

                    case R.id.navigation_listen:
                        // User selects "Listen" item
                        Intent intent2 = new Intent(Home.this, Listen.class);
                        startActivity(intent2);
                        break;

                    case R.id.navigation_favorites:
                        // User selects "Favorites" item
                        Intent intent4 = new Intent(Home.this, Favorites.class);
                        startActivity(intent4);
                        break;

                    case R.id.navigation_settings:
                        // User selects "Settings" item
                        Intent intent5 = new Intent();
                            intent5.setClassName("org.trackhouse.trackhouse", "org.trackhouse.trackhouse.Settings");
                            startActivity(intent5);
                        Log.d(TAG, "Settings navigation from Home");
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Check if permissions have been given and request them if not
     */
    @Override
    public void onStart() {
        super.onStart();
        //TODO: Add code so that this only checks for Marshmallow release and up?
        if (!checkPermissions()) {
            requestPermissions();
            Log.d(TAG, "Requested permission if none given");
        } else {
            getLastLocation();
            Log.d(TAG, "Called getLastLocation");
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userId = user.getUid();
                            mLastLocation = task.getResult();

                            latitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    mLatitudeLabel,
                                    mLastLocation.getLatitude()));
                            longitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    mLongitudeLabel,
                                    mLastLocation.getLongitude()));
                            Log.d(TAG, "Location values saved");
                            Double latitude = mLastLocation.getLatitude();
                            Double longitude = mLastLocation.getLongitude();
                            updateUserInformation(userId, latitude, longitude);
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            Log.d(TAG, "No location was detected");
                            Toast.makeText(getApplicationContext(), "No Location Detected", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Saves user location to firebase. This updates the latitude and longitude in the db
     * @param userId
     * @param latitude
     * @param longitude
     */
    private void updateUserInformation(String userId, Double latitude, Double longitude){

        databaseReference.child("users").child(userId).child("latitude").setValue(latitude);
        databaseReference.child("users").child(userId).child("longitude").setValue(longitude);

        Toast.makeText(this,"Location updated", Toast.LENGTH_SHORT).show();

    }


    /**
     * Return the current state of the permissions needed.
     * @return permissionState
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "Permission state returned as " + permissionState);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(Home.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
        Log.d(TAG, "Permission request started");
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "request permission with provider rationale");

        // Provide an additional rationale to the user.
        if (shouldProvideRationale) {
            Log.d(TAG, "Displaying permission rationale to provide additional context.");

            Toast.makeText(getApplicationContext(), "Location permission is needed for core functionality",
                    Toast.LENGTH_LONG).show();
                    startLocationPermissionRequest();

        } else {
            Log.d(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
            Log.d(TAG, "Started location permission request");
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.d(TAG, "User interaction was cancelled for permission request code");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Log.d(TAG,"getLastLocation called");
                getLastLocation();
            } else {
                Log.d(TAG, "Permission was denied");
                // Permission denied.

                // Notify the user via a Toast that they have rejected a core permission for the
                // app, which makes the Activity useless.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Toast.makeText(getApplicationContext(), "Permission was denied, but is needed for core functionality",
                        Toast.LENGTH_LONG).show();

                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        };
            }
        }
    }
}





