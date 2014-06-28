package Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.HashMap;

import model.Materia;

/**
 * O BaseAdapter é responsável por construir as views do gridview
 */
public class MateriasAdapter extends BaseAdapter {

    public ArrayList<Materia> materias;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    public MateriasAdapter(Context context) {
        this.context = context;

        materias = new ArrayList<model.Materia>();
    }

    @Override
    public int getCount() {
        return materias.size();
    }

    @Override
    public Object getItem(int position) {
        return materias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView myInitialLetter;
        TextView nome_materia;
        View back_color;
        TextView numero_fotos;
        CheckBox checkbox_materia;
        Drawable drawable;
        View icon;

        ViewHolder(View v) {
            myInitialLetter = (TextView) v.findViewById(R.id.letra_inicial_image);
            nome_materia = (TextView) v.findViewById(R.id.materia_nome_text);
            numero_fotos = (TextView) v.findViewById(R.id.materia_numero_fotos_text);
            icon = v.findViewById(R.id.imagem_materia);
            checkbox_materia = (CheckBox) v.findViewById(R.id.checkbox_materia);
            back_color = v.findViewById(R.id.foto_back);
            drawable  = context.getResources().getDrawable(R.drawable.materia);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_materia_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final model.Materia item = (model.Materia) getItem(position);

        String nome_materia = "#" + materias.get(position).getName().toLowerCase();

        Paint paint = new Paint();

        //Seleciona o ícone da materia
        holder.icon.setBackgroundResource(materias.get(position).getIcon_id());

        //Seta o nome e tamanho
        holder.nome_materia.setText("#" + materias.get(position).getName().toLowerCase());
        //holder.nome_materia.setTextSize(perfectSize);
        correctWidth(holder.nome_materia, 200);

        //holder.numero_fotos.setText(fotos.get(position).getNumero_fotos() + " fotos");


        holder.drawable.setColorFilter(materias.get(position).getColor(), PorterDuff.Mode.SRC_ATOP);

        //Troca cor de fundo das matérias
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.back_color.setBackgroundDrawable(holder.drawable);
        } else {
            holder.back_color.setBackground(holder.drawable);
        }



        views.put(item.getId(), row);

        //Se está no modo de edição?
        boolean checkBoxFlag = ((MateriasActivity)context).getMateriasFragment().isFakeActionModeOn();

        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(checkBoxFlag) {
            holder.checkbox_materia.setVisibility(CheckBox.VISIBLE);
            holder.icon.setVisibility(CheckBox.GONE);
        }
        else {
            holder.checkbox_materia.setVisibility(CheckBox.GONE);
            holder.icon.setVisibility(CheckBox.VISIBLE);
            holder.checkbox_materia.setChecked(false);

        }

        return row;
    }

    public void correctWidth(TextView textView, int desiredWidth)
    {
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > desiredWidth)
        {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public View getView(int id){
        return views.get(id);
    }

}
