package helper;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Foto;
import model.Materia;
import model.Topico;
import camera.PictureTaker;
import view_fragment.AddMateriaFragment;
import view_fragment.CameraFragment;
import view_fragment.GaleriaFragment;
import view_fragment.MateriasFragment;
import view_fragment.SingleMateriaFragment;
import view_fragment.TopicosFragment;

/**
 * Created by Andrew on 19-Apr-14.
 */
public class Singleton {
    public static String TITLE = "title";
    public static String REPLACE_FRAGMENT = "replace_fragment";
    public static String MATERIA = "materia";
    public static String MATERIA_ID = "materia_id";
    public static String TOPICO_ID = "topico_id";
    public static String REDIRECT = "redirect";
    public static String COLOR = "color";
    public static int IMAGE_PICKER_SELECT = 2;
    public static int THUMBNAIL_SIZE = 100;
    public static String APPNAME = "Notecam";
    //public static final String NOTECAM_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" +APPNAME;
    public static final String NOTECAM_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM" + "/" +APPNAME;

    public static String FRAGMENT_TYPE = "fragment_type";
    public static String FRAGMENT_TYPE_MATERIAS = "fragment_materias";
    public static String FRAGMENT_TYPE_MATERIA = "fragment_materia";

    public static int DIRECT_EDIT_SUBJECT = 0;
    public static int DIRECT_ADD_SUBJECT = 1;

    public static MateriasFragment materiasFragment = null;
    public static SingleMateriaFragment singleMateriaFragment = null;
    private static TopicosFragment topicosFragment = null;
    private static AddMateriaFragment addMateriaFragment = null;
    private static GaleriaFragment galeriaFragment = null;
    private static CameraFragment cameraFragment = null;

    private static MateriasActivity materiasActivity;
    private static DatabaseHelper db;

    private static Materia materia_selecionada = null;
    private static Materia materia_em_aula = null;
    private static Topico topico_selecionado = null;

    private static boolean primeiraFoto = true;

    private static PictureTaker pictureTaker;

    private static ProgressDialog progress;
    private static  CustomAsyncTask asyncTask;



    public static void resetarSingleton(){
        singleMateriaFragment = null;
    }

    public static Materia getMateria_selecionada() {
        return materia_selecionada;
    }

    public static void setMateria_selecionada(Materia materia) {
        materia_selecionada = materia;
        if(singleMateriaFragment != null) {
            singleMateriaFragment.reload(materia);
        }
    }

    public static PictureTaker getPictureTaker() {
        return pictureTaker;
    }

    public static void setPictureTaker(PictureTaker pictureTaker) {
        Singleton.pictureTaker = pictureTaker;
    }

    public static Materia getMateria_em_aula() {
        return materia_em_aula;
    }

    public static void setMateria_em_aula(Materia materia_em_aula) {
        Singleton.materia_em_aula = materia_em_aula;
    }

    public static MateriasActivity getMateriasActivity() {
        return materiasActivity;
    }

    public static void setMateriasActivity(MateriasActivity activity) {
        materiasActivity = activity;
    }

    public static AddMateriaFragment getAddMateriaFragment() {
        return addMateriaFragment;
    }

    public static void setAddMateriaFragment(AddMateriaFragment addMateriaFragment) {
        Singleton.addMateriaFragment = addMateriaFragment;
    }

    public static DatabaseHelper getDb() {
        return db;
    }

    public static void setDb(DatabaseHelper db) {
        Singleton.db = db;
    }

    public static TopicosFragment getTopicosFragment() {
        return topicosFragment;
    }

    public static void setTopicosFragment(TopicosFragment topicosFragment) {
        Singleton.topicosFragment = topicosFragment;
    }

    public static boolean isPrimeiraFoto() {
        return primeiraFoto;
    }

    public static void setPrimeiraFoto(boolean primeiraFoto) {
        Singleton.primeiraFoto = primeiraFoto;
    }

    public static GaleriaFragment getGaleriaFragment() {
        return galeriaFragment;
    }

    public static void setGaleriaFragment(GaleriaFragment galeriaFragment) {
        Singleton.galeriaFragment = galeriaFragment;
    }

    public static Topico getTopico_selecionado() {
        return topico_selecionado;
    }

    public static void setTopico_selecionado(Topico topico_selecionado) {
        Singleton.topico_selecionado = topico_selecionado;
    }

    public static CameraFragment getCameraFragment() {
        return cameraFragment;
    }

    public static void setCameraFragment(CameraFragment cameraFragment) {
        Singleton.cameraFragment = cameraFragment;
    }

    public static ProgressDialog getProgress() {
        return progress;
    }

    public static void setProgress(ProgressDialog progress) {
        Singleton.progress = progress;
    }

    public static CustomAsyncTask getAsyncTask() {
        return asyncTask;
    }

    public static void setAsyncTask(CustomAsyncTask asyncTask) {
        Singleton.asyncTask = asyncTask;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri, ContentValues content);
    }


    public static Bitmap getPreview(String uri) {
        File image = new File(uri);

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / THUMBNAIL_SIZE;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    public static void deleteFile(String path) {
        ContentResolver resolver = getMateriasActivity().getContentResolver();
        resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{path});
    }

    public static int get_dp_in_px(int padding_in_dp){
        final float scale = getMateriasActivity().getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    public static void setActionBarColor(int color) {
        ActionBar actionBar = getMateriasActivity().getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

    }

    public static void changeFragments(final Fragment fragment){
        getMateriasActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getMateriasActivity().changeFragments(fragment, null);

            }
        });
    }

    public static void mudarCorHeader(Fragment fragment, int color){
        View fake_action_bar = fragment.getView().findViewById(R.id.fake_action_bar);
        fake_action_bar.setBackgroundColor(color);
    }

    public static ArrayList<Integer> getListaCores(){
        ArrayList<Integer> cores = new ArrayList<Integer>();

        cores.add(getMateriasActivity().getResources().getColor(R.color.pink));
        cores.add(getMateriasActivity().getResources().getColor(R.color.amarelo));
        cores.add(getMateriasActivity().getResources().getColor(R.color.azul));
        cores.add(getMateriasActivity().getResources().getColor(R.color.verde));
        cores.add(getMateriasActivity().getResources().getColor(R.color.red));
        cores.add(getMateriasActivity().getResources().getColor(R.color.roxo));
        cores.add(getMateriasActivity().getResources().getColor(R.color.blue));
        cores.add(getMateriasActivity().getResources().getColor(R.color.green));
        cores.add(getMateriasActivity().getResources().getColor(R.color.yellow));
        cores.add(getMateriasActivity().getResources().getColor(R.color.orange));
        cores.add(getMateriasActivity().getResources().getColor(R.color.purple));
        cores.add(getMateriasActivity().getResources().getColor(R.color.gray));

        return cores;
    }

    public static ArrayList<Integer> getListaIcones(){

        ArrayList<Integer> icones = new ArrayList<Integer>();
        icones.add(R.drawable.lab);
        icones.add(R.drawable.ic_sobre);
        icones.add(R.drawable.ic_salvar);
        return icones;
    }

    public static void move_fotos(ArrayList<Foto> fotos, String novo_topico){
        String photoFolder = Singleton.NOTECAM_FOLDER + "/" + getMateria_selecionada().getName() + "/" + getMateria_selecionada().getName() + "-";

        File storageDir = new File(photoFolder + getTopico_selecionado().getName());
        File novo_folder = new File(photoFolder + novo_topico);
        storageDir.renameTo(novo_folder);

        File old_file = null;
        for (Foto foto : fotos) {
            File new_file = new File(novo_folder, foto.getName() + ".jpg");
            old_file = new File(foto.getPath());

            old_file.renameTo(new_file);

            //Desescaneia arquivo antigo
            deleteFile(foto.getPath());

            foto.setPath(new_file.getAbsolutePath());
            escanear_foto(foto, getTopico_selecionado());
        }

        if(getGaleriaFragment() != null)
            //Setando Titulo do Action Bar
            ((TextView)getGaleriaFragment().getView().findViewById(R.id.header_text)).setText(novo_topico);
    }


    public static void escanear_foto(final Foto foto, final Topico topico){

        //Faz a foto aparecer na galeria de fotos do android
        MediaScannerConnection.scanFile(getMateriasActivity(),
                new String[]{foto.getPath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("LOG", "scanned : " + path);
                        foto.setUri(uri);
                        foto.save();

                        if (topico != null)
                            topico.popularFotos();

                        BaseAdapter adapter = (BaseAdapter) getTopicosFragment().getLista().getAdapter();
                        adapter.notifyDataSetChanged();

                        getGaleriaFragment().reload();
                    }
                }
        );

    }

    public static void showProgressDialog(String mensagem){
        setProgress(new ProgressDialog(getMateriasActivity()));

        getProgress().setMessage(mensagem);
        getProgress().setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        getProgress().setIndeterminate(false);
        getProgress().setMax(100);
        getProgress().show();
    }



    public static void gerar_pdf(final Materia materia,final List<Topico> topicos){
        setAsyncTask(new CustomAsyncTask() {
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                getProgress().setProgress(values[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog("Gerando PDF...");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                getProgress().dismiss();



                //Open PDF
                File file = new File(this.path_gerado);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getMateriasActivity().startActivity(intent);
            }

            @Override
            protected String doInBackground(Context... params) {

                PdfCreator pdfCreator = new PdfCreator();
                Collections.reverse(topicos);
                pdfCreator.criarPDF(materia, topicos);
                return null;
            }

            @Override
            public void updateProgress(int i) {
                publishProgress(i);
            }

        });

        getAsyncTask().execute();

    }


}


