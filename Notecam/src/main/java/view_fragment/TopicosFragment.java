package view_fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.io.Console;
import java.util.List;
import java.util.Set;

import Dialogs.CreateTopicoDialog;
import helper.DatabaseHelper;
import helper.Singleton;
import list.TopicosAdapter;
import model.Foto;
import model.Materia;
import model.Topico;

public class TopicosFragment extends Fragment implements View.OnClickListener {

    private DatabaseHelper db;

    private ListView lista;
    private Materia materia;

    //Booleana que diz se o ActionMode (LongPress) foi ativado ou não. Caso sim ele ativa as checkboxes dos items da lista
    private boolean fakeActionModeOn = false;


    @Override
    public void onResume() {
        super.onResume();
        reload(Singleton.getMateria_selecionada());
        setFakeActionModeOn(false);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topicos, container, false);
        final ListView list = (ListView) view.findViewById(R.id.topicos_list);
        setLista(list);
        list.setAdapter(new TopicosAdapter(getActivity(), materia));

        //Seta um evento de Pressionar por longo tempo na lista
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //Callback
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int position, long id) {

                //Avisa que estamos no modo de edição (Selecionar e Deletar items)
                setFakeActionModeOn(true);

                reload(materia);

                //Seta o checkbox do item que foi pressionado como selecionado automaticamente
                ((CheckBox) v.findViewById(R.id.checkbox)).setChecked(true);


                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Singleton.setPrimeiraFoto(true);
                final Topico topico_selecionado = materia.getTopicos().get(i);

                //Faz fotos aparecerem na galeria de fotos do android
                for (final Foto foto : topico_selecionado.getFotos()) {
                    MediaScannerConnection.scanFile(getActivity(),
                            new String[]{foto.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.d("LOG", "scanned : " + path);
                                    foto.setUri(uri);
                                    //foto.save(getActivity());

                                    Singleton.setTopico_selecionado(topico_selecionado);
                                    Singleton.setGaleriaFragment(new GaleriaFragment());
                                    Singleton.changeFragments(Singleton.getGaleriaFragment());



                                    /*if (Singleton.isPrimeiraFoto()) {
                                        Singleton.setPrimeiraFoto(false);

                                        //Se houver fotos
                                        if (!(materia.getTopicos().get(i).getFotos() == null || materia.getTopicos().get(i).getFotos().size() == 0)) {

                                            //Metodo 2 - Funciona do jeito que eu quero! aewwwww!
                                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                                            String mime = "image/*";
                                            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                                            if (mimeTypeMap.hasExtension(
                                                    mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                                                mime = mimeTypeMap.getMimeTypeFromExtension(
                                                        mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                                            intent.setDataAndType(uri,mime);
                                            startActivity(intent);

                                            //Metodo 3 - Também funciona do jeito que eu quero! aewwwww!
                                            /*String[] strings = uri.toString().split("/");
                                            String imageId = strings[strings.length - 1];
                                            openInGallery(imageId);*/

                                        //} else {
                                        //    Toast.makeText(getActivity(), "Não há fotos para exibir", Toast.LENGTH_SHORT).show();
                                        //}
                                    //}
                                }
                            }
                    );
                }

            }
        });


        //Setando listener do botão de Menu
        view.findViewById(R.id.back).setOnClickListener(this);

        //Setando listener do botão de Editar Matéria
        view.findViewById(R.id.editar_materia).setOnClickListener(this);

        //Setando listener do botão de Adicionar Topico
        view.findViewById(R.id.adicionar_topico).setOnClickListener(this);

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

    public void openInGallery(String imageId) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(imageId).build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private Singleton.OnFragmentInteractionListener mListener;

    public static TopicosFragment newInstance(int materia_id) {
        TopicosFragment fragment = new TopicosFragment();
        Bundle args = new Bundle();
        args.putInt(Singleton.MATERIA_ID, materia_id);
        args.putString(Singleton.FRAGMENT_TYPE, Singleton.FRAGMENT_TYPE_MATERIA);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TopicosFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tem de habilitar para mudar o ActionBar
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            this.materia = Singleton.getMateria_selecionada();
        }

    }

    public void reload(model.Materia nova_materia) {
        this.materia = nova_materia;

        getLista().setAdapter(new TopicosAdapter(getActivity(), Singleton.getMateria_selecionada()));
        Singleton.mudarCorHeader(this, materia.getColor());

        TextView nome_materia = (TextView) getView().findViewById(R.id.nome_materia);
        nome_materia.setText(materia.getName());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //Abre Drawer Menu
            case R.id.back:
                ((MateriasActivity) getActivity()).getDrawerLayout().openDrawer(Gravity.LEFT);
                break;

            //Abre tela de edição de matéria
            case R.id.editar_materia:

                //Cria uma nova instância do Fragment AddMateriaFragment e passa o id da materia como parametro para edição
                AddMateriaFragment addMateriaFragment = AddMateriaFragment.newInstance(materia.getId());
                Singleton.setAddMateriaFragment(addMateriaFragment);

                //Muda o fragment
                Singleton.changeFragments(Singleton.getAddMateriaFragment());
                break;

            //Cria um novo tópico
            case R.id.adicionar_topico:
                OpenCreateTopicoDialog();

            //Cancela o Fake Action Mode
            case R.id.cancelar:
                setFakeActionModeOn(false);
                break;

            //Deleta os topicos
            case R.id.deletar:
                deletar_topicos();
                setFakeActionModeOn(false);
                break;
        }
    }

    public void deletar_topicos(){
        //Pega a referencia do banco do Singleton
        DatabaseHelper db = Singleton.getDb();

        //Para cada subject da lista
        for(model.Topico topico : materia.getTopicos()){

            //Descobre em qual a view corresponde a este subject
            View view = ((TopicosAdapter)getLista().getAdapter()).getView(topico.getId());

            //Pega uma referência para o checkbox dele
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            //Se ele estiver marcado
            if(checkBox.isChecked()){

                //Deleta esse subject e todas as suas classes
                db.deleteTopico(topico.getId());
            }
        }

        materia.popularTopicos();
        reload(materia);
    }

    public void OpenCreateTopicoDialog() {
        //Cria um dialog passa os argumentos
        CreateTopicoDialog dialog = new CreateTopicoDialog();
        dialog.setMateria(materia);
        dialog.show(getFragmentManager(), "Digite o nome do Tópico");
    }

    public ListView getLista() {
        return lista;
    }

    public void setLista(ListView lista) {
        this.lista = lista;
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
}

