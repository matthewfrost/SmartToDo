package co.matthewfrost.taskmanager;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.util.UUID;

/**
 * Created by matth on 07/06/2016.
 */
public class SimpleTask
{
    String name;
    String description;
    String start;
    String end;
    String endTime;
    int urgency;
    String uid;
    boolean hasTarget;
    int notificationID;
    String address;
    boolean hasLocation;

    public SimpleTask(){
        this.uid = UUID.randomUUID().toString();
    }

    public SimpleTask(String name, String Desc, String Start, String End, String time, int urgency){
        this.name = name;
        this.description = Desc;
        this.start = Start;
        this.end = End;
        this.endTime = time;
        this.urgency = urgency;
        uid = UUID.randomUUID().toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public String getUid() {
        return uid;
    }

    public boolean isHasTarget() {
        return hasTarget;
    }

    public void setHasTarget(boolean hasTarget) {
        this.hasTarget = hasTarget;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation) {
        this.hasLocation = hasLocation;
    }
}
