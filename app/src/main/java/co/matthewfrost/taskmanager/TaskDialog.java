package co.matthewfrost.taskmanager;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import co.matthewfrost.taskmanager.databinding.ActivityTaskDialogBinding;

public class TaskDialog extends AppCompatActivity {
    Task currentTask;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseApp app;
    DialogFragment dateDialog;
    DialogFragment timeDialog;
    Button map;
    TextView addressText;
    EditText address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_dialog);


        Intent i = getIntent();
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        ref = database.getReference("Tasks/" + i.getStringExtra("uid") );
        currentTask = (Task) i.getSerializableExtra("task");
        final boolean isNew = i.getBooleanExtra("isNew", true);
        if(currentTask == null){
            currentTask = new Task();
        }

        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);


        ActivityTaskDialogBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_task_dialog);
        binding.setTask(currentTask);


        Button cancel = (Button) findViewById(R.id.dialogCancel);
        Button OK = (Button) findViewById(R.id.dialogOK);
        map = (Button) findViewById(R.id.map);
        address = (EditText) findViewById(R.id.address);
        addressText = (TextView) findViewById(R.id.addressText);

        final CheckBox urgent = (CheckBox) findViewById(R.id.dialogUrgent);
        final TextView endDate = (TextView) findViewById(R.id.endDate);
        final TextView endTime = (TextView) findViewById(R.id.endTime);
        final Switch target = (Switch) findViewById(R.id.target);
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
                if(currentTask.getHasTarget()) {

                    if(currentTask.getNotificationID() == 0) {
                        currentTask.setNotificationID((int) System.currentTimeMillis());
                    }
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


        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog map = new Dialog(getApplication());
                map.setContentView(R.layout.map_dialog);
                map.show();
            }
        });

    }

    public void switchLocation(View v){
        if(currentTask.getHasTarget()){
            currentTask.setHasTarget(false);
            map.setVisibility(View.GONE);
            addressText.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }
        else{
            currentTask.setHasTarget(true);
            map.setVisibility(View.VISIBLE);
            addressText.setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
        }
    }
}
