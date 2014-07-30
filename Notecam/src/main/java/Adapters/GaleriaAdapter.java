package Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.koruja.notecam.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import helper.Singleton;
import model.Foto;

/**
 * O BaseAdapter é responsável por construir as views do gridview
 */
public class GaleriaAdapter extends BaseAdapter {

    public ArrayList<Foto> fotos;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    public GaleriaAdapter(Context context) {
        this.context = context;
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
        FrameLayout foto_back;
        CheckBox checkbox;

        ViewHolder(View v) {
            foto_back = (FrameLayout) v.findViewById(R.id.foto_back);
            checkbox = (CheckBox) v.findViewById(R.id.checkbox_galeria);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_galeria, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final Foto item = (Foto) getItem(position);


        //Seleciona o thumbnail da foto
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(item.getPath());
            byte[] imageData=exif.getThumbnail();

            boolean outofmemory = false;

            Bitmap thumbnail = null;

            if(imageData == null){
                final int THUMBSIZE = 128;

                try {
                    //thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(item.getPath()),THUMBSIZE, THUMBSIZE);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Singleton.getMateriasActivity().getContentResolver(), item.getUri());
                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);
                }catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(Singleton.getMateriasActivity(), context.getString(R.string.erro_ao_carregar_imagem), Toast.LENGTH_SHORT).show();
                }catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    outofmemory = true;
                    Toast.makeText(Singleton.getMateriasActivity(), context.getString(R.string.erro_ao_carregar_imagem), Toast.LENGTH_SHORT).show();
                }
            }

            //Se imagem foi deletada por algum motivo
            if (imageData == null && thumbnail == null) {
                if(!outofmemory) {
                    item.delete_safe();
                    Singleton.getTopico_selecionado().popularFotos();
                    fotos = (ArrayList<Foto>) Singleton.getTopico_selecionado().getFotos();
                    //Singleton.getGaleriaFragment().reload();
                    this.notifyDataSetChanged();
                }
            }
            else {

                if(thumbnail == null)
                    thumbnail = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                Drawable ob = new BitmapDrawable(context.getResources(), thumbnail);

                //Troca cor de fundo das matérias
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.foto_back.setBackgroundDrawable(ob);
                } else {
                    holder.foto_back.setBackground(ob);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            //Se deletou arquivo fora do notecam
            item.delete_safe();
            Singleton.getTopico_selecionado().popularFotos();
            fotos = (ArrayList<Foto>) Singleton.getTopico_selecionado().getFotos();
        }


        views.put(item.getId(), row);

        //Se está no modo de edição?
        boolean checkBoxFlag = Singleton.getGaleriaFragment().isFakeActionModeOn();

        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(checkBoxFlag) {
            holder.checkbox.setVisibility(CheckBox.VISIBLE);
        }
        else {
            holder.checkbox.setVisibility(CheckBox.GONE);
            holder.checkbox.setChecked(false);
        }

        return row;
    }

    public View getView(int id){
        return views.get(id);
    }

}
