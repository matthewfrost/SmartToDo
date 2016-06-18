package co.matthewfrost.taskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class settings extends AppCompatActivity {

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
}
