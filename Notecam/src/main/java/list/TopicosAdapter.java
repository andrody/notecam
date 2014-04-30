package list;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.koruja.notecam.R;

import java.util.ArrayList;

import model.Subject;
import model.Topico;

public class TopicosAdapter extends BaseExpandableListAdapter {

    ArrayList<Topico> topicos;
    Subject materia;

    String[] listaPai = { "Categoria 1", "Categoria 2", "Categoria 3" };
    String[][] listafilho = { { "Subcategoria 1", "Subcategoria 1.2" },
            { "Subcategoria 2" }, { "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
            "Subcategoria 3",
    }
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}
            ,{ "Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3","Subcategoria 3", "Subcategoria 3"}

    };
    Context context;
    public TopicosAdapter(Context context, Subject materia) {
        this.context = context;
        this.materia = materia;
        topicos = new ArrayList<Topico>();
        topicos.add(new Topico("Matrizes"));
        topicos.add(new Topico("Logaritmos"));
        topicos.add(new Topico("Vetores Bidimensionais"));
        topicos.add(new Topico("Funções exponenciais"));
        topicos.add(new Topico("Funções exponenciais"));
        topicos.add(new Topico("Funções exponenciais"));
        topicos.add(new Topico("Funções exponenciais"));
        topicos.add(new Topico("Funções exponenciais"));
        topicos.add(new Topico("Funções exponenciais"));
        topicos.add(new Topico("Funções exponenciais"));
    }

    @Override
    public int getGroupCount() {
        return topicos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listafilho[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return topicos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listafilho[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    class ViewHolderGroup {
        CheckBox checkbox;
        GradientDrawable sphere;
        TextView letra_sphere;
        TextView nome_topico;
        TextView numero_fotos;
        ImageView ic_expandivel;
        ViewHolderGroup(View v) {
            letra_sphere = (TextView) v.findViewById(R.id.topico_letra);
            nome_topico = (TextView) v.findViewById(R.id.nome_topico);
            numero_fotos = (TextView) v.findViewById(R.id.numero_fotos);
            checkbox = (CheckBox) v.findViewById(R.id.checkBox_topico);
            ic_expandivel = (ImageView) v.findViewById(R.id.expandivel_icon);
            sphere = (GradientDrawable) v.findViewById(R.id.sphere).getBackground();
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolderGroup holder = null;

        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.topico_item, parent, false);
            holder = new ViewHolderGroup(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolderGroup) row.getTag();
        }

        Topico item = (Topico) getGroup(groupPosition);

        holder.letra_sphere.setText(groupPosition + 1 + "");
        holder.sphere.setColor(materia.getColor());
        holder.nome_topico.setText(item.getName());
        holder.numero_fotos.setText(item.getNumber() + " fotos");

        if(isExpanded) {
            holder.ic_expandivel.setImageResource(R.drawable.ic_action_collapse);
            holder.numero_fotos.setVisibility(View.VISIBLE);
        }
        else {
            holder.ic_expandivel.setImageResource(R.drawable.ic_action_expand);
            holder.numero_fotos.setVisibility(View.GONE);
        }

        return row;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Criamos um TextView que conterá as informações da listafilho que criamos
        TextView textViewSubLista = new TextView(context);
        textViewSubLista.setText(listafilho[groupPosition][childPosition]);
        // Definimos um alinhamento
        textViewSubLista.setPadding(10, 5, 0, 5);

        return textViewSubLista;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
