package co.matthewfrost.taskmanager;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.EventLogTags;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by matth on 05/06/2016.
 */
public class Task extends BaseObservable implements Serializable {
    ObservableField<String> name = new ObservableField<String>();
    ObservableField<String> description = new ObservableField<String>();
    ObservableField<String> start = new ObservableField<String>();
    ObservableField<String> end = new ObservableField<String>();
    ObservableField<String> endTime = new ObservableField<String>();
    ObservableInt urgency = new ObservableInt();
    ObservableField<String> uid = new ObservableField<String>();
    ObservableBoolean hasTarget = new ObservableBoolean();
    String dbKey;
    int notificationID;

    public Task(){
        uid.set(UUID.randomUUID().toString());
        hasTarget.set(false);
    }

    public Task(String name, String Desc, String Start, String End, String time, int urgency){
        this.name.set(name);
        this.description.set(Desc);
        this.start.set(Start);
        this.end.set(End);
        this.endTime.set(time);
        this.urgency.set(urgency);
        uid.set(UUID.randomUUID().toString());
    }

    public Task(String name, String Desc, String Start, String End, String time, int urgency, String uid){
        this.name.set(name);
        this.description.set(Desc);
        this.start.set(Start);
        this.end.set(End);
        this.endTime.set(time);
        this.urgency.set(urgency);
        this.uid.set(uid);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getEndTime() {
        return endTime.get();
    }

    public void setEndTime(String endTime) {
        this.endTime.set(endTime);
    }

    public String getStart() {
        return start.get();
    }

    public void setStart(String start) {
        this.start.set(start);
    }

    public String getEnd() {
        return end.get();
    }

    public void setEnd(String end) {
        this.end.set(end);
    }

    public int getUrgency() {
        return urgency.get();
    }

    public void setUrgency(int urgency) {
        this.urgency.set(urgency);
    }

    public String getUid() {
        return uid.get();
    }

    public void setDbKey(String s){
        this.dbKey = s;
    }

    public String getDbKey(){
        return dbKey;
    }

    public boolean getHasTarget() {
        return hasTarget.get();
    }

    public void setHasTarget(boolean hasTarget) {
        this.hasTarget.set(hasTarget);
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }
}
