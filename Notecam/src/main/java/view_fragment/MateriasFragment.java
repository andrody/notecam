package view_fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;
import com.koruja.notecam.SubjectsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.DatabaseHelper;
import helper.Singleton;
import model.Subject;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link view_fragment.MateriasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link view_fragment.MateriasFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MateriasFragment extends Fragment {

    GridView gridview;
    MateriasAdapter materiasAdapter  = null;

    //Booleana que diz se o ActionMode (LongPress) foi ativado ou não. Caso sim ele ativa as checkboxes dos items da lista
    private boolean checkboxFlag = false;

    Resources resources;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Referencia aos Resources (R.strings...)
        resources = getActivity().getResources();

        //Adiciona titulo ao mTitle da Activity
        if (mListener != null) {
            ContentValues values = new ContentValues();
            values.put(Singleton.TITLE, getActivity().getResources().getString(R.string.materias));
            mListener.onFragmentInteraction(null, values);
        }
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.materias));
        getActivity().getActionBar().setSubtitle("5 horas até a próxima aula");

        //Cria o adapter
        materiasAdapter = new MateriasAdapter(getActivity());

        //Syncroniza lista com o banco
        syncDB();

        gridview = (GridView) getActivity().findViewById(R.id.materias_grid_view);
        gridview.setAdapter(materiasAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Seleciona ou deseleciona materia se estiver em modo de edição
                if(checkboxFlag){
                    CheckBox checkbox = ((CheckBox) view.findViewById(R.id.checkBox_subject));
                    checkbox.setChecked(!checkbox.isChecked());
                }
            }
        });

        //Seta um evento de Pressionar por longo tempo na lista
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //Callback
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int position, long id) {

                //Inicia o ActionMode (Header especializado azul)
                MateriasFragment.this.getActivity().startActionMode(mActionModeCallback);

                //Avisa que estamos no modo de edição (Selecionar e Deletar items)
                setCheckboxFlag(true);

                //Atualiza a lista
                materiasAdapter.notifyDataSetChanged();

                //Seta o checkbox do item que foi pressionado como selecionado automaticamente
                ((CheckBox) v.findViewById(R.id.checkBox_subject)).setChecked(true);

                return true;
            }
        });

        //Marca a opção do menu
        LinearLayout materias = (LinearLayout)getActivity().findViewById(R.id.menu_option_materias);
        materias.setBackgroundColor(getResources().getColor(R.color.background_menu_selected));

    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MateriasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MateriasFragment newInstance(String param1, String param2) {
        MateriasFragment fragment = new MateriasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public MateriasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_materias, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri, null);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean isCheckboxFlag() {
        return checkboxFlag;
    }

    public void setCheckboxFlag(boolean checkboxFlag) {
        this.checkboxFlag = checkboxFlag;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri, ContentValues content);
    }

    //Syncroniza com o banco de dados
    public void syncDB(){
        //Pega a referencia do banco da activity
        DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

        //Pede todos os subjects do banco
        ArrayList<Subject> subjects =(ArrayList<Subject>) db.getAllSubjects();

        //Pede toda as classes desse subject e armazena nele
        for(Subject subject : subjects){
            subject.setAulas(db.getAllClassesBySubject(subject.getId()));
        }

        //Limpa o adapter
        //materiasAdapter.clear();

        //Se existir pelo menos um subject, adiciona no adapter
        if(!subjects.isEmpty())
            materiasAdapter.materias = subjects;
        //materiasAdapter.setData(subjects);

        //Atualiza tela
        materiasAdapter.notifyDataSetChanged();
    }

    //Callback para quando clica nos ícones do header no ActionMode (Modo de edição)
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            //Adiciona as opções de deletar e compartilhar
            menu.add(resources.getString(R.string.deletar))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(resources.getString(R.string.exportar))
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            //Se o botão selecionado pelo usuario for o de deletar
            if(item.getTitle().equals("Deletar")){

                //Pede todos os subjects do adapter e põe numa lista
                List<Subject> subjects = materiasAdapter.materias;

                //Pega a referencia do banco da activity
                DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

                //Para cada subject da lista
                for(Subject subject : subjects){

                    //Descobre em qual a view corresponde a este subject
                    View view = materiasAdapter.getView(subject.getId());

                    //Pega uma referência para o checkbox dele
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_subject);

                    //Se ele estiver marcado
                    if(checkBox.isChecked()){

                        //Deleta esse subject e todas as suas classes
                        db.deleteSubjectAndClasses(subject);
                    }
                }
            }

            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Quando o ActionMode sumir, seta a flag como falso
            setCheckboxFlag(false);

            //Atualiza a tela
            syncDB();
        }
    };

}

class Materia {
    int image_id, numero_fotos, color_id;
    String nome;
    Materia(String nome, int image_id, int numero_fotos, int color_id) {
        this.nome = nome;
        this.image_id = image_id;
        this.color_id = color_id;
        this.numero_fotos = numero_fotos;
    }

}

/**
 * O BaseAdapter é responsável por construir as views do gridview
 */
class MateriasAdapter extends BaseAdapter {

    ArrayList<Subject> materias;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    MateriasAdapter(Context context) {
        this.context = context;

        materias = new ArrayList<Subject>();
        //Resources res = context.getResources();

        // TODO: Remover essas materias testes
        //materias.add(new Materia("Matematica", R.drawable.ma, 2, R.color.red));
        //materias.add(new Materia("Física", R.drawable.ma, 47, R.color.blue));
        //materias.add(new Materia("Geografia", R.drawable.ma, 0, R.color.blue));
        //materias.add(new Materia("História", R.drawable.ma, 38, R.color.red));
        //materias.add(new Materia("Português", R.drawable.ma, 12, R.color.red));
    }

    @Override
    public int getCount() {
        return materias.size();
    }

    @Override
    public Object getItem(int position) {
        return materias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView myInitialLetter;
        TextView nome_materia;
        TextView numero_fotos;
        CheckBox checkbox;
        ViewHolder(View v) {
            myInitialLetter = (ImageView) v.findViewById(R.id.letra_inicial_image);
            nome_materia = (TextView) v.findViewById(R.id.materia_nome_text);
            numero_fotos = (TextView) v.findViewById(R.id.materia_numero_fotos_text);
            checkbox = (CheckBox) v.findViewById(R.id.checkBox_subject);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        //Se estamos chamando o getView pela primeira vez (Operações custosas)
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_materia_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        final Subject item = (Subject) getItem(position);

        //holder.myInitialLetter.setImageResource(materias.get(position).image_id);
        holder.myInitialLetter.setBackgroundResource(R.color.red);//materias.get(position).getColor());
        holder.nome_materia.setText(materias.get(position).getName());
        holder.numero_fotos.setText(materias.get(position).getNumero_fotos() + " fotos");

        views.put(item.getId(), row);

        //Se está no modo de edição?
        boolean checkBoxFlag = ((MateriasActivity)context).getMateriasFragment().isCheckboxFlag();

        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(checkBoxFlag)
            holder.checkbox.setVisibility(CheckBox.VISIBLE);
        else
            holder.checkbox.setVisibility(CheckBox.GONE);

        /*final ViewHolder holder_final = holder;

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxFlag)
                    holder_final.checkbox.setChecked(true);
            }
        });*/

        return row;
    }

    public View getView(int id){
        return views.get(id);
    }




}
