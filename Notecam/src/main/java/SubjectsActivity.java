import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.koruja.notecam.R;

import helper.DatabaseHelper;
import view_fragment.AddSubjectFragment;
import view_fragment.ClassesFragment;
import view_fragment.ClassesListFragment;
import view_fragment.SubjectsFragment;
import view_fragment.SubjectsListFragment;

public class SubjectsActivity extends FragmentActivity {
    public static String REDIRECT = "redirect";
    public static int DIRECT_EDIT_SUBJECT = 0;
    public static int DIRECT_ADD_SUBJECT = 1;

    //Armazena uma referência para o SubjectsFragment
    private SubjectsFragment subjectsFragment;

   //Armazena uma referência para o SubjectsListFragment
    private SubjectsListFragment subjectsListFragment;

    //Armazena uma referência para o SubjectsListFragment
    private AddSubjectFragment addSubjectsFragment;

    //Armazena uma referência para o ClassesFragment
    private ClassesFragment classesFragment;

    //Armazena uma referência para o ClassesListFragment
    private ClassesListFragment classesListFragment;

    //Cria uma nova conexão com o Banco de Dados
    private DatabaseHelper db = new DatabaseHelper(this);

    //Referencia para colocar uma custom font
    private Typeface fontType;

    /**
     * Botão de voltar (do header) pra activity anterior
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Pega o id do item
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Ao selecionar alguma opção do header
     **/
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);

        //Se for a opção de Adicionar Subject
        if(item.getTitle().equals("Add")){

            //Cria uma nova instância do Fragment addSubjectsFragment
            addSubjectsFragment = new AddSubjectFragment();

            //Inicia a transação
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, addSubjectsFragment);

            //Adiciona ele na pilha de retorno (Para quando apertar o botão de voltar, voltar para este fragment)
            transaction.addToBackStack(null);

            //Efetuar a transação do novo fragment
            transaction.commit();
        }

        return false;
    }

    /**
     * Cria as opções do header
     **/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu.add("Add")
                .setIcon(R.drawable.ic_action_new)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add("Share")
                .setIcon(R.drawable.ic_action_share)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add("About")
                .setIcon(R.drawable.ic_action_new)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        menu.add("Pro Version")
                .setIcon(R.drawable.ic_action_new)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);*/
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Set custom font to segoesc
        setFontType(Typeface.createFromAsset(getAssets(), "fonts/segoesc.ttf"));

        AddSubjectFragment addSubjectFragment = null;

        //Pega os parametros enviados se houver algum
        if (getIntent().getExtras() != null) {
             if(getIntent().getExtras().getInt(SubjectsActivity.REDIRECT) == SubjectsActivity.DIRECT_EDIT_SUBJECT ||
                     getIntent().getExtras().getInt(SubjectsActivity.REDIRECT) == SubjectsActivity.DIRECT_ADD_SUBJECT) {
                 addSubjectFragment = new AddSubjectFragment();
                 setAddSubjectsFragment(addSubjectFragment);
                 addSubjectFragment.setArguments(getIntent().getExtras());
             }
        }

        //Instância o Fragment dos Subjects
        subjectsFragment = new SubjectsFragment();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        subjectsFragment.setArguments(getIntent().getExtras());

        Fragment fragment;
        if (addSubjectFragment != null)
            fragment = addSubjectFragment;
        else
            fragment = subjectsFragment;

        int id;

        //Verifica se está no modo portrait ou landscape e seleciona o layout adequado
        if (findViewById(R.id.fragment_container) != null)
            id = R.id.fragment_container;
        else
            id = R.id.fragment_container_land;

        //Se não houver nenhum fragment, Inicia o subjectsFragment
        if(getSupportFragmentManager().findFragmentById(id) == null)
            getSupportFragmentManager().beginTransaction().add(id, fragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("initiated", true);
    }

    public SubjectsFragment getSubjectsFragment() {
        return subjectsFragment;
    }

    public void setSubjectsFragment(SubjectsFragment subjectsFragment) {
        this.subjectsFragment = subjectsFragment;
    }

    public AddSubjectFragment getAddSubjectsFragment() {
        return addSubjectsFragment;
    }

    public void setAddSubjectsFragment(AddSubjectFragment addSubjectsFragment) {
        this.addSubjectsFragment = addSubjectsFragment;
    }

    public DatabaseHelper getDb() {
        return db;
    }

    public void setDb(DatabaseHelper db) {
        this.db = db;
    }

    public ClassesListFragment getClassesListFragment() {
        return classesListFragment;
    }

    public void setClassesListFragment(ClassesListFragment classesListFragment) {
        this.classesListFragment = classesListFragment;
    }

    public SubjectsListFragment getSubjectsListFragment() {
        return subjectsListFragment;
    }

    public void setSubjectsListFragment(SubjectsListFragment subjectsListFragment) {
        this.subjectsListFragment = subjectsListFragment;
    }

    public Typeface getFontType() {
        return fontType;
    }

    public void setFontType(Typeface fontType) {
        this.fontType = fontType;
    }

    public ClassesFragment getClassesFragment() {
        return classesFragment;
    }

    public void setClassesFragment(ClassesFragment classesFragment) {
        this.classesFragment = classesFragment;
    }
}
