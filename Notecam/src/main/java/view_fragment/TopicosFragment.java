package view_fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    ExpandableListView lista;
    private Subject materia;


    @Override
    public void onResume() {
        super.onResume();
        reload(Singleton.getMateria_selecionada());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Se o botão selecionado pelo usuario for o de ver as fotos (icone pasta)
        if(item.getTitle().equals(getResources().getString(R.string.add_topico))){

            AddTopicoFragment addTopicoFragment = AddTopicoFragment.newInstance(materia.getId(), -1);

            ((MateriasActivity)getActivity()).changeFragments(addTopicoFragment, null);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout that contains the ExpandableListView
        View view =  inflater.inflate(R.layout.fragment_topicos, container, false);
        final ExpandableListView list = (ExpandableListView)view.findViewById(R.id.expandableListView_aulas);
        lista = list;
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
        inflater.inflate(R.menu.topicos, menu);

        try {
            getActivity().getActionBar().setTitle(materia.getName() + " / Tópicos");
            if(Singleton.getMateria_em_aula() != null && Singleton.getMateria_selecionada().getId() == Singleton.getMateria_em_aula().getId())
                getActivity().getActionBar().setSubtitle("Em aula!");
            else
                getActivity().getActionBar().setSubtitle("Sem aula agora");
        }
        catch (NullPointerException e){
            e.printStackTrace();

        }



        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tem de habilitar para mudar o ActionBar
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            //int materia_id = getArguments().getInt(Singleton.MATERIA_ID);
            //db = ((MateriasActivity)getActivity()).getDb();
            //if(!db.getAllSubjects().isEmpty())
            //    materia = db.getSubject((long) materia_id);
            this.materia = Singleton.getMateria_selecionada();
        }
    }

    public void reload(Subject nova_materia){
        this.materia = nova_materia;

        lista.setAdapter(new TopicosAdapter(getActivity(), Singleton.getMateria_selecionada()));

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
