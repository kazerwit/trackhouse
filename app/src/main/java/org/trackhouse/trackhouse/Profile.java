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

    //only to test git commit, remove later

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    //view objects
    private EditText enterUsername;
    private Button buttonSave;
    private TextView displayUsername;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        //TODO: if user hasn't created a username, prompt them to save one
        //TODO: else just display username and show normal profile

        //get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        //if user is not logged in, return user to Login activity
        if (firebaseAuth.getCurrentUser() == null) {
            //close this activity
            finish();
            startActivity(new Intent(this, Login.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //initialize views
        displayUsername = (TextView) findViewById(R.id.display_username);
        enterUsername = (EditText) findViewById(R.id.enter_username);
        buttonSave = (Button) findViewById(R.id.button_save);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        //display logged in user email or display username if available
        //TODO: display username if available, else display user email
        displayUsername.setText("Welcome " + user.getEmail());

        //add listener to button
        buttonSave.setOnClickListener(this);
    }

    //saves username for this user
    private void saveUserInformation(){
        String username = enterUsername.getText().toString().trim();

        UserInformation userInformation = new UserInformation(username);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(userInformation);

        Toast.makeText(this,"Username created", Toast.LENGTH_SHORT).show();

        //reloads activity once username is saved
        startActivity(new Intent(this,Profile.class));
    }

    @Override
    //saves username once user clicks Save button
    public void onClick(View view){
        //if Save button clicked
        if(view == buttonSave){
            saveUserInformation();
        }
    }
}
