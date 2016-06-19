package co.matthewfrost.taskmanager;

import android.databinding.ObservableField;

import java.io.Serializable;

/**
 * Created by matth on 15/06/2016.
 */
public class User implements Serializable
{
    private ObservableField<String>  email = new ObservableField<>();
    private ObservableField<String>  name = new ObservableField<>();

    public User(String email, String name){
        this.email.set(email);
        this.name.set(name);
    }

    public User(){

    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getName(){
        return name.get();
    }

    public void setName(String forename){
        this.name.set(forename);
    }

}
