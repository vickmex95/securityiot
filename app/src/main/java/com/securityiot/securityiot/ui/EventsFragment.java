package com.securityiot.securityiot.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.securityiot.securityiot.util.Helpers;
import com.securityiot.securityiot.util.PinEntryEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class EventsFragment extends Fragment implements View.OnClickListener, TextWatcher {

    public static final String TAG = EventsFragment.class.getSimpleName();


    private Context context;
    private PinEntryEditText pinEntry;
    private Button btnEntryPin;
    private EditText mEditText;
    private Button btnAddEvent;
    private ProgressBar mProgressBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean validate = false;


    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(Context context) {
        EventsFragment dialog = new EventsFragment();
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
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
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
        return inflater.inflate(R.layout.fragment_events, container, false);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mProgressBar = view.findViewById(R.id.progressBar);

        mEditText = view.findViewById(R.id.edt_event_name);
        btnAddEvent = view.findViewById(R.id.btn_event_send);
        btnAddEvent.setEnabled(false);
        btnAddEvent.setOnClickListener(this);


        mEditText.addTextChangedListener(this);

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Helpers.hideKeyboard(mEditText);
                    handled = true;
                }
                return handled;
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
        callSaveInputsService(new EventCallback() {
            @Override
            public void onEventCallback() {
                Log.d("upload", "success");
            }

        });

    }

    private boolean validateInputs(){
        if(mEditText.getText().length() < 2){
            return false;
        }
        return true;
    }

    private void callSaveInputsService(final EventCallback eventCallback){
        showDialog();

        String id = databaseReference.push().getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy', ' hh:mm aaa", Locale.getDefault());
        Date date = new Date();
        String fechaEvento = dateFormat.format(date);

        int numAleatorio = (int) (Math.random() * 9999) + 1;
        Event event = new Event(id, mEditText.getText().toString(), fechaEvento, numAleatorio);
        databaseReference.push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "add event successfull");
                hideDialog();
                Toast.makeText(getContext(), getString(R.string.success_event), Toast.LENGTH_SHORT).show();
                eventCallback.onEventCallback();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideDialog();
                Log.d(TAG, "add event failed");
                Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(validateInputs()){
            btnAddEvent.setBackground(context.getResources().getDrawable(R.drawable.bg_button_enabled));
            btnAddEvent.setEnabled(true);

        }

    }

    private interface  EventCallback {
        void onEventCallback();
    }
}
