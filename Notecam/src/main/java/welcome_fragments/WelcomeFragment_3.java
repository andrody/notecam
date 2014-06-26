package welcome_fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.koruja.notecam.R;

import helper.Singleton;
import view_fragment.AddMateriaFragment;


public class WelcomeFragment_3 extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.welcome_3, container, false);

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
                Singleton.setAddMateriaFragment(new AddMateriaFragment());
                Singleton.changeFragments(Singleton.getAddMateriaFragment());
                break;
        }


    }
}



