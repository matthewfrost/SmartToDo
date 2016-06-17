package co.matthewfrost.taskmanager;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.*;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassword extends AppCompatActivity {
    private EditText email;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email = (EditText) findViewById(R.id.userEmail);
    }

    public void sendPasswordReset(View v){
        String userEmail = email.getText().toString();
        mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(forgotPassword.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(forgotPassword.this, "Email is not a registered user", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
