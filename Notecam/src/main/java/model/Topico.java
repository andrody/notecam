package model;

import android.content.Context;

import com.koruja.notecam.MateriasActivity;

import java.util.List;

import helper.DatabaseHelper;
import helper.Singleton;

/**
 * Created by Andrew on 10/24/13.
 */
public class Topico {
    public static String ID = "id";
    public static String NAME = "name";

    private boolean checkboxSelecionada = false;
    private List<Foto> fotos;
    private int id = -1;
    private int number = 0;
    private int subject_id;
    private String name = "";
    private int createdAt;
    private Context context;

    public Topico(Context context, String name, int subject_id){
        this.name = name;
        this.subject_id = subject_id;
        this.context = context;
    }

    public Topico(Context context){ this.context = context;}

    public Topico(Context context, String nome){
        this.name = nome;
        this.context = context;

    }

    public void save(Context context){
        if(this.id == -1)
            this.id = (int) Singleton.getDb().createTopico(this);
        else
            Singleton.getDb().updateTopico(this);
    }

    public void popularFotos(){
        DatabaseHelper db = ((MateriasActivity)context).getDb();
        setFotos(db.getAllFotosByTopico(this.getId()));

    }

    public String get_path(){
        return Singleton.NOTECAM_FOLDER + "/" + Singleton.getMateria_selecionada().getName() + "/" + Singleton.getMateria_selecionada().getName() + "-" + this.getName() + "/";
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

    public int getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    //TODO: deletar depois
    public String getWeekDayLong(int i){
        return "";
    }
    public int getWeekday(){
        return 0;
    }

    public List<Foto> getFotos() {
        if(fotos ==null)
            popularFotos();
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    public void delete(){
        Singleton.getDb().deleteTopico(this.getId());
    }
}
