package com.example.keenan.scanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class loginScreen extends AppCompatActivity {

    public static final String USER_NAME = "John";
    public static final String ADMIN_NEWUSER_PASSWORD = "rt12";
    public static final String AUTH_PASSWORD = "rice1423_j";
    public static final String Email_Extension = "@ricetool.com";

    Button mloginButton;
    EditText mUserNameInput;
    LinearLayout mCreateUserLayout;
    Button mShowCreateNewUser;
    EditText mCreateNewUser;
    EditText mAdminPword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        mloginButton = (Button) findViewById(R.id.button_login);
        mUserNameInput = (EditText) findViewById(R.id.et_userName);
        mCreateUserLayout = (LinearLayout) findViewById(R.id.ll_createNewUser);
        mShowCreateNewUser = (Button) findViewById(R.id.button_showCreateNewUser);
        mCreateNewUser = (EditText) findViewById(R.id.et_userNameCreateNew);
        mAdminPword = (EditText) findViewById(R.id.et_adminCreateNewPword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            buildAlert(currentUser);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCreateUserLayout.setVisibility(View.INVISIBLE);
        mShowCreateNewUser.setVisibility(View.VISIBLE);

    }

    public void signIn(View view) {
        String userName = mUserNameInput.getText().toString();

        mAuth.signInWithEmailAndPassword(userName+Email_Extension, AUTH_PASSWORD)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            loadScannerActivity(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Signin", "signInWithEmail:failure", task.getException());
                            Toast.makeText(loginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loadScannerActivity(FirebaseUser user) {
        Toast.makeText(getApplicationContext(), "Logging in as: "+ user.getEmail(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        //you can add code here to send data..
        startActivity(intent);
    }



    public void showCreateUser(View view) {
        mCreateUserLayout.setVisibility(View.VISIBLE);
        mShowCreateNewUser.setVisibility(View.INVISIBLE);
    }
    //this method is for creating a new user on firebase..
    public void createNewUserLogin(View view) {
        String username;
        username = mCreateNewUser.getText().toString();

        if (mAdminPword.getText().toString().equals(ADMIN_NEWUSER_PASSWORD)) {

            mAuth.createUserWithEmailAndPassword(username + Email_Extension, AUTH_PASSWORD)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                setNewUserDisplayName(user);
                                Toast.makeText(loginScreen.this, "Authentication Successful.", Toast.LENGTH_SHORT).show();
                                loadScannerActivity(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("authFailed", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(loginScreen.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "You must input the admin password to create a new user.", Toast.LENGTH_SHORT).show();
        }

    }
    //this method takes the user's name before the email and sets it as the display name for the user.
    public void setNewUserDisplayName(FirebaseUser user) {
        String displayName;
        String emailId = user.getEmail();
        String[] parts = emailId.split("@");
        displayName = parts[0];

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("profileUpdate", "User profile updated.");
                        }
                    }
                });
    }

    private void buildAlert(final FirebaseUser user) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Is this you?")
                .setMessage("Current user logged in is: " + user.getEmail() + "\nWould you like to login as this user?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        loadScannerActivity(user);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
