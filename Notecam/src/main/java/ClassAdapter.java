import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.List;

import model.Aula;


public class ClassAdapter extends ArrayAdapter<Aula> {
    private LayoutInflater mInflater;
    private List<Aula> items = new ArrayList<Aula>();

    // our ViewHolder.
    // caches our TextView
    public static class ViewHolder {
        Button letra;
        Button weekday;
        Button startTime;
        Button endTime;
    }

    public ClassAdapter(Context context, int layout, List<Aula> items) {
        super(context, layout, items);
        this.items = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Aula> data) {
        clear();
        if (data != null) {
            for (Aula appEntry : data) {
                add(appEntry);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.add_class_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.weekday = (Button)convertView.findViewById(R.id.button_weekday);
            viewHolder.letra = (Button)convertView.findViewById(R.id.letra);
            viewHolder.startTime = (Button)convertView.findViewById(R.id.button_start_time);
            viewHolder.endTime = (Button)convertView.findViewById(R.id.button_end_time);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();



        Aula item = getItem(position);
        if (item != null){
            String weekday = Aula.getWeekDayString(item.getWeekday());

            viewHolder.weekday.setText(weekday);
            viewHolder.startTime.setText(item.getStartTime().format("%H:%M"));
            viewHolder.endTime.setText(item.getEndTime().format("%H:%M"));

            viewHolder.letra.setOnClickListener(((SubjectsActivity)getContext()).getAddSubjectsFragment().getAddClassesFragment());
            viewHolder.weekday.setOnClickListener(((SubjectsActivity)getContext()).getAddSubjectsFragment().getAddClassesFragment());
            viewHolder.startTime.setOnClickListener(((SubjectsActivity)getContext()).getAddSubjectsFragment().getAddClassesFragment());
            viewHolder.endTime.setOnClickListener(((SubjectsActivity)getContext()).getAddSubjectsFragment().getAddClassesFragment());

        }

        return convertView;
    }



    public List<Aula> getItems() {
        return items;
    }

}

