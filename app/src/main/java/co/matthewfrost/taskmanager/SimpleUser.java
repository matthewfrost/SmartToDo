package co.matthewfrost.taskmanager;

/**
 * Created by matth on 19/06/2016.
 */
public class SimpleUser {
    private String Name;
    private String Email;

    public SimpleUser(){

    }

    public SimpleUser(String e, String n){
        Name = n;
        Email = e;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }
}
