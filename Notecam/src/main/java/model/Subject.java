package model;

import android.content.Context;
import android.text.format.Time;

import com.koruja.notecam.R;

import java.util.List;
import java.util.Random;

import helper.DatabaseHelper;

/**
 * Created by Andrew on 10/24/13.
 */
public class Subject {
    public static String ID = "id";
    public static String NAME = "name";

    @Override
    public String toString() {
        return name;
    }

    private boolean checkboxSelecionada = false;
    private List<Aula> aulas;
    private int color;
    private int id = -1;
    private int image_id = 0;
    private int numero_fotos = 0;
    private String name = "";
    Context context;

    public Subject(String name, Context context){
        this.name = name;
        this.context = context;
    }
    public Subject(Context context){ this.context = context; }

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

    public List<Aula> getAulas() {
        return aulas;
    }

    public void setAulas(List<Aula> aulas) {
        this.aulas = aulas;
    }

    public void setRandomColor() {
        Random random = new Random();
        int color = (random.nextInt(10));
        switch (this.color){
            case 0:
                setColor(context.getResources().getColor(R.color.red));
            case 1:
                setColor( context.getResources().getColor(R.color.red));
            case 2:
                setColor( context.getResources().getColor(R.color.red));
            case 3:
                setColor( context.getResources().getColor(R.color.red));
            case 4:
                setColor( context.getResources().getColor(R.color.red));
            case 5:
                setColor( context.getResources().getColor(R.color.blue));
            case 6:
                setColor( context.getResources().getColor(R.color.blue));
            case 7:
                setColor( context.getResources().getColor(R.color.blue));
            case 8:
                setColor( context.getResources().getColor(R.color.blue));
            case 9:
                setColor( context.getResources().getColor(R.color.blue));
            default:
                setColor( context.getResources().getColor(R.color.blue));
        };
    }

    public boolean isColored(){
        if(this.color >= 0)
            return true;
        return false;
    }

    public int getColorNumber(){
        return this.color;
    }

    public int getColor() {
        return this.color;
        /*switch (this.color){
            case 0:
                return Color.rgb(245,85,95);
            case 1:
                return Color.rgb(120,210,132);
            case 2:
                return Color.rgb(241,150,61);
            case 3:
                return Color.rgb(95,135,237);
            case 4:
                return Color.rgb(248,191,80);
            case 5:
                return Color.rgb(177,230,88);
            case 6:
                return Color.rgb(98,215,208);
            case 7:
                return Color.rgb(222,127,243);
            case 8:
                return Color.rgb(249,143,233);
            case 9:
                return Color.rgb(209,209,209);
            default:
                return Color.rgb(0,0,0);
        }*/
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void popularClasses(DatabaseHelper db){
        setAulas(db.getAllClassesBySubject(this.getId()));
    }

    /*
    * Checa se está em horario de aula
     */
    public Aula ChecarHorario(){
        Time time = new Time();
        time.setToNow();
        for (Aula cl : aulas)
            if(time.after(cl.getStartTime()) && time.before(cl.getEndTime()) && cl.getWeekday() == time.weekDay)
                return cl;
        return null;
    }

    public int getNumberDays(DatabaseHelper db) {
        return db.getAllDaysBySubject(this.getId()).size();
    }

    public int getNumero_fotos() {
        return numero_fotos;
    }

    public void setNumero_fotos(int numero_fotos) {
        this.numero_fotos = numero_fotos;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }
}
