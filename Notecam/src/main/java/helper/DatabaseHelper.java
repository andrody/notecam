package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

import com.koruja.notecam.MateriasActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.Aula;
import model.Foto;
import model.Subject;
import model.Topico;

public class DatabaseHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 16;

    // Database Name
    private static final String DATABASE_NAME = "subjectsManager";

    // Table Names
    private static final String TABLE_SUBJECT = "subjects";
    private static final String TABLE_CLASS = "classes";
    private static final String TABLE_TOPICO = "topicos";
    private static final String TABLE_FOTO = "fotos";
    private static final String TABLE_DAY = "days";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    // SUBJECTS Table - column names
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_STATUS = "status";
    private static final String KEY_COLOR = "color";


    // CLASSES Table - column names
    private static final String KEY_CLASS_WEEK_DAY = "class_week_day";
    private static final String KEY_CLASS_START_HOUR = "class_start_hour";
    private static final String KEY_CLASS_START_MINUTE = "class_start_minute";
    private static final String KEY_CLASS_END_HOUR = "class_end_hour";
    private static final String KEY_CLASS_END_MINUTE = "class_end_minute";
    private static final String KEY_CLASS_SUBJECTID = "class_subjectID";


    // DAYS Table - column names
    private static final String KEY_DAY_SUBJECT = "day_subjectID";
    private static final String KEY_DAY_NAME = "day_name";
    private static final String KEY_DAY_NUMBER = "day_number";
    private static final String KEY_DAY_WEEKDAY = "day_weekday";

    // TOPICOS Table - column names
    private static final String KEY_TOPICO_SUBJECT = "topico_subjectID";
    private static final String KEY_TOPICO_NAME = "topico_name";
    private static final String KEY_TOPICO_NUMBER = "topico_number";

    // FOTOS Table - column names
    private static final String KEY_FOTO_TOPICO = "foto_topicoID";
    private static final String KEY_FOTO_NAME = "foto_name";
    private static final String KEY_FOTO_NUMBER = "foto_number";
    private static final String KEY_FOTO_PATH = "foto_path";


    // Table Create Statements
    // Subject table create statement
    private static final String CREATE_TABLE_SUBJECT = "CREATE TABLE "
            + TABLE_SUBJECT + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_SUBJECT + " TEXT,"
            + KEY_COLOR + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    // Class table create statement
    private static final String CREATE_TABLE_CLASS = "CREATE TABLE "
            + TABLE_CLASS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_CLASS_WEEK_DAY + " TEXT,"
            + KEY_CLASS_START_HOUR + " INTEGER,"
            + KEY_CLASS_START_MINUTE + " INTEGER,"
            + KEY_CLASS_END_HOUR + " INTEGER,"
            + KEY_CLASS_END_MINUTE + " INTEGER,"
            + KEY_CLASS_SUBJECTID + " INTEGER,"
            + KEY_CREATED_AT + " DATETIME"
            + ")";

    // Day table create statement
    private static final String CREATE_TABLE_DAY = "CREATE TABLE "
            + TABLE_DAY + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_DAY_NAME + " TEXT,"
            + KEY_DAY_SUBJECT + " INTEGER,"
            + KEY_DAY_NUMBER + " INTEGER,"
            + KEY_DAY_WEEKDAY + " INTEGER,"
            + KEY_CREATED_AT + " INTEGER"
            + ")";

    // Topico table create statement
    private static final String CREATE_TABLE_TOPICO = "CREATE TABLE "
            + TABLE_TOPICO + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TOPICO_NAME + " TEXT,"
            + KEY_TOPICO_SUBJECT + " INTEGER,"
            + KEY_TOPICO_NUMBER + " INTEGER,"
            + KEY_CREATED_AT + " INTEGER"
            + ")";

    // Foto table create statement
    private static final String CREATE_TABLE_FOTO = "CREATE TABLE "
            + TABLE_FOTO + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_FOTO_NAME + " TEXT,"
            + KEY_FOTO_PATH + " TEXT,"
            + KEY_FOTO_TOPICO + " INTEGER,"
            + KEY_FOTO_NUMBER + " INTEGER,"
            + KEY_CREATED_AT + " INTEGER"
            + ")";


    Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_SUBJECT);
        db.execSQL(CREATE_TABLE_CLASS);
        db.execSQL(CREATE_TABLE_TOPICO);
        db.execSQL(CREATE_TABLE_FOTO);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOPICO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOTO);

        // create new tables
        onCreate(db);
    }
    // ------------------------ Utils table methods ----------------//

    public int createSubjectAndClasses(Subject subject, List<Aula> aulas){
        long subject_id = createSubject(subject);
        if(aulas != null)
            createClasses(aulas, subject_id);
        return (int)subject_id;
    }

    public void updateSubjectAndClasses(Subject subject, List<Aula> aulas){
        updateSubject(subject);
        deleteAllClassesBySubject(subject.getId());
        createClasses(aulas, subject.getId());
        //closeDB();
    }

    public void deleteSubjectAndClasses(Subject subject){
        deleteAllFotosBySubject(subject.getId());
        deleteAllTopicosBySubject(subject.getId());
        deleteAllClassesBySubject(subject.getId());
        deleteSubject(subject.getId());
        //closeDB();
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    // ------------------------ "Subject" table methods ----------------//

    /*
     * Creating a Subject
     */
    private long createSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, subject.getName());
        values.put(KEY_COLOR, subject.getColorNumber());
        values.put(KEY_CREATED_AT, getDateTime());


        // insert row
        long subject_id = db.insert(TABLE_SUBJECT, null, values);

        // assigning tags to subject
        //for (long class_id : classes_ids) {
        //    createTodoTag(subject_id, class_id);
        // }

        ((MateriasActivity)context).getViewPager().getAdapter().notifyDataSetChanged();
        ((MateriasActivity)context).getMateriasFragment().syncDB();
        return subject_id;
    }

    /*
    * get single subject
    */
    public Subject getSubject(long subject_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECT + " WHERE "
                + KEY_ID + " = " + subject_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Subject sb = new Subject(context);
        sb.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        sb.setName((c.getString(c.getColumnIndex(KEY_SUBJECT))));
        sb.setColor((c.getInt(c.getColumnIndex(KEY_COLOR))));

        sb.setTopicos(getAllTopicosBySubject(subject_id));


        return sb;
    }

    /*
    * getting all subjects
    * */
    public List<Subject> getAllSubjects() {
        List<Subject> subjects = new ArrayList<Subject>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBJECT;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Subject sb = new Subject(context);
                sb.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                sb.setName((c.getString(c.getColumnIndex(KEY_SUBJECT))));
                sb.setColor((c.getInt(c.getColumnIndex(KEY_COLOR))));

                // adding to subject list
                subjects.add(sb);
            } while (c.moveToNext());
        }
        closeDB();

        return subjects;
    }

    /*
    * Updating subject
    */
    private int updateSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, subject.getName());
        values.put(KEY_COLOR, subject.getColorNumber());


        // updating row
        return db.update(TABLE_SUBJECT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(subject.getId()) });
    }

    /*
    * Deleting subject
    */
    private int deleteSubject(long subject_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int retorno = db.delete(TABLE_SUBJECT, KEY_ID + " = ?",
                new String[] { String.valueOf(subject_id) });

        ((MateriasActivity)context).getViewPager().getAdapter().notifyDataSetChanged();
        ((MateriasActivity)context).getMateriasFragment().syncDB();
        return retorno;

    }





    // ------------------------ "Classes" table methods ----------------//

    /*
    * Creating class
    */
    private long createClass(Aula cl, long subject_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLASS_WEEK_DAY, cl.getWeekday());
        values.put(KEY_CLASS_START_HOUR, cl.getStartTime().hour);
        values.put(KEY_CLASS_START_MINUTE, cl.getStartTime().minute);
        values.put(KEY_CLASS_END_HOUR, cl.getEndTime().hour);
        values.put(KEY_CLASS_END_MINUTE, cl.getEndTime().minute);
        values.put(KEY_CLASS_SUBJECTID, subject_id);

        // insert row
        long class_id = db.insert(TABLE_CLASS, null, values);

        return class_id;
    }

    private void createClasses(List<Aula> aulas, long subject_id){
        for(Aula cl : aulas)
            createClass(cl, subject_id);
    }

    /*
    * Updating a class
    */
    private int updateClass(Aula cl) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLASS_WEEK_DAY, cl.getWeekday());
        values.put(KEY_CLASS_START_HOUR, cl.getStartTime().hour);
        values.put(KEY_CLASS_START_MINUTE, cl.getStartTime().hour);
        values.put(KEY_CLASS_END_HOUR, cl.getEndTime().hour);
        values.put(KEY_CLASS_END_MINUTE, cl.getEndTime().minute);
        values.put(KEY_CLASS_SUBJECTID, cl.getSubjectId());


        // updating row
        return db.update(TABLE_CLASS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(cl.getId()) });
    }

    /*
    * getting all classes under single subject
    */
    public List<Aula> getAllClassesBySubject(long subject_id) {
        List<Aula> aulas = new ArrayList<Aula>();

        String selectQuery = "SELECT  * FROM "
                + TABLE_CLASS + " WHERE "
                + KEY_CLASS_SUBJECTID + " = " + subject_id;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Aula cl = new Aula();
                //cl.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                cl.setSubjectId((int)subject_id);
                cl.setWeekday((c.getInt(c.getColumnIndex(KEY_CLASS_WEEK_DAY))));
                cl.setStartTime(c.getInt(c.getColumnIndex(KEY_CLASS_START_HOUR)), c.getInt(c.getColumnIndex(KEY_CLASS_START_MINUTE)));
                cl.setEndTime(c.getInt(c.getColumnIndex(KEY_CLASS_END_HOUR)), c.getInt(c.getColumnIndex(KEY_CLASS_END_MINUTE)));

                // adding to class list
                aulas.add(cl);
            } while (c.moveToNext());
        }

        return aulas;
    }

    private void deleteClass(long class_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS, KEY_CLASS_SUBJECTID + " = ?",
                new String[] { String.valueOf(class_id) });
    }

    private void deleteAllClassesBySubject(long subject_id) {
        /*List<Class> classes = getAllClassesBySubject(subject_id);
        for(Class cl : classes)
            deleteClass(cl.getId());*/
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS, KEY_CLASS_SUBJECTID + " = ?",
                new String[] { String.valueOf(subject_id) });
    }

    public void deleteAllClasses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASS, null, null);
    }

    // ----------------------------------------------------------//
    // ------------------------ Topico table  methods ----------------//
    // ----------------------------------------------------------//


    /*
    * Creating Topico
    */
    public long createTopico(Topico topico) {
        SQLiteDatabase db = this.getWritableDatabase();



        ContentValues values = new ContentValues();
        values.put(KEY_TOPICO_NAME, topico.getName());
        values.put(KEY_TOPICO_SUBJECT, topico.getSubject_id());
        values.put(KEY_TOPICO_NUMBER, topico.getNumber());

        Time time = new Time();
        time.setToNow();
        values.put(KEY_CREATED_AT, time.toMillis(true));

        // insert row
        long topico_id = db.insert(TABLE_TOPICO, null, values);

        return topico_id;
    }

    /*
    * Updating Topico
    */
    public int updateTopico(Topico topico) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TOPICO_NAME, topico.getName());
        values.put(KEY_TOPICO_SUBJECT, topico.getSubject_id());
        values.put(KEY_TOPICO_NUMBER, topico.getNumber());


        // updating topico
        assert db != null;
        return db.update(TABLE_TOPICO, values, KEY_ID + " = ?",
                new String[] { String.valueOf(topico.getId()) });
    }

    /*
    * Deleting topico
    */
    private void deleteTopico(long topico_id) {
        Topico topico = getTopico(topico_id);
        Subject subject = getSubject(topico.getSubject_id());
        DeleteRecursive(new File(Singleton.NOTECAM_FOLDER + "/" + subject.getName() + "/" + topico.getName()));

        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        db.delete(TABLE_TOPICO, KEY_ID + " = ?",
                new String[] { String.valueOf(topico_id) });
    }

    /*
    * get single topico
    */
    public Topico getTopico(long topico_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TOPICO + " WHERE "
                + KEY_ID + " = " + topico_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Topico topico = new Topico(context);
        topico.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        topico.setName((c.getString(c.getColumnIndex(KEY_TOPICO_NAME))));
        topico.setNumber((c.getInt(c.getColumnIndex(KEY_TOPICO_NUMBER))));
        topico.setSubject_id((c.getInt(c.getColumnIndex(KEY_TOPICO_SUBJECT))));
        topico.setCreatedAt(c.getInt(c.getColumnIndex(KEY_CREATED_AT)));


        return topico;
    }

    /*
    * getting all topicos
    *
     */
    public List<Topico> getAllTopicos() {
        List<Topico> topicos = new ArrayList<Topico>();
        String selectQuery = "SELECT  * FROM " + TABLE_TOPICO;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Topico topico = new Topico(context);
                topico.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                topico.setName((c.getString(c.getColumnIndex(KEY_TOPICO_NAME))));
                topico.setNumber((c.getInt(c.getColumnIndex(KEY_TOPICO_NUMBER))));
                topico.setSubject_id((c.getInt(c.getColumnIndex(KEY_SUBJECT))));
                topico.setCreatedAt(c.getInt(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to subject list
                topicos.add(topico);
            } while (c.moveToNext());
        }
        closeDB();

        return topicos;
    }

    /*
    * delete all topicos of a subject
    */
    private void deleteAllTopicosBySubject(long subject_id) {
        DeleteRecursive(new File(Singleton.NOTECAM_FOLDER + "/" + getSubject(subject_id).getName()));
        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        db.delete(TABLE_TOPICO, KEY_TOPICO_SUBJECT + " = ?",
                new String[] { String.valueOf(subject_id) });
    }

    void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);

        fileOrDirectory.delete();
    }

    /*
   * getting all topicos under single subject
   */
    public List<Topico> getAllTopicosBySubject(long subject_id) {
        List<Topico> topicos = new ArrayList<Topico>();

        String selectQuery = "SELECT  * FROM "
                + TABLE_TOPICO + " WHERE "
                + KEY_TOPICO_SUBJECT + " = " + subject_id + " ORDER BY " + KEY_CREATED_AT + " DESC";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Topico topico = new Topico(context);
                topico.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                topico.setName((c.getString(c.getColumnIndex(KEY_TOPICO_NAME))));
                topico.setNumber((c.getInt(c.getColumnIndex(KEY_TOPICO_NUMBER))));
                topico.setSubject_id((c.getInt(c.getColumnIndex(KEY_TOPICO_SUBJECT))));
                topico.setCreatedAt(c.getInt(c.getColumnIndex(KEY_CREATED_AT)));
                Log.e(LOG, "Topico " + topico.getName() + " - Criado em " + topico.getCreatedAt());

                // adding to subject list
                topicos.add(topico);
            } while (c.moveToNext());
        }

        return topicos;
    }

    // ----------------------------------------------------------//
    // -------------------- Fotos table  methods ----------------//
    // ----------------------------------------------------------//


    /*
    * Creating Foto
    */
    public long createFoto(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOTO_NAME, foto.getName());
        values.put(KEY_FOTO_TOPICO, foto.getTopico().getId());
        values.put(KEY_FOTO_PATH, foto.getPath());

        Time time = new Time();
        time.setToNow();
        values.put(KEY_CREATED_AT, time.toMillis(true));

        // insert row
        long foto_id = db.insert(TABLE_FOTO, null, values);

        return foto_id;
    }

    /*
    * Updating Foto
    */
    public int updateFoto(Foto foto) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FOTO_NAME, foto.getName());
        values.put(KEY_FOTO_TOPICO, foto.getTopico().getId());
        values.put(KEY_FOTO_PATH, foto.getPath());


        // updating foto
        assert db != null;
        return db.update(TABLE_FOTO, values, KEY_ID + " = ?",
                new String[] { String.valueOf(foto.getId()) });
    }

    /*
    * Deleting foto
    */
    private void deleteFoto(long foto_id) {
        File file = new File(getFoto(foto_id).getPath());
        file.delete();

        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        db.delete(TABLE_FOTO, KEY_ID + " = ?",
                new String[] { String.valueOf(foto_id) });
    }

    /*
    * get single foto
    */
    public Foto getFoto(long foto_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_FOTO + " WHERE "
                + KEY_ID + " = " + foto_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Foto foto = new Foto();
        foto.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        foto.setName((c.getString(c.getColumnIndex(KEY_FOTO_NAME))));
        foto.setPath((c.getString(c.getColumnIndex(KEY_FOTO_PATH))));
        //foto.setNumber((c.getInt(c.getColumnIndex(KEY_TOPICO_NUMBER))));
        foto.setTopico_id((c.getInt(c.getColumnIndex(KEY_FOTO_TOPICO))));
        foto.setCreatedAt(c.getInt(c.getColumnIndex(KEY_CREATED_AT)));


        return foto;
    }

    /*
    * getting all fotos
    *
     */
    public List<Foto> getAllFotos() {
        List<Foto> fotos = new ArrayList<Foto>();
        String selectQuery = "SELECT  * FROM " + TABLE_FOTO;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto foto = new Foto();
                foto.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                foto.setName((c.getString(c.getColumnIndex(KEY_FOTO_NAME))));
                foto.setPath((c.getString(c.getColumnIndex(KEY_FOTO_PATH))));
                //foto.setNumber((c.getInt(c.getColumnIndex(KEY_TOPICO_NUMBER))));
                foto.setTopico_id((c.getInt(c.getColumnIndex(KEY_FOTO_TOPICO))));
                foto.setCreatedAt(c.getInt(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to subject list
                fotos.add(foto);
            } while (c.moveToNext());
        }
        closeDB();

        return fotos;
    }

    /*
    * delete all fotos of a topico
    */
    private void deleteAllFotosByTopico(long topico_id) {
        for(Foto foto : getTopico(topico_id).getFotos()) {
            deleteFoto(foto.getId());
        }
        SQLiteDatabase db = this.getWritableDatabase();
        assert db != null;
        db.delete(TABLE_FOTO, KEY_FOTO_TOPICO + " = ?",
                new String[] { String.valueOf(topico_id) });
    }

    /*
    * delete all fotos of a subject
    */
    private void deleteAllFotosBySubject(long subject_id) {
        Subject subject = getSubject(subject_id);
        for(Topico topico : subject.getTopicos()) {
            deleteAllFotosByTopico(topico.getId());
        }
    }

    /*
   * getting all fotos under single topico
   */
    public List<Foto> getAllFotosByTopico(long topico_id) {
        List<Foto> fotos = new ArrayList<Foto>();

        String selectQuery = "SELECT  * FROM "
                + TABLE_FOTO + " WHERE "
                + KEY_FOTO_TOPICO + " = " + topico_id + " ORDER BY " + KEY_CREATED_AT + " ASC";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Foto foto = new Foto();
                foto.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                foto.setName((c.getString(c.getColumnIndex(KEY_FOTO_NAME))));
                foto.setPath((c.getString(c.getColumnIndex(KEY_FOTO_PATH))));
                //foto.setNumber((c.getInt(c.getColumnIndex(KEY_TOPICO_NUMBER))));
                foto.setTopico_id((c.getInt(c.getColumnIndex(KEY_FOTO_TOPICO))));
                foto.setCreatedAt(c.getInt(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to subject list
                fotos.add(foto);
            } while (c.moveToNext());
        }

        return fotos;
    }


    // ----------------------------------------------------------//
    // ------------------------ Other methods ----------------//
    // ----------------------------------------------------------//


    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }






}




/*
    * getting all classes under single subject
    *
    public List<Class> getAllClassesBySubject(int subject_id) {
        List<Class> classes = new ArrayList<Class>();

        String selectQuery = "SELECT  * FROM "
                + TABLE_CLASS + " cl, "
                + TABLE_SUBJECT + " sb, "
                + TABLE_SUBJECT_CLASS + " sc WHERE sb."
                + KEY_SUBJECT_ID + " = '" + subject_id + "'"+ " AND sb."
                + KEY_ID + " = " + "sc."
                + KEY_SUBJECT_ID + " AND cl."
                + KEY_ID + " = "
                + "sc." + KEY_CLASS_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Class cl = new Class();
                //cl.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                cl.setSubjectId(subject_id);
                cl.setSubjectId(c.getInt((c.getColumnIndex(KEY_ID))));
                cl.setWeekday((c.getInt(c.getColumnIndex(KEY_CLASS_WEEK_DAY))));
                cl.setStartTime(c.getInt(c.getColumnIndex(KEY_CLASS_START_HOUR)), c.getColumnIndex(KEY_CLASS_START_MINUTE));
                cl.setEndTime(c.getInt(c.getColumnIndex(KEY_CLASS_END_HOUR)), c.getColumnIndex(KEY_CLASS_END_MINUTE));

                // adding to class list
                classes.add(cl);
            } while (c.moveToNext());
        }

        return classes;
    }*/