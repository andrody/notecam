package model;

import android.content.Context;
import android.net.Uri;

import com.koruja.notecam.MateriasActivity;

import helper.Singleton;

/**
 * Created by Andrew on 30/04/14.
 */
public class Foto {

    private boolean checkboxSelecionada = false;
    private Topico topico;
    private int id = -1;
    private int topico_id;
    private String name = "";
    private String path;
    private Uri uri;
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

    public void save(){
        //Se foto não existe cria uma
        if(this.id == -1)
            this.id = (int) Singleton.getDb().createFoto(this);

        //Se já existe apenas atualiza
        else
            Singleton.getDb().updateFoto(this);


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

    public void delete() {
        Singleton.getDb().deleteFoto(this);
    }



    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
