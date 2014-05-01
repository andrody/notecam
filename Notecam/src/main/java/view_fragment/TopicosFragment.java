package view_fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import helper.DatabaseHelper;
import helper.Singleton;
import list.TopicosAdapter;
import model.Subject;

public class TopicosFragment extends Fragment {

    private DatabaseHelper db;


    private Subject materia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout that contains the ExpandableListView
        View view =  inflater.inflate(R.layout.fragment_topicos, container, false);
        final ExpandableListView list = (ExpandableListView)view.findViewById(R.id.expandableListView_aulas);
        list.setAdapter(new TopicosAdapter(getActivity(), materia));

        list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (list.isGroupExpanded(groupPosition)) {
                    list.collapseGroup(groupPosition);
                } else {
                    list.expandGroup(groupPosition);
                    list.smoothScrollToPositionFromTop(groupPosition,0);

                }

                return true;
            }
        });

        return view;
    }

    private Singleton.OnFragmentInteractionListener mListener;

    public static TopicosFragment newInstance(int materia_id) {
        TopicosFragment fragment = new TopicosFragment();
        Bundle args = new Bundle();
        args.putInt(Singleton.MATERIA_ID, materia_id);
        args.putString(Singleton.FRAGMENT_TYPE, Singleton.FRAGMENT_TYPE_MATERIA);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TopicosFragment() {
    }

    /**
     * Cria as opções do header
     **/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main, menu);
        //super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.materias, menu);

        getActivity().getActionBar().setTitle(materia.getName() + " / Tópicos");
        getActivity().getActionBar().setSubtitle("Sem aulas hoje");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tem de habilitar para mudar o ActionBar
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            int materia_id = getArguments().getInt(Singleton.MATERIA_ID);
            db = ((MateriasActivity)getActivity()).getDb();
            if(!db.getAllSubjects().isEmpty())
                materia = db.getSubject((long) materia_id);
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
