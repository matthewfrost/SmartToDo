package co.matthewfrost.taskmanager;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class settings extends AppCompatActivity {
    Dialog passwordDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void logoutUser(View v){
        Intent i;
        FirebaseAuth.getInstance().signOut();
        i = new Intent(getBaseContext(), login.class);
        startActivity(i);

    }

    public void resetCurrentUserPassword(View v){
        passwordDialog = new Dialog(this);
        passwordDialog.setContentView(R.layout.password_reset);
        passwordDialog.setTitle("Reset Password");

        passwordDialog.show();
    }
}
