package co.matthewfrost.taskmanager;

import android.databinding.ObservableField;

/**
 * Created by matth on 15/06/2016.
 */
public class User
{
    private ObservableField<String>  email = new ObservableField<>();
    private ObservableField<String>  password = new ObservableField<>();
    private ObservableField<String>  forename = new ObservableField<>();
    private ObservableField<String> surname = new ObservableField<>();

    public User(String email, String password, String forename, String surname){
        this.email.set(email);
        this.password.set(password);
        this.forename.set(forename);
        this.surname.set(surname);
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
        return forename.get();
    }

    public void setForename(String forename){
        this.forename.set(forename);
    }

    public String getSurname(){
        return surname.get();
    }

    public void setSurname(String surname){
        this.surname.set(surname);
    }
}
