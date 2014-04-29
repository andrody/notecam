package com.koruja.notecam;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import helper.DatabaseHelper;
import helper.Singleton;
import model.Subject;
import view_fragment.AddSubjectFragment;
import view_fragment.MateriasFragment;
import view_fragment.SingleMateriaFragment;

public class MateriasActivity extends ActionBarActivity implements Singleton.OnFragmentInteractionListener {

    ViewPager viewPager = null;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout drawerLayout;
    View drawerView;
    private String mTitle = "Notecam";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }




    //Referencia para colocar uma custom font
    private Typeface fontType;

    //Cria uma nova conexão com o Banco de Dados
    private DatabaseHelper db = new DatabaseHelper(this);

    //Armazena uma referência para o Fragmento de Adicionar Materias
    private AddSubjectFragment addSubjectsFragment;

    //Armazena uma referência para o Fragmento de Materias
    private MateriasFragment materiasFragment;

    //Armazena uma referência para o Fragmento de Materias
    private SingleMateriaFragment singleMateriasFragment;

    //Lista de opções do menu
    final ArrayList<LinearLayout> lista_options_menu = new ArrayList<LinearLayout>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = findViewById(R.id.drawer);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), this));

        setUpClickListenersMenu();

        Button buttonCloseDrawer = (Button)findViewById(R.id.closedrawer);
        buttonCloseDrawer.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                drawerLayout.closeDrawers();
            }});

        drawerLayout.setDrawerListener(myDrawerListener);

        drawerView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });

        //Criar botao de menu no actionbar
        setUpDrawerToggle();

        //Set custom font to comicneue
        fontType = Typeface.createFromAsset(getAssets(), "fonts/ComicNeue-Bold.ttf");

        if(Singleton.materiasFragment != null) {
            this.setMateriasFragment(Singleton.materiasFragment);
            this.setSingleMateriasFragment(Singleton.singleMateriaFragment);
        }

        //setMateriasFragment(new MateriasFragment());
        //getSupportFragmentManager().beginTransaction().add(R.id.mainLinearLayout, getMateriasFragment(), "materias").commit();

    }

    public void setUpClickListenersMenu(){
        final LinearLayout materias = (LinearLayout)findViewById(R.id.menu_option_materias);
        final LinearLayout exportar_tudo = (LinearLayout)findViewById(R.id.menu_option_exportar);
        final LinearLayout sobre = (LinearLayout)findViewById(R.id.menu_option_sobre);
        final LinearLayout premium = (LinearLayout)findViewById(R.id.menu_option_premium);

        lista_options_menu.add(materias);
        lista_options_menu.add(exportar_tudo);
        lista_options_menu.add(sobre);
        lista_options_menu.add(premium);

        for(final LinearLayout view : lista_options_menu) {
            view.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    for(LinearLayout v : lista_options_menu)
                        v.setBackgroundColor(0);
                    view.setBackgroundColor(getResources().getColor(R.color.background_menu_selected));
                    drawerLayout.closeDrawers();

                    //Se clicou na opção Matérias, troca de fragmentos para Materias
                    if(view.equals(materias)) {
                        changeFragments(materiasFragment, null);
                    }

                }});

            //menu_on_hover(view);

        }
    }



    /*@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void menu_on_hover(final View view){
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                view.setBackgroundColor(getResources().getColor(R.color.blue));
                return false;
            }
        });
    }*/

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setUpDrawerToggle(){
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                             /* host Activity */
                drawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(getmTitle());
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getString(R.string.menu_do_notecam));
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        // NB: required for the drawer indicator to show up!
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(mDrawerToggle);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu.add("Adicionar Materia")
                .setIcon(R.drawable.ic_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        /*menu.add("Share")
                .setIcon(R.drawable.ic_action_new)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);*/


        // Inflate the menu; this adds items to the action bar if it is present.
        //hiding default app icon
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(false);
        //displaying custom ActionBar
        //View mActionBarView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        //actionBar.setCustomView(mActionBarView);
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        //getMenuInflater().inflate(R.menu.materias, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        if(item.getTitle().equals("Add Materia")){

            //Cria uma nova instância do Fragment addSubjectsFragment
            addSubjectsFragment = new AddSubjectFragment();

            changeFragments(addSubjectsFragment, null);
        }

        return super.onOptionsItemSelected(item);

    }

    DrawerListener myDrawerListener = new DrawerListener(){

        @Override
        public void onDrawerClosed(View drawerView) {
            //textPrompt.setText("onDrawerClosed");
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            //textPrompt.setText("onDrawerOpened");
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            //textPrompt.setText("onDrawerSlide: " + String.format("%.2f", slideOffset));
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            String state;
            switch(newState){
                case DrawerLayout.STATE_IDLE:
                    state = "STATE_IDLE";
                    break;
                case DrawerLayout.STATE_DRAGGING:
                    state = "STATE_DRAGGING";
                    break;
                case DrawerLayout.STATE_SETTLING:
                    state = "STATE_SETTLING";
                    break;
                default:
                    state = "unknown!";
            }

            //textPrompt2.setText(state);
        }};

    @Override
    public void onFragmentInteraction(Uri uri, ContentValues content) {
        if (content != null) {
            if(content.containsKey(Singleton.TITLE))
                mTitle = content.getAsString(Singleton.TITLE);

            //É para trocar de fragmento?
            if(content.containsKey(Singleton.REPLACE_FRAGMENT)){
                //Sim? Mas para qual fragmento?
                //Materia?
                if(content.getAsString(Singleton.REPLACE_FRAGMENT).equals(Singleton.MATERIA)) {
                    int materia_id = content.getAsInteger(Singleton.MATERIA_ID);
                    //singleMateriasFragment = SingleMateriaFragment.newInstance(materia_id);
                    singleMateriasFragment.reload(materia_id);
                    viewPager.getAdapter().notifyDataSetChanged();
                    viewPager.setCurrentItem(1, true);
                    //TrocaFragments
                    //changeFragments(singleMateriasFragment, null);
                }
            }
        }
    }

    public void seleciona_option_certo_no_menu(Fragment fragment){
        //Limpa seleção
        for(LinearLayout v : lista_options_menu)
            v.setBackgroundColor(0);

        //Se novo fragmento for o materiasFragment
        if(materiasFragment != null && fragment.equals(materiasFragment)) {
            //Seleciona a opção Materia
            lista_options_menu.get(0).setBackgroundColor(getResources().getColor(R.color.background_menu_selected));
        }

    }

    public void changeFragments(Fragment fragment, Fragment fragment_origin){
        seleciona_option_certo_no_menu(fragment);
        //Inicia a transação
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(fragment instanceof MateriasFragment){
            //Se clicou no menu Materias (enquanto estava com o viewpager invisivel)
            if(!(viewPager.getVisibility() == View.VISIBLE)){
                viewPager.setVisibility(View.VISIBLE);
                transaction.remove(fragment_origin);
            }
            viewPager.setCurrentItem(0, true);
        }
        else if(fragment instanceof SingleMateriaFragment){

        }
        else {
            viewPager.setVisibility(View.GONE);

            //TrocaFragments
            transaction.replace(R.id.mainLinearLayout, fragment);

            //Adiciona ele na pilha de retorno (Para quando apertar o botão de voltar, voltar para este fragment)
            transaction.addToBackStack(null);
        }

        //Efetuar a transação do novo fragment
        transaction.commit();
    }

    public void moveFragmentPager(int position){

    }

    public void toggleMenu(View v) {
        drawerLayout.openDrawer(drawerView);
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public DatabaseHelper getDb() {
        return db;
    }

    public void setDb(DatabaseHelper db) {
        this.db = db;
    }

    public AddSubjectFragment getAddSubjectsFragment() {
        return addSubjectsFragment;
    }

    public MateriasFragment getMateriasFragment() {
        return materiasFragment;
    }

    public void setMateriasFragment(MateriasFragment materiasFragment) {
        this.materiasFragment = materiasFragment;
        Singleton.materiasFragment = materiasFragment;
    }

    public SingleMateriaFragment getSingleMateriasFragment() {
        return singleMateriasFragment;
    }

    public void setSingleMateriasFragment(SingleMateriaFragment singleMateriasFragment) {
        this.singleMateriasFragment = singleMateriasFragment;
        Singleton.singleMateriaFragment = singleMateriasFragment;
    }
}

class PagerAdapter extends FragmentPagerAdapter {
    /*private final class MateriaPageListener implements
            MateriaPageFragmentListener {
        public void onSwitchToNextFragment(int materia_id) {
            mFragmentManager.beginTransaction().remove(mFragmentAtPos1)
                    .commit();
            mFragmentAtPos1 = NextFragment.newInstance(listener);

            notifyDataSetChanged();
        }
    }*/

    Context context;
    private Fragment mFragmentAtPos1 = null;
    final private FragmentManager mFragmentManager;

    //ListaDeFragmentos
    //private ArrayList<Task> fragments = new ArrayList<Task>();


    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        if(position == 0){
            fragment = MateriasFragment.newInstance();
            ((MateriasActivity)context).setMateriasFragment((MateriasFragment) fragment);
        }
        else if(position == 1){
            Subject subject;
            if (mFragmentAtPos1 == null) {
                subject = (Subject) ((MateriasActivity)context).getDb().getAllSubjects().get(0);
                mFragmentAtPos1 = SingleMateriaFragment.newInstance(subject.getId());
            }
            ((MateriasActivity)context).setSingleMateriasFragment((SingleMateriaFragment) mFragmentAtPos1);
            return mFragmentAtPos1;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

interface MateriaPageFragmentListener {
    void onSwitchToNextFragment(int materia_id);
}



