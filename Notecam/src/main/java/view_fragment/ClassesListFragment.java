package view_fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.koruja.notecam.R;
import com.koruja.notecam.SubjectsActivity;

import java.util.ArrayList;
import java.util.List;

import helper.DatabaseHelper;
import list.DayAdapter;
import model.Topico;
import model.Subject;

// A ListFragment is used to display a list of items
public class ClassesListFragment extends ListFragment  {

    //Guarda uma referencia do adapter
    private DayAdapter adapter;

    //Subject
    private Subject subject;

    //Booleana que diz se o ActionMode (LongPress) foi ativado ou não. Caso sim ele ativa as checkboxes dos items da lista
    private boolean checkboxFlag = false;

    /**
     * Cria as opções do header
     **/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main, menu);
        //super.onCreateOptionsMenu(menu, inflater);
        menu.add("Edit")
                .setIcon(R.drawable.ic_action_edit)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add("Share")
                .setIcon(R.drawable.ic_action_share)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getTitle().equals("Edit")){
            //Cria novo AddSubjectFragment e referencia na Activity
            AddSubjectFragment addSubjectsFragment = new AddSubjectFragment();
            ((SubjectsActivity)getActivity()).setAddSubjectsFragment(addSubjectsFragment);

            //Modo de edição. Adiciona o Id do Subject
            Bundle args = new Bundle();
            args.putInt(Subject.ID, subject.getId());
            args.putString(Subject.NAME, subject.getName());

            addSubjectsFragment.setArguments(args);

            //Faz os transactions entre fragments
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, addSubjectsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        return true;

    }

    //Ao voltar do fragment de Adicionar classe
    @Override
    public void onResume() {
        super.onResume();

        //Atualiza a lista
        syncDB();
    }

    //OnLongClick Listener
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        getListView().setDivider(null);
        //Seta um evento de Pressionar por longo tempo na lista
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //Callback
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int position, long id) {

                //Inicia o ActionMode (Header especializado azul)
                ClassesListFragment.this.getActivity().startActionMode(mActionModeCallback);

                //Avisa que estamos no modo de edição (Selecionar e Deletar items)
                setCheckboxFlag(true);

                //Atualiza a lista
                adapter.notifyDataSetChanged();

                //Seta o checkbox do item que foi pressionado como selecionado automaticamente
                ((CheckBox)v.findViewById(R.id.checkBox_subject)).setChecked(true);

                return true;
            }
        });

    }



    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Se não tiver nenhum subject
        setEmptyText("No days passed");
    }

    // Initializes the Fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Avisa que vai mudar o header
        setHasOptionsMenu(true);

        //Layout da lista de subjects
        int layout = R.layout.list;

        //Cria o adapter
        adapter = new DayAdapter(getActivity(),layout, new ArrayList<Topico>());

        //Cria novo subject
        subject = new Subject(getActivity());

        //Se foi passado algum parametro, adiciona no subject
        if (getArguments() != null) {
            subject.setId(getArguments().getInt(Subject.ID));
            subject.setName(getArguments().getString(Subject.NAME));
        }


        //Syncroniza lista com o banco
        syncDB();

        //Seta o adapter
        setListAdapter(adapter);


    }




    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Se o ActionMode está ativado (Ele segurou o dedo)
        if(isCheckboxFlag()){
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox_subject);
            checkBox.setChecked(!checkBox.isChecked());
        }
        else {
            //TODO abrir fragment das fotos


        }

    }

    //Syncroniza com o banco de dados
    public void syncDB(){
        //Pega a referencia do banco da activity
        DatabaseHelper db = ((SubjectsActivity)getActivity()).getDb();

        //Pede todos os days do subject do banco
        ArrayList<Topico> topicos =(ArrayList<Topico>) db.getAllTopicosBySubject(this.subject.getId());

        //Limpa o adapter
        //adapter.clear();

        //Se existir pelo menos um subject, adiciona no adapter
        if(!topicos.isEmpty())
            adapter.setData(topicos);

        //Atualiza tela
        adapter.notifyDataSetChanged();
    }

    //Callback para quando clica nos ícones do header no ActionMode (Modo de edição)
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            //Adiciona as opções de deletar e compartilhar
            menu.add("Delete")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add("Share")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            //Se o botão selecionado pelo usuario for o de deletar
            if(item.getTitle().equals("Delete")){

                //Pede todos os days do adapter e põe numa lista
                List<Topico> topicos = adapter.getItems();

                //Pega a referencia do banco da activity
                DatabaseHelper db = ((SubjectsActivity)getActivity()).getDb();

                //Para cada day da lista
                for(Topico topico : topicos){

                    //Descobre em qual a view corresponde a este day
                    View view = adapter.getView(topico.getId());

                    //Pega uma referência para o checkbox dele
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_subject);

                    //Se ele estiver marcado
                    if(checkBox.isChecked()){

                        //Deleta esse day
                        //db.deleteSubjectAndClasses(subject);
                        //TODO esvazia as fotos do dia
                    }
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Quando o ActionMode sumir, seta a flag como falso
            setCheckboxFlag(false);

            //Atualiza a tela
            syncDB();
        }
    };

    public boolean isCheckboxFlag() {
        return checkboxFlag;
    }

    public void setCheckboxFlag(boolean checkboxFlag) {
        this.checkboxFlag = checkboxFlag;
    }




}