package co.matthewfrost.taskmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import co.matthewfrost.taskmanager.databinding.TaskdialogBinding;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    ListView taskList;
    ArrayList<Task> list;
    Dialog taskDialog, deleteDialog;
    DialogFragment dateDialog;
    DialogFragment timeDialog;
    TaskArrayAdapter arrayAdapter;
    String date;
    String time;
    Task currentTask;
    FirebaseApp app;
    FirebaseDatabase database;
    DatabaseReference ref;
    Button addTask;
    ProgressBar spinner;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String uid;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<Task>();
        addTask = (Button) findViewById(R.id.addTask);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("firebase auth", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("firebase auth", "onAuthStateChanged:signed_out");
                }
            }
        };

        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        ref = database.getReference("Tasks/" + uid );

        Log.v("debug", "onCreate");
        taskList = (ListView) findViewById(R.id.listView);
        arrayAdapter = new TaskArrayAdapter(this, list);
        taskList.setAdapter(arrayAdapter);
        taskList.setLongClickable(true);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                spinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                spinner.setVisibility(View.INVISIBLE);
            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SimpleTask recievedTask = dataSnapshot.getValue(SimpleTask.class);
                Task t = new Task(recievedTask.getName(), recievedTask.getDescription(), recievedTask.getStart(), recievedTask.getEnd(), recievedTask.getEndTime(), recievedTask.getUrgency(), recievedTask.getUid());
                t.setDbKey(dataSnapshot.getKey());
                list.add(t);
                arrayAdapter.notifyDataSetChanged();
                spinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                SimpleTask recievedTask = dataSnapshot.getValue(SimpleTask.class);
                Task t = new Task(recievedTask.getName(), recievedTask.getDescription(), recievedTask.getStart(), recievedTask.getEnd(), recievedTask.getEndTime(), recievedTask.getUrgency(), recievedTask.getUid());
                for(int i = 0; i < list.size(); i++){
                    if(list.get(i).getUid().equals(t.getUid())){
                        list.set(i, t);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                SimpleTask t = dataSnapshot.getValue(SimpleTask.class);
                for(int i = 0; i < list.size(); i++){
                    if(list.get(i).getUid().equals(t.getUid())){
                        list.remove(i);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Task task = (Task) parent.getItemAtPosition(position);
                editTaskDialog(task, position);
            }
        });

        taskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteDialog = new Dialog(MainActivity.this);
                deleteDialog.setContentView(R.layout.deletedialog);
                deleteDialog.setTitle("Delete Task");

                Button Yes, No;

                Yes = (Button) deleteDialog.findViewById(R.id.yes);
                No = (Button) deleteDialog.findViewById(R.id.no);

                No.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog.dismiss();
                    }
                });

                Yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Task toDelete = list.get(position);
                        ref.child(toDelete.getDbKey()).removeValue();
                        deleteDialog.dismiss();
                    }
                });

                deleteDialog.show();

                return true;
            }
        });


        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTaskDialog();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void createTaskDialog() {
        taskDialog = new Dialog(this);
        taskDialog.setContentView(R.layout.taskdialog);
        taskDialog.setTitle("Add Task");

        Button cancel = (Button) taskDialog.findViewById(R.id.dialogCancel);
        Button OK = (Button) taskDialog.findViewById(R.id.dialogOK);
        final EditText Name = (EditText) taskDialog.findViewById(R.id.dialogNameText);
        final EditText Description = (EditText) taskDialog.findViewById(R.id.dialogDescriptionText);
        final CheckBox urgent = (CheckBox) taskDialog.findViewById(R.id.dialogUrgent);
        final TextView endDate = (TextView) taskDialog.findViewById(R.id.endDate);
        final TextView endTime = (TextView) taskDialog.findViewById(R.id.endTime);

        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        endDate.setText(day + "-" + month + "-" + year);
        endTime.setText(hour + ":" + minute);

        endDate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dateDialog = new DatePickerFragment();
                dateDialog.show(getFragmentManager(), "datePicker");
            }
        });

        endTime.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                timeDialog = new TimePickerFragmenrt();
                timeDialog.show(getFragmentManager(), "timePicker");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDialog.dismiss();
            }
        });

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isurgent;
                int urgencyLevel;
                isurgent = urgent.isChecked();
                if (isurgent) {
                    urgencyLevel = 1000;
                } else {
                    urgencyLevel = 100;
                }
                    DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = new Date();
                    String s = dateformat.format(d);
                    String taskEnd = endDate.getText().toString();
                    String time = endTime.getText().toString();

                    Task task = new Task(Name.getText().toString(), Description.getText().toString(), s, taskEnd, time, urgencyLevel);

                    ref.push().setValue(task);
                    taskDialog.dismiss();
                    arrayAdapter.notifyDataSetChanged();
            }
        });

        taskDialog.show();
    }


    protected void editTaskDialog(final Task t, final int postion) {
        taskDialog = new Dialog(this);
        taskDialog.setTitle("Edit Task");
        currentTask = t;

        Context context = taskDialog.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        TaskdialogBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.taskdialog, null, false);
        taskDialog.setContentView(mBinding.getRoot());
        mBinding.setTask(currentTask);


        final DateFormat dateformat;
        dateformat = new SimpleDateFormat("dd-MM-yyyy");
        Button cancel = (Button) taskDialog.findViewById(R.id.dialogCancel);
        Button OK = (Button) taskDialog.findViewById(R.id.dialogOK);
        final EditText Name = (EditText) taskDialog.findViewById(R.id.dialogNameText);
        final EditText Description = (EditText) taskDialog.findViewById(R.id.dialogDescriptionText);
        final CheckBox urgent = (CheckBox) taskDialog.findViewById(R.id.dialogUrgent);
        final TextView endDate = (TextView) taskDialog.findViewById(R.id.endDate);
        final TextView endTime = (TextView) taskDialog.findViewById(R.id.endTime);


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

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ref.child(currentTask.getDbKey() + "/").setValue(currentTask);
                    taskDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                taskDialog.dismiss();
            }
        });

        taskDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        taskDialog.show();

    }

    private void setEndDate(Dialog d, String date){
        TextView endDate = (TextView) d.findViewById(R.id.endDate);
        endDate.setText(date);
    }

    private void setEndTime(Dialog d, String time){
        TextView endTime = (TextView) d.findViewById(R.id.endTime);
        endTime.setText(time);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        setEndDate(taskDialog, date);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String formattedMinutes, minutes;
        minutes = "00" + Integer.toString(minute);
        formattedMinutes = minutes.substring((minutes.length() - 2), (minutes.length() - 1));
        if(formattedMinutes.length() == 1){
            formattedMinutes = formattedMinutes + "0";
        }
        time = hourOfDay + ":" + formattedMinutes;
        setEndTime(taskDialog, time);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://co.matthewfrost.taskmanager/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://co.matthewfrost.taskmanager/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}