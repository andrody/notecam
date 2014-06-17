package model;

import android.content.Context;
import android.text.format.Time;

import com.koruja.notecam.MateriasActivity;
import com.koruja.notecam.R;

import java.util.List;
import java.util.Random;

import helper.DatabaseHelper;
import helper.Singleton;

/**
 * Created by Andrew on 10/24/13.
 */
public class Materia {
    public static String ID = "id";
    public static String NAME = "name";

    @Override
    public String toString() {
        return name;
    }

    private boolean checkboxSelecionada = false;
    private List<Aula> aulas;
    private List<Topico> topicos;
    private int color = -1;
    private int id = -1;
    private int icon_id = -1;
    private int numero_fotos = 0;
    private String name = "";
    Context context;

    public Materia(String name, Context context){
        this.name = name;
        this.context = context;
    }
    public Materia(Context context){
        this.context = context;
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
        popularTopicos();
        popularClasses();
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

    public void addTopico(String nome) {
        Topico novo_topico = new Topico(context, nome, this.getId());
        novo_topico.save(context);
        novo_topico.popularFotos();
        topicos.add(novo_topico);
    }

    public void setRandomColor() {
        Random random = new Random();
        int r = (random.nextInt(Singleton.getListaCores().size()));
        setColor(Singleton.getListaCores().get(r));
    }

    public void setRandomIcon() {
        Random random = new Random();
        int r = (random.nextInt(Singleton.getListaIcones().size()));
        setIcon_id(Singleton.getListaIcones().get(r));
    }

    public boolean isColored(){
        if(this.color != -1)
            return true;
        return false;
    }

    public boolean isIconed(){
        if(this.icon_id != -1)
            return true;
        return false;
    }

    public int getColorNumber(){
        return this.color;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void popularClasses(){
        DatabaseHelper db = ((MateriasActivity)context).getDb();
        setAulas(db.getAllClassesBySubject(this.getId()));
    }

    public void popularTopicos(){
        DatabaseHelper db = ((MateriasActivity)context).getDb();
        setTopicos(db.getAllTopicosBySubject(this.getId()));
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
        return db.getAllTopicosBySubject(this.getId()).size();
    }

    public int getNumero_fotos() {
        int i = 0;
        for(Topico t: getTopicos()){
            i += t.getFotos().size();
        }
        return i;
    }

    public void setNumero_fotos(int numero_fotos) {
        this.numero_fotos = numero_fotos;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
    }

    public List<Topico> getTopicos() {
        return topicos;
    }

    public void setTopicos(List<Topico> topicos) {
        this.topicos = topicos;
    }
}
