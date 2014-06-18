package list;

/**
 * Created by Andrew on 30-Apr-14.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.HashMap;

import helper.Singleton;
import model.Foto;
import model.Topico;

/**
 * O BaseAdapter é responsável por construir as views do gridview
 */
public class FotoAdapter extends BaseAdapter {

    ArrayList<Foto> fotos;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    public FotoAdapter(Context context, Topico topico) {
        this.context = context;
        fotos = (ArrayList<Foto>) topico.getFotos();
    }

    @Override
    public int getCount() {
        return fotos.size();
    }

    @Override
    public Object getItem(int position) {
        return fotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView foto_imagem;
        TextView foto_numero;
        CheckBox checkbox;
        ViewHolder(View v) {
            foto_imagem = (ImageView) v.findViewById(R.id.foto_imagem);
            foto_numero = (TextView) v.findViewById(R.id.foto_numero_text);
            checkbox = (CheckBox )v.findViewById(R.id.checkbox);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_foto_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final Foto item = (Foto) getItem(position);

        Bitmap bitmap = Singleton.getPreview(item.getPath());

        //Seta imagem da foto
        holder.foto_imagem.setImageBitmap(bitmap);



        holder.foto_numero.setText(position + 1 + "");

        views.put(item.getId(), row);

        //Se está no modo de edição?
        boolean checkBoxFlag = ((MateriasActivity)context).getMateriasFragment().isFakeActionModeOn();

        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(checkBoxFlag)
            holder.checkbox.setVisibility(CheckBox.VISIBLE);
        else
            holder.checkbox.setVisibility(CheckBox.GONE);

        /*final ViewHolder holder_final = holder;

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxFlag)
                    holder_final.checkbox.setChecked(true);
            }
        });*/

        return row;
    }

    public View getView(int id){
        return views.get(id);
    }



}
