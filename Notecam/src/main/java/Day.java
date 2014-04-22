import java.util.List;

/**
 * Created by Andrew on 10/24/13.
 */
public class Day {
    public static String ID = "id";
    public static String NAME = "name";

    private boolean checkboxSelecionada = false;
    private List<Class> classes;
    private int color = -1;
    private int id = -1;
    private int weekday = 0;
    private int number;
    private int subject_id;
    private String name = "";
    private int createdAt;

    public Day(String name){
        this.name = name;
    }
    public Day(int subject_id, int color){
        this.setSubject_id(subject_id);
        this.setColor(color);
    }
    public Day(){}

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

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public String getWeekDayLong(int i){
        if(i == 0)
            return "Sunday";
        if(i == 1)
            return "Monday";
        if(i == 2)
            return "Tuesday";
        if(i == 3)
            return "Wednesday";
        if(i == 4)
            return "Thursday";
        if(i == 5)
            return "Friday";
        if(i == 6)
            return "Saturday" ;
        return "";

    }
}
