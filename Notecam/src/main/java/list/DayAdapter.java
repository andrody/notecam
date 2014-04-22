package list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.koruja.notecam.R;
import com.koruja.notecam.SubjectsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.*;


public class DayAdapter extends ArrayAdapter<Day> {
    private LayoutInflater mInflater;
    private List<Day> items;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    public DayAdapter(Context context, int layout, List<Day> items) {
        super(context, layout, items);
        this.items = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Day> data) {
        clear();
        if (data != null) {
            for (Day appEntry : data) {
                add(appEntry);
            }
        }
    }

    // our ViewHolder.
    // caches our TextView
    public static class ViewHolder {
        TextView title;
        TextView class_number;
        TextView letra;
        TextView weekdays;
        GradientDrawable sphere;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.classes_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.title = ((TextView)convertView.findViewById(R.id.class_name));
            viewHolder.class_number = ((TextView)convertView.findViewById(R.id.class_number));
            viewHolder.letra = ((TextView)convertView.findViewById(R.id.sphere_letra));
            viewHolder.weekdays = ((TextView)convertView.findViewById(R.id.classesOn));
            viewHolder.sphere = ((GradientDrawable)(convertView.findViewById(R.id.sphere)).getBackground());


            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        Day item = getItem(position);

        views.put(item.getId(), convertView);

        if(views.get(item.getId()) == null){
            views.put(item.getId(), convertView);
        }

        //Set custom font
        viewHolder.title.setTypeface(((SubjectsActivity)getContext()).getFontType());
        viewHolder.class_number.setTypeface(((SubjectsActivity)getContext()).getFontType());


        //To upperCase first letter
        String name = "";
        if (!item.getName().equals(""))
            name = Character.toUpperCase(item.getName().charAt(0)) + item.getName().substring(1).toLowerCase();

        //Setar titulo
        viewHolder.title.setText(name);

        //Seta o weekday
        viewHolder.weekdays.setText(item.getWeekDayLong(item.getWeekday()));

        //Setar letra da sphera
        viewHolder.letra.setText("" + (position + 1));

        //Setar cor da esfera colorido
        viewHolder.sphere.setColor(Color.rgb(43, 132, 100));

        //((FrameLayout)viewHolder.letra.getParent()).setBackgroundColor(item.getColor());

        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(((SubjectsActivity)getContext()).getClassesListFragment().isCheckboxFlag())
            convertView.findViewById(R.id.checkBox_subject).setVisibility(CheckBox.VISIBLE);
        else
            convertView.findViewById(R.id.checkBox_subject).setVisibility(CheckBox.GONE);

        return convertView;
    }

    public int getPosition(View v){
        assert ((ListView)v.getParent()) != null;
        return ((ListView)v.getParent()).getPositionForView(v);
    }

    public List<Day> getItems() {
        return items;
    }

    public View getView(int id){
        return views.get(id);
    }

}

