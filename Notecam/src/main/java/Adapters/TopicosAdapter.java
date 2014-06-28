package Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.Singleton;
import model.Materia;
import model.Topico;


public class TopicosAdapter extends BaseAdapter {
    Context context;
    public Materia materia;
    private LayoutInflater mInflater;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    public List<Topico> items = new ArrayList<Topico>();

    public TopicosAdapter(Context context, Materia materia) {
        this.context = context;
        this.materia = materia;
        this.items = materia.getTopicos();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder {
        ImageView seta_direita;
        ImageView tag_labels;
        TextView numero_fotos_topico;
        TextView nome_topico;
        CheckBox checkbox;

        ViewHolder(View v) {
            seta_direita = (ImageView) v.findViewById(R.id.seta_direita);
            tag_labels = (ImageView) v.findViewById(R.id.tag_labels);
            numero_fotos_topico = (TextView) v.findViewById(R.id.numero_fotos_topico);
            nome_topico = (TextView) v.findViewById(R.id.nome_topico);
            checkbox = (CheckBox) v.findViewById(R.id.checkbox);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = null;

        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_topico, viewGroup, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        //Seta nome do tópico
        holder.nome_topico.setText(items.get(i).getName());

        //Seta numero de fotos do tópico
        holder.numero_fotos_topico.setText(items.get(i).getFotos().size() + " " + context.getString(R.string.fotos));



        //Muda cor do fundo para cor da matéria da seta
        Drawable drawable = holder.seta_direita.getDrawable();
        drawable.setColorFilter(materia.getColor(), PorterDuff.Mode.SRC_ATOP);

        //Muda cor do fundo para cor da matéria do icone de labels
        drawable = holder.tag_labels.getDrawable();
        drawable.setColorFilter(materia.getColor(), PorterDuff.Mode.SRC_ATOP);

        //Se está no modo de edição?
        boolean checkBoxFlag = Singleton.getTopicosFragment().isFakeActionModeOn();

        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(checkBoxFlag) {
            holder.checkbox.setVisibility(CheckBox.VISIBLE);
            holder.tag_labels.setVisibility(CheckBox.GONE);
        }
        else {
            holder.checkbox.setVisibility(CheckBox.GONE);
            holder.tag_labels.setVisibility(CheckBox.VISIBLE);
            holder.checkbox.setChecked(false);

        }


        views.put(items.get(i).getId(), row);

        return row;
    }

    public View getView(int id){
        return views.get(id);
    }

}

