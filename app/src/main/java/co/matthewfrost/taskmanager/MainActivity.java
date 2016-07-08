package co.matthewfrost.taskmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
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
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
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
    DatabaseReference userRef;
    Button addTask;
    ProgressBar spinner;
    User user;
    SimpleUser sUser;
    SharedPreferences prefs;
    AlarmManager alarmManager;

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
        String email = null;
        String name = null;
        list = new ArrayList<Task>();
        addTask = (Button) findViewById(R.id.addTask);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        FirebaseAuth mAuth;
        FirebaseAuth.AuthStateListener mAuthListener;

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
        userRef = database.getReference("Users/" + uid);

        taskList = (ListView) findViewById(R.id.listView);
        arrayAdapter = new TaskArrayAdapter(this, list);
        taskList.setAdapter(arrayAdapter);
        taskList.setLongClickable(true);

        if(intent.getSerializableExtra("user") != null){
            user = (User) intent.getSerializableExtra("user");
        }
        else{
            prefs = getApplicationContext().getSharedPreferences("co.matthewfrost.taskmanager", Context.MODE_PRIVATE);
            email = prefs.getString("email", null);
            name = prefs.getString("name", null);
        }
        if((name == null || email == null) && user == null){
            userRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    sUser = dataSnapshot.getValue(SimpleUser.class);
                    user = new User(sUser.getEmail(), sUser.getName());
                    userRef.removeEventListener(this);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

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
                t.setHasTarget(recievedTask.isHasTarget());
                if(t.getHasTarget()) {
                    t.setNotificationID(recievedTask.getNotificationID());
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
                    DateTime endDT = formatter.parseDateTime(t.getEnd() + " " + t.getEndTime());
                    DateTime curr = new DateTime();
                    if (endDT.isAfter(curr)) {
                        createNotifications(t);
                    }
                }
                t.setHasLocation(recievedTask.isHasLocation());
                if(t.getHasTarget()){
                    t.setPlaceID(recievedTask.getPlaceID());
                    t.setLongitude(recievedTask.getLongitude());
                    t.setLat(recievedTask.getLat());
                }
                t.setDbKey(dataSnapshot.getKey());
                list.add(t);
                arrayAdapter.notifyDataSetChanged();
                spinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                SimpleTask recievedTask = dataSnapshot.getValue(SimpleTask.class);
                Task t = new Task(recievedTask.getName(), recievedTask.getDescription(), recievedTask.getStart(), recievedTask.getEnd(), recievedTask.getEndTime(), recievedTask.getUrgency(), recievedTask.getUid());
                t.setHasTarget(recievedTask.isHasTarget());
                if(t.getHasTarget()) {
                    t.setNotificationID(recievedTask.getNotificationID());
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
                    DateTime endDT = formatter.parseDateTime(t.getEnd() + " " + t.getEndTime());
                    DateTime curr = new DateTime();
                    if (endDT.isAfter(curr)) {
                        createNotifications(t);
                    }
                }
                t.setDbKey(dataSnapshot.getKey());
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTaskDialog(null, -1);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    protected void editTaskDialog(final Task t, final int postion) {
        /*taskDialog = new Dialog(this);
        taskDialog.setTitle("Edit Task");


        taskDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        taskDialog.show();*/
        Intent i = new Intent(this, TaskDialog.class);
        i.putExtra("task", t);
        if(postion == -1){
            i.putExtra("isNew", true);
        }
        else{
            i.putExtra("isNew", false);
        }
        i.putExtra("uid", uid);
        startActivity(i);
    }

    public void createNotifications(Task t){
        Date d = new Date();
        DateTime dt = new DateTime(d);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTime endDT = formatter.parseDateTime(t.getEnd() +" " +t.getEndTime());

        Long diff = endDT.getMillis() - dt.getMillis();
        Intent i = new Intent(getBaseContext(), TaskAlerter.class);
        i.putExtra("title", t.getName());
        i.putExtra("desc", t.getDescription());
        i.putExtra("notificationID", t.getNotificationID());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getBaseContext(), (int)System.currentTimeMillis(), i, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + diff, alarmIntent);
        }
        else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + diff, alarmIntent);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_settings:
                Task top = list.get(0);
                Intent i;
                i = new Intent(getBaseContext(), settings.class);
                i.putExtra("user", user);
                i.putExtra("title", top.getName());
                i.putExtra("desc", top.getDescription());
                startActivity(i);
                break;
            default:
                break;
        }

        return true;
    }

}
