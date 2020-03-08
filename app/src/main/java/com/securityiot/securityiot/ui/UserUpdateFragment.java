package com.securityiot.securityiot.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.securityiot.securityiot.R;
import com.securityiot.securityiot.util.PinEntryEditText;

public class UserUpdateFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = UserUpdateFragment.class.getSimpleName();


    private Context context;
    private Button btnUpdate;
    private ProgressBar mProgressBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean statusUser = false;

    public UserUpdateFragment() {
        // Required empty public constructor
    }

    public static UserUpdateFragment newInstance(Context context) {
        UserUpdateFragment dialog = new UserUpdateFragment();
        dialog.setContext(context);
        return dialog;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserUpdateFragment newInstance(String param1, String param2) {
        UserUpdateFragment fragment = new UserUpdateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Acceso");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_update, container, false);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        btnUpdate = view.findViewById(R.id.btn_update_user);
        btnUpdate.setOnClickListener(this);
        mProgressBar = view.findViewById(R.id.progressBar);


    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }


    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public void onClick(View view) {


        callUpdateUserService(new UserUpdateCallback() {
            @Override
            public void onUserUpdateCallback() {
                Log.d("upload", "success");
            }

        });

    }

    private void callUpdateUserService(final UserUpdateCallback userUpdateCallback){
        showDialog();
        databaseReference.setValue(false);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "update event successfull");
                hideDialog();
                Toast.makeText(getContext(), getString(R.string.update_event), Toast.LENGTH_SHORT).show();
                userUpdateCallback.onUserUpdateCallback();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "update event failed");
                hideDialog();
                Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });




    }

    private interface  UserUpdateCallback {
        void onUserUpdateCallback();
    }
}
