package org.trackhouse.trackhouse;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class Profile extends AppCompatActivity implements View.OnClickListener {


    // Firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //view objects
    private EditText enterUsername;
    private Button buttonSave;
    private Button buttonSkip;
    private TextView displayUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // Get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        // If user is not logged in, return user to Login activity
        if (firebaseAuth.getCurrentUser() == null) {
            //close this activity
            finish();
            startActivity(new Intent(this, Login.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        displayUsername = (TextView) findViewById(R.id.display_username);
        enterUsername = (EditText) findViewById(R.id.enter_username);
        buttonSave = (Button) findViewById(R.id.button_save);
        buttonSkip = (Button) findViewById(R.id.button_skip);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Display logged in user email
        displayUsername.setText("Welcome, " + user.getEmail());

        //add listener to button
        buttonSave.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);
    }


    // Creates user in database with userId, username, and email
    private void saveUserInformation(String userId, String username, String email){
        UserInformation userInformation = new UserInformation(username, email);

        databaseReference.child("users").child(userId).setValue(userInformation);

        Toast.makeText(this,"Username created", Toast.LENGTH_SHORT).show();

        //reloads activity once username is saved
        startActivity(new Intent(this,Profile.class));
    }

    @Override
    // View handling for buttons
    public void onClick(View view){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();
        String username = enterUsername.getText().toString().trim();
        String email = user.getEmail();

        //if Save button clicked
        if(view == buttonSave){
            saveUserInformation(userId, username, email);
        }

        if(view == buttonSkip) {
            startActivity(new Intent(this, Home.class));

        }
    }
}
