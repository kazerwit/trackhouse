package org.trackhouse.trackhouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class Home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        //Setup bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        //Switch to handle bottom navigation
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
                        Intent intent5 = new Intent(Home.this, Settings.class);
                        startActivity(intent5);
                        Log.d(TAG, "Settings navigation from Home");
                        break;
                }
                return false;
            }
        });
    }
}





