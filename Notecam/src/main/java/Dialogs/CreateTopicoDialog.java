package Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.koruja.notecam.R;

import helper.Singleton;
import model.Materia;
import model.Topico;


/**
 * Dialog para escolher o Start Time ou End Time da Classe
 */
public class CreateTopicoDialog extends DialogFragment {
    private model.Materia materia;
    private boolean flagEdit;
    Topico topico;
    View view;

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public interface Communicator {
        public void onDialogMessage(int color);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder b =  new  AlertDialog.Builder(getActivity())
                .setTitle("Adicionar Tópico")
                .setPositiveButton("Salvar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                salvarTopico();

                                materia.popularTopicos();
                                Singleton.getTopicosFragment().reload(materia);
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

        LayoutInflater i = getActivity().getLayoutInflater();

        view = i.inflate(R.layout.add_topico,null);
        b.setView(view);

        return b.create();
    }

    public void salvarTopico(){

        //Pega o nome do topico digitado pelo usuario
        String nome_topico = ((EditText)view.findViewById(R.id.topico_input)).getText().toString();

        //Se não digitar o nome do tópico, dá um nome automático
        if(nome_topico.length() < 0) {
            nome_topico = "Topico " + 1;
        }

        //Primeira letra maiuscula
        String name = Character.toUpperCase(nome_topico.charAt(0)) + nome_topico.substring(1).toLowerCase();

        //Se é modo edição
        if (topico != null)
            this.flagEdit = true;
        else topico = new Topico(getActivity(), name, materia.getId());

        //Se Não existe no DB ainda, então não possui ID
        if(!flagEdit)
            Singleton.getDb().createTopico(topico);

        //Se já existe no Banco, apenas da um update
        else
            Singleton.getDb().updateTopico(topico);
    }



}