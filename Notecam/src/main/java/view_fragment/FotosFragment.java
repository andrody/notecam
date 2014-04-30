package view_fragment;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;

import com.koruja.notecam.R;

import helper.Singleton;
import list.FotoAdapter;

public class FotosFragment extends Fragment {
    private Singleton.OnFragmentInteractionListener mListener;
    GridView gridview;
    FotoAdapter fotosAdapter  = null;
    int materia_id, topico_id;




    //Booleana que diz se o ActionMode (LongPress) foi ativado ou não. Caso sim ele ativa as checkboxes dos items da lista
    private boolean checkboxFlag = false;

    Resources resources;

    public static FotosFragment newInstance(int materia_id, int topico_id) {
        FotosFragment fragment = new FotosFragment();
        Bundle args = new Bundle();
        args.putInt(Singleton.MATERIA_ID, materia_id);
        args.putInt(Singleton.TOPICO_ID, topico_id);
        fragment.setArguments(args);
        return fragment;
    }

    public FotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tem de habilitar para mudar o ActionBar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fotos, container, false);

        //Referencia aos Resources (R.strings...)
        resources = getActivity().getResources();

        //Cria o adapter
        fotosAdapter = new FotoAdapter(getActivity());

        //Syncroniza lista com o banco
        //syncDB();

        gridview = (GridView) v.findViewById(R.id.fotos_grid_view);
        gridview.setAdapter(fotosAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Seleciona ou deseleciona materia se estiver em modo de edição
                if(checkboxFlag){
                    CheckBox checkbox = ((CheckBox) view.findViewById(R.id.checkBox_subject));
                    checkbox.setChecked(!checkbox.isChecked());
                }
                else {
                    //TODO: Ver foto em full screen
                    if (mListener != null) { }
                }
            }
        });

        //Seta um evento de Pressionar por longo tempo na lista
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //Callback
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int position, long id) {

                /*/Inicia o ActionMode (Header especializado azul)
                MateriasFragment.this.getActivity().startActionMode(mActionModeCallback);

                //Avisa que estamos no modo de edição (Selecionar e Deletar items)
                setCheckboxFlag(true);

                //Atualiza a lista
                materiasAdapter.notifyDataSetChanged();

                //Seta o checkbox do item que foi pressionado como selecionado automaticamente
                ((CheckBox) v.findViewById(R.id.checkBox_subject)).setChecked(true);*/

                return true;
            }
        });

        return v;
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
        inflater.inflate(R.menu.materias, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
