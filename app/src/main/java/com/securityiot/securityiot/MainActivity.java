package com.securityiot.securityiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.securityiot.securityiot.model.User;
import com.securityiot.securityiot.ui.AdministratorFragment;
import com.securityiot.securityiot.ui.EditEventFragment;
import com.securityiot.securityiot.ui.LoginActivity;
import com.securityiot.securityiot.ui.ProfileActivity;
import com.securityiot.securityiot.ui.RegistryUsersActivity;
import com.securityiot.securityiot.ui.UserFragment;
import com.securityiot.securityiot.ui.UserUpdateFragment;
import com.securityiot.securityiot.ui.VisitFragment;
import com.securityiot.securityiot.ui.EventsFragment;
import com.securityiot.securityiot.util.SessionHandler;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String IS_GUESS = "IS_GUESS";
    private static final String TAG = "MainActivity";


    private VisitFragment visitFragment;
    private EventsFragment eventsFragment;
    private EditEventFragment editEventFragment;
    private AdministratorFragment administratorFragment;
    private UserUpdateFragment userUpdateFragment;
    private UserFragment userFragment;
    private BottomNavigationView bottomNavigationView;
    private FirebaseDatabase mDb;
    private DatabaseReference databaseReference;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private User user;
    private TextView toolbar;

    boolean isGuess = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getExtras() != null) {
            isGuess = getIntent().getExtras().getBoolean(IS_GUESS, false);
        }
        toolbar = findViewById(R.id.toolbar);

        mProgressBar = findViewById(R.id.progressBar);

        mDb = FirebaseDatabase.getInstance();
        databaseReference = mDb.getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottom_nav_home);
        setBottomNavView();

        if(isGuess){
            setToolbarTitle("Realizar visita");
            setBoldActionBartitle();

            visitFragment = VisitFragment.newInstance(getApplicationContext());
            addOrReplaceFragment(visitFragment, R.id.fragment_container);
        }else{
            showDialog();
            callUserService(new UserServiceCallback() {
                @Override
                public void onUserServiceCallback() {
                    if(user.getRol().equals("Usuario")){
                        setToolbarTitle("Pantalla Usuario");
                        setBoldActionBartitle();

                        hideDialog();
                        userFragment = UserFragment.newInstance(getApplicationContext());
                        addOrReplaceFragment(userFragment, R.id.fragment_container);

                    }else if(user.getRol().equals("Administrador")){
                        setToolbarTitle("Pantalla Administrador");
                        setBoldActionBartitle();
                        hideDialog();
                        administratorFragment = AdministratorFragment.newInstance(getApplicationContext());
                        addOrReplaceFragment(administratorFragment, R.id.fragment_container);

                    }
                }
            });

        }





    }

    private void setToolbarTitle(String title) {
        try {
            toolbar.setText(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void callUserService(final UserServiceCallback userServiceCallback){


        final String uid = mAuth.getCurrentUser().getUid();
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged:success");


                    userServiceCallback.onUserServiceCallback();

                }else{
                    Log.d(TAG, "onAuthStateChanged:failed");
                    Toast.makeText(MainActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Toast.makeText(MainActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onClick(View v)
    {
        Intent miIntent=null;
        switch (v.getId()){
            case R.id.btn_registro_usuarios:
                miIntent=new Intent(MainActivity.this, RegistryUsersActivity.class);
                break;
            case R.id.btn_registro_casas:
                //miIntent=new Intent(MainActivity.this,ConsultarClientesActivity.class);
                break;
            case R.id.btn_register_events:
                eventsFragment = EventsFragment.newInstance(getApplicationContext());
                addOrReplaceFragment(eventsFragment, R.id.fragment_container);
                break;
            case R.id.btn_dar_acceso:
                userUpdateFragment = UserUpdateFragment.newInstance(getApplicationContext());
                addOrReplaceFragment(userUpdateFragment, R.id.fragment_container);
                break;
        }
        if (miIntent!=null){
            startActivity(miIntent);
        }
    }

    private void setBottomNavView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(androidx.appcompat.R.id.icon);
            iconView.setPadding(0,0,0,0);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,0);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            iconView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();


        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }
        return false;
    }

    private interface  UserServiceCallback {
        void onUserServiceCallback();
    }
}
