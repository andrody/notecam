package welcome_fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import helper.Singleton;
import view_fragment.AddMateriaFragment;


public class WelcomeFragment_1 extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_1, container, false);

        //Setando listener do botão de Chamar o Menu
        View tela = view.findViewById(R.id.tela);
        tela.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Abre Drawer Menu
            case R.id.tela:
                Singleton.getMateriasActivity().getViewPager().setCurrentItem(1, true);
                break;
        }


    }
}



