package co.matthewfrost.taskmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.app.Fragment;
import android.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.UUID;

import co.matthewfrost.taskmanager.databinding.ActivityTaskDialogBinding;

public class TaskDialog extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Task currentTask;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseApp app;
    DialogFragment dateDialog;
    DialogFragment timeDialog;
    TextView addressText;
    RelativeLayout locationGroup;
    GoogleApiClient mGoogleApiClient;
    Place selectedPlace;
    Geofence geofence;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent i = getIntent();
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        ref = database.getReference("Tasks/" + i.getStringExtra("uid") );
        currentTask = (Task) i.getSerializableExtra("task");
        final boolean isNew = i.getBooleanExtra("isNew", true);
        if(currentTask == null){
            currentTask = new Task();
        }

        ActivityTaskDialogBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_task_dialog);
        binding.setTask(currentTask);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);





        Button cancel = (Button) findViewById(R.id.dialogCancel);
        Button OK = (Button) findViewById(R.id.dialogOK);
        addressText = (TextView) findViewById(R.id.addressText);
        locationGroup = (RelativeLayout) findViewById(R.id.locationGroup);
        final CheckBox urgent = (CheckBox) findViewById(R.id.dialogUrgent);
        final TextView endDate = (TextView) findViewById(R.id.endDate);
        final TextView endTime = (TextView) findViewById(R.id.endTime);
        final Switch target = (Switch) findViewById(R.id.target);
        final Switch location = (Switch) findViewById(R.id.location);
        final TextView txtTarget = (TextView) findViewById(R.id.dialogEndDate);


        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isurgent;
                isurgent = urgent.isChecked();
                if (isurgent) {
                    currentTask.setUrgency(1000);
                } else {
                    currentTask.setUrgency(100);
                }
                if(target.isChecked()) {
                    if (currentTask.getHasTarget()) {

                        if (currentTask.getNotificationID() == 0) {
                            currentTask.setNotificationID((int) System.currentTimeMillis());
                        }
                    }
                }
                else{
                    currentTask.setHasTarget(false);
                }

                if(location.isChecked()) {
                    if (currentTask.getHasLocation() && selectedPlace != null) {
                        LatLng placeLatLng;
                        placeLatLng = selectedPlace.getLatLng();
                        currentTask.setPlaceID(selectedPlace.getId());
                        currentTask.setLat(placeLatLng.latitude);
                        currentTask.setLongitude(placeLatLng.longitude);
                        geofence = new Geofence.Builder()
                                .setCircularRegion(
                                        currentTask.getLat(),
                                        currentTask.getLongitude(),
                                        150
                                )
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT)
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setRequestId(UUID.randomUUID().toString()).build();



                    }
                }
                else{
                    currentTask.setHasLocation(false);
                }

                if(isNew){
                    ref.push().setValue(currentTask);
                }
                else {
                    ref.child(currentTask.getDbKey() + "/").setValue(currentTask);
                }
                finish();
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("", "Place: " + place.getName());
                selectedPlace = place;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("", "An error occurred: " + status);
            }
        });

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentTask.getHasTarget()){
                    currentTask.setHasTarget(false);
                    endDate.setVisibility(View.GONE);
                    endTime.setVisibility(View.GONE);
                    txtTarget.setVisibility(View.GONE);
                }
                else{
                    currentTask.setHasTarget(true);
                    endDate.setVisibility(View.VISIBLE);
                    endTime.setVisibility(View.VISIBLE);
                    txtTarget.setVisibility(View.VISIBLE);

                    final Calendar c = Calendar.getInstance();

                    if(currentTask.getEnd() == null){

                        int day = c.get(Calendar.DAY_OF_MONTH);
                        int month = c.get(Calendar.MONTH);
                        int year = c.get(Calendar.YEAR);

                        endDate.setText(day + "/" + (month + 1) + "/" + year);
                    }

                    if(currentTask.getEndTime() == null){
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        endTime.setText(hour + ":" + minute);
                    }

                    endDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dateDialog = new DatePickerFragment();
                            dateDialog.show(getFragmentManager(), "datePicker");
                        }


                    });

                    endTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timeDialog = new TimePickerFragmenrt();
                            timeDialog.show(getFragmentManager(), "timePicker");
                        }
                    });


                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //back to main
            }
        });


    }

    public void switchLocation(View v){
        if(currentTask.getHasTarget()){
            currentTask.setHasTarget(false);
            addressText.setVisibility(View.GONE);
            locationGroup.setVisibility(View.GONE);
        }
        else{
            currentTask.setHasTarget(true);
            addressText.setVisibility(View.VISIBLE);
            locationGroup.setVisibility(View.VISIBLE );
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
