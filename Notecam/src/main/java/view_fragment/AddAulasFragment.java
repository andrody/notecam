package view_fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.List;

import helper.DatabaseHelper;
import list.ClassAdapter;
import model.Aula;
import model.Materia;

// A ListFragment is used to display a list of items

public class AddAulasFragment extends ListFragment implements View.OnClickListener, TimePickerFragment.OnOkDialogListener {

    //Armazena o model do Subject em questão
    private Materia materia;

    //Referencia para a lista com os models das classes
    List<Aula> aulas;

    //Referência para o adapter
    private ClassAdapter adapter;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Se não houver nenhuma class, imprime isto
        //setEmptyText("Add a class!");
    }

    // Initializes the Fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Cria um nova Lista de classes
        aulas = new ArrayList<Aula>();
        int layout = R.layout.list;

        //Cria um novo adapter, passando como referencia a lista de classes
        setAdapter(new ClassAdapter(getActivity(),layout, aulas));
        setListAdapter(getAdapter());

        //Se estamos no modo de edição, i.e. não estamos criando um novo subject
        if(getMateria() != null && getMateria().getId() >=0) {
            //Pega referência do Banco de Dados
            DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

            //Recupera do Banco as classes relativas ao Subject
            List<Aula> aulas = db.getAllClassesBySubject(getMateria().getId());

            //Adiciona classes no adapter
            addElementList(aulas);
        }
        else{
            Aula a = new Aula();
            getAdapter().add(a);
        }

    }

    //Adiciona uma nova classe
    public void addElement() {
        getAdapter().add(new Aula(Time.MONDAY));
    }

    //Adiciona uma lista de classes
    public void addElementList(List<Aula> aulas) {
        for(Aula cl : aulas)
            getAdapter().add(cl);
    }


    @Override
    public void onClick(View v) {
        //Se clicou no botão de Start Time ou End Time
        if(v.getId() == R.id.button_start_time || v.getId() == R.id.button_end_time) {

            //Cria um Bundle e passa os argumentos do botão para ele
            Bundle args = new Bundle();
            args.putInt(Aula.HORA, Integer.parseInt(((TextView) v).getText().subSequence(0, 2).toString()));
            args.putInt(Aula.MINUTO, Integer.parseInt(((TextView)v).getText().subSequence(3, 5).toString()));
            args.putInt(Aula.POSITION, getPosition(v));

            //Cria um dialog para pegar o tempo e passa os dados do botão para ele
            DialogFragment dialogFragment = new TimePickerFragment();
            dialogFragment.setArguments(args);

            //Se for o botão de Start time
            if(v.getId() == R.id.button_start_time){
                //Avisa pro dialog que é o Start Time
                args.putBoolean(Aula.STARTIME, true);

                //Abre o dialog
                dialogFragment.show(getActivity().getSupportFragmentManager(), "StartTime");
            }

            //Se for o botão de End time
            if(v.getId() == R.id.button_end_time){
                //Avisa pro dialog que é o End Time
                args.putBoolean(Aula.STARTIME, false);

                //Abre o dialog
                dialogFragment.show(getActivity().getSupportFragmentManager(), "EndTime");
            }
        }

        //Se for o botão de remover
        if(v.getId() == R.id.letra){

            //Remove do adapter
            getAdapter().remove(getAdapter().getItem(getPosition(v)));
        }

        //Se for o botão do dia da semana
        if(v.getId() == R.id.button_weekday)
        {
            //Coloca o view como final para ser acessível dentro da inner class
            final View view = v;

            //Cria um dialog para escolher os dias da semana
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

            //Coloca o titulo
            b.setTitle("Dia da semana");

            //Especifica as opções do dialog
            String[] types = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta","Sexta", "Sabado"};

            //Coloca as opções e um ClickListener
            b.setItems(types, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int week) {
                    //Quando clica em alguma opção, Fecha o dialog
                    dialog.dismiss();

                    //Altera no model do adapter a semana selecionada
                    getAdapter().getItem(getPosition(view)).setWeekday(week);

                    //Notifica o adapter que houve uma mudança de dados para que ele atualize a tela
                    getAdapter().notifyDataSetChanged();
                }

            });

            //Mostra o dialog
            b.show();
        }



    }


    //Quando eu aperto OK na dialog de escolher o tempo inicial ou final
    @Override
    public void onOkDialog(int hour, int minute, int position, boolean startime) {
        //Pega o modelo da classe
        Aula row = getAdapter().getItem(position);

        //Se for o tempo inicial
        if(startime)
            row.setStartTime(hour, minute);
        else
            row.setEndTime(hour, minute);

        //Notifica o adapter que houve uma mudança de dados para que ele atualize a tela
        getAdapter().notifyDataSetChanged();

    }

    //Retorna a posição da view (Um item/linha da lista) na lista
    public int getPosition(View v){
        return ((ListView)v.getParent().getParent().getParent().getParent()).getPositionForView(v);
    }

    public ClassAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ClassAdapter adapter) {
        this.adapter = adapter;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(model.Materia materia) {
        this.materia = materia;
    }
}