package list;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.List;

import helper.Singleton;
import model.Aula;
import model.Materia;


public class ClassAdapter extends ArrayAdapter<Aula> {
    private LayoutInflater mInflater;
    private List<Aula> items = new ArrayList<Aula>();
    Materia materia;
    Context context;

    // our ViewHolder.
    // caches our TextView
    public static class ViewHolder {
        FrameLayout del_back;
        TextView weekday;
        TextView startTime;
        TextView endTime;
        ImageView del_x;

    }

    public ClassAdapter(Context context, int layout, List<Aula> items, Materia materia) {
        super(context, layout, items);
        this.items = items;
        this.context = context;
        this.materia = materia;
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
            convertView = mInflater.inflate(R.layout.item_aula, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.weekday = (TextView)convertView.findViewById(R.id.button_weekday);
            viewHolder.del_back = (FrameLayout)convertView.findViewById(R.id.del_back);
            viewHolder.startTime = (TextView)convertView.findViewById(R.id.button_start_time);
            viewHolder.endTime = (TextView)convertView.findViewById(R.id.button_end_time);
            viewHolder.del_x = (ImageView) convertView.findViewById(R.id.del_x);


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

            viewHolder.del_back.setOnClickListener(Singleton.getAddMateriaFragment().getAddAulasFragment());
            viewHolder.weekday.setOnClickListener(Singleton.getAddMateriaFragment().getAddAulasFragment());
            viewHolder.startTime.setOnClickListener(Singleton.getAddMateriaFragment().getAddAulasFragment());
            viewHolder.endTime.setOnClickListener(Singleton.getAddMateriaFragment().getAddAulasFragment());

            //Muda cor do fundo para cor da materia
            Drawable drawable = viewHolder.del_x.getDrawable();
            //drawable.setColorFilter(context.getResources().getColor(R.color.background_header), PorterDuff.Mode.SRC_ATOP);
            drawable.setColorFilter(materia.getColor(), PorterDuff.Mode.SRC_ATOP);

            drawable = viewHolder.del_back.getBackground();
            drawable.setColorFilter(materia.getColor(), PorterDuff.Mode.SRC_ATOP);

        }

        return convertView;
    }



    public List<Aula> getItems() {
        return items;
    }

}

