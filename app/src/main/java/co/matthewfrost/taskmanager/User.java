package co.matthewfrost.taskmanager;

import android.databinding.ObservableField;

/**
 * Created by matth on 15/06/2016.
 */
public class User
{
    private ObservableField<String>  email = new ObservableField<>();
    private ObservableField<String>  password = new ObservableField<>();
    private ObservableField<String>  name = new ObservableField<>();

    public User(String email, String password, String name){
        this.email.set(email);
        this.password.set(password);
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

    public String getPassword(){
        return password.get();
    }

    public void setPassword(String password){
        this.password.set(password);
    }

    public String getForename(){
        return name.get();
    }

    public void setForename(String forename){
        this.name.set(forename);
    }

}
