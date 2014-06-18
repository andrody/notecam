package view_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import helper.DatabaseHelper;
import helper.Singleton;
import model.*;


public class AddTopicoFragment extends Fragment {
    @Override
    public void onDetach() {
        MateriasActivity activity = ((MateriasActivity)getActivity());
        activity.changeFragments(activity.getMateriasFragment(),this);
        super.onDetach();
    }

    //Armazena o model da Materia em questão
    private model.Materia materia;

    //Armazena o model do Subject em questão
    private Topico topico;

    //Referencia do banco
    DatabaseHelper db;

    //Flag para saber se ele está editando
    private boolean flagEdit = false;

    //Flag para saber se o usuario clicou em OK (Salva dados no Banco) ou Cancel (Descarta mudanças e volta pra tela anterior)
    private boolean flagActionModeCancel = false;

    public static AddTopicoFragment newInstance(int materia_id, int topico_id) {
        AddTopicoFragment fragment = new AddTopicoFragment();
        Bundle args = new Bundle();
        args.putInt(Singleton.MATERIA_ID, materia_id);
        args.putInt(Singleton.TOPICO_ID, topico_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_topico, container, false);

        //Referencia para o banco de dados
        db = ((MateriasActivity)getActivity()).getDb();

        //Cria nova materia
        materia = db.getSubject(getArguments().getInt(Singleton.MATERIA_ID));

        //Cria novo topico
        int topico_id = getArguments().getInt(Singleton.TOPICO_ID);
        if(topico_id > 0){
            for (Topico t: materia.getTopicos())
                if (t.getId() == topico_id)
                    this.topico = t;
            this.flagEdit = true;
        }
        else topico = new Topico(getActivity(), "", materia.getId());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Inicia o modo ActionMode (Header fica branco e com opções especializadas)
        getActivity().startActionMode(mActionModeCallback);

        //Fazer teclado desaparecer ao EditText perder foco (Por algum motivo ele não perde sozinho)
        ((EditText)getView().findViewById(R.id.topico_input)).setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(v.getId() == R.id.topico_input && !hasFocus) {
                    InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });
    }

    //Callback para criar e manipular o ActionMode
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            //Cria opção de cancelar alterações
            menu.add("Cancel")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //Se clicou no icone de cancelar, ativa a flag para não salvar mudanças
            if(item.getTitle().equals("Cancel")){
                flagActionModeCancel = true;
            }

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Pega o nome do topico digitado pelo usuario
            String nome_topico = ((EditText)getView().findViewById(R.id.topico_input)).getText().toString();

            //Se ele clicou no icone de OK e digitou um nome com mais de 1 letra, salva mudanças
            if(!flagActionModeCancel && nome_topico.length() > 0){

                //Primeira letra maiuscula
                String name = Character.toUpperCase(nome_topico.charAt(0)) + nome_topico.substring(1).toLowerCase();

                //Atualiza o nome do topico no model do materia
                topico.setName(name);

                //Se Não existe no DB ainda, então não possui ID
                if(!flagEdit)
                    db.createTopico(topico);

                    //Se já existe no Banco, apenas da um update
                else
                    db.updateTopico(topico);
            }

            //if (flagEdit)
            //    getActivity().onBackPressed();
            // else
            //Volta pra tela anterior
            ((MateriasActivity)getActivity()).getViewPager().getAdapter().notifyDataSetChanged();

            getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
            //getActivity().getSupportFragmentManager().beginTransaction().replace(AddTopicoFragment.this);
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            Singleton.singleMateriaFragment.reload(materia);
            Singleton.getTopicosFragment().reload(materia);

        }
    };

}