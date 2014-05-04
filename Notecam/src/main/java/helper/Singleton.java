package helper;

import android.content.ContentValues;
import android.net.Uri;

import model.Subject;
import photo.PictureTaker;
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

    public static String FRAGMENT_TYPE = "fragment_type";
    public static String FRAGMENT_TYPE_MATERIAS = "fragment_materias";
    public static String FRAGMENT_TYPE_MATERIA = "fragment_materia";

    public static int DIRECT_EDIT_SUBJECT = 0;
    public static int DIRECT_ADD_SUBJECT = 1;

    public static MateriasFragment materiasFragment = null;
    public static SingleMateriaFragment singleMateriaFragment = null;
    public static TopicosFragment topicosFragment = null;

    private static Subject materia_selecionada = null;
    private static Subject materia_em_aula = null;

    private static PictureTaker pictureTaker;

    public static Subject getMateria_selecionada() {
        return materia_selecionada;
    }

    public static void setMateria_selecionada(Subject materia) {
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

    public static Subject getMateria_em_aula() {
        return materia_em_aula;
    }

    public static void setMateria_em_aula(Subject materia_em_aula) {
        Singleton.materia_em_aula = materia_em_aula;
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
}
