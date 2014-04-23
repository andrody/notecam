package view_fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import helper.DatabaseHelper;
import helper.Singleton;
import model.Subject;

public class SingleMateriaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MATERIA_ID = "materia_id";

    // TODO: Rename and change types of parameters
    private Subject materia;
    private DatabaseHelper db;

    private Singleton.OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param materia_id Parameter 1.
     * @return A new instance of fragment MateriaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleMateriaFragment newInstance(int materia_id) {
        SingleMateriaFragment fragment = new SingleMateriaFragment();
        Bundle args = new Bundle();
        args.putInt(MATERIA_ID, materia_id);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleMateriaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int materia_id = getArguments().getInt(MATERIA_ID);
            db = ((MateriasActivity)getActivity()).getDb();
            materia = db.getSubject((long)materia_id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_materia, container, false);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
