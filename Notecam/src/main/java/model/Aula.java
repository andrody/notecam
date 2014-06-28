package model;

import android.text.format.Time;

import com.koruja.notecam.R;

import helper.Singleton;

public class Aula {

    public static String HORA = "hora";
    public static String MINUTO = "minuto";
    public static String POSITION = "position";
    public static String STARTIME = "startime";


    private int subjectId;
    private int id = -1;
    private int weekday;
    private Time startTime;
    private Time endTime;
    private int position;

    public Aula(int weekday){
        this.setWeekday(weekday);
        startTime = new Time();
        endTime = new Time();
        setStartTime(0,0);
        setEndTime(0,0);
    }

    public Aula(int weekday, int StartHour, int StartMinute, int EndHour, int EndMinute){
        this.setWeekday(weekday);
        startTime = new Time();
        endTime = new Time();
        setStartTime(StartHour,StartMinute);
        setEndTime(EndHour,EndMinute);
    }

    public Aula(){
        startTime = new Time();
        endTime = new Time();
        setStartTime(0,0);
        setEndTime(0,0);
    }


    public static String getWeekDayString(int weekday){
        switch(weekday){
            case 0:
                return Singleton.getMateriasActivity().getString(R.string.dom);
            case 1:
                return Singleton.getMateriasActivity().getString(R.string.seg);
            case 2:
                return Singleton.getMateriasActivity().getString(R.string.ter);
            case 3:
                return Singleton.getMateriasActivity().getString(R.string.qua);
            case 4:
                return Singleton.getMateriasActivity().getString(R.string.qui);
            case 5:
                return Singleton.getMateriasActivity().getString(R.string.sex);
            case 6:
                return Singleton.getMateriasActivity().getString(R.string.sab);

        }
        return null;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public Time getStartTime() {
        Time time = new Time();
        time.setToNow();
        time.set(startTime.second,startTime.minute,startTime.hour,time.monthDay,time.month,time.year);
        return time;
    }

    public void setStartTime(int hour, int minute) {
        this.startTime.set(0,minute,hour,0,0,0);
    }

    public Time getEndTime() {
        Time time = new Time();
        time.setToNow();
        time.set(endTime.second,endTime.minute,endTime.hour,time.monthDay,time.month,time.year);
        return time;
    }

    public void setEndTime(int hour, int minute) {
        this.endTime.set(0,minute,hour,0,0,0);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
