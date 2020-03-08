package com.securityiot.securityiot.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.securityiot.securityiot.R;
import com.securityiot.securityiot.model.Rol;
import com.securityiot.securityiot.model.User;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;
import static com.securityiot.securityiot.util.Check.doStringsMatch;

public class RegistryUsersActivity extends AppCompatActivity implements
        View.OnClickListener
{
    private static final String TAG = "RegisterActivity";

    //widgets
    private EditText mEmail, mPassword, mConfirmPassword;
    private ProgressBar mProgressBar;
    private Spinner mRoles;
    private ImageView btnBack;

    //vars
    private ArrayList<String> roles;
    private FirebaseDatabase mDb;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    String[] arrayRoles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry_users);
        btnBack = findViewById(R.id.btn_back_action);

        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        findViewById(R.id.btn_register).setOnClickListener(this);
        mDb = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mRoles = findViewById(R.id.roles);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        hideSoftKeyboard();

        initSpinners();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private void initSpinners() {
        ArrayList<String> rolesList = new ArrayList<>();
        rolesList.add("Administrador");
        rolesList.add("Usuario");
        fillRoles(mRoles, rolesList);

    }


    private void fillRoles(Spinner mRoles, List<String> roles) {
        arrayRoles = getRolesArray(roles);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                R.layout.spinner_item, arrayRoles);
        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mRoles.setAdapter(adapterSpinner);
    }

    private String[] getRolesArray(List<String> roles){
        String[] rolesArr = new String[roles.size() + 1];
        rolesArr[0] = "Selecciona un rol";
        for(int counter = 0; counter < roles.size(); counter++){
            rolesArr[counter+1] = roles.get(counter);
        }
        return rolesArr;
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
        String rol = mRoles.getSelectedItem().toString().trim();

        User user = new User(username, email, rol, true);

        databaseReference.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideDialog();
                Log.d(TAG, "add user successfull");
                resetActivity();
                Toast.makeText(getApplicationContext(), getString(R.string.success_user), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideDialog();
                Log.d(TAG, "add event failed");
                Toast.makeText(getApplicationContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void resetActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    private void signUp() {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (validateForm(email, password)) {
            showDialog();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        onAuthSuccess(task.getResult().getUser());
                    } else {
                        Toast.makeText(RegistryUsersActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
    private void cleanInputs(){
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");
        mEmail.setText("");
        mPassword.setText("");
        mConfirmPassword.setText("");
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
                        Toast.makeText(RegistryUsersActivity.this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegistryUsersActivity.this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private interface RolesCallback {
        void onRolesCallback(List<Rol> roles);
    }
}

