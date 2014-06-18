package view_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import Dialogs.CreateTopicoDialog;
import helper.DatabaseHelper;
import helper.Singleton;
import list.TopicosAdapter;
import model.Materia;

public class TopicosFragment extends Fragment implements View.OnClickListener {

    private DatabaseHelper db;

    private ListView lista;
    private Materia materia;


    @Override
    public void onResume() {
        super.onResume();
        reload(Singleton.getMateria_selecionada());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Se o botão selecionado pelo usuario for o de ver as fotos (icone pasta)
        if (item.getTitle().equals(getResources().getString(R.string.add_topico))) {

            AddTopicoFragment addTopicoFragment = AddTopicoFragment.newInstance(materia.getId(), -1);

            ((MateriasActivity) getActivity()).changeFragments(addTopicoFragment, null);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topicos, container, false);
        final ListView list = (ListView) view.findViewById(R.id.topicos_list);
        setLista(list);
        list.setAdapter(new TopicosAdapter(getActivity(), materia));

        //Setando listener do botão de Menu
        view.findViewById(R.id.back).setOnClickListener(this);

        //Setando listener do botão de Editar Matéria
        view.findViewById(R.id.editar_materia).setOnClickListener(this);

        //Setando listener do botão de Adicionar Topico
        view.findViewById(R.id.adicionar_topico).setOnClickListener(this);


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
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.topicos, menu);

        try {
            Singleton.setActionBarTitle(materia.getName());

            //updateSubTitle();
        } catch (NullPointerException e) {
            e.printStackTrace();

        }
        reload(Singleton.getMateria_selecionada());

        super.onCreateOptionsMenu(menu, inflater);
    }


    public void updateSubTitle() {
        /*if(((MateriasActivity)getActivity()).getViewPager().getCurrentItem() == 2){
            if(Singleton.getMateria_em_aula() != null && Singleton.getMateria_selecionada().getId() == Singleton.getMateria_em_aula().getId())
                getActivity().getActionBar().setSubtitle("Em aula!");
            else
                getActivity().getActionBar().setSubtitle("Sem aula agora");
        }*/

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

    public void reload(model.Materia nova_materia) {
        this.materia = nova_materia;

        getLista().setAdapter(new TopicosAdapter(getActivity(), Singleton.getMateria_selecionada()));
        Singleton.mudarCorHeader(this, materia.getColor());

        TextView nome_materia = (TextView) getView().findViewById(R.id.nome_materia);
        nome_materia.setText(materia.getName());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Abre Drawer Menu
            case R.id.back:
                ((MateriasActivity) getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);
                break;

            //Abre tela de edição de matéria
            case R.id.editar_materia:

                //Cria uma nova instância do Fragment AddMateriaFragment e passa o id da materia como parametro para edição
                AddMateriaFragment addMateriaFragment = AddMateriaFragment.newInstance(materia.getId());
                Singleton.setAddMateriaFragment(addMateriaFragment);

                //Muda o fragment
                Singleton.changeFragments(Singleton.getAddMateriaFragment());
                break;

            //Cria um novo tópico
            case R.id.adicionar_topico:
                OpenCreateTopicoDialog();
        }
    }

    public void OpenCreateTopicoDialog() {
        //Cria um dialog passa os argumentos
        CreateTopicoDialog dialog = new CreateTopicoDialog();
        dialog.setMateria(materia);
        dialog.show(getFragmentManager(), "Digite o nome do Tópico");
    }

    public ListView getLista() {
        return lista;
    }

    public void setLista(ListView lista) {
        this.lista = lista;
    }
}

