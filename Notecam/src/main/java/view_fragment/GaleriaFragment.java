package view_fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import helper.Singleton;
import model.Foto;
import model.Topico;


public class GaleriaFragment extends Fragment implements View.OnClickListener {

    GridView gridview;
    GaleriaAdapter galeriaAdapter = null;

    //Booleana que diz se o ActionMode (LongPress) foi ativado ou não. Caso sim ele ativa as checkboxes dos items da lista
    private boolean fakeActionModeOn = false;

    @Override
    public void onResume() {
        super.onResume();
        galeriaAdapter.notifyDataSetChanged();
        setFakeActionModeOn(false);
    }

    public void reload(){
        Singleton.getTopico_selecionado().popularFotos();
        //galeriaAdapter = new GaleriaAdapter(getActivity());
        //galeriaAdapter.fotos =
        gridview.setAdapter(new GaleriaAdapter(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_galeria, container, false);

        Singleton.setActionBarTitle(Singleton.getTopico_selecionado().getName());

        //Faz o header mudar para a cor selecionada
        View fake_action_bar = view.findViewById(R.id.fake_action_bar);
        fake_action_bar.setBackgroundColor(Singleton.getMateria_selecionada().getColor());

        //Cria o adapter
        galeriaAdapter = new GaleriaAdapter(getActivity());
        galeriaAdapter.fotos = (ArrayList<Foto>) Singleton.getTopico_selecionado().getFotos();

        gridview = (GridView) view.findViewById(R.id.galeria_grid_view);
        gridview.setAdapter(galeriaAdapter);



        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Seleciona ou deseleciona materia se estiver em modo de edição
                if(fakeActionModeOn){
                    CheckBox checkbox = ((CheckBox) view.findViewById(R.id.checkbox_galeria));
                    checkbox.setChecked(!checkbox.isChecked());
                }
                //Abre foto na galeria
                else {
                    abrir_foto_na_galeria(position);
                }
            }
        });

        //Seta um evento de Pressionar por longo tempo na lista
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //Callback
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int position, long id) {

                //Avisa que estamos no modo de edição (Selecionar e Deletar items)
                setFakeActionModeOn(true);


                //Seta o checkbox do item que foi pressionado como selecionado automaticamente
                ((CheckBox) v.findViewById(R.id.checkbox_galeria)).setChecked(true);


                //Atualiza a lista
                galeriaAdapter.notifyDataSetChanged();

                return true;
            }
        });


        //Setando listener do botão de Voltar
        view.findViewById(R.id.back).setOnClickListener(this);

        //Setando listener do botão de Deletar
        view.findViewById(R.id.deletar).setOnClickListener(this);;

        //Setando listener do botão de Compartilhar
        view.findViewById(R.id.compartilhar).setOnClickListener(this);

        //Setando listener do botão de Cancelar
        view.findViewById(R.id.cancelar).setOnClickListener(this);

        //Setando listener do botão de Cancelar Também
        view.findViewById(R.id.cancelar2).setOnClickListener(this);


        return view;
    }

    public void abrir_foto_na_galeria(int position){
        Uri uri = Singleton.getTopico_selecionado().getFotos().get(position).getUri();
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        String mime = "image/*";
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (mimeTypeMap.hasExtension(
                mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
            mime = mimeTypeMap.getMimeTypeFromExtension(
                    mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        intent.setDataAndType(uri,mime);
        startActivity(intent);
    }

    public boolean isFakeActionModeOn() {
        return fakeActionModeOn;
    }

    public void setFakeActionModeOn(boolean fakeActionModeOn) {
        if(getView() != null)
            if(fakeActionModeOn)
                getView().findViewById(R.id.fake_action_mode).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.fake_action_mode).setVisibility(View.GONE);

        this.fakeActionModeOn = fakeActionModeOn;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //Volta para o fragment anterior
            case R.id.back:
                //getActivity().onBackPressed();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                break;

            //Cancela o Fake Action Mode
            case R.id.cancelar:
                setFakeActionModeOn(false);
                break;

            //Cancela o Fake Action Mode
            case R.id.deletar:
                //deletar_materias();
                setFakeActionModeOn(false);
                break;
        }
    }

    @Override
    public void onDetach() {
        MateriasActivity activity = ((MateriasActivity)getActivity());
        activity.reload();
        super.onDetach();
    }


}

/**
 * O BaseAdapter é responsável por construir as views do gridview
 */
class GaleriaAdapter extends BaseAdapter {

    ArrayList<Foto> fotos;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    GaleriaAdapter(Context context) {
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

            //Se imagem foi deletada por algum motivo
            if (imageData == null) {
                item.delete();
                Singleton.getTopico_selecionado().popularFotos();
                fotos = (ArrayList<Foto>) Singleton.getTopico_selecionado().getFotos();
                //Singleton.getGaleriaFragment().reload();
                this.notifyDataSetChanged();
            }
            else {

                Bitmap thumbnail = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
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
