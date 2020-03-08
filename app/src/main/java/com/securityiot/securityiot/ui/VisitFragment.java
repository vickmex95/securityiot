package com.securityiot.securityiot.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.securityiot.securityiot.R;
import com.securityiot.securityiot.model.Event;
import com.securityiot.securityiot.model.Visit;
import com.securityiot.securityiot.util.PinEntryEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class VisitFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = VisitFragment.class.getSimpleName();


    private Context context;
    private PinEntryEditText pinEntry;
    private Button btnEntryPin;
    private EditText edtVisitantName;
    private ProgressBar mProgressBar;
    private ImageView btnBack;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public VisitFragment() {
        // Required empty public constructor
    }

    public static VisitFragment newInstance(Context context) {
        VisitFragment dialog = new VisitFragment();
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
    public static VisitFragment newInstance(String param1, String param2) {
        VisitFragment fragment = new VisitFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Eventos");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit, container, false);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mProgressBar = view.findViewById(R.id.progressBar);
        btnEntryPin = view.findViewById(R.id.btn_entry_pin);
        pinEntry = view.findViewById(R.id.nip_test);
        edtVisitantName = view.findViewById(R.id.edt_visitant_name);

        btnEntryPin.setOnClickListener(this);

        if (pinEntry != null) {
            pinEntry.setCursorVisible(false);
            pinEntry.setOnPinEnteredListener(
                    new PinEntryEditText.OnPinListener() {
                        @Override
                        public void onPinEntered(CharSequence str) {
                        }

                        @Override
                        public void onPinErased() {

                        }
                    });
        }

        btnBack = view.findViewById(R.id.btn_back_action);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public void onClick(View view) {

        callPinEntryService(new EventCallback() {
            @Override
            public void onEventCallback() {
                showDialog();
                DatabaseReference visitasRef = firebaseDatabase.getReference("Visitas");

                SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy', ' hh:mm aaa", Locale.getDefault());
                Date date = new Date();
                String fechaVisita = dateFormat.format(date);

                String visitId = UUID.randomUUID().toString();
                Visit visit = new Visit(visitId, fechaVisita, edtVisitantName.getText().toString(), false);
                visitasRef.push().setValue(visit).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideDialog();
                        Log.d(TAG, "add event successfull");
                        Toast.makeText(getContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideDialog();
                        Log.d(TAG, "add event failed");
                        Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("upload", "success");
            }

        });

    }

    private void callPinEntryService(final EventCallback eventCallback){

        if (pinEntry != null) {
            Query query =
                    databaseReference.child("codigoEvento").equalTo(pinEntry.toString());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "pin successfull");

                    eventCallback.onEventCallback();



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "pin failed");
                    Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();

                }
            });


        }
    }

    private interface  EventCallback {
        void onEventCallback();
    }
}
