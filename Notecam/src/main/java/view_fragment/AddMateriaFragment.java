package view_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.lang.reflect.Field;
import java.util.List;

import Dialogs.ColorPickerFragment;
import Dialogs.IconPickerFragment;
import helper.DatabaseHelper;
import helper.Singleton;
import model.Aula;
import model.Materia;


public class AddMateriaFragment extends Fragment implements View.OnClickListener {
    private boolean criando_primeira_materia = false;
    private boolean modo_edicao = false;

    @Override
    public void onDetach() {
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }


        MateriasActivity activity = Singleton.getMateriasActivity();

        if (!criando_primeira_materia)
            activity.reload();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_materia, container, false);

        //Referencia para o banco de dados
        DatabaseHelper db = ((MateriasActivity) getActivity()).getDb();

        //Cria novo materia
        materia = new Materia(getActivity());

        //Se é modo edição
        if (modo_edicao) {

            flagEditFromHome = true;

            //materia.setId(getArguments().getInt(Materia.ID));
            materia = db.getSubject(Singleton.getMateria_selecionada().getId());

            assert view != null;
            ((EditText) view.findViewById(R.id.editText_subject)).setText(materia.getName());

            TextView text_criar_materia = (TextView) view.findViewById(R.id.text_criar_materia);
            text_criar_materia.setText(getActivity().getString(R.string.pronto));

            FrameLayout button_criar_materia = (FrameLayout) view.findViewById(R.id.btn_criar_materia);
            button_criar_materia.setVisibility(View.VISIBLE);

            final FrameLayout container_das_aulas = (FrameLayout) view.findViewById(R.id.fragment_container).getParent();
            container_das_aulas.setPadding(0, 0, 0, Singleton.get_dp_in_px(75));


        }

        //Se matéria ainda não possui cor
        if (!materia.isColored()) {
            materia.setRandomColor();
        } else {
            //Se não possuir cor, o que significa que é modo de edição, muda o Header para "Editar Matéria"
            TextView header_text = (TextView) view.findViewById(R.id.header_text);
            header_text.setText(getActivity().getString(R.string.editar_materia));
        }

        //Faz o header mudar para a cor selecionada
        View fake_action_bar = view.findViewById(R.id.fake_action_bar);
        fake_action_bar.setBackgroundColor(materia.getColor());

        //Se matéria ainda não possui cor
        if (!materia.isIconed()) {
            materia.setRandomIcon();
        }

        //Faz o icone do header mudar pro icone selecionado
        ImageView icone_materia = (ImageView) view.findViewById(R.id.icone_materia);
        icone_materia.setImageResource(materia.getIcon_id());


        //Setando listener do botão de Voltar
        view.findViewById(R.id.back).setOnClickListener(this);

        //Setando listener do botão de Selecionar Cor
        view.findViewById(R.id.colorselect).setOnClickListener(this);

        //Setando listener do botão de Selecionar Cor
        view.findViewById(R.id.iconselect).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Listener do botão "Adicionar Aulas"
        LinearLayout btn_nova_aula = (LinearLayout) getView().findViewById(R.id.button_addClass);
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

        final FrameLayout container_das_aulas = (FrameLayout) getView().findViewById(R.id.fragment_container).getParent();


        nome_da_materia.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count >= 1) {
                    button_criar_materia.setVisibility(View.VISIBLE);
                    container_das_aulas.setPadding(0, 0, 0, Singleton.get_dp_in_px(75));
                } else {
                    button_criar_materia.setVisibility(View.INVISIBLE);
                    container_das_aulas.setPadding(0, 0, 0, 0);
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
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, getAddAulasFragment());
        transaction.commit();

        //Fazer teclado desaparecer ao EditText perder foco (Por algum motivo ele não perde sozinho)
        (getView().findViewById(R.id.editText_subject)).setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (v.getId() == R.id.editText_subject && !hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }

        });
    }

    public void openColorDialog() {

        //Cria um dialog passa os argumentos
        ColorPickerFragment colorDialog = new ColorPickerFragment();
        //colorDialog.setArguments(args);
        colorDialog.setMateria(materia);
        colorDialog.show(getFragmentManager(), getActivity().getString(R.string.select_a_color));


    }

    public void openIconDialog() {

        //Cria um dialog passa os argumentos
        IconPickerFragment iconDialog = new IconPickerFragment();
        //colorDialog.setArguments(args);
        iconDialog.setMateria(materia);
        iconDialog.show(getFragmentManager(), getActivity().getString(R.string.select_a_icon));

    }

    public void criar_ou_editar_materia() {
        //Pega o nome do materia digitado pelo usuario
        String subjectName = ((EditText) getView().findViewById(R.id.editText_subject)).getText().toString();

        //Se ele clicou no icone de OK e digitou um nome com mais de 1 letra, salva mudanças
        if (!flagActionModeCancel && subjectName.length() > 0) {

            //Primeira letra maiuscula
            String name = Character.toUpperCase(subjectName.charAt(0)) + subjectName.substring(1).toLowerCase();

            //Atualiza o nome do materia no model do materia
            materia.setName(name);

            if (!materia.isColored())
                materia.setRandomColor();

            //Pega as classes que foram adicionadas/alteradas
            List<Aula> aulas = addAulasFragment.getAdapter().getItems();

            //Pega a referência do banco de dados
            DatabaseHelper db = ((MateriasActivity) getActivity()).getDb();

            //Se Não existe no DB ainda, então não possui ID
            if (materia.getId() <= 0) {
                materia.setId(db.createSubjectAndClasses(materia, aulas));
            }

            //Se já existe no Banco, apenas da um update
            else {
                db.updateSubjectAndClasses(materia, aulas);
            }

            Singleton.setMateria_selecionada(materia);
            Singleton.setNova_materia_selecionada(true);

            if (((MateriasActivity) getActivity()).isEmptyFragments()) {
                ((MateriasActivity) getActivity()).setEmptyFragments(false);
                this.criando_primeira_materia = true;
                Singleton.changeFragments(new MateriasFragment());
            }


            ((MateriasActivity) getActivity()).getViewPager().getAdapter().notifyDataSetChanged();
            if (!criando_primeira_materia) {
                Singleton.getMateriasFragment().syncDB();
                //((MateriasActivity)getActivity()).setEmptyFragments(db.getAllSubjects().isEmpty());
                ((MateriasActivity) getActivity()).getViewPager().getAdapter().notifyDataSetChanged();
                ((MateriasActivity) getActivity()).checarHorario();
            }

        }

        if (!criando_primeira_materia)
            getActivity().onBackPressed();


    }


    //Cria uma nova classe
    public void addClass(View v) {
        getAddAulasFragment().addElement();
    }

    public AddAulasFragment getAddAulasFragment() {
        return addAulasFragment;
    }

    public void setAddAulasFragment(AddAulasFragment addAulasFragment) {
        this.addAulasFragment = addAulasFragment;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Volta para o fragment anterior
            case R.id.back:
                getActivity().onBackPressed();
                break;

            //Abre Color Select Dialog
            case R.id.colorselect:
                openColorDialog();
                break;

            //Abre Color Select Dialog
            case R.id.iconselect:
                openIconDialog();
                break;
        }
    }

    public boolean isModo_edicao() {
        return modo_edicao;
    }

    public void setModo_edicao(boolean modo_edicao) {
        this.modo_edicao = modo_edicao;
    }
}