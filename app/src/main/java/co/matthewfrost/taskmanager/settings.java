package co.matthewfrost.taskmanager;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;

import co.matthewfrost.taskmanager.databinding.ActivitySettingsBinding;

public class settings extends AppCompatActivity {
    Dialog passwordDialog;
    FirebaseUser user;
    User currentUser;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent i = getIntent();
        currentUser = (User) i.getSerializableExtra("user");
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setUser(currentUser);
        final Switch persist = (Switch) findViewById(R.id.persist);


        assert persist != null;
        persist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                Intent intent = getIntent();
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("co.matthewfrost.taskmanager", Context.MODE_PRIVATE);
                id = sharedPref.getInt("persistantNotificationID", 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (persist.isChecked()) {
                    if (id == 0) {
                        id = (int) System.currentTimeMillis();
                        editor.putInt("persistantNotificationID", id);
                        editor.apply();
                    }

                    NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(getApplicationContext())
                            .setContentTitle(intent.getStringExtra("title"))
                            .setContentText(intent.getStringExtra("desc"))
                            .setSmallIcon(android.R.color.transparent)
                            .setOngoing(true).build();

                    manager.notify(id, notification);
                } else {
                    NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(id);
                    editor.putInt("persistantNotificationID", 0);
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void logoutUser(View v) {
        Intent i;
        FirebaseAuth.getInstance().signOut();
        i = new Intent(getBaseContext(), login.class);
        startActivity(i);

    }

    public void resetCurrentUserPassword(View v) {
        passwordDialog = new Dialog(this);
        passwordDialog.setContentView(R.layout.password_reset);
        passwordDialog.setTitle("Reset Password");

        final EditText password = (EditText) passwordDialog.findViewById(R.id.oldPassword);
        final EditText confirmPassword = (EditText) passwordDialog.findViewById(R.id.confirmOld);
        final EditText newPassword = (EditText) passwordDialog.findViewById(R.id.newPassword);
        Button changePassword = (Button) passwordDialog.findViewById(R.id.changePassword);
        Button closeDialog = (Button) passwordDialog.findViewById(R.id.cancelPasswordChange);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential credential;
                if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    credential = EmailAuthProvider.getCredential(user.getEmail(), password.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    user.updatePassword(newPassword.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Password Successfully changed", Toast.LENGTH_SHORT).show();
                                                        passwordDialog.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });

                }
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordDialog.dismiss();
            }
        });


        passwordDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "settings Page", // TODO: Define a title for the content shown.
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
                "settings Page", // TODO: Define a title for the content shown.
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
