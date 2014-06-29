package com.koruja.notecam;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.SlowViewPager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import Adapters.PagerAdapter;
import helper.DatabaseHelper;
import helper.Singleton;
import model.Aula;
import model.Materia;
import view_fragment.MateriasFragment;
import welcome_fragments.WelcomeFragment_3;


public class MateriasActivity extends Activity implements ViewPager.OnPageChangeListener  {

    // ----------------------------------------------------------//
    // --------------------  Variables Declaration---------------//
    // ----------------------------------------------------------//

    private SlowViewPager viewPager = null;
    ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawerLayout;
    View drawerView;
    public PagerAdapter pagerAdapter;
    private boolean emptyFragments = false;
    private boolean primeira_vez = true;
    private int mPosition;

    //Cria uma nova conexão com o Banco de Dados
    private DatabaseHelper db = new DatabaseHelper(this);

    //Lista de opções do menu
    final ArrayList<LinearLayout> lista_options_menu = new ArrayList<LinearLayout>();

    //Coloca todos os fragmentos nessa lista, para conseguir atualizar o pagerview
    List<WeakReference<Fragment>> fragList = new ArrayList<WeakReference<Fragment>>();


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

    // ----------------------------------------------------------//
    // --------------------  Override Methods ---------------//
    // ----------------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        Singleton.resetarSingleton();
        Singleton.setDb(this.db);
        Singleton.setMateriasActivity(this);


        //Seleciona a primeira matéria selecionada
        if(!db.getAllSubjects().isEmpty())
            Singleton.setMateria_selecionada(db.getAllSubjects().get(0));

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

        checarHorario();

        timedTask.run();

        //Fazer o StatusBar como Overlay
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        //setMateriasFragment(new MateriasFragment());
        //getSupportFragmentManager().beginTransaction().add(R.id.mainLinearLayout, getMateriasFragment(), "materias").commit();

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
            try {
                getFragmentManager().popBackStackImmediate();
            }catch (IllegalStateException e){

                //Se der erro reinicia o app
                e.printStackTrace();
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            if(mPosition == 2 && !isEmptyFragments() && Singleton.getTopicosFragment() != null)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Singleton.IMAGE_PICKER_SELECT)
            Singleton.getGaleriaFragment().onActivityResult2(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onAttachFragment (Fragment fragment) {
        fragList.add(new WeakReference(fragment));
    }

    //ViewPager.OnPageChangeListener Methods

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(!isEmptyFragments()) {
            switch (state) {
                //Se for na posição 1 (Camera), vira fullscreen escondendo o statusbar
                case ViewPager.SCROLL_STATE_IDLE:
                    if (mPosition == 1) {
                        //if(Singleton.getCameraFragment() != null)
                        //    Singleton.getCameraFragment().reload(null);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                        if (Singleton.isNova_materia_selecionada()) {
                            Singleton.getCameraFragment().reload(null);
                            Singleton.setNova_materia_selecionada(false);
                        }
                    } else {
                        if (mPosition == 2) {
                            try {
                                Singleton.getTopicosFragment().reload();
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            Singleton.setNova_materia_selecionada_topicos(false);

                        }

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    }
            }
        }
        else
            onWelcomePageScrollStateChanged(state);
    }
    // ----------------------------------------------------------//
    // --------------------  General Methods ---------------//
    // ----------------------------------------------------------//

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
                else Toast.makeText(this, getString(R.string.em_aula_de) + m.getName(), Toast.LENGTH_SHORT).show();

                //if(Singleton.materiasFragment != null) Singleton.materiasFragment.updateSubTitle();
                break;
            }
        }
        if(aula == null)
            Singleton.setMateria_em_aula(null);
        primeira_vez = false;
    }

    public void setUpClickListenersMenu(){
        final LinearLayout folder = (LinearLayout)findViewById(R.id.menu_option_materias);
        //final LinearLayout exportar_tudo = (LinearLayout)findViewById(R.id.menu_option_exportar);
        final LinearLayout contato = (LinearLayout)findViewById(R.id.menu_option_contato);
        final LinearLayout sobre = (LinearLayout)findViewById(R.id.menu_option_sobre);
        final LinearLayout ajuda = (LinearLayout)findViewById(R.id.menu_option_ajuda);
        final LinearLayout premium = (LinearLayout)findViewById(R.id.menu_option_premium);
        final LinearLayout twitter = (LinearLayout)findViewById(R.id.menu_option_twitter);
        final TextView pro = (TextView)findViewById(R.id.pro);
        //final LinearLayout notecam = (LinearLayout)findViewById(R.id.notecam);

        lista_options_menu.add(folder);
        //lista_options_menu.add(exportar_tudo);
        lista_options_menu.add(contato);
        lista_options_menu.add(sobre);
        lista_options_menu.add(ajuda);
        lista_options_menu.add(premium);
        lista_options_menu.add(twitter);


        if(!Singleton.isPaidVersion()) {
            pro.setText(getString(R.string.free_version));
            premium.setVisibility(View.VISIBLE);
        }

        for(final LinearLayout view : lista_options_menu) {
            view.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    //for(LinearLayout v : lista_options_menu)
                    //    v.setBackgroundColor(0);
                    //view.setBackgroundColor(getResources().getColor(R.color.background_menu_selected));
                    getDrawerLayout().closeDrawers();

                    //Se clicou na opção abrir diretórios
                    if(view.equals(folder)) {
                        Uri startDir = Uri.fromFile(new File(Singleton.NOTECAM_FOLDER));
                        Intent intent = new Intent();
                        intent.setData(startDir);
                        intent.setType("text/csv");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivity(Intent.createChooser(intent, getString(R.string.choose_how_open_folder)));
                    }


                    //Se clicou na opção Sobre nós, vai para o site da koruja
                    if(view.equals(sobre)) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://notecam.co/about")));
                    }

                    //Se clicou na opção Sobre nós, vai para o site da koruja
                    if(view.equals(ajuda) ) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://notecam.co/help")));
                    }

                    //Se clicou na opção Sobre nós, vai para o site da koruja
                    if(view.equals(twitter) ) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://twitter.com/Notecam")));
                    }

                    //Se clicou na opção Virar Premium, vai para o site da koruja
                    if(view.equals(premium)) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Singleton.PRO_VERSION_PACKAGE)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + Singleton.PRO_VERSION_PACKAGE)));
                        }
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
                //mDrawerToggle.syncState();
            }
        });

        getDrawerLayout().setDrawerListener(mDrawerToggle);
    }

    public void seleciona_option_certo_no_menu(Fragment fragment){
        //Limpa seleção
        /*for(LinearLayout v : lista_options_menu)
            v.setBackgroundColor(0);

        //Se novo fragmento for o materiasFragment
        if(Singleton.getMateriasFragment() != null && fragment.equals(Singleton.getMateriasFragment())) {
            //Seleciona a opção Materia
            lista_options_menu.get(0).setBackgroundColor(getResources().getColor(R.color.background_menu_selected));
        }*/

    }

    public void changeFragments(Fragment fragment, Fragment fragment_origin){
        seleciona_option_certo_no_menu(fragment);

        //Inicia a transação
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if(fragment instanceof MateriasFragment || fragment instanceof WelcomeFragment_3){
            //Se clicou no menu Materias (enquanto estava com o viewpager invisivel)
            if(!(getViewPager().getVisibility() == View.VISIBLE)){
                getViewPager().setVisibility(View.VISIBLE);
                if(fragment_origin != null)
                    transaction.remove(fragment_origin);
            }
            //getViewPager().setCurrentItem(0);
        }

        else {

            //Troca cor de fundo das matérias
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                findViewById(R.id.mainLinearLayout).setBackgroundDrawable(getScreenShot());
            } else {
                findViewById(R.id.mainLinearLayout).setBackground(getScreenShot());
            }

            getViewPager().setVisibility(View.GONE);


            //TrocaFragments
            //transaction.replace(R.id.mainLinearLayout, fragment);
            transaction.setCustomAnimations(R.animator.frag_slide_in_from_left, R.animator.frag_slide_out_from_right, R.animator.frag_slide_in_from_left, R.animator.frag_slide_out_from_right);
            transaction.replace(R.id.mainLinearLayout, fragment);
            //mDrawerToggle.setDrawerIndicatorEnabled(false);

            //Adiciona ele na pilha de retorno (Para quando apertar o botão de voltar, voltar para este fragment)
            transaction.addToBackStack(null);
        }

        //Efetuar a transação do novo fragment
        transaction.commit();

    }

    private Drawable getScreenShot() {
        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //int statusBarHeight = frame.top;
        Bitmap b = Bitmap.createBitmap(drawingCache, 0, 0, drawingCache.getWidth(), drawingCache.getHeight());
        view.destroyDrawingCache();
        return new BitmapDrawable(getResources(),b);
    }

    public void reload(){

        if(isEmptyFragments())
            changeFragments(pagerAdapter.mFragmentAtPos2, null);
        else {
            changeFragments(Singleton.getMateriasFragment(), null);

        }

    }

    public void moveFragmentPager(int position){
        this.getViewPager().setCurrentItem(position, true);
    }

    public void recarregar_view_pager(){
        mPosition = 2;

        pagerAdapter.mFragmentAtPos0 = null;
        pagerAdapter.mFragmentAtPos1 = null;
        pagerAdapter.mFragmentAtPos2 = null;

        //getViewPager().getAdapter().notifyDataSetChanged();

        //getViewPager().setAdapter(null);

        //pagerAdapter = new PagerAdapter(getFragmentManager(), this);
        FragmentManager fragmentManager = pagerAdapter.getmFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        List<Fragment> fragments = getActiveFragments();
        for (Fragment f : fragments) {
            transaction.remove(f);
        }
        transaction.commit();
        getViewPager().setAdapter(pagerAdapter);
        getViewPager().getAdapter().notifyDataSetChanged();
    }

    public void onWelcomePageScrollStateChanged(int state){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }


    // ----------------------------------------------------------//
    // --------------------  Gets and Sets ---------------//
    // ----------------------------------------------------------//


    public DatabaseHelper getDb() {
        return db;
    }

    public MateriasFragment getMateriasFragment() {
        return Singleton.getMateriasFragment();
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

    public List<Fragment> getActiveFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for(WeakReference<Fragment> ref : fragList) {
            Fragment f = ref.get();
            if(f != null) {
                    ret.add(f);
            }
        }
        return ret;
    }

    public void setEmptyFragments(boolean emptyFragments) {
        //Se acabou de criar uma matéria, Zera o pageview
        if(this.emptyFragments && !emptyFragments) {
            this.emptyFragments = emptyFragments;
            recarregar_view_pager();
        }

        this.emptyFragments = emptyFragments;


    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

}





