package model;

/**
 * Created by Andrew on 30/04/14.
 */
public class Foto {
    public static String ID = "id";
    public static String NAME = "name";

    private boolean checkboxSelecionada = false;
    private Topico topico;
    private int id = -1;
    private int subject_id;
    private String name = "";
    private int createdAt;

    public Foto(String name){
        this.name = name;
    }
    public Foto(String name, int subject_id, Topico topico, int id){
        this.setSubject_id(subject_id);
        this.name = name;
        this.topico = topico;
        this.id = id;
    }
    public Foto(){}

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

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }


}
