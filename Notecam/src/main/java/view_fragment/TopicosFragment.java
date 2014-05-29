package view_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.util.List;

import helper.DatabaseHelper;
import helper.Singleton;
import list.TopicosAdapter;
import model.*;
import model.Materia;

public class TopicosFragment extends Fragment {

    private DatabaseHelper db;

    ExpandableListView lista;
    private Materia materia;



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
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                List<Foto> fotos = materia.getTopicos().get(groupPosition).getFotos();
                /*if(!fotos.isEmpty()){
                    ((MateriasActivity)getActivity()).startScan(fotos.get(1).getPath());
                }
                else {
                    Toast.makeText(getActivity(), "Não há fotos nesse tópico", Toast.LENGTH_SHORT).show();
                }*/
                if(fotos.isEmpty())
                    Toast.makeText(getActivity(), "Não há fotos nesse tópico", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.topicos, menu);

        try {
            getActivity().getActionBar().setTitle(materia.getName() + " / Tópicos");
            updateSubTitle();
        }
        catch (NullPointerException e){
            e.printStackTrace();

        }
        reload(Singleton.getMateria_selecionada());

        super.onCreateOptionsMenu(menu, inflater);
    }



    public void updateSubTitle(){
        if(((MateriasActivity)getActivity()).getViewPager().getCurrentItem() == 2){
            if(Singleton.getMateria_em_aula() != null && Singleton.getMateria_selecionada().getId() == Singleton.getMateria_em_aula().getId())
                getActivity().getActionBar().setSubtitle("Em aula!");
            else
                getActivity().getActionBar().setSubtitle("Sem aula agora");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tem de habilitar para mudar o ActionBar
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            this.materia = Singleton.getMateria_selecionada();
        }
    }

    public void reload(model.Materia nova_materia){
        this.materia = nova_materia;

        lista.setAdapter(new TopicosAdapter(getActivity(), Singleton.getMateria_selecionada()));
        updateSubTitle();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}

