package list;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
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
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return topicos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
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

    class ViewHolderChild {
        GridView gridview;

        ViewHolderChild(View v) {
            gridview = (GridView) v.findViewById(R.id.fotos_grid_view);
        }
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolderChild holder = null;

        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_fotos, parent, false);
            holder = new ViewHolderChild(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolderChild) row.getTag();
        }

        holder.gridview.setAdapter(new FotoAdapter(context));

        // initialize the following variables (i've done it based on your layout
        // note: rowHeightDp is based on my grid_cell.xml, that is the height i've
        //    assigned to the items in the grid.
        final int spacingDp = 10;
        final int colWidthDp = 100;
        final int rowHeightDp = 160;

        // convert the dp values to pixels
        final float COL_WIDTH = context.getResources().getDisplayMetrics().density * colWidthDp;
        final float ROW_HEIGHT = context.getResources().getDisplayMetrics().density * rowHeightDp;
        final float SPACING = context.getResources().getDisplayMetrics().density * spacingDp;

        // calculate the column and row counts based on your display
        final int colCount = (int)Math.floor((parent.getWidth() - (2 * SPACING)) / (COL_WIDTH + SPACING));
        final int rowCount = (int)Math.ceil((6 + 0d) / colCount);

        // calculate the height for the current grid
        final int GRID_HEIGHT = Math.round(rowCount * (ROW_HEIGHT + SPACING));

        // set the height of the current grid
        holder.gridview.getLayoutParams().height = GRID_HEIGHT;

        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
