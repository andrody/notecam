package Dialogs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.HashMap;

import helper.Singleton;
import model.Materia;


/**
 * Dialog para escolher o Start Time ou End Time da Classe
 */
public class IconPickerFragment extends DialogFragment {
    public static int ITEMS_LIBERADOS_FREE = 3;

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
        getDialog().setTitle(getActivity().getString(R.string.selecione_um_icone));

        //Cria o adapter
        final IconAdapter adapter = new IconAdapter(getActivity(), this);

        gridview = (GridView) view.findViewById(R.id.dialog_grid_view);
        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(Singleton.isPaidVersion() || position <= ITEMS_LIBERADOS_FREE) {
                    //Seta o icone da matéria
                    //getMateria().setIcon_id(adapter.icons.get(position));
                    getMateria().setIcon_id(position);

                    //Faz o icone do header mudar pro icone selecionado
                    ImageView icone_materia = (ImageView) Singleton.getAddMateriaFragment().getView().findViewById(R.id.icone_materia);
                    icone_materia.setImageResource(getMateria().getIcon_id());

                    //Seleciona o icone nesse Dialog
                    adapter.notifyDataSetChanged();
                }

                else {
                    Singleton.show_only_pro_dialog(getActivity().getString(R.string.desculpa_icone_apenas_versao_completa) ,
                            getActivity().getString(R.string.sim_exc3),
                            getActivity().getString(R.string.agora_nao));
                }

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
class IconAdapter extends BaseAdapter {

    ArrayList<Integer> icons;
    model.Materia materia;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    IconAdapter(Context context, IconPickerFragment fragment) {
        this.context = context;
        icons = Singleton.getListaIcones();
        materia = fragment.getMateria();
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public Object getItem(int position) {
        return icons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView icon;
        View tick_icon;
        View paid;
        Drawable drawable_tick_icon;

        ViewHolder(View v) {
            icon = (ImageView) v.findViewById(R.id.icon_image);
            tick_icon = v.findViewById(R.id.tick_icon);
            paid = v.findViewById(R.id.paid);
            drawable_tick_icon  = context.getResources().getDrawable(R.drawable.ic_tick);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_icon_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        holder.icon.setImageResource(icons.get(position));

        //final model.Materia item = (model.Materia) getItem(position);

        if(icons.get(position) == materia.getIcon_id())
            holder.tick_icon.setVisibility(View.VISIBLE);
        else
            holder.tick_icon.setVisibility(View.GONE);

        //Se for versão free, só libera 4 tipos de cores
        if(!Singleton.isPaidVersion() && position > ColorPickerFragment.ITEMS_LIBERADOS_FREE) {
            holder.paid.setVisibility(View.VISIBLE);
        }
        //Versão Completa libera todas cores
        else {
            holder.paid.setVisibility(View.GONE);
        }

        //Paint paint = new Paint();
        //holder.drawable_tick_icon.setColorFilter(context.getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);

        //Troca cor de fundo das matérias
        /*int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.drawable_tick_icon.setBackgroundDrawable(holder.drawable);
        } else {
            holder.drawable_tick_icon.setBackground(holder.drawable);
        }*/



        //views.put(item.getId(), row);
        return row;
    }

    public View getView(int id){
        return views.get(id);
    }

}