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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditEventFragment extends Fragment implements View.OnClickListener, TextWatcher {

    public static final String TAG = EditEventFragment.class.getSimpleName();


    private Context context;
    private PinEntryEditText pinEntry;
    private Button btnEntryPin;
    private EditText mEditText;
    private Button btnUpdate;
    private Button btnDelete;
    private Button btnFind;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private boolean validate = false;
    private Event event;


    public EditEventFragment() {
        // Required empty public constructor
    }

    public static EditEventFragment newInstance(Context context) {
        EditEventFragment dialog = new EditEventFragment();
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
    public static EditEventFragment newInstance(String param1, String param2) {
        EditEventFragment fragment = new EditEventFragment();
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
        return inflater.inflate(R.layout.fragment_edit_events, container, false);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mEditText = view.findViewById(R.id.edt_name);
        btnUpdate = view.findViewById(R.id.btn_update);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnFind = view.findViewById(R.id.btn_find);
        btnFind.setEnabled(true);
        btnFind.setOnClickListener(this);

        btnUpdate.setEnabled(false);
        btnUpdate.setOnClickListener(this);
        btnDelete.setEnabled(false);
        btnDelete.setOnClickListener(this);


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

    public void setContext(Context context) {
        this.context = context;
    }


    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_find:
                callFindEvent(new FindEventCallback() {
                    @Override
                    public void onFindEventCallback() {
                        Log.d("upload", "success");
                    }

                });
                break;
            case R.id.btn_update:
                callUpdateEvent(new UpdateEventCallback() {
                    @Override
                    public void onUpdateEventCallback() {
                        Log.d("upload", "success");
                    }

                });
                break;
            case R.id.btn_delete:
                callDeleteEvent(new DeleteEventCallback() {
                    @Override
                    public void onDeleteEventCallback() {
                        Log.d("upload", "success");
                    }

                });
                break;
        }

    }

    private boolean validateInputs(){
        if(mEditText.getText().length() < 2){
            return false;
        }
        return true;
    }

    private void callFindEvent(final FindEventCallback findEventCallback) {

        final String eventInput = mEditText.getText().toString();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                List<Event> events = new ArrayList<>();
                Event eventValue = dataSnapshot.getValue(Event.class);

                String eventId = eventValue.getEventId();
                String nombre = eventValue.getNombre();
                String fechaCreacion = eventValue.getFechaCreacion();
                int codeEvent = eventValue.getCodigoEvento();
                Event event = new Event(eventId, nombre, fechaCreacion, codeEvent);
                events.add(event);

                for (Event event1 : events) {
                    if (event1.getNombre().equals(eventInput)){

                        btnUpdate.setBackground(context.getResources().getDrawable(R.drawable.bg_button_enabled));
                        btnDelete.setBackground(context.getResources().getDrawable(R.drawable.bg_button_enabled));
                        findEventCallback.onFindEventCallback();
                    }
                    else {
                        Toast.makeText(getContext(), "El evento no existe", Toast.LENGTH_LONG).show();
                        limpiar();
                    }
                }
                Toast.makeText(getContext(), "El evento no existe", Toast.LENGTH_LONG).show();
                limpiar();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void callUpdateEvent(final UpdateEventCallback updateEventCallback){


        databaseReference.child(event.getEventId()).setValue(event);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "update event successfull");
                Toast.makeText(getContext(), getString(R.string.update_event), Toast.LENGTH_SHORT).show();
                updateEventCallback.onUpdateEventCallback();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "update event failed");
                Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void callDeleteEvent(final DeleteEventCallback deleteEventCallback){


        databaseReference.child(event.getEventId());

        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "delete event successfull");
                Toast.makeText(getContext(), getString(R.string.delete_event), Toast.LENGTH_SHORT).show();
                deleteEventCallback.onDeleteEventCallback();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "delete event failed");
                Toast.makeText(getContext(), getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void limpiar() {
        mEditText.setText("");
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

            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);

        }

    }

    private interface  FindEventCallback {
        void onFindEventCallback();
    }

    private interface  UpdateEventCallback {
        void onUpdateEventCallback();
    }

    private interface  DeleteEventCallback {
        void onDeleteEventCallback();
    }
}
