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

public class SingleMateriaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MATERIA_ID = "materia_id";

    // TODO: Rename and change types of parameters
    private Subject materia;
    private DatabaseHelper db;

    private Singleton.OnFragmentInteractionListener mListener;


    public static SingleMateriaFragment newInstance(int materia_id) {
        SingleMateriaFragment fragment = new SingleMateriaFragment();
        Bundle args = new Bundle();
        args.putInt(MATERIA_ID, materia_id);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleMateriaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int materia_id = getArguments().getInt(MATERIA_ID);
            db = ((MateriasActivity)getActivity()).getDb();
            materia = db.getSubject((long)materia_id);
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

        super.onViewCreated(view, savedInstanceState);
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
