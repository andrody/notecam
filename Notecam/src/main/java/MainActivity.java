import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.koruja.notecam.R;

import helper.DatabaseHelper;
import view_fragment.HomeFragment;

public class MainActivity extends FragmentActivity {

    //Cria uma nova conexão com o Banco de Dados
    private DatabaseHelper db = new DatabaseHelper(this);

    //Referencia para colocar uma custom font
    private Typeface fontType;

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";

    // file url to store image/video
    private Uri fileUri;


    /**
     * Cria as opções do header
     **/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Subjects")
                .setIcon(R.drawable.ic_action_collection)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add("Share")
                .setIcon(R.drawable.ic_action_new)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add("About")
                .setIcon(R.drawable.ic_action_new)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add("Pro Version")
                .setIcon(R.drawable.ic_action_new)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }

    /**
     * Ao selecionar alguma opção do header
     **/
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        if(item.getTitle().equals("Subjects")){
            Intent intent = new Intent(this, SubjectsActivity.class);
            startActivity(intent);
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        criaFragment();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Por enquanto esquece esse if
        if (savedInstanceState != null) {
           boolean initiated = savedInstanceState.getBoolean("initiated");
              if(initiated)
                    //return;
               ;
        }

        //Set custom font to segoesc
        fontType = Typeface.createFromAsset(getAssets(), "fonts/segoesc.ttf");
    }

    public void criaFragment(){
        //Cria uma instância do Home Fragment
        HomeFragment homeFragment = new HomeFragment();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        homeFragment.setArguments(getIntent().getExtras());

        int id;

        //Verifica se está no modo portrait ou landscape e seleciona o layout adequado
        if (findViewById(R.id.fragment_container) != null)
            id = R.id.fragment_container;
        else
            id = R.id.fragment_container_land;

        //Se o fragment ja existir faz um replace, senão, adiciona (Serve para editar subject da home)
        if(getSupportFragmentManager().findFragmentById(id) != null)
            getSupportFragmentManager().beginTransaction().replace(id, homeFragment).commit();
        else
            //Inicia o fragment
            getSupportFragmentManager().beginTransaction().add(id, homeFragment).commit();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("initiated", true);
    }


    public DatabaseHelper getDb() {
        return db;
    }

    public void setDb(DatabaseHelper db) {
        this.db = db;
    }

    public Typeface getFontType() {
        return fontType;
    }

    public void setFontType(Typeface fontType) {
        this.fontType = fontType;
    }
}
