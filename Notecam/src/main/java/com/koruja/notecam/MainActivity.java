package com.koruja.notecam;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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

public class MainActivity extends ActionBarActivity implements MateriasFragment.OnFragmentInteractionListener {

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout drawerLayout;
    View drawerView;

    //Referencia para colocar uma custom font
    private Typeface fontType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        Button buttonOpenDrawer = (Button)findViewById(R.id.opendrawer);
        buttonOpenDrawer.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                drawerLayout.openDrawer(drawerView);
            }});

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
        fontType = Typeface.createFromAsset(getAssets(),"fonts/ComicNeue-Bold.ttf");

        MateriasFragment materias_fragment = new MateriasFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainLinearLayout, materias_fragment, "materias").commit();

    }

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
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
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

        getMenuInflater().inflate(R.menu.materias, menu);
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
    public void onFragmentInteraction(Uri uri) {

    }

    public void toggleMenu(View v) {
        drawerLayout.openDrawer(drawerView);
    }
}



