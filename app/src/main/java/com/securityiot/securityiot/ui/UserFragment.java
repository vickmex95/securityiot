package com.securityiot.securityiot.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.securityiot.securityiot.R;

public class UserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private Context context;

    public UserFragment() {
        // Required empty public constructor
    }



    public static UserFragment newInstance(Context context) {
        UserFragment dialog = new UserFragment();
        dialog.setContext(context);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }


    public void setContext(Context context) {
        this.context = context;
    }
}
