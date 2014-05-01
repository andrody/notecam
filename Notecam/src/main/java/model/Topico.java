package model;

import android.content.Context;

import com.koruja.notecam.MateriasActivity;

import java.util.List;

/**
 * Created by Andrew on 10/24/13.
 */
public class Topico {
    public static String ID = "id";
    public static String NAME = "name";

    private boolean checkboxSelecionada = false;
    private List<Aula> aulas;
    private int id = -1;
    private int number = 0;
    private int subject_id;
    private String name = "";
    private int createdAt;

    public Topico(String name, int subject_id){
        this.name = name;
        this.subject_id = subject_id;
    }

    public Topico(){}
    public Topico(String nome){
        this.name = nome;
    }

    public void save(Context context){
        this.id = (int) ((MateriasActivity) context).getDb().createTopico(this);
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

}
