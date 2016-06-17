package co.matthewfrost.taskmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.matthewfrost.taskmanager.R;
import co.matthewfrost.taskmanager.Task;

/**
 * Created by matth on 22/05/2016.
 */
public class TaskArrayAdapter extends ArrayAdapter<Task> {
    private final Context context;
    private final ArrayList<Task> values;

    public TaskArrayAdapter(Context c, ArrayList<Task> values){
        super(c, R.layout.customlistview, values);
        this.context = c;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.customlistview, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.Name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView description = (TextView) rowView.findViewById(R.id.Description);
        Task currentTask = values.get(position);
        textView.setText(currentTask.getName());
        description.setText(currentTask.getDescription());
        // Change the icon for Windows and iPhone
/*        String s = values[position];
        if (s.startsWith("Windows7") || s.startsWith("iPhone")
                || s.startsWith("Solaris")) {
            imageView.setImageResource(R.drawable.no);
        } else {
            imageView.setImageResource(R.drawable.ok);
        }*/

        return rowView;
    }

}
