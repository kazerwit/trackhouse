package org.trackhouse.trackhouse;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.provider.Settings;
import java.util.Locale;


public class Home extends AppCompatActivity {

    //TODO: Remove location services from activity

    private static final String TAG = "HomeActivity";
    private TextView greeting;


    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate Successful");

        //setup bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        //setup views
        greeting = (TextView) findViewById(R.id.display_username);


        //get firebase auth instance and create user variable based on logged in user
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        //display logged in user email
        greeting.setText(user.getEmail());

        //handles bottom navigation selection
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        // User selects "Home" item
                        Intent intent = new Intent(Home.this, Reddit.class);
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
}





