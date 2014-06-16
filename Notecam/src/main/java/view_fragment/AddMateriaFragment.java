package view_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.lang.reflect.Field;
import java.util.List;

import helper.DatabaseHelper;
import helper.Singleton;
import model.Aula;
import model.Materia;


public class AddMateriaFragment extends Fragment {
    @Override
    public void onDetach() {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        MateriasActivity activity = ((MateriasActivity)getActivity());
        activity.reload();
        //activity.changeFragments(activity.getMateriasFragment(),this);
        super.onDetach();
    }

    //Armazena o model do Subject em questão
    private Materia materia;

    //Guarda uma referência ao fragment das classes
    private AddAulasFragment addAulasFragment;

    //Flag para saber se o usuario clicou em OK (Salva dados no Banco) ou Cancel (Descarta mudanças e volta pra tela anterior)
    private boolean flagActionModeCancel = false;

    //Flag para saber se ele está editando da home
    private boolean flagEditFromHome = false;

    public static AddMateriaFragment newInstance(int materia_id) {
        AddMateriaFragment fragment = new AddMateriaFragment();
        Bundle args = new Bundle();
        args.putInt(Singleton.MATERIA_ID, materia_id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // needed to indicate that the fragment would
        // like to add items to the Options Menu
        setHasOptionsMenu(true);

        // update the actionbar to show the up carat/affordance
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Cria as opções do header
     **/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.add_materia, menu);

        try {
            Singleton.setActionBarTitle("Criar ou editar matéria");
        }
        catch (NullPointerException e){
            e.printStackTrace();

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        switch (item.getItemId()) {
            case android.R.id.home:
                //called when the up affordance/carat in actionbar is pressed
                getActivity().onBackPressed();
                return true;
            case R.id.colorselect:
                openColorDialog();
                return true;
            case R.id.iconselect:
                openIconDialog();
                return true;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_materia, container, false);

        //Referencia para o banco de dados
        DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

        //Cria novo materia
        materia = new Materia(getActivity());

        //Se foi passado algum parametro, adiciona no materia
        if (getArguments() != null) {

            //Se é modo edição
            if(getArguments().containsKey(Singleton.MATERIA_ID)){
                flagEditFromHome = true;
                int materia_id = getArguments().getInt(Singleton.MATERIA_ID);

                materia.setId(getArguments().getInt(Materia.ID));
                materia = db.getSubject(materia_id);

                assert view != null;
                ((EditText)view.findViewById(R.id.editText_subject)).setText(materia.getName());

                TextView text_criar_materia = (TextView) view.findViewById(R.id.text_criar_materia);
                text_criar_materia.setText("Pronto");

                FrameLayout button_criar_materia = (FrameLayout) view.findViewById(R.id.btn_criar_materia);
                button_criar_materia.setVisibility(View.VISIBLE);

                final FrameLayout container_das_aulas =  (FrameLayout) view.findViewById(R.id.fragment_container).getParent();
                container_das_aulas.setPadding(0,0,0,Singleton.get_dp_in_px(75));

            }


        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Listener do botão "Adicionar Aulas"
        LinearLayout btn_nova_aula= (LinearLayout) getView().findViewById(R.id.button_addClass);
        btn_nova_aula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClass(v);

                //Recolher teclado
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        //Listener do botão "Criar Matéria"
        final FrameLayout button_criar_materia = (FrameLayout) getView().findViewById(R.id.btn_criar_materia);
        button_criar_materia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criar_ou_editar_materia();
            }
        });

        //Listener do EditText "Nome da materia"
        EditText nome_da_materia = (EditText) getView().findViewById(R.id.editText_subject);

        final FrameLayout container_das_aulas =  (FrameLayout) getView().findViewById(R.id.fragment_container).getParent();


        nome_da_materia.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count >= 1) {
                    button_criar_materia.setVisibility(View.VISIBLE);
                    container_das_aulas.setPadding(0,0,0, Singleton.get_dp_in_px(75));
                }
                else {
                    button_criar_materia.setVisibility(View.INVISIBLE);
                    container_das_aulas.setPadding(0,0,0,0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });





        //Cria novo Fragmento de Aulas
        setAddAulasFragment(new AddAulasFragment());
        getAddAulasFragment().setMateria(materia);

        //Faz as transactions do fragmento das classes
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, getAddAulasFragment());
        transaction.commit();

        //Inicia o modo ActionMode (Header fica branco e com opções especializadas)
        //getActivity().startActionMode(mActionModeCallback);

        //Fazer teclado desaparecer ao EditText perder foco (Por algum motivo ele não perde sozinho)
        (getView().findViewById(R.id.editText_subject)).setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(v.getId() == R.id.editText_subject && !hasFocus) {
                    InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });
    }

    public void openColorDialog(){
        //Toast.makeText(getActivity(), "teste", Toast.LENGTH_SHORT).show();

        Bundle args = null;

        //Se está em modo de edição
        /*if(!(materia.getId() <= 0)){

            //Cria um Bundle e passa a cor da materia
            args = new Bundle();
            args.putInt(Singleton.COLOR, materia.getColor());
        }*/

        //Cria um dialog passa os argumentos
        ColorPickerFragment colorDialog = new ColorPickerFragment();
        //colorDialog.setArguments(args);
        colorDialog.setMateria(materia);
        colorDialog.show(getFragmentManager(), "Selecione uma cor");



    }

    public void openIconDialog(){

        //Cria um dialog passa os argumentos
        IconPickerFragment iconDialog = new IconPickerFragment();
        //colorDialog.setArguments(args);
        iconDialog.setMateria(materia);
        iconDialog.show(getFragmentManager(), "Selecione um icone");



    }

    public void criar_ou_editar_materia(){
        //Pega o nome do materia digitado pelo usuario
        String subjectName = ((EditText)getView().findViewById(R.id.editText_subject)).getText().toString();

        //Se ele clicou no icone de OK e digitou um nome com mais de 1 letra, salva mudanças
        if(!flagActionModeCancel && subjectName.length() > 0){

            //Primeira letra maiuscula
            String name = Character.toUpperCase(subjectName.charAt(0)) + subjectName.substring(1).toLowerCase();

            //Atualiza o nome do materia no model do materia
            materia.setName(name);

            if(!materia.isColored())
                materia.setRandomColor();

            //Pega as classes que foram adicionadas/alteradas
            List<Aula> aulas = addAulasFragment.getAdapter().getItems();

            //Pega a referência do banco de dados
            DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

            //Se Não existe no DB ainda, então não possui ID
            if(materia.getId() <= 0){
                db.createSubjectAndClasses(materia, aulas);
                List<model.Materia> materias = db.getAllSubjects();
                Singleton.setMateria_selecionada(materias.get(materias.size() - 1));
            }

            //Se já existe no Banco, apenas da um update
            else {
                db.updateSubjectAndClasses(materia, aulas);
            }

            ((MateriasActivity)getActivity()).setEmptyFragments(db.getAllSubjects().isEmpty());
            ((MateriasActivity)getActivity()).getViewPager().getAdapter().notifyDataSetChanged();
            ((MateriasActivity)getActivity()).checarHorario();


        }

        //if (flagEditFromHome)
        getActivity().onBackPressed();
        //else
        //Volta pra tela anterior
        //    getActivity().getSupportFragmentManager().popBackStackImmediate();

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
            //Pega o nome do materia digitado pelo usuario
            String subjectName = ((EditText)getView().findViewById(R.id.editText_subject)).getText().toString();

            //Se ele clicou no icone de OK e digitou um nome com mais de 1 letra, salva mudanças
            if(!flagActionModeCancel && subjectName.length() > 0){

                //Primeira letra maiuscula
                String name = Character.toUpperCase(subjectName.charAt(0)) + subjectName.substring(1).toLowerCase();

                //Atualiza o nome do materia no model do materia
                materia.setName(name);

                if(!materia.isColored())
                    materia.setRandomColor();

                //Pega as classes que foram adicionadas/alteradas
                List<Aula> aulas = addAulasFragment.getAdapter().getItems();

                //Pega a referência do banco de dados
                DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

                //Se Não existe no DB ainda, então não possui ID
                if(materia.getId() <= 0){
                    db.createSubjectAndClasses(materia, aulas);
                    List<model.Materia> materias = db.getAllSubjects();
                    Singleton.setMateria_selecionada(materias.get(materias.size() - 1));
                }

                //Se já existe no Banco, apenas da um update
                else {
                    db.updateSubjectAndClasses(materia, aulas);
                }

                ((MateriasActivity)getActivity()).setEmptyFragments(db.getAllSubjects().isEmpty());
                ((MateriasActivity)getActivity()).getViewPager().getAdapter().notifyDataSetChanged();
                ((MateriasActivity)getActivity()).checarHorario();


            }

            //if (flagEditFromHome)
            getActivity().onBackPressed();
            //else
            //Volta pra tela anterior
            //    getActivity().getSupportFragmentManager().popBackStackImmediate();


        }
    };

    //Cria uma nova classe
    public void addClass(View v){
        getAddAulasFragment().addElement();
    }

    public AddAulasFragment getAddAulasFragment() {
        return addAulasFragment;
    }

    public void setAddAulasFragment(AddAulasFragment addAulasFragment) {
        this.addAulasFragment = addAulasFragment;
    }
}