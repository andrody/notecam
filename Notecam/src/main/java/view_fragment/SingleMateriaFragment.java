package view_fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.MenuItem;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        //Se o botão selecionado pelo usuario for o de ver as fotos (icone pasta)
        if(item.getTitle().equals(getResources().getString(R.string.action_go_folder))){
            ((MateriasActivity)getActivity()).getViewPager().setCurrentItem(2);
        }

        //Se o botão selecionado pelo usuario for o de editar materia
        if(item.getTitle().equals(getResources().getString(R.string.action_editar_materia))){
            //Cria uma nova instância do Fragment addSubjectsFragment
            AddSubjectFragment addSubjectFragment = AddSubjectFragment.newInstance(materia.getId());

            ((MateriasActivity)getActivity()).setAddSubjectsFragment(addSubjectFragment);

            ((MateriasActivity)getActivity()).changeFragments(((MateriasActivity) getActivity()).getAddSubjectsFragment(), null);
        }



        return super.onOptionsItemSelected(item);
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
            setMateria(db.getSubject((long) materia_id));

            //Tem de habilitar para mudar o ActionBar
            setHasOptionsMenu(true);
        }
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
        //Se não houver topicos criados na materia, cria um
        if (getMateria().getTopicos().isEmpty()) {
            getMateria().addTopico("Geral");
        }
        this.topico = getMateria().getTopicos().get(0);

        if(!Singleton.getMateria_selecionada().equals(getMateria())) Singleton.setMateria_selecionada(getMateria());





        //Setando cor da materia aos graficos
        final Drawable camera = getResources().getDrawable( R.drawable.compact_camera );
        final ColorFilter filter = new LightingColorFilter(getMateria().getColor(), Color.BLACK);
        final ColorFilter filter_branco = new LightingColorFilter(Color.WHITE, Color.WHITE);
        camera.setColorFilter(filter);

        //Referencia da camera
        final ImageView image_camera = (ImageView) view.findViewById(R.id.camera_dentro_circulo);

        //Cor do circulo da camera
        final GradientDrawable background = (GradientDrawable) view.findViewById(R.id.camera_circulo).getBackground();
        background.setStroke(12, getMateria().getColor());

        //Cor e texto do nome da materia
        TextView nome_materia = ((TextView)view.findViewById(R.id.nome_materia));
        nome_materia.setText(getMateria().getName());
        nome_materia.setTextColor(getMateria().getColor());

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
                    Singleton.getPictureTaker().TakePicture("teste");
                }
                else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    background.setColor(getMateria().getColor());
                    image_camera.setColorFilter(filter_branco);
                }

                return true;
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Singleton.getPictureTaker().TakePicture("teste");
            }
        });


        View view_f = view;

        //Botao de topicos
        View topicos_box = view.findViewById(R.id.topicos_box);
        topicos_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Coloca o view como final para ser acessível dentro da inner class
                final View view_f = v;

                //Cria um dialog para escolher os dias da semana
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

                //Coloca o titulo
                b.setTitle("Topicos");

                String[] types = new String[getMateria().getTopicos().size()];

                for(int i = 0; i< getMateria().getTopicos().size(); i++ )
                    types[i] = getMateria().getTopicos().get(i).getName();

                //Coloca as opções e um ClickListener
                b.setItems(types, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        //Quando clica em alguma opção, Fecha o dialog
                        dialog.dismiss();

                        //Altera no model do adapter a semana selecionada
                        SingleMateriaFragment.this.topico = getMateria().getTopicos().get(position);

                        //Texto do Topico
                        TextView nome_topico = ((TextView)view_f.findViewById(R.id.nome_topico));
                        nome_topico.setText(topico.getName());
                    }

                });

                //Mostra o dialog
                b.show();

            }
        });

        //Botao de adicioar novo topico
        View novo_topico = view.findViewById(R.id.novo_topico);
        novo_topico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTopicoFragment addTopicoFragment = AddTopicoFragment.newInstance(materia.getId(), -1);
                ((MateriasActivity)getActivity()).changeFragments(addTopicoFragment, null);
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
            getActivity().getActionBar().setTitle(getMateria().getName());
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
        setMateria(db.getSubject((long)materia_id));
        criarUI(view_do_onViewCreated);
    }

    public Subject getMateria() {
        return materia;
    }

    public void setMateria(Subject materia) {
        this.materia = materia;
    }
}
