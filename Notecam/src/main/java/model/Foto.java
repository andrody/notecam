package model;

import android.content.Context;

import com.koruja.notecam.MateriasActivity;

/**
 * Created by Andrew on 30/04/14.
 */
public class Foto {
    public static String ID = "id";
    public static String NAME = "name";

    private boolean checkboxSelecionada = false;
    private Topico topico;
    private int id = -1;
    private int topico_id;
    private String name = "";
    private String path;
    private int createdAt;

    public Foto(String name){
        this.name = name;
    }
    public Foto(String name, String path, Topico topico){
        this.name = name;
        this.path = path;
        this.setTopico(topico);
    }
    public Foto(){}

    public void save(Context context){
        this.id = (int) ((MateriasActivity) context).getDb().createFoto(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCheckboxSelecionada() {
        return checkboxSelecionada;
    }

    public void setCheckboxSelecionada(boolean checkboxSelecionada) {
        this.checkboxSelecionada = checkboxSelecionada;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }


    public Topico getTopico() {
        return topico;
    }

    public void setTopico(Topico topico) {
        this.topico = topico;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getTopico_id() {
        return topico_id;
    }

    public void setTopico_id(int topico_id) {
        this.topico_id = topico_id;
    }
}
