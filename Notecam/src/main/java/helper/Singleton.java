package helper;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.io.File;
import java.util.ArrayList;

import model.Materia;
import photo.PictureTaker;
import view_fragment.AddMateriaFragment;
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
    public static int THUMBNAIL_SIZE = 100;
    public static String APPNAME = "Notecam";
    public static final String NOTECAM_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" +APPNAME;

    public static String FRAGMENT_TYPE = "fragment_type";
    public static String FRAGMENT_TYPE_MATERIAS = "fragment_materias";
    public static String FRAGMENT_TYPE_MATERIA = "fragment_materia";

    public static int DIRECT_EDIT_SUBJECT = 0;
    public static int DIRECT_ADD_SUBJECT = 1;

    public static MateriasFragment materiasFragment = null;
    public static SingleMateriaFragment singleMateriaFragment = null;
    private static TopicosFragment topicosFragment = null;
    private static AddMateriaFragment addMateriaFragment = null;

    private static MateriasActivity materiasActivity;
    private static DatabaseHelper db;

    private static Materia materia_selecionada = null;
    private static Materia materia_em_aula = null;

    private static PictureTaker pictureTaker;

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

    public static void setActionBarTitle(String title) {
        Spannable actionBarTitle = new SpannableString(title);
        actionBarTitle.setSpan(
                new ForegroundColorSpan(Color.WHITE),
                0,
                actionBarTitle.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getMateriasActivity().getActionBar().setTitle(actionBarTitle);
    }

    public static void setActionBarColor(int color) {
        ActionBar actionBar = getMateriasActivity().getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(color));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

    }

    public static void changeFragments(android.support.v4.app.Fragment fragment){
        getMateriasActivity().changeFragments(fragment, null);
    }

    public static void mudarCorHeader(android.support.v4.app.Fragment fragment, int color){
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
}
