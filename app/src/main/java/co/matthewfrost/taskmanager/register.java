package co.matthewfrost.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {
    private EditText newEmail;
    private EditText newPassword;
    private EditText confirmPassword;
    private EditText name;
    private Button registerNewUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseApp app;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newEmail = (EditText) findViewById(R.id.newEmail);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        name = (EditText) findViewById(R.id.userName);

        mAuth = FirebaseAuth.getInstance();
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    User u;
                    SimpleUser sU;
                    Context c = getBaseContext();
                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("co.matthewfrost.taskmanager", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("email", newEmail.getText().toString());
                    editor.putString("name", name.getText().toString());
                    editor.apply();
                    database = FirebaseDatabase.getInstance(app);
                    ref = database.getReference("Users/" + user.getUid() );
                    u = new User(newEmail.getText().toString(), name.getText().toString());
                    sU = new SimpleUser(newEmail.getText().toString(), name.getText().toString());
                    ref.push().setValue(sU);
                    Log.d("firebase auth", "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("user", u);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d("firebase auth", "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    public void createUser(View v){
        String email,password,confirmedPassword;

        email = newEmail.getText().toString();
        password = newPassword.getText().toString();
        confirmedPassword = confirmPassword.getText().toString();

        if(password.equals(confirmedPassword)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("firebase auth", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                /*Toast.makeText(this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();*/
                            }
                            else{

                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
