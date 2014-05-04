package view_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.lang.reflect.Field;
import java.util.List;

import helper.DatabaseHelper;
import helper.Singleton;
import model.Aula;
import model.Subject;


public class AddSubjectFragment extends Fragment {
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
    private Subject materia;

    //Guarda uma referência ao fragment das classes
    private AddClassesFragment addClassesFragment;

    //Flag para saber se o usuario clicou em OK (Salva dados no Banco) ou Cancel (Descarta mudanças e volta pra tela anterior)
    private boolean flagActionModeCancel = false;

    //Flag para saber se ele está editando da home
    private boolean flagEditFromHome = false;

    public static AddSubjectFragment newInstance(int materia_id) {
        AddSubjectFragment fragment = new AddSubjectFragment();
        Bundle args = new Bundle();
        args.putInt(Singleton.MATERIA_ID, materia_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_subject, container, false);

        //Referencia para o banco de dados
        DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

        //Cria novo materia
        materia = new Subject(getActivity());

        //Se foi passado algum parametro, adiciona no materia
        if (getArguments() != null) {

            //Se é modo edição
            if(getArguments().containsKey(Singleton.MATERIA_ID)){
                flagEditFromHome = true;
                int materia_id = getArguments().getInt(Singleton.MATERIA_ID);

                materia.setId(getArguments().getInt(Subject.ID));
                materia = db.getSubject(materia_id);

                assert view != null;
                ((EditText)view.findViewById(R.id.editText_subject)).setText(materia.getName());
            }


        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Listener do botão "Adicionar Classe"
        Button button= (Button) getView().findViewById(R.id.button_addClass);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addClass(v);

                //Recolher teclado
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        //Cria novo Fragmento de Classes
        setAddClassesFragment(new AddClassesFragment());
        getAddClassesFragment().setSubject(materia);

        //Faz as transactions do fragmento das classes
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, getAddClassesFragment());
        transaction.commit();

        //Inicia o modo ActionMode (Header fica branco e com opções especializadas)
        getActivity().startActionMode(mActionModeCallback);

        //Fazer teclado desaparecer ao EditText perder foco (Por algum motivo ele não perde sozinho)
        ((EditText)getView().findViewById(R.id.editText_subject)).setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(v.getId() == R.id.editText_subject && !hasFocus) {
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
            //Pega o nome do materia digitado pelo usuario
            String subjectName = ((EditText)getView().findViewById(R.id.editText_subject)).getText().toString();

            //Se ele clicou no icone de OK e digitou um nome com mais de 1 letra, salva mudanças
            if(!flagActionModeCancel && subjectName.length() > 0){

                //Primeira letra maiuscula
                String name = Character.toUpperCase(subjectName.charAt(0)) + subjectName.substring(1).toLowerCase();

                //Atualiza o nome do materia no model do materia
                materia.setName(name);

                //if(!materia.isColored())
                materia.setRandomColor();

                //Pega as classes que foram adicionadas/alteradas
                List<Aula> aulas = addClassesFragment.getAdapter().getItems();

                //Pega a referência do banco de dados
                DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

                //Se Não existe no DB ainda, então não possui ID
                if(materia.getId() <= 0){
                    db.createSubjectAndClasses(materia, aulas);
                    List<Subject> materias = db.getAllSubjects();
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
        getAddClassesFragment().addElement();
    }

    public AddClassesFragment getAddClassesFragment() {
        return addClassesFragment;
    }

    public void setAddClassesFragment(AddClassesFragment addClassesFragment) {
        this.addClassesFragment = addClassesFragment;
    }
}