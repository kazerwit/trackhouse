package org.trackhouse.trackhouse;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Welcome screen for users who are not already signed in. Displays options to register or sign in.
 */

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#dfdbda"));

        //get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //if user is already logged in they are taken to the HomeActivity activity
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
            finish();
        }

        final Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(WelcomeActivity.this, LoginActivity.class);

                // currentContext.startActivity(activityChangeIntent);

                WelcomeActivity.this.startActivity(activityChangeIntent);
            }
        });

        final Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent2 = new Intent(WelcomeActivity.this, SignUpActivity.class);

                // currentContext.startActivity(activityChangeIntent2);

                WelcomeActivity.this.startActivity(activityChangeIntent2);
            }
        });
    }
}
