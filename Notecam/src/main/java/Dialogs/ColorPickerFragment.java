package Dialogs;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.HashMap;

import helper.Singleton;
import model.Materia;


/**
 * Dialog para escolher o Start Time ou End Time da Classe
 */
public class ColorPickerFragment extends DialogFragment {
    int color;
    GridView gridview;
    private model.Materia materia;

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public interface Communicator {
        public void onDialogMessage(int color);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_grid, null);
        getDialog().setTitle("Selecione uma cor");

        //Cria o adapter
        final ColorAdapter adapter = new ColorAdapter(getActivity(), this);

        gridview = (GridView) view.findViewById(R.id.dialog_grid_view);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Seta a cor da matéria
                getMateria().setColor(adapter.cores.get(position));

                //Faz o header mudar para a cor selecionada
                Singleton.mudarCorHeader(Singleton.getAddMateriaFragment(), getMateria().getColor());

                //Faz a cor dos icones de deletar das aulas mudarem de cor também
                Singleton.getAddMateriaFragment().getAddAulasFragment().getAdapter().notifyDataSetChanged();

                //Seleciona a cor nesse Dialog
                adapter.notifyDataSetChanged();

                //Fecha a tela assim que clicar em uma cor
                dismiss();
            }
        });

        return view;
    }



}

/**
 * O BaseAdapter é responsável por construir as views do gridview
 */
class ColorAdapter extends BaseAdapter {

    ArrayList<Integer> cores;
    model.Materia materia;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    ColorAdapter(Context context, ColorPickerFragment fragment) {
        this.context = context;
        cores = Singleton.getListaCores();
        materia = fragment.getMateria();
    }

    @Override
    public int getCount() {
        return cores.size();
    }

    @Override
    public Object getItem(int position) {
        return cores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        View background;
        View tick_icon;
        Drawable drawable;

        ViewHolder(View v) {
            background = v.findViewById(R.id.grid_back);
            tick_icon = v.findViewById(R.id.tick_icon);
            drawable  = context.getResources().getDrawable(R.drawable.circle_background);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_color_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        //final model.Materia item = (model.Materia) getItem(position);

        if(cores.get(position) == materia.getColor())
            holder.tick_icon.setVisibility(View.VISIBLE);
        else
            holder.tick_icon.setVisibility(View.INVISIBLE);


        //Paint paint = new Paint();
        holder.drawable.setColorFilter(cores.get(position), PorterDuff.Mode.SRC_ATOP);

        //Troca cor de fundo das matérias
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.background.setBackgroundDrawable(holder.drawable);
        } else {
            holder.background.setBackground(holder.drawable);
        }



        //views.put(item.getId(), row);
        return row;
    }

    public View getView(int id){
        return views.get(id);
    }

}