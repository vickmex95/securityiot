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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.securityiot.securityiot.R;
import com.securityiot.securityiot.model.User;
import com.securityiot.securityiot.util.SessionHandler;

import java.util.UUID;

import static android.text.TextUtils.isEmpty;
import static com.securityiot.securityiot.util.Check.doStringsMatch;


public class RegisterActivity extends AppCompatActivity implements
        View.OnClickListener
{
    private static final String TAG = "RegisterActivity";

    //widgets
    private EditText mEmail, mPassword, mConfirmPassword;
    private ProgressBar mProgressBar;

    //vars
    private FirebaseDatabase mDb;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.btn_register).setOnClickListener(this);
        mDb = FirebaseDatabase.getInstance();
        databaseReference = mDb.getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();


        hideSoftKeyboard();
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private void onAuthSuccess(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail();
        String username = email;
        if (email != null && email.contains("@")) {
            username = email.split("@")[0];
        }

        User user = new User(username, email, "Usuario", true);
        databaseReference.child(firebaseUser.getUid()).setValue(user);

        redirectLoginScreen();
    }


    private void signUp() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (validateForm(email, password)) {
            showDialog();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    hideDialog();
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            return true;
        }
    }



    /**
     * Redirects the user to the login screen
     */
    private void redirectLoginScreen(){
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
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
            case R.id.btn_register:{
                Log.d(TAG, "onClick: attempting to register.");

                //check for null valued EditText fields
                if(isValidEmail(mEmail.getText())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())){

                    //check if passwords match
                    if(doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())){

                        //Initiate registration task
                        signUp();
                    }else{
                        Toast.makeText(RegisterActivity.this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
