package list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
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
        FrameLayout letra;
        TextView weekday;
        TextView startTime;
        TextView endTime;
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
            convertView = mInflater.inflate(R.layout.add_aula_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.weekday = (TextView)convertView.findViewById(R.id.button_weekday);
            viewHolder.letra = (FrameLayout)convertView.findViewById(R.id.letra);
            viewHolder.startTime = (TextView)convertView.findViewById(R.id.button_start_time);
            viewHolder.endTime = (TextView)convertView.findViewById(R.id.button_end_time);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();



        Aula item = getItem(position);
        if (item != null){
            String weekday = Aula.getWeekDayString(item.getWeekday());

            viewHolder.weekday.setText(weekday.toLowerCase());
            viewHolder.startTime.setText(item.getStartTime().format("%H:%M"));
            viewHolder.endTime.setText(item.getEndTime().format("%H:%M"));

            viewHolder.letra.setOnClickListener(((MateriasActivity)getContext()).getAddSubjectsFragment().getAddAulasFragment());
            viewHolder.weekday.setOnClickListener(((MateriasActivity)getContext()).getAddSubjectsFragment().getAddAulasFragment());
            viewHolder.startTime.setOnClickListener(((MateriasActivity)getContext()).getAddSubjectsFragment().getAddAulasFragment());
            viewHolder.endTime.setOnClickListener(((MateriasActivity)getContext()).getAddSubjectsFragment().getAddAulasFragment());

        }

        return convertView;
    }



    public List<Aula> getItems() {
        return items;
    }

}

