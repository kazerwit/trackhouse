package org.trackhouse.trackhouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;

/**
 * This is where a new user will sign up to use the app.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton;
    private EditText emailSignup, usernameSignup, passwordSignup, passwordSignupReenter;
    private TextView signIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //create views and set OnClickListeners
        progressDialog = new ProgressDialog(this);
        registerButton = (Button) findViewById(R.id.register_button);
        emailSignup = (EditText) findViewById(R.id.email_signup);
        passwordSignup = (EditText) findViewById(R.id.password_signup);
        passwordSignupReenter = (EditText) findViewById(R.id.password_reenter);
        signIn = (TextView) findViewById(R.id.already_registered);
        registerButton.setOnClickListener(this);
        emailSignup.setOnClickListener(this);
        passwordSignup.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Registers a new user with email and password in Firebase. Validates inputs and displays
     * errors as needed via toasts. Shows progress bar when validations are OK.
     */
    private void registerUser(){
        String email = emailSignup.getText().toString().trim();
        String password = passwordSignup.getText().toString().trim();
        String password2 = passwordSignupReenter.getText().toString().trim();

        //validate all fields
        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();

            //stops function from executing
            return;
        }

        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();

            //stops function from executing
            return;
        }

        if(password.equals(password2)) {
            //passwords match
        } else {
            Toast.makeText(this,"Passwords do not match, please try again", Toast.LENGTH_SHORT).show();

            //stops function from executing
            return;
        }


        //if validations are OK we will first show a progress bar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        /**
         * Calls Firebase method to create new user in db with email and password
         * @param email
         * @param password
         */
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User is successfully registered. Displays toast message and starts ProfileActivity activity
                            Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent profileIntent = new Intent(SignUpActivity.this, ProfileActivity.class);
                            SignUpActivity.this.startActivity(profileIntent);

                        } else {
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Toast.makeText(SignUpActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }

    /**
     * Calls method registerUser() if "Register" button is clicked
     * Starts LoginActivity activity if "LoginActivity" is selected
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view == registerButton){
            registerUser();
        }
        if (view == signIn) {
            Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            SignUpActivity.this.startActivity(loginIntent);
        }
    }
}
