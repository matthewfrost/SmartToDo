package co.matthewfrost.taskmanager;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Intent i = getIntent();
        currentUser = (User) i.getSerializableExtra("user");
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setUser(currentUser);

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

        final EditText password = (EditText) passwordDialog.findViewById(R.id.oldPassword);
        final EditText confirmPassword = (EditText) passwordDialog.findViewById(R.id.confirmOld);
        final EditText newPassword = (EditText) passwordDialog.findViewById(R.id.newPassword);
        Button changePassword = (Button) passwordDialog.findViewById(R.id.changePassword);
        Button closeDialog = (Button) passwordDialog.findViewById(R.id.cancelPasswordChange);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthCredential credential;
                if(password.getText().toString().equals(confirmPassword.getText().toString())) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    credential = EmailAuthProvider.getCredential(user.getEmail(), password.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

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
}
