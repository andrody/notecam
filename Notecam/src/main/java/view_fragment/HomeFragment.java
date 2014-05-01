package view_fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.koruja.notecam.MainActivity;
import com.koruja.notecam.R;
import com.koruja.notecam.SubjectsActivity;

import java.util.List;

import helper.DatabaseHelper;
import model.Aula;
import model.Topico;
import model.Subject;


public class HomeFragment extends Fragment implements View.OnClickListener {

    //Referencia do banco de dados
    DatabaseHelper db;

    //Referencia da lista dos subjects
    List<Subject> subjects;

    //Subject do horario atual
    Subject subjectAtual = null;

    //Classe do horario atual
    Aula classeAtual = null;

    //Aula atual
    Topico topicoAtual = null;

    //Flag se mudou de subject
    boolean flagMudouSubject = true;

    //Flag se mudou de subject de forma manual
    boolean flagMudouSubjectManual = false;

    //Handler para atualizar a cada 30 segundos
    Handler handler = new Handler();

    //Refresh a cada 30 segundos
    Runnable timedTask = new Runnable(){

        @Override
        public void run() {
            //Só chama o checar horário se eu não tiver mudado o subject de maneira manual
            if(!flagMudouSubjectManual)
                ChecarHorario();
            try{
                UpdateSubject();
            }
            catch (IndexOutOfBoundsException e) { }
            handler.postDelayed(timedTask, 10000);
        }
    };



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db = ((MainActivity) activity).getDb();
        subjects  = getAllSubjects();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home, container, false);
        v.findViewById(R.id.top_bar).setOnClickListener(this);
        v.findViewById(R.id.edit_subject).setOnClickListener(this);
        v.findViewById(R.id.addSubject).setOnClickListener(this);

        //Set custom font
        ((TextView)v.findViewById(R.id.home_subject_name)).setTypeface(((MainActivity)getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.home_hour)).setTypeface(((MainActivity)getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.home_letra)).setTypeface(((MainActivity) getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.topicOfDay)).setTypeface(((MainActivity) getActivity()).getFontType());
//        ((TextView)v.findViewById(R.id.photosNumber)).setTypeface(((MainActivity) getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.classNumber)).setTypeface(((MainActivity) getActivity()).getFontType());
        ((TextView)v.findViewById(R.id.takeShot)).setTypeface(((MainActivity) getActivity()).getFontType());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Se for a primeira vez que abre o aplicativo cria um subject "Other"
        List<Subject> sbs = db.getAllSubjects();

        if (sbs.isEmpty()) {
            Subject sub = new Subject("Other", getActivity());
            sub.setRandomColor();
            int sub_id = db.createSubjectAndClasses(sub, null);

            //Topico d = new Topico(sub_id, sub.getColorNumber());
            //d.setWeekday(0);

            //Salva no banco
            //db.createTopico(d);

        }

        ChecarHorario();
        UpdateSubject();
        timedTask.run();
    }

    @Override
    public void onStart() {
        super.onStart();

        //Referencia para o topico do dia
        final EditText editText = ((EditText)getView().findViewById(R.id.topicOfDay));

        //Seta o keyboard do topico do dia para terminar com o "Done/Feito"
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //Seta o listener para salvar no banco quando clicar em feito
        editText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            //Salva no banco de dados
                            save();

                            editText.clearFocus();

                            //Fecha keyboard
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                            return true;
                        }

                        return false;
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        ChecarHorario();
        UpdateSubject();
    }



    @Override
    public void onClick(View v) {

        //Se clicou no botão de escolher subject (top_bar)
        if(v.getId() == R.id.top_bar)
        {
            //Coloca o view como final para ser acessível dentro da inner class
            final View view = v;

            //Cria um dialog para escolher os dias da semana
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

            //Coloca o titulo
            b.setTitle("Subjects");

            //Pega cada um dos nomes dos subjects e coloca em uma lista


            String [] subjectStrings = new String [subjects.size()];

            for(int i=0; i<subjects.size(); i++)
                subjectStrings[i] = subjects.get(i).toString();

            //Coloca as opções e um ClickListener
            b.setItems(subjectStrings, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int subject_pos) {
                    //Quando clica em alguma opção, Fecha o dialog
                    dialog.dismiss();

                    //Colocar como subject atual o subject selecionado
                    subjectAtual = subjects.get(subject_pos);
                    flagMudouSubject = true;
                    flagMudouSubjectManual = true;

                    //Coloca como a aula atual a primeira aula do subject
                    try{
                        classeAtual = subjectAtual.getAulas().get(0);
                    }
                    catch (IndexOutOfBoundsException e) {
                        
                    }

                    UpdateSubject();
                }

            });

            //Mostra o dialog
            b.show();
        }

        //Se clicou no botão de editar subject
        if(v.getId() == R.id.edit_subject){
            //Modo de edição. Adiciona o Id do Subject
            Intent intent = new Intent(getActivity(), SubjectsActivity.class);
            intent.putExtra(SubjectsActivity.REDIRECT, SubjectsActivity.DIRECT_EDIT_SUBJECT);
            intent.putExtra(Subject.ID, subjectAtual.getId());
            startActivity(intent);
        }

        //Se clicou no botão de editar subject
        if(v.getId() == R.id.addSubject){
            Intent intent = new Intent(getActivity(), SubjectsActivity.class);
            intent.putExtra(SubjectsActivity.REDIRECT, SubjectsActivity.DIRECT_ADD_SUBJECT);
            startActivity(intent);
        }
    }


    private void UpdateSubject() {
        try{
            //Só faz update de subject se o mesmo não for nulo e a flag de mudança de classe está ativada
            if(subjectAtual != null && flagMudouSubject || subjectAtual.getId() == db.getAllSubjects().get(0).getId()){

                //Checa se já existe aula criada nesse horario
                if(classeAtual != null)
                    topicoAtual = ChecaSeDayExiste();

                //Se ele mudou de maneira manual, não cria nova aula, pega a ultima
                if(flagMudouSubjectManual) {
                    List<Topico> topico = db.getAllTopicosBySubject(subjectAtual.getId());
                    if(!topico.isEmpty())
                        topicoAtual = topico.get(topico.size() - 1);
                }


                //Se não existe a aula no banco
                if(topicoAtual == null && classeAtual != null){

                    //Cria nova aula
                    //topicoAtual = new Topico(subjectAtual.getId(), subjectAtual.getColorNumber());
                    if(classeAtual != null)
                    //    topicoAtual.setWeekday(classeAtual.getWeekday());
                   // else
                     //   topicoAtual.setWeekday(0);


                    //Salva no banco
                    topicoAtual.setId((int) db.createTopico(topicoAtual));
                }

                //Letra Background
                FrameLayout sphere = (FrameLayout)getView().findViewById(R.id.sphere);

                //Letra Background Shape color
                GradientDrawable  shapeDrawable = (GradientDrawable)sphere.getBackground();
                shapeDrawable.setColor(subjectAtual.getColor());


                //Nome do Subject
                String nomeSub = subjectAtual.getName();
                if (nomeSub.length() > 10)
                    nomeSub = nomeSub.substring(0,10);
                ((TextView)getView().findViewById(R.id.home_subject_name)).setText(nomeSub);

                //Letra Inicial do Subject
                ((TextView)getView().findViewById(R.id.home_letra)).setText(subjectAtual.getName().substring(0,1));

                //Topico do dia
                ((TextView)getView().findViewById(R.id.topicOfDay)).setText(topicoAtual.getName());

                //Hora inicial e final da etiqueta
                ((TextView)getView().findViewById(R.id.home_hour)).setText(classeAtual.getStartTime().format("%H:%M") + " - " + classeAtual.getEndTime().format("%H:%M"));

                //Numero de aulas
                ((TextView)getView().findViewById(R.id.classNumber)).setText("Class " + subjectAtual.getNumberDays(db));



                //Contador
                int i = 1;

                //String
                String classesOn = "";

                //Quantos dias da semana
                int size = subjectAtual.getAulas().size();

                //Verifica os dias da semana que tem aula dessa matéria e coloca como visivel
                for(Aula cl : subjectAtual.getAulas()){
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
                        classesOn += "Saturday";
                    if(i < size)
                        if(i == size - 1)
                            classesOn += " and ";
                        else
                            classesOn += ", ";
                    i++;
                }
                //((TextView)getView().findViewById(R.id.ClassesOn)).setText(classesOn);

                flagMudouSubject = false;
            }
        }
        catch (NullPointerException e){

        }
    }

    /*
    Para evitar criar varias aulas para o mesmo horario, checa se já existe no banco
     */
    private Topico ChecaSeDayExiste() {
        List<Topico> topicos = db.getAllTopicosBySubject(subjectAtual.getId());
        if(!topicos.isEmpty()){
            long endTime = classeAtual.getEndTime().toMillis(true);
            long startTime = classeAtual.getStartTime().toMillis(true);
            if(topicos.size() > 0) {
                Topico topico = topicos.get(topicos.size() - 1);
                if(((int)startTime) <= topico.getCreatedAt() && topico.getCreatedAt() <= ((int)endTime))
                    return topico;
            }
        }

        return null;
    }

    private void save() {
        if(topicoAtual != null){
            try {
                String topic = ((TextView)getView().findViewById(R.id.topicOfDay)).getText().toString();
                topicoAtual.setName(topic);
                db.updateTopico(topicoAtual);
            }
            catch (NullPointerException e) {

            }

        }

    }

    /*
     Procura em cada classe de cada subject se  o horario atual bate com o horario de alguma aula
     (Ele para na primeira aula encontrada que bate)
     */
    public void ChecarHorario(){
        flagMudouSubjectManual = false;
        Aula cl;
        for(Subject subject : subjects){
            cl = subject.ChecarHorario();
            if(cl != null){
                //Salvar aula
                save();

                //Se for um subject diferente do que já estava
                if(!(this.subjectAtual != null && this.subjectAtual.equals(subject))){
                    this.subjectAtual = subject;
                    this.classeAtual = cl;

                    //Seta a flag para true para avisar que houve alteração de aula/classe
                    this.flagMudouSubject = true;
                }

                return;
            }
        }

        //Salvar aula
        save();
        try{
            this.subjectAtual = db.getAllSubjects().get(0);
        }
        catch (IndexOutOfBoundsException e){
            subjectAtual = null;
        }
        this.classeAtual = null;
        this.topicoAtual = null;
    }

    /*
    Retorna todos os subjects
     */
    public List<Subject> getAllSubjects(){
        List<Subject> subjects = db.getAllSubjects();
        for (Subject subject : subjects)
            subject.popularClasses(db);
        return subjects;
    }



}


