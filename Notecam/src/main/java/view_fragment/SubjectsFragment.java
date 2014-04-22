package view_fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koruja.notecam.SubjectsActivity;
import com.koruja.notecam.R;

import helper.DatabaseHelper;


public class SubjectsFragment extends Fragment  {
    private SubjectsListFragment subjectsListFragment;

    //Pega a referencia do banco da activity
    DatabaseHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.subjects, container, false);

        //Set custom font
        ((TextView)v.findViewById(R.id.subjects_subject)).setTypeface(((SubjectsActivity)getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.subjectsNumber)).setTypeface(((SubjectsActivity)getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.photosNumber)).setTypeface(((SubjectsActivity)getActivity()).getFontType());

        //Pega numero de subjects e coloca na textview subjectsNumber
        db = ((SubjectsActivity)getActivity()).getDb();
        int subjectsNumber = db.getAllSubjects().size();
        ((TextView)v.findViewById(R.id.subjectsNumber)).setText(subjectsNumber + " subjects");

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Cria novo Fragmento da Lista dos Subjects
        setSubjectsListFragment(new SubjectsListFragment());
        ((SubjectsActivity)getActivity()).setSubjectsListFragment(getSubjectsListFragment());


        //Faz as transactions do fragmento das classes
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, getSubjectsListFragment());
        transaction.commit();
    }

    public SubjectsListFragment getSubjectsListFragment() {
        return subjectsListFragment;
    }

    public void setSubjectsListFragment(SubjectsListFragment subjectsListFragment) {
        this.subjectsListFragment = subjectsListFragment;
    }
}