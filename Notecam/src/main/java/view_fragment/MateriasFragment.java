package view_fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import helper.DatabaseHelper;
import helper.PdfCreator;
import helper.Singleton;


public class MateriasFragment extends Fragment implements View.OnClickListener {

    GridView gridview;
    MateriasAdapter materiasAdapter  = null;

    //Booleana que diz se o ActionMode (LongPress) foi ativado ou não. Caso sim ele ativa as checkboxes dos items da lista
    private boolean fakeActionModeOn = false;

    Resources resources;

    @Override
    public void onResume() {
        super.onResume();
        setFakeActionModeOn(false);

    }

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
            //mListener.onFragmentInteraction(null, values);
        }


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
                if(fakeActionModeOn){
                    CheckBox checkbox = ((CheckBox) view.findViewById(R.id.checkbox_materia));
                    checkbox.setChecked(!checkbox.isChecked());
                }
                else {
                    //Pede ao Activity para mudar fragment (Materia)
                    //if (mListener != null) {

                        //Pega a materia selecionada
                        model.Materia materia = (model.Materia)materiasAdapter.getItem(position);
                        Singleton.setMateria_selecionada(materia);
                        Singleton.getCameraFragment().reload(null);
                        Singleton.getMateriasActivity().getViewPager().setCurrentItem(1, true);
                        //((MateriasActivity)getActivity()).getViewPager().getAdapter().notifyDataSetChanged();

                        //ContentValues values = new ContentValues();
                        //values.put(Singleton.REPLACE_FRAGMENT, Singleton.MATERIA);
                        //values.put(Singleton.MATERIA_ID, materia.getId());

                        //mListener.onFragmentInteraction(null, values);
                    }
                //}
            }
        });

        //Seta um evento de Pressionar por longo tempo na lista
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //Callback
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int position, long id) {

                //Avisa que estamos no modo de edição (Selecionar e Deletar items)
                setFakeActionModeOn(true);


                //Seta o checkbox do item que foi pressionado como selecionado automaticamente
                ((CheckBox) v.findViewById(R.id.checkbox_materia)).setChecked(true);


                //Atualiza a lista
                materiasAdapter.notifyDataSetChanged();

                return true;
            }
        });

        //Marca a opção do menu
        LinearLayout materias = (LinearLayout)getActivity().findViewById(R.id.menu_option_materias);
        materias.setBackgroundColor(getResources().getColor(R.color.background_menu_selected));

    }

    private Singleton.OnFragmentInteractionListener mListener;


    public static MateriasFragment newInstance() {
        MateriasFragment fragment = new MateriasFragment();
        Bundle args = new Bundle();
        args.putString(Singleton.FRAGMENT_TYPE, Singleton.FRAGMENT_TYPE_MATERIAS);
        fragment.setArguments(args);
        return fragment;
    }
    public MateriasFragment() {
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
        View view = inflater.inflate(R.layout.fragment_materias, container, false);

        //Setando listener do botão de Chamar o Menu
        View menu = view.findViewById(R.id.menu);
        menu.setOnClickListener(this);

        //Setando listener do botão de Adicionar Matéria
        View add_materia = view.findViewById(R.id.add_materia);
        add_materia.setOnClickListener(this);

        //Setando listener do botão de Deletar
        View deletar = view.findViewById(R.id.deletar);
        deletar.setOnClickListener(this);

        //Setando listener do botão de Compartilhar
        View compartilhar = view.findViewById(R.id.compartilhar);
        compartilhar.setOnClickListener(this);

        //Setando listener do botão de Cancelar
        View cancelar = view.findViewById(R.id.cancelar);
        cancelar.setOnClickListener(this);

        //Setando listener do botão de Cancelar Também
        View cancelar2 = view.findViewById(R.id.cancelar2);
        cancelar2.setOnClickListener(this);


        return view;
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
            mListener = (Singleton.OnFragmentInteractionListener) activity;
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

    public boolean isFakeActionModeOn() {
        return fakeActionModeOn;
    }

    public void setFakeActionModeOn(boolean fakeActionModeOn) {
        if(fakeActionModeOn){
            getView().findViewById(R.id.fake_action_mode).setVisibility(View.VISIBLE);
        }
        else {
            getView().findViewById(R.id.fake_action_mode).setVisibility(View.GONE);
        }

        this.fakeActionModeOn = fakeActionModeOn;

    }

    public void reload() {
        materiasAdapter = new MateriasAdapter(getActivity());
        gridview.setAdapter(materiasAdapter);

    }

    //Syncroniza com o banco de dados
    public void syncDB(){
        try {
            //Pega a referencia do banco da activity
            DatabaseHelper db = ((MateriasActivity) getActivity()).getDb();

            //Pede todos os subjects do banco
            ArrayList<model.Materia> materias = (ArrayList<model.Materia>) db.getAllSubjects();

            //Pede toda as classes desse subject e armazena nele
            for (model.Materia materia : materias) {
                materia.setAulas(db.getAllClassesBySubject(materia.getId()));
            }

            //Limpa o adapter
            //galeriaAdapter.clear();

            //Se existir pelo menos um subject, adiciona no adapter
            if (!materias.isEmpty())
                materiasAdapter.materias = materias;
            //galeriaAdapter.setData(subjects);

            //Atualiza tela
            materiasAdapter.notifyDataSetChanged();

            ((MateriasActivity)getActivity()).setEmptyFragments(db.getAllSubjects().isEmpty());
            ((MateriasActivity)getActivity()).getViewPager().getAdapter().notifyDataSetChanged();

            //Atualiza tela
            materiasAdapter.notifyDataSetChanged();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        };
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
                List<model.Materia> materias = materiasAdapter.materias;

                //Pega a referencia do banco da activity
                DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

                //Para cada subject da lista
                for(model.Materia materia : materias){

                    //Descobre em qual a view corresponde a este subject
                    View view = materiasAdapter.getView(materia.getId());

                    //Pega uma referência para o checkbox dele
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_subject);

                    //Se ele estiver marcado
                    if(checkBox.isChecked()){

                        //Deleta esse subject e todas as suas classes
                        db.deleteSubjectAndClasses(materia);
                    }
                }
                syncDB();
                if(db.getAllSubjects().isEmpty())
                    MateriasFragment.this.reload();


            }
            mode.finish();
            return true;
        }



        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Quando o ActionMode sumir, seta a flag como falso
            setFakeActionModeOn(false);

            //Atualiza a tela
            syncDB();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //Abre Drawer Menu
            case R.id.menu:
                //((MateriasActivity)getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);
                //new PdfCreator().criarPDF(Singleton.getMateria_selecionada(), Singleton.getMateria_selecionada().getTopicos());
                Singleton.gerar_pdf(Singleton.getMateria_selecionada(), Singleton.getMateria_selecionada().getTopicos());
                break;

            //Adiciona nova materia
            case R.id.add_materia:
                Singleton.setAddMateriaFragment(new AddMateriaFragment());
                Singleton.changeFragments(Singleton.getAddMateriaFragment());
                break;

            //Cancela o Fake Action Mode
            case R.id.cancelar:
                setFakeActionModeOn(false);
                break;

            //Cancela o Fake Action Mode
            case R.id.deletar:
                deletar_materias();
                setFakeActionModeOn(false);
                break;
        }
    }

    public void deletar_materias(){

        //Pede todos os subjects do adapter e põe numa lista
        List<model.Materia> materias = materiasAdapter.materias;

        //Pega a referencia do banco da activity
        DatabaseHelper db = ((MateriasActivity)getActivity()).getDb();

        //Para cada subject da lista
        for(model.Materia materia : materias){

            //Descobre em qual a view corresponde a este subject
            View view = materiasAdapter.getView(materia.getId());

            //Pega uma referência para o checkbox dele
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_materia);

            //Se ele estiver marcado
            if(checkBox.isChecked()){

                //Deleta esse subject e todas as suas classes
                db.deleteSubjectAndClasses(materia);
            }
        }

        //Atualiza a tela
        syncDB();
        if(db.getAllSubjects().isEmpty())
            MateriasFragment.this.reload();

    }
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

    ArrayList<model.Materia> materias;
    private HashMap<Integer, View> views = new HashMap<Integer, View>();

    Context context;

    MateriasAdapter(Context context) {
        this.context = context;

        materias = new ArrayList<model.Materia>();
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
        TextView myInitialLetter;
        TextView nome_materia;
        View back_color;
        TextView numero_fotos;
        CheckBox checkbox_materia;
        Drawable drawable;
        View icon;

        ViewHolder(View v) {
            myInitialLetter = (TextView) v.findViewById(R.id.letra_inicial_image);
            nome_materia = (TextView) v.findViewById(R.id.materia_nome_text);
            numero_fotos = (TextView) v.findViewById(R.id.materia_numero_fotos_text);
            icon = v.findViewById(R.id.imagem_materia);
            checkbox_materia = (CheckBox) v.findViewById(R.id.checkbox_materia);
            back_color = v.findViewById(R.id.foto_back);
            drawable  = context.getResources().getDrawable(R.drawable.materia);
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

        final model.Materia item = (model.Materia) getItem(position);

        String nome_materia = "#" + materias.get(position).getName().toLowerCase();

        Paint paint = new Paint();

        //Seleciona o ícone da materia
        holder.icon.setBackgroundResource(materias.get(position).getIcon_id());

        //Seta o nome e tamanho
        holder.nome_materia.setText("#" + materias.get(position).getName().toLowerCase());
        //holder.nome_materia.setTextSize(perfectSize);
        correctWidth(holder.nome_materia, 200);

        //holder.numero_fotos.setText(fotos.get(position).getNumero_fotos() + " fotos");


        holder.drawable.setColorFilter(materias.get(position).getColor(), PorterDuff.Mode.SRC_ATOP);

        //Troca cor de fundo das matérias
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.back_color.setBackgroundDrawable(holder.drawable);
        } else {
            holder.back_color.setBackground(holder.drawable);
        }



        views.put(item.getId(), row);

        //Se está no modo de edição?
        boolean checkBoxFlag = ((MateriasActivity)context).getMateriasFragment().isFakeActionModeOn();

        //Se está no modo de edição (deletar) torna o checkbox visivel
        if(checkBoxFlag) {
            holder.checkbox_materia.setVisibility(CheckBox.VISIBLE);
            holder.icon.setVisibility(CheckBox.GONE);
        }
        else {
            holder.checkbox_materia.setVisibility(CheckBox.GONE);
            holder.icon.setVisibility(CheckBox.VISIBLE);
            holder.checkbox_materia.setChecked(false);

        }

        return row;
    }

    public void correctWidth(TextView textView, int desiredWidth)
    {
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > desiredWidth)
        {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
    }

    public View getView(int id){
        return views.get(id);
    }

}
