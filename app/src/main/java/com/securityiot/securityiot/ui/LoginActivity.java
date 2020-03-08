package com.securityiot.securityiot.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.securityiot.securityiot.BaseActivity;
import com.securityiot.securityiot.MainActivity;
import com.securityiot.securityiot.R;
import com.securityiot.securityiot.model.User;
import com.securityiot.securityiot.util.SessionHandler;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends BaseActivity implements
        View.OnClickListener
{

    private static final String TAG = "LoginActivity";
    private FirebaseDatabase mDb;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private TextView toolbar;

    //Firebase

    // widgets
    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mProgressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        setToolbarTitle("Iniciar Sesión");
        setBoldActionBartitle();

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.btn_guess).setOnClickListener(this);
        findViewById(R.id.btn_register_principal).setOnClickListener(this);

        hideSoftKeyboard();

        mAuth = FirebaseAuth.getInstance();


        mDb = FirebaseDatabase.getInstance();
        databaseReference = mDb.getReference("Users");
    }

    private void setToolbarTitle(String title) {
        try {
            toolbar.setText(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    private void onAuthSuccess(FirebaseUser firebaseUser) {



        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        myIntent.putExtra("IS_GUESS", false);
        startActivity(myIntent);
    }

    private void signIn() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (validateForm(email, password)) {
            showDialog();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    hideDialog();
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else if (TextUtils.isEmpty(password)) {
            return false;
        } else {
            mEmail.setError(null);
            mPassword.setError(null);
            return true;
        }
    }




    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.email_sign_in_button:{
               signIn();
               break;
            }
            case R.id.btn_register_principal:{
                Intent myIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(myIntent);
                break;
            }
            case R.id.btn_guess:{
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                myIntent.putExtra("IS_GUESS", true);
                startActivity(myIntent);
                break;
            }
        }
    }
}