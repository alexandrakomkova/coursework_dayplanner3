package oop.dayplanner3.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME="DayPlanner.db";
    private final String log_tag = "DayPlanner";
    private Context context;

    public static final String TABLE_TASK = "tasks";
    public static final String COLUMN_TASK_ID = "_id"; //1
    public static final String COLUMN_TITLE_ID = "title"; //2
    public static final String COLUMN_TIMESTART = "start_time";//4
    public static final String COLUMN_TIMEFINISH = "finish_time";//4

    public static final String TABLE_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_ID = "_id"; //1
    public static final String COLUMN_CATEGORY_TITLE = "title"; //2


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+TABLE_CATEGORY+" (" + COLUMN_CATEGORY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_TITLE + " TEXT UNIQUE);"); //tried to add unique option/ check if it is working


        db.execSQL("CREATE TABLE "+TABLE_TASK+" (" + COLUMN_TASK_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE_ID + " INTEGER, "
                + COLUMN_TIMESTART + " TEXT, "
                + COLUMN_TIMEFINISH + " TEXT, "
                + " constraint _id_fk foreign key("+COLUMN_TITLE_ID+") references "+TABLE_CATEGORY+"("+ COLUMN_CATEGORY_ID+")"
                +" on delete cascade on update cascade);");


        insertDataToCategory(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CATEGORY);
        onCreate(db);
    }

    public void insertDataToCategory(SQLiteDatabase db){
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Work');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Eat Time');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Nap');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Study');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Family');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Friends');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Shopping');");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY +
                " (" + COLUMN_CATEGORY_TITLE + ") " +
                " VALUES ('Sleep');");
    }

    public Cursor readAllCategoryData()
    {
        String query = "select * from " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

//    public static final int DATABASE_VERSION = 1;
//    public static final String DATABASE_NAME="Task.db";
//    private final String log_tag = "DayPlanner";
//    private Context context;
//
//    public static final String TABLE_TASK = "tasks";
//    public static final String COLUMN_IDTASK = "_id"; //1
//    //public static final String COLUMN_TITLE = "title"; //2
//    public static final String COLUMN_IDTITLE = "title"; //2
//    public static final String COLUMN_TIMESTART = "start_time";//4
//    public static final String COLUMN_TIMEFINISH = "finish_time";//4
//
//    public static final String TABLE_CATEGORY = "category";
//    public static final String COLUMN_IDCATEGORY = "_id"; //1
//    public static final String COLUMN_CATEGORYTITLE = "title"; //2
//
//
//    public DatabaseHelper(@Nullable Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        this.context = context;
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
////        db.execSQL("CREATE TABLE "+TABLE_TASK+" (" + COLUMN_IDTASK
////                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
////                + COLUMN_TITLE + " TEXT, "
////                + COLUMN_TIMESTART + " TEXT, "
////                + COLUMN_TIMEFINISH + " TEXT);");
//
//        db.execSQL("CREATE TABLE "+TABLE_TASK+" (" + COLUMN_IDTASK
//                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_IDTITLE + " INTEGER, "
//                + COLUMN_TIMESTART + " TEXT, "
//                + COLUMN_TIMEFINISH + " TEXT, "
//                + " constraint _id_fk foreign key("+COLUMN_IDTITLE+") references "+TABLE_CATEGORY+"("+ COLUMN_IDCATEGORY+")"
//                +" on delete cascade on update cascade);");
//
//        db.execSQL("CREATE TABLE "+TABLE_CATEGORY+" (" + COLUMN_IDCATEGORY
//                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//                + COLUMN_CATEGORYTITLE + " TEXT);");
//
//        insertDataToCategory(db);
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
//        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TASK);
//        onCreate(db);
//    }
//
//    public void insertDataToCategory(SQLiteDatabase db){
//        try {
//            db.rawQuery("select * from " + TABLE_CATEGORY, null);
//        } catch (SQLException r) {
//            db.execSQL("INSERT INTO " + TABLE_CATEGORY +
//                    " (" + COLUMN_CATEGORYTITLE + ") " +
//                    " VALUES ('Work');");
//        }
//    }
}