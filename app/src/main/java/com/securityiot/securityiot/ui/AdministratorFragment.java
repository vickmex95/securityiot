package com.securityiot.securityiot.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.securityiot.securityiot.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdministratorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdministratorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdministratorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private Context context;

    public AdministratorFragment() {
        // Required empty public constructor
    }



    public static AdministratorFragment newInstance(Context context) {
        AdministratorFragment dialog = new AdministratorFragment();
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
        return inflater.inflate(R.layout.fragment_administrator, container, false);
    }


    public void setContext(Context context) {
        this.context = context;
    }
}
