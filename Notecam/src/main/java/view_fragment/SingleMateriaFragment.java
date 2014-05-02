package view_fragment;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import helper.DatabaseHelper;
import helper.Singleton;
import model.Subject;
import model.Topico;

public class SingleMateriaFragment extends Fragment {

    private Subject materia;
    private Topico topico;
    private DatabaseHelper db;
    private View view_do_onViewCreated;

    private Singleton.OnFragmentInteractionListener mListener;


    public static SingleMateriaFragment newInstance(int materia_id) {
        SingleMateriaFragment fragment = new SingleMateriaFragment();
        Bundle args = new Bundle();
        args.putInt(Singleton.MATERIA_ID, materia_id);
        args.putString(Singleton.FRAGMENT_TYPE, Singleton.FRAGMENT_TYPE_MATERIA);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleMateriaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int materia_id = -1;
        if (getArguments() != null) {
            materia_id = getArguments().getInt(Singleton.MATERIA_ID);
        }


        db = ((MateriasActivity) getActivity()).getDb();
        if (!db.getAllSubjects().isEmpty()) {
            materia = db.getSubject((long) materia_id);

            //Se não houver topicos criados na materia, cria um
            if (materia.getTopicos().isEmpty()) {
                materia.addTopico("Geral");
            }
            this.topico = materia.getTopicos().get(materia.getTopicos().size() -1);
        }


        //Tem de habilitar para mudar o ActionBar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_materia, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view_do_onViewCreated = view;
        if(!db.getAllSubjects().isEmpty())
            criarUI(view);
        super.onViewCreated(view, savedInstanceState);
    }


    public void criarUI(View view){
        //Setando cor da materia aos graficos
        final Drawable camera = getResources().getDrawable( R.drawable.compact_camera );
        final ColorFilter filter = new LightingColorFilter(materia.getColor(), Color.BLACK);
        final ColorFilter filter_branco = new LightingColorFilter(Color.WHITE, Color.WHITE);
        camera.setColorFilter(filter);

        //Referencia da camera
        final ImageView image_camera = (ImageView) view.findViewById(R.id.camera_dentro_circulo);

        //Cor do circulo da camera
        final GradientDrawable background = (GradientDrawable) view.findViewById(R.id.camera_circulo).getBackground();
        background.setStroke(12, materia.getColor());

        //Cor e texto do nome da materia
        TextView nome_materia = ((TextView)view.findViewById(R.id.nome_materia));
        nome_materia.setText(materia.getName());
        nome_materia.setTextColor(materia.getColor());

        //Texto do Topico
        TextView nome_topico = ((TextView)view.findViewById(R.id.nome_topico));
        nome_topico.setText(topico.getName());

        View v = view.findViewById(R.id.camera_circulo);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    background.setColor(0);
                    image_camera.setColorFilter(filter);
                }
                else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    background.setColor(materia.getColor());
                    image_camera.setColorFilter(filter_branco);
                }

                return true;
            }
        });
        background.setColor(0);
        image_camera.setColorFilter(filter);
    }

    /**
     * Cria as opções do header
     **/
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main, menu);
        //super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.single_materia, menu);

        try {
            getActivity().getActionBar().setTitle(materia.getName());
            getActivity().getActionBar().setSubtitle("Sem aulas hoje");
        }
        catch (NullPointerException e){
            e.printStackTrace();

        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void reload(int materia_id) {
        materia = db.getSubject((long)materia_id);
        criarUI(view_do_onViewCreated);
    }
}
