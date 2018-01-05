package org.trackhouse.trackhouse;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        final Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent = new Intent(Welcome.this, Login.class);

                // currentContext.startActivity(activityChangeIntent);

                Welcome.this.startActivity(activityChangeIntent);
            }
        });

        final Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent activityChangeIntent2 = new Intent(Welcome.this, SignUp.class);

                // currentContext.startActivity(activityChangeIntent2);

                Welcome.this.startActivity(activityChangeIntent2);
            }
        });
    }
}
