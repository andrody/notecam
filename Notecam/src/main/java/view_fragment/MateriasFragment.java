package view_fragment;

import android.app.Fragment;
import android.content.res.Resources;


import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.MateriasAdapter;
import helper.DatabaseHelper;
import helper.Singleton;


public class MateriasFragment extends Fragment implements View.OnClickListener {


    // ----------------------------------------------------------//
    // --------------------  Variables Declaration---------------//
    // ----------------------------------------------------------//

    GridView gridview;
    MateriasAdapter materiasAdapter  = null;
    private AdView adView;

    //Booleana que diz se o ActionMode (LongPress) foi ativado ou não. Caso sim ele ativa as checkboxes dos items da lista
    private boolean fakeActionModeOn = false;

    Resources resources;

    public MateriasFragment(){

    }






    // ----------------------------------------------------------//
    // --------------------  Override Methods ---------------//
    // ----------------------------------------------------------//

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //Abre Drawer Menu
            case R.id.menu:
                ((MateriasActivity)getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);
                break;

            //Adiciona nova materia
            case R.id.add_materia:
                Singleton.setAddMateriaFragment(new AddMateriaFragment());
                Singleton.changeFragments(Singleton.getAddMateriaFragment());
                break;

            //Cancela o Fake Action Mode
            case R.id.cancelar:
                setFakeActionModeOn(false);
                break;

            //Cancela o Fake Action Mode
            case R.id.deletar:
                deletar_materias();

                if(!Singleton.getMateriasActivity().isEmptyFragments())
                    setFakeActionModeOn(false);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_materias, container, false);

        //Setando listener do botão de Chamar o Menu
        View menu = view.findViewById(R.id.menu);
        menu.setOnClickListener(this);

        //Setando listener do botão de Adicionar Matéria
        View add_materia = view.findViewById(R.id.add_materia);
        add_materia.setOnClickListener(this);

        //Setando listener do botão de Deletar
        View deletar = view.findViewById(R.id.deletar);
        deletar.setOnClickListener(this);

        //Setando listener do botão de Compartilhar
        View compartilhar = view.findViewById(R.id.compartilhar);
        compartilhar.setOnClickListener(this);

        //Setando listener do botão de Cancelar
        View cancelar = view.findViewById(R.id.cancelar);
        cancelar.setOnClickListener(this);

        //Setando listener do botão de Cancelar Também
        View cancelar2 = view.findViewById(R.id.cancelar2);
        cancelar2.setOnClickListener(this);

        //Se for versão gratis adiciona ads
        if(!Singleton.isPaidVersion())
            add_advertising(view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(adView != null)
            adView.resume();
        setFakeActionModeOn(false);

    }

    @Override
    public void onPause() {
        if(adView != null)
            adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if(adView != null)
            adView.destroy();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Referencia aos Resources (R.strings...)
        resources = getActivity().getResources();

        //Cria o adapter
        materiasAdapter = new MateriasAdapter(getActivity());

        //Syncroniza lista com o banco
        syncDB();

        gridview = (GridView) getActivity().findViewById(R.id.materias_grid_view);
        gridview.setAdapter(materiasAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Seleciona ou deseleciona materia se estiver em modo de edição
                if(fakeActionModeOn){
                    CheckBox checkbox = ((CheckBox) view.findViewById(R.id.checkbox_materia));
                    checkbox.setChecked(!checkbox.isChecked());
                }
                else {


                    //Pega a materia selecionada
                    model.Materia materia = (model.Materia)materiasAdapter.getItem(position);
                    Singleton.setMateria_selecionada(materia);
                    Singleton.getCameraFragment().reload(null);
                    Singleton.getMateriasActivity().getViewPager().setCurrentItem(1, true);

                }
                //}
            }
        });

        //Seta um evento de Pressionar por longo tempo na lista
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //Callback
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int position, long id) {

                //Avisa que estamos no modo de edição (Selecionar e Deletar items)
                setFakeActionModeOn(true);


                //Seta o checkbox do item que foi pressionado como selecionado automaticamente
                ((CheckBox) v.findViewById(R.id.checkbox_materia)).setChecked(true);


                //Atualiza a lista
                materiasAdapter.notifyDataSetChanged();

                return true;
            }
        });

        //Marca a opção do menu
        //LinearLayout materias = (LinearLayout)getActivity().findViewById(R.id.menu_option_materias);
        //materias.setBackgroundColor(getResources().getColor(R.color.background_menu_selected));

    }




    // ----------------------------------------------------------//
    // --------------------  General Methods ---------------//
    // ----------------------------------------------------------//

    public void add_advertising(View view){
        //Adicionando um ad
        if (Singleton.check_google_services()) {
            // Criar o adView.
            adView = new AdView(getActivity());
            adView.setAdUnitId(Singleton.ID_BANNER_1);
            adView.setAdSize(AdSize.BANNER);


            // Pesquisar seu LinearLayout presumindo que ele foi dado
            // o atributo android:id="@+id/mainLayout".
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.topLinearLayout);

            // Adicionar o adView a ele.
            layout.addView(adView);

            // Iniciar uma solicitação genérica.
            AdRequest adRequest = new AdRequest.Builder().build();//.addTestDevice("045DDF942A96893E629AA4ABB6A88335").build(); // Meu telefone de teste Galaxy Nexus;

            // Carregar o adView com a solicitação de anúncio.
            adView.loadAd(adRequest);
        }
    }

    //Syncroniza com o banco de dados
    public void syncDB(){
        try {
            //Pega a referencia do banco da activity
            DatabaseHelper db = ((MateriasActivity) getActivity()).getDb();

            //Pede todos os subjects do banco
            ArrayList<model.Materia> materias = (ArrayList<model.Materia>) db.getAllSubjects();

            //Pede toda as classes desse subject e armazena nele
            for (model.Materia materia : materias) {
                materia.setAulas(db.getAllClassesBySubject(materia.getId()));
            }

            //Limpa o adapter
            //galeriaAdapter.clear();

            //Se existir pelo menos um subject, adiciona no adapter
            if (!materias.isEmpty())
                materiasAdapter.materias = materias;
            //galeriaAdapter.setData(subjects);

            //Atualiza tela
            materiasAdapter.notifyDataSetChanged();

            ((MateriasActivity)getActivity()).setEmptyFragments(db.getAllSubjects().isEmpty());
//            ((MateriasActivity)getActivity()).getViewPager().getAdapter().notifyDataSetChanged();

            //Atualiza tela
            materiasAdapter.notifyDataSetChanged();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        };
    }

    public void deletar_materias(){

        //Pede todos os subjects do adapter e põe numa lista
        List<model.Materia> materias = materiasAdapter.materias;

        //Pega a referencia do banco da activity
        DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

        //Para cada subject da lista
        for(model.Materia materia : materias){

            //Descobre em qual a view corresponde a este subject
            View view = materiasAdapter.getView(materia.getId());

            //Pega uma referência para o checkbox dele
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_materia);

            //Se ele estiver marcado
            if(checkBox.isChecked()){

                //Deleta esse subject e todas as suas classes
                db.deleteSubjectAndClasses(materia);
            }
        }

        if(!db.getAllSubjects().isEmpty()){
            Singleton.setMateria_selecionada(db.getAllSubjects().get(0));
            Singleton.setNova_materia_selecionada(true);
        }

        //Atualiza a tela
        syncDB();

        if(db.getAllSubjects().isEmpty()){
            //MateriasFragment.this.reload();
            Singleton.getMateriasActivity().setEmptyFragments(true);
            Singleton.getMateriasActivity().recarregar_view_pager();
        }

    }





    // ----------------------------------------------------------//
    // --------------------  Gets and Sets ---------------//
    // ----------------------------------------------------------//

    public boolean isFakeActionModeOn() {
        return fakeActionModeOn;
    }

    public void setFakeActionModeOn(boolean fakeActionModeOn) {
        if(fakeActionModeOn){
            getView().findViewById(R.id.fake_action_mode).setVisibility(View.VISIBLE);
        }
        else {
            getView().findViewById(R.id.fake_action_mode).setVisibility(View.GONE);
        }

        this.fakeActionModeOn = fakeActionModeOn;

    }





}


