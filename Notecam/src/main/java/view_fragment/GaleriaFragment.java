package view_fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import Dialogs.CreateTopicoDialog;
import camera.CustomCameraHost;
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
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                galeriaAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_galeria, container, false);


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
        view.findViewById(R.id.deletar).setOnClickListener(this);

        //Setando listener do botão de Editar topico
        view.findViewById(R.id.edit_topic).setOnClickListener(this);

        //Setando listener do botão de Adiciona Imagem
        view.findViewById(R.id.add_picture).setOnClickListener(this);

        //Setando listener do botão de Compartilhar
        view.findViewById(R.id.compartilhar).setOnClickListener(this);

        //Setando listener do botão de Cancelar
        view.findViewById(R.id.cancelar).setOnClickListener(this);

        //Setando listener do botão de Cancelar Também
        view.findViewById(R.id.cancelar2).setOnClickListener(this);


        //Setando Titulo do Action Bar
        ((TextView)view.findViewById(R.id.header_text)).setText(Singleton.getTopico_selecionado().getName());

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

    public void open_edit_topic_dialog() {
        //Cria um dialog passa os argumentos
        CreateTopicoDialog dialog = new CreateTopicoDialog();
        dialog.setMateria(Singleton.getMateria_selecionada());
        dialog.setTopico(Singleton.getTopico_selecionado());
        dialog.show(getFragmentManager(), "Digite o nome do Tópico");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //Volta para o fragment anterior
            case R.id.back:
                getActivity().onBackPressed();
                //getActivity().getFragmentManager().popBackStackImmediate();
                break;

            //Volta para o fragment anterior
            case R.id.edit_topic:
                open_edit_topic_dialog();
                break;

            //Volta para o fragment anterior
            case R.id.add_picture:
                add_picture();
                break;

            //Cancela o Fake Action Mode
            case R.id.cancelar:
                setFakeActionModeOn(false);
                break;

            //Cancela o Fake Action Mode
            case R.id.deletar:
                deletar_fotos();

                setFakeActionModeOn(false);
                break;
        }
    }

    public void add_picture(){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Singleton.getMateriasActivity().startActivityForResult(i, Singleton.IMAGE_PICKER_SELECT);

        // To open up a gallery browser
        //Intent intent = new Intent();
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent, "Select Picture"),Singleton.IMAGE_PICKER_SELECT);
    }

    /** * Photo Selection result */
    public void onActivityResult2(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getActivity(), "falha ao mover arquivo", Toast.LENGTH_SHORT).show();
        if (requestCode == Singleton.IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Foto new_foto = new Foto(Singleton.getTopico_selecionado().getName() + "_" + CustomCameraHost.get_proxima_foto_numero(),
                    "", Singleton.getTopico_selecionado());
            new_foto.setUri(uri);
            try {
                new_foto.setPath(getRealPathFromURI(uri));
            }catch (IllegalStateException e){
                Toast.makeText(getActivity(), "Falha! Selecione somente arquivos locais.", Toast.LENGTH_SHORT).show();
            }
            new_foto.save();

            //Singleton.escanear_foto(new_foto, Singleton.getTopico_selecionado());



            Singleton.getTopico_selecionado().popularFotos();
            galeriaAdapter.fotos = (ArrayList<Foto>) Singleton.getTopico_selecionado().getFotos();
            galeriaAdapter.notifyDataSetChanged();


            /*InputStream is = null;
            try {
                is = getActivity().getContentResolver().openInputStream(uri);
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            if(path != null) {
                File old_file= new File(path);

                String nome = Singleton.getTopico_selecionado().getName() + "_" + CustomCameraHost.get_proxima_foto_numero();
                String new_path = Singleton.getTopico_selecionado().get_path() + nome + ".jpg";
                File filename = new File(new_path);

                boolean sucesso = old_file.renameTo(filename);
                if (!sucesso)
                    Toast.makeText(getActivity(), "falha ao mover arquivo", Toast.LENGTH_SHORT).show();

                Foto new_foto = new Foto(nome, filename.getAbsolutePath(), Singleton.getTopico_selecionado());
                new_foto.save();
                Singleton.getTopico_selecionado().popularFotos();

                //Para aparecer na galeria precisa escanear
                Singleton.escanear_foto(new_foto, Singleton.getTopico_selecionado());
            }
            else {
                Toast.makeText(getActivity(), "Erro. Somente funciona com imagens armazenados no dispositivo", Toast.LENGTH_LONG).show();
            }*/



        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = null;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }



    public void deletar_fotos(){

        //Para cada foto da lista
        for(Foto foto : Singleton.getTopico_selecionado().getFotos()) {

            //Descobre em qual a view corresponde a esta foto
            View view = galeriaAdapter.getView(foto.getId());

            try {
                //Pega uma referência para o checkbox dele
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_galeria);

                //Se ele estiver marcado
                if (checkBox.isChecked()) {

                    //Deleta essa foto
                    Singleton.getDb().deleteFoto(foto);
                }
            }catch (NullPointerException e){

            }
        }

        Singleton.getTopico_selecionado().popularFotos();
        galeriaAdapter.fotos = (ArrayList<Foto>) Singleton.getTopico_selecionado().getFotos();
        galeriaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        MateriasActivity activity = ((MateriasActivity)getActivity());
        activity.reload();
        super.onDetach();
    }

    /** * Photo Selection result */
    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Singleton.IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            String nome = Singleton.getTopico_selecionado().getName() + "_" + CustomCameraHost.get_proxima_foto_numero();
            String path = Singleton.getTopico_selecionado().get_path() + nome + ".jpg";
            File filename = new File(path);

            if (!filename.getParentFile().isDirectory())
            {
                filename.getParentFile().mkdirs();
            }

            //Bitmap bitmap = getBitmapFromCameraData(data, getActivity());
            Uri uri = data.getData();
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                is = getActivity().getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();

                boolean sucesso = storeImage(bitmap, path);
                if (sucesso) {
                    Foto foto = new Foto(nome, path, Singleton.getTopico_selecionado());
                    Singleton.escanear_foto(foto, Singleton.getTopico_selecionado());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }





            /*FileOutputStream out = null;
            try {
                out = new FileOutputStream(filename);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    out.close();

                    Foto foto = new Foto(nome, path, Singleton.getTopico_selecionado());
                    Singleton.escanear_foto(foto, Singleton.getTopico_selecionado());

                } catch(Throwable ignore) {}
            }*/

        //}
    //}

    private boolean storeImage(Bitmap imageData, String path) {
        //get path to external storage (SD card)

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /** * Use for decoding camera response data. * * @param data * @param context * @return */
    /*public static Bitmap getBitmapFromCameraData(Intent data, Context context){
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return BitmapFactory.decodeFile(picturePath);
    }*/



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


            Bitmap thumbnail = null;

            if(imageData == null){
                final int THUMBSIZE = 128;

                try {
                    //thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(item.getPath()),THUMBSIZE, THUMBSIZE);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Singleton.getMateriasActivity().getContentResolver(), item.getUri());
                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);
                }catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(Singleton.getMateriasActivity(), "Erro ao carregar uma imagem, excluindo ela...", Toast.LENGTH_SHORT).show();
                }
            }

            //Se imagem foi deletada por algum motivo
            if (imageData == null && thumbnail == null) {
                item.delete();
                Singleton.getTopico_selecionado().popularFotos();
                fotos = (ArrayList<Foto>) Singleton.getTopico_selecionado().getFotos();
                //Singleton.getGaleriaFragment().reload();
                this.notifyDataSetChanged();
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
