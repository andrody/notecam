package com.koruja.notecam;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import helper.Singleton;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MateriasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MateriasFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MateriasFragment extends Fragment {

    GridView gridview;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Adiciona titulo ao mTitle da Activity
        if (mListener != null) {
            ContentValues values = new ContentValues();
            values.put(Singleton.TITLE, getActivity().getResources().getString(R.string.materias));
            mListener.onFragmentInteraction(null, values);
        }
        getActivity().getActionBar().setTitle(getActivity().getResources().getString(R.string.materias));
        getActivity().getActionBar().setSubtitle("5 horas até a próxima aula");
        gridview = (GridView) getActivity().findViewById(R.id.materias_grid_view);
        gridview.setAdapter(new MateriasAdapter(getActivity()));
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

    ArrayList<Materia> materias;
    Context context;

    MateriasAdapter(Context context) {
        this.context = context;

        materias = new ArrayList<Materia>();
        //Resources res = context.getResources();

        // TODO: Remover essas materias testes
        materias.add(new Materia("Matematica", R.drawable.ma, 2, R.color.red));
        materias.add(new Materia("Física", R.drawable.ma, 47, R.color.blue));
        materias.add(new Materia("Geografia", R.drawable.ma, 0, R.color.blue));
        materias.add(new Materia("História", R.drawable.ma, 38, R.color.red));
        materias.add(new Materia("Português", R.drawable.ma, 12, R.color.red));
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
        ViewHolder(View v) {
            myInitialLetter = (ImageView) v.findViewById(R.id.letra_inicial_image);
            nome_materia = (TextView) v.findViewById(R.id.materia_nome_text);
            numero_fotos = (TextView) v.findViewById(R.id.materia_numero_fotos_text);
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

        //holder.myInitialLetter.setImageResource(materias.get(position).image_id);
        holder.myInitialLetter.setBackgroundResource(materias.get(position).color_id);
        holder.nome_materia.setText(materias.get(position).nome);
        holder.numero_fotos.setText(materias.get(position).numero_fotos + " fotos");

        return row;
    }
}
