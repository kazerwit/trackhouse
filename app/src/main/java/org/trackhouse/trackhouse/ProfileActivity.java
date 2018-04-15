package org.trackhouse.trackhouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * User can view the details of their profile.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {


    // Firebase auth object
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private EditText enterUsername;
    private Button buttonSave;
    private Button buttonSkip;
    private TextView displayUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //if user is not logged in, return user to LoginActivity activity
        if (firebaseAuth.getCurrentUser() == null) {
            //close this activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //create views
        displayUsername = (TextView) findViewById(R.id.display_username);
        enterUsername = (EditText) findViewById(R.id.enter_username);
        buttonSave = (Button) findViewById(R.id.button_save);
        buttonSkip = (Button) findViewById(R.id.button_skip);

        //assigns "user" variable to Firebase logged in user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //display logged in user email
        displayUsername.setText("WelcomeActivity, " + user.getEmail());

        //add listeners to buttons
        buttonSave.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);
    }


    /**
     * Creates user in Firebase "users" table. For now, when a user saves or updates their username
     * here, the location is reset to 0 long/0 lat. Redirects user to SearchActivity activity directly
     * after this action so that location can be saved again.
     * @param userId
     * @param username
     * @param email
     */
    private void saveUserInformation(String userId, String username, String email){
        UserInformation userInformation = new UserInformation(username, email);

        databaseReference.child("users").child(userId).setValue(userInformation);

        Toast.makeText(this,"Username created", Toast.LENGTH_SHORT).show();

        //loads SearchActivity activity once username is updated
        startActivity(new Intent(this,SearchActivity.class));
    }

    @Override
    /**
     * Calls saveUserInformation method if "Save" clicked
     * @param view
     */
    public void onClick(View view){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();
        String username = enterUsername.getText().toString().trim();
        String email = user.getEmail();

        //if "Save" button clicked
        if(view == buttonSave){
            saveUserInformation(userId, username, email);
        }

        if(view == buttonSkip) {
            startActivity(new Intent(this, SearchActivity.class));

        }
    }
}
