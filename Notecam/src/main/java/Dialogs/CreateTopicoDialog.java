package Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.Random;

import helper.Singleton;
import model.Foto;
import model.Materia;
import model.Topico;


/**
 * Dialog para escolher o Start Time ou End Time da Classe
 */
public class CreateTopicoDialog extends DialogFragment {
    private model.Materia materia;
    private boolean flagEdit;
    private Topico topico;
    View view;

    public CreateTopicoDialog(){

    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Topico getTopico() {
        return topico;
    }

    public void setTopico(Topico topico) {
        this.topico = topico;
    }

    public interface Communicator {
        public void onDialogMessage(int color);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater i = getActivity().getLayoutInflater();
        view = i.inflate(R.layout.add_topico,null);

        String title = "Adicionar Tópico";

        if(topico != null){
            ((EditText)view.findViewById(R.id.topico_input)).setText(topico.getName());
            title = "Editar Tópico";
        }

        AlertDialog.Builder b =  new  AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton("Salvar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                salvarTopico();

                                materia.popularTopicos();
                                Singleton.getTopicosFragment().reload();
                                //((BaseAdapter)Singleton.getTopicosFragment().getLista().getAdapter()).notifyDataSetChanged();

                                dialog.dismiss();
                            }
                        }
                )
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );


        b.setView(view);

        return b.create();
    }

    public void salvarTopico(){

        //Pega o nome do topico digitado pelo usuario
        String nome_topico = ((EditText)view.findViewById(R.id.topico_input)).getText().toString();



        //Se não digitar o nome do tópico, dá um nome automático
        if(nome_topico.length() <= 0) {
            Random random = new Random();
            int r = (random.nextInt(9999));

            nome_topico = "Topico " + r;
        }

        //Primeira letra maiuscula
        String name = Character.toUpperCase(nome_topico.charAt(0)) + nome_topico.substring(1).toLowerCase();

        //Se é modo edição
        if (getTopico() != null) {
            this.flagEdit = true;

            //Se mudou o nome
            if(!name.equals(getTopico().getName())){

                Singleton.move_fotos((java.util.ArrayList<model.Foto>) getTopico().getFotos(), name);

                getTopico().setName(name);
                getTopico().save(getActivity());
                ((TextView)Singleton.getGaleriaFragment().getView().findViewById(R.id.header_text)).setText(name);
            }
        }
        else setTopico(new Topico(getActivity(), name, materia.getId()));

        //Se Não existe no DB ainda, então não possui ID
        if(!flagEdit)
            Singleton.getDb().createTopico(getTopico());

        //Se já existe no Banco, apenas da um update
        else {
            Singleton.getDb().updateTopico(getTopico());
        }
    }



}