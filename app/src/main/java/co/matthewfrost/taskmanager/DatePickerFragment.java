package co.matthewfrost.taskmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by matth on 31/05/2016.
 */
public class DatePickerFragment extends DialogFragment
{
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), (MainActivity)getActivity(), year, month, day);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}
