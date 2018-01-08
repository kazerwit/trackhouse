package org.trackhouse.trackhouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private Button registerButton;
    private EditText emailSignup;
    private EditText passwordSignup;
    private TextView signIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.primaryTextColor));
        setSupportActionBar(toolbar);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        registerButton = (Button) findViewById(R.id.register_button);
        emailSignup = (EditText) findViewById(R.id.email_signup);
        passwordSignup = (EditText) findViewById(R.id.password_signup);
        signIn = (TextView) findViewById(R.id.already_registered);

        registerButton.setOnClickListener(this);
        emailSignup.setOnClickListener(this);
        passwordSignup.setOnClickListener(this);
    }

    private void registerUser(){
        String email = emailSignup.getText().toString().trim();
        String password = passwordSignup.getText().toString().trim();

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

        //if validations are OK
        //we will first show a progress bar
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //user is successfully registered
                            //for now displays a toast and starts Home activity
                            //TO DO - start profile activity instead of Home
                            Toast.makeText(SignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(SignUp.this, Home.class);
                            SignUp.this.startActivity(homeIntent);
                        } else {
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            Toast.makeText(SignUp.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == registerButton){
            registerUser();
        }
        if (view == signIn) {
            Intent loginIntent = new Intent(SignUp.this, Login.class);
            SignUp.this.startActivity(loginIntent);
        }
    }
}
