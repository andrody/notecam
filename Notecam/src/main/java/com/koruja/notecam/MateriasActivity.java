package com.koruja.notecam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v13.app.FragmentPagerAdapter;
import android.app.FragmentTransaction;
import android.support.v4.view.SlowViewPager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.camera.SimpleCameraHost;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import camera.CustomCameraHost;
import helper.DatabaseHelper;
import helper.Singleton;
import model.Aula;
import model.Materia;
import camera.PictureTaker;
import view_fragment.CameraFragment;
import view_fragment.MateriasFragment;
import view_fragment.SingleMateriaFragment;
import view_fragment.TopicosFragment;




public class MateriasActivity extends ActionBarActivity implements Singleton.OnFragmentInteractionListener, MediaScannerConnection.MediaScannerConnectionClient, ViewPager.OnPageChangeListener  {


    private SlowViewPager viewPager = null;
    ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;
    View drawerView;
    PagerAdapter pagerAdapter;
    private String mTitle = "Notecam";
    private boolean emptyFragments = false;
    private boolean primeira_vez = true;
    private int mPosition;

    //Handler para atualizar a cada 30 segundos
    Handler handler = new Handler();

    //Refresh a cada 30 segundos
    Runnable timedTask = new Runnable(){

        @Override
        public void run() {
            if(Singleton.getMateria_em_aula() != null){
                Aula aula = Singleton.getMateria_em_aula().ChecarHorario();
                if (aula == null){
                    Singleton.setMateria_em_aula(null);
                    Singleton.getCameraFragment().reload(null);
                    Singleton.getTopicosFragment().reload();
                    MateriasActivity.this.checarHorario();
                }
            }
            else MateriasActivity.this.checarHorario();
            Log.e("LOG", "checando horario");
            handler.postDelayed(timedTask, 10000);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Singleton.IMAGE_PICKER_SELECT)
            Singleton.getGaleriaFragment().onActivityResult2(requestCode, resultCode, data);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        getViewPager().getAdapter().notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        // check to see if stack is empty
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
            if(mPosition == 2)
                Singleton.getTopicosFragment().reload();
        }
        else {
            if(mPosition == 1){
                getViewPager().setCurrentItem(0, true);
            }
            else if(mPosition == 2){
                getViewPager().setCurrentItem(1, true);
            }
            else
                super.onBackPressed();
        }

    }

    //Referencia para colocar uma custom font
    private Typeface fontType;

    //Cria uma nova conexão com o Banco de Dados
    private DatabaseHelper db = new DatabaseHelper(this);

    //Armazena uma referência para o Fragmento de Materias
    //private MateriasFragment materiasFragment;

    //Armazena uma referência para o Fragmento de uma Materia
    //private SingleMateriaFragment singleMateriasFragment;

    //Armazena uma referência para o Fragmento de topicos de uma Materia
    //private TopicosFragment topicosFragment;

    //Lista de opções do menu
    final ArrayList<LinearLayout> lista_options_menu = new ArrayList<LinearLayout>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        Singleton.resetarSingleton();
        Singleton.setDb(this.db);

        Singleton.setMateriasActivity(this);

        //Singleton.setActionBarTitle("Notecam2");


        //Muda o ícone no Actionbar do app
        /*int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if(SDK_INT >= 14)
            getActionBar().setIcon(R.drawable.ic_launcher_stroke);*/

        //Seleciona a primeira matéria selecionada
        if(!db.getAllSubjects().isEmpty())
            Singleton.setMateria_selecionada(db.getAllSubjects().get(0));

        //Cria um nova instância da biblioteca de tirar fotos
        Singleton.setPictureTaker(new PictureTaker(this));

        //Verifica se há alguma matéria criada e armazena a booleana correspondente
        this.setEmptyFragments(db.getAllSubjects().isEmpty());


        setDrawerLayout((DrawerLayout)findViewById(R.id.drawer_layout));
        drawerView = findViewById(R.id.drawer);
        setViewPager((SlowViewPager) findViewById(R.id.pager));

        pagerAdapter = new PagerAdapter(getFragmentManager(), this);

        getViewPager().setAdapter(pagerAdapter);
        getViewPager().setOnPageChangeListener(this);

        setUpClickListenersMenu();

        Button buttonCloseDrawer = (Button)findViewById(R.id.closedrawer);
        buttonCloseDrawer.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                getDrawerLayout().closeDrawers();
            }});

        //drawerLayout.setDrawerListener(myDrawerListener);

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

        checarHorario();

        timedTask.run();

        //Fazer o StatusBar como Overlay
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        //setMateriasFragment(new MateriasFragment());
        //getSupportFragmentManager().beginTransaction().add(R.id.mainLinearLayout, getMateriasFragment(), "materias").commit();

    }

    public void setUpClickListenersMenu(){
        final LinearLayout materias = (LinearLayout)findViewById(R.id.menu_option_materias);
        final LinearLayout exportar_tudo = (LinearLayout)findViewById(R.id.menu_option_exportar);
        final LinearLayout contato = (LinearLayout)findViewById(R.id.menu_option_contato);
        final LinearLayout sobre = (LinearLayout)findViewById(R.id.menu_option_sobre);
        final LinearLayout ajuda = (LinearLayout)findViewById(R.id.menu_option_ajuda);
        final LinearLayout premium = (LinearLayout)findViewById(R.id.menu_option_premium);

        lista_options_menu.add(materias);
        lista_options_menu.add(exportar_tudo);
        lista_options_menu.add(contato);
        lista_options_menu.add(sobre);
        lista_options_menu.add(ajuda);
        lista_options_menu.add(premium);

        for(final LinearLayout view : lista_options_menu) {
            view.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    for(LinearLayout v : lista_options_menu)
                        v.setBackgroundColor(0);
                    view.setBackgroundColor(getResources().getColor(R.color.background_menu_selected));
                    getDrawerLayout().closeDrawers();

                    //Se clicou na opção Matérias, troca de fragmentos para Materias
                    if(view.equals(materias)) {
                        changeFragments(Singleton.materiasFragment, null);
                    }


                    //Se clicou na opção Sobre nós, vai para o site da koruja
                    if(view.equals(sobre)||view.equals(premium)|| view.equals(ajuda) ) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://koruja.herokuapp.com/")));
                    }

                    //Se clicou na opção Contato, abre leitor de email
                    if(view.equals(contato)) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","andrewcsz@gmail.com", null));
                        //intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "[Notecam]");
                        //intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

                        startActivity(Intent.createChooser(intent, "Send Email"));
                    }



                }});

            //menu_on_hover(view);

        }
    }


    private void setUpDrawerToggle(){
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        //if (android.os.Build.VERSION.SDK_INT >=14)
        //    actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                             /* host Activity */
                getDrawerLayout(),                    /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(getmTitle());
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(getString(R.string.menu_do_notecam));
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        // NB: required for the drawer indicator to show up!
        getDrawerLayout().post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        getDrawerLayout().setDrawerListener(mDrawerToggle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        /*if(item.getTitle().equals("Add Materia")){

            //Cria uma nova instância do Fragment addSubjectsFragment
            setAddSubjectsFragment(new AddMateriaFragment());

            changeFragments(getAddSubjectsFragment(), null);
        }*/

        return super.onOptionsItemSelected(item);
    }

    DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener(){

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

                    //Singleton.getCameraFragment().reload(null);
                    getViewPager().getAdapter().notifyDataSetChanged();
                    getViewPager().setCurrentItem(1, true);
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
        if(Singleton.materiasFragment != null && fragment.equals(Singleton.materiasFragment)) {
            //Seleciona a opção Materia
            lista_options_menu.get(0).setBackgroundColor(getResources().getColor(R.color.background_menu_selected));
        }

    }

    public void changeFragments(Fragment fragment, Fragment fragment_origin){
        seleciona_option_certo_no_menu(fragment);

        //Inicia a transação
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if(fragment instanceof MateriasFragment){
            //Se clicou no menu Materias (enquanto estava com o viewpager invisivel)
            if(!(getViewPager().getVisibility() == View.VISIBLE)){
                getViewPager().setVisibility(View.VISIBLE);
                if(fragment_origin != null)
                    transaction.remove(fragment_origin);
            }
            //getViewPager().setCurrentItem(0);
        }

        else {
            getViewPager().setVisibility(View.GONE);

            //TrocaFragments
            //transaction.replace(R.id.mainLinearLayout, fragment);
            transaction.replace(R.id.mainLinearLayout, fragment);
            mDrawerToggle.setDrawerIndicatorEnabled(false);

            //Adiciona ele na pilha de retorno (Para quando apertar o botão de voltar, voltar para este fragment)
            transaction.addToBackStack(null);
        }

        //Efetuar a transação do novo fragment
        transaction.commit();

    }

    public void reload(){

            //getViewPager().setAdapter(new PagerAdapter(getFragmentManager(), this));
            changeFragments(getMateriasFragment(), null);

        //if(Singleton.getMateria_selecionada() != null)
         //   getSingleMateriasFragment().reload(Singleton.getMateria_selecionada());

    }

    /*
     Procura em cada classe de cada subject se  o horario atual bate com o horario de alguma aula
     (Ele para na primeira aula encontrada que bate)
     */
    public void checarHorario(){
        Aula aula = null;
        java.util.List<Materia> materias = db.getAllSubjects();
        for(Materia m: materias){
            aula = m.ChecarHorario();
            if(aula != null){
                Singleton.setMateria_em_aula(m);
                Singleton.setMateria_selecionada(m);
                if(primeira_vez){
                    moveFragmentPager(1);
                }
                else Toast.makeText(this, "Em aula de " + m.getName(), Toast.LENGTH_SHORT).show();

                //if(Singleton.materiasFragment != null) Singleton.materiasFragment.updateSubTitle();
                break;
            }
        }
        if(aula == null)
            Singleton.setMateria_em_aula(null);
        primeira_vez = false;
    }

    public void moveFragmentPager(int position){
        this.getViewPager().setCurrentItem(position, true);
    }

    public void toggleMenu(View v) {
        getDrawerLayout().openDrawer(drawerView);
    }

    public String getmTitle() {
        return mTitle;
    }

    public DatabaseHelper getDb() {
        return db;
    }

    public void setDb(DatabaseHelper db) {
        this.db = db;
    }


    public MateriasFragment getMateriasFragment() {
        return Singleton.materiasFragment;
    }



    public SingleMateriaFragment getSingleMateriasFragment() {
        return Singleton.singleMateriaFragment;
    }

    public SlowViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(SlowViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public boolean isEmptyFragments() {
        return emptyFragments;
    }

    public void setEmptyFragments(boolean emptyFragments) {
        this.emptyFragments = emptyFragments;
    }

    private String SCAN_PATH ;
    private static final String FILE_TYPE = "image/*";
    private MediaScannerConnection conn;

    public void startScan(String path)
    {
        SCAN_PATH = path;
        Log.d("Connected", "success" + conn);
        if(conn!=null)
        {
            conn.disconnect();
        }
        conn = new MediaScannerConnection(this,this);
        //conn.connect();
        //Intent intent = new Intent(Intent.ACTION_VIEW,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setType("image/jpeg");
        //startActivity(intent);

        // To open up a gallery browser
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivity(intent);*/
    }

    @Override
    public void onMediaScannerConnected() {
        Log.d("onMediaScannerConnected","success"+conn);
        conn.scanFile(SCAN_PATH, FILE_TYPE);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        try {
            Log.d("onScanCompleted",uri + "success"+conn);
            if (uri != null)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setData(uri);
                startActivity(intent);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/external/images/media/16")));
            }
        } finally
        {
            conn.disconnect();
            conn = null;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch(state) {
            //Se for na posição 1 (Camera), vira fullscreen escondendo o statusbar
            case ViewPager.SCROLL_STATE_IDLE:
                if(mPosition == 1) {
                    //if(Singleton.getCameraFragment() != null)
                    //    Singleton.getCameraFragment().reload(null);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                }
        }
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

}

class PagerAdapter extends FragmentPagerAdapter {

    Context context;
    private Fragment mFragmentAtPos1 = null;
    private Fragment mFragmentAtPos2 = null;
    final private FragmentManager mFragmentManager;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        this.context = context;
    }

    private final String TAG_CAMERA_FRAGMENT = "camera_fragment";

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(position == 0){
            fragment = MateriasFragment.newInstance();
            Singleton.materiasFragment = (MateriasFragment) fragment;
        }
        else if(position == 1){
            CameraFragment c = null;
            if (mFragmentAtPos1 == null) {
                c = new CameraFragment(); //CameraFragment.newInstance(false);
                c.setHost(new CustomCameraHost(context));
                mFragmentAtPos1 = c;
            }
            Singleton.setCameraFragment((CameraFragment) mFragmentAtPos1);
            return mFragmentAtPos1;
        }
        else if(position == 2){
            if (mFragmentAtPos2 == null) {
                mFragmentAtPos2 = TopicosFragment.newInstance(Singleton.getMateria_selecionada().getId());
            }
            Singleton.setTopicosFragment((TopicosFragment) mFragmentAtPos2);
            return mFragmentAtPos2;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        if(((MateriasActivity)context).isEmptyFragments()){
            return 1;
        }
        else
            return 3;
    }
}

interface MateriaPageFragmentListener {
    void onSwitchToNextFragment(int materia_id);
}



