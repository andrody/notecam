package view_fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koruja.notecam.R;
import com.koruja.notecam.SubjectsActivity;

import helper.DatabaseHelper;
import model.*;


public class ClassesFragment extends Fragment implements View.OnClickListener {
    private ClassesListFragment classesListFragment;
    //Subject
    private Subject subject;

    //Pega a referencia do banco da activity
    DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = ((SubjectsActivity)getActivity()).getDb();

        //Se foi passado algum parametro, adiciona no subject
        if (getArguments() != null) {
            subject = db.getSubject(getArguments().getInt(Subject.ID));
            subject.popularClasses();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.classes, container, false);

        v.findViewById(R.id.top_bar).setOnClickListener(this);

        //Set custom font
        ((TextView)v.findViewById(R.id.classes_subject)).setTypeface(((SubjectsActivity)getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.classesNumber)).setTypeface(((SubjectsActivity)getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.photosNumber)).setTypeface(((SubjectsActivity)getActivity()).getFontType());

        //Seta o nome do subject
        ((TextView)v.findViewById(R.id.classes_subject)).setText(subject.getName());

        //Seta a cor da esfera
        ((GradientDrawable)v.findViewById(R.id.sphere).getBackground()).setColor(subject.getColor());

        //Seta letra da sphere
        ((TextView)v.findViewById(R.id.classes_letra)).setText(subject.getName().substring(0, 1));



        //Pega numero de classes e coloca na textview classessNumber
        int classesNumber = db.getAllTopicosBySubject(subject.getId()).size();
        ((TextView)v.findViewById(R.id.classesNumber)).setText(classesNumber + " classes");

        //Contador
        int i = 1;

        //String
        String classesOn = "";

        //Seta as classes do subject na view
        //Quantos dias da semana
        int size = subject.getAulas().size();

        //Verifica os dias da semana que tem aula dessa matéria e coloca como visivel
        for(Aula cl : subject.getAulas()){
            int k = cl.getWeekday();
            if(k == 0)
                classesOn += "Sunday";
            if(k == 1)
                classesOn += "Monday";
            if(k == 2)
                classesOn += "Tuesday";
            if(k == 3)
                classesOn += "Wednesday";
            if(k == 4)
                classesOn += "Thursday";
            if(k == 5)
                classesOn += "Friday";
            if(k == 6)
                classesOn += "Saturday" ;
            if(i < size)
                if(i == size - 1)
                    classesOn += " and ";
                else
                    classesOn += ", ";
            i++;
        }

        //Seta classesOn
        ((TextView)v.findViewById(R.id.classesOn)).setText(classesOn);


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Cria novo Fragmento da Lista dos Subjects
        setClassesListFragment(new ClassesListFragment());
        ((SubjectsActivity)getActivity()).setClassesListFragment(getClassesListFragment());

        //Modo de edição. Adiciona o Id do Subject
        Bundle args = new Bundle();
        args.putInt(Subject.ID, subject.getId());

        getClassesListFragment().setArguments(args);

        //Faz as transactions do fragmento das classes
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, getClassesListFragment());
        transaction.commit();
    }

    public ClassesListFragment getClassesListFragment() {
        return classesListFragment;
    }

    public void setClassesListFragment(ClassesListFragment classesListFragment) {
        this.classesListFragment = classesListFragment;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.top_bar) {
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

    }
}