package co.matthewfrost.taskmanager;

import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by matth on 05/06/2016.
 */
public class TimePickerFragmenrt extends DialogFragment
{
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), (TaskDialog)getActivity(), hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

}
