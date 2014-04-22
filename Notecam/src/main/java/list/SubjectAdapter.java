package list;

import android.content.Context;
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

import model.Aula;
import model.Subject;


public class SubjectAdapter extends ArrayAdapter<Subject> {
    private LayoutInflater mInflater;
    private List<Subject> items;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    public SubjectAdapter(Context context, int layout,List<Subject> items) {
        super(context, layout, items);
        this.items = items;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Subject> data) {
        clear();
        if (data != null) {
            for (Subject appEntry : data) {
                add(appEntry);
            }
        }
    }

    // our ViewHolder.
    // caches our TextView
    public static class ViewHolder {
        TextView title;
        TextView letra;
        TextView weekdays;
        GradientDrawable sphere;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.subjects_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.title = ((TextView)convertView.findViewById(R.id.subject_name));
            viewHolder.letra = ((TextView)convertView.findViewById(R.id.subject_letra));
            viewHolder.weekdays = ((TextView)convertView.findViewById(R.id.classesOn));
            viewHolder.sphere = ((GradientDrawable)(convertView.findViewById(R.id.sphere)).getBackground());


            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        Subject item = getItem(position);

        //Set custom font
        viewHolder.title.setTypeface(((SubjectsActivity)getContext()).getFontType());
        //viewHolder.weekdays.setTypeface(((SubjectsActivity) getContext()).getFontType());

        views.put(item.getId(), convertView);

        if(views.get(item.getId()) == null){
            views.put(item.getId(), convertView);
        }

        //To upperCase first letter
        String name = Character.toUpperCase(item.getName().charAt(0)) + item.getName().substring(1).toLowerCase();

        viewHolder.title.setText(name);
        //viewHolder.title.setTextColor(item.getColor());

        viewHolder.letra.setText(name.substring(0, 1));
        viewHolder.sphere.setColor(item.getColor());
        //((FrameLayout)viewHolder.letra.getParent()).setBackgroundColor(item.getColor());

        //Contador
        int i = 1;

        //String
        String classesOn = "";

        //Seta as classes do subject na view
        //Quantos dias da semana
        int size = item.getAulas().size();

        //Verifica os dias da semana que tem aula dessa matéria e coloca como visivel
        for(Aula cl : item.getAulas()){
            int k = cl.getWeekday();
            if(k == 0)
                classesOn += "Sunday";
            if(k == 1)
                classesOn += "Monday";
            if(k == 2)
                classesOn += "Tuesday";
            if(k == 3)
                classesOn += "Wednesday";
            if(k == 4)
                classesOn += "Thursday";
            if(k == 5)
                classesOn += "Friday";
            if(k == 6)
                classesOn += "Saturday" ;
            if(i < size)
                if(i == size - 1)
                    classesOn += " and ";
                else
                    classesOn += ", ";
            i++;
        }
        viewHolder.weekdays.setText(classesOn);


        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(((SubjectsActivity)getContext()).getSubjectsListFragment().isCheckboxFlag())
            convertView.findViewById(R.id.checkBox_subject).setVisibility(CheckBox.VISIBLE);
        else
            convertView.findViewById(R.id.checkBox_subject).setVisibility(CheckBox.GONE);

        return convertView;
    }

    public int getPosition(View v){
        return ((ListView)v.getParent()).getPositionForView(v);
    }

    public List<Subject> getItems() {
        return items;
    }

    public View getView(int id){
        return views.get(id);
    }

}

