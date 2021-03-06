package view_fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.commonsware.cwac.camera.PictureTransaction;
import com.koruja.notecam.R;

import helper.Singleton;

public class CameraFragment extends com.commonsware.cwac.camera.CameraFragment implements View.OnClickListener {
    boolean flashMode = false;

    @Override
    public void onResume() {
        super.onResume();
        reload(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View cameraView = super.onCreateView(inflater, container, savedInstanceState);
        View results =inflater.inflate(R.layout.fragment_camera, container, false);

        ((ViewGroup)results.findViewById(R.id.camera)).addView(cameraView);

        results.findViewById(R.id.go_to_topicos).setOnClickListener(this);
        results.findViewById(R.id.go_to_materias).setOnClickListener(this);
        results.findViewById(R.id.change_flash).setOnClickListener(this);
        results.findViewById(R.id.change_topico).setOnClickListener(this);
        results.findViewById(R.id.take_picture).setOnClickListener(this);
        results.findViewById(R.id.camera).setOnClickListener(this);

        reload(results);

        return(results);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //Apertou no botão de matérias
            case R.id.go_to_materias:
                Singleton.getMateriasActivity().moveFragmentPager(0);
                break;

            //Apertou no botão de tópicos
            case R.id.go_to_topicos:
                Singleton.getMateriasActivity().moveFragmentPager(3);
                break;

            //Apertou no botão de tirar foto
            case R.id.take_picture:
                try {
                    takePicture();
                }catch(IllegalStateException e){
                    e.printStackTrace();
                }
                break;

            //Apertou no botão de Mudar modo Flash
            case R.id.change_flash:
                change_flash_mode();

                break;

            //Apertou no botão de Mudar Topico
            case R.id.change_topico:
                mudar_topico();
                break;

            //Apertou no botão de Mudar Topico
            case R.id.camera:
                cancelAutoFocus();
                autoFocus();
                break;

        }

    }

    void change_flash_mode(){
        CheckBox flash_checkbox = (CheckBox) getView().findViewById(R.id.change_flash);
        if (flash_checkbox.isChecked())
            setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        else
            setFlashMode(Parameters.FLASH_MODE_OFF);
    }



    public void mudar_topico(){
        //Coloca o view como final para ser acessível dentro da inner class
        final View view_f = getView();

        //Cria um dialog para escolher o tópico
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        //Coloca o titulo
        b.setTitle("Topicos");

        String[] types = new String[Singleton.getMateria_selecionada().getTopicos().size()];

        for(int i = 0; i< Singleton.getMateria_selecionada().getTopicos().size(); i++ )
            types[i] = Singleton.getMateria_selecionada().getTopicos().get(i).getName();

        //Coloca as opções e um ClickListener
        b.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int position) {
                //Quando clica em alguma opção, Fecha o dialog
                dialog.dismiss();

                //Altera no model do adapter a semana selecionada
                Singleton.setTopico_selecionado(Singleton.getMateria_selecionada().getTopicos().get(position));

                //Texto do Topico
                TextView nome_topico = ((TextView)view_f.findViewById(R.id.nome_topico));
                nome_topico.setText(Singleton.getTopico_selecionado().getName());
            }

        });

        //Mostra o dialog
        b.show();
    }

    public void reload(View v){

        //Se não for chamado antes do OnCreateView
        if (v == null)
            v = getView();

        if(Singleton.getMateria_selecionada().getTopicos() == null || Singleton.getMateria_selecionada().getTopicos().size() > 0)
            Singleton.getMateria_selecionada().popularTopicos();

        Singleton.setTopico_selecionado(Singleton.getMateria_selecionada().getTopicos().get(0));

        //Texto do Topico
        TextView nome_topico = ((TextView)v.findViewById(R.id.nome_topico));
        nome_topico.setText(Singleton.getTopico_selecionado().getName());



        //Muda cor do botão de Tirar Foto
        ImageView botao_take_picture = (ImageView) v.findViewById(R.id.take_picture);
        Drawable drawable = botao_take_picture.getDrawable();
        drawable.setColorFilter(Singleton.getMateria_selecionada().getColor(), PorterDuff.Mode.SRC_ATOP);

    }


}