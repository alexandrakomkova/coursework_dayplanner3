package oop.dayplanner3.activity;


import static oop.dayplanner3.TaskProviderDatabase.TASK_URI;
import static oop.dayplanner3.TaskProviderDatabase.log_tag;

import android.annotation.SuppressLint;

import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import oop.dayplanner3.R;
import oop.dayplanner3.adapter.CategoryAdapter;
import oop.dayplanner3.database.DatabaseHelper;
import oop.dayplanner3.model.Category;


public class NewTaskActivity extends AppCompatActivity {
    Spinner addTaskTitle;
    EditText taskStartTime;
    EditText taskFinishTime;
    Button addTask, addCategory;
    TextView timeStartTitle, timeFinishTitle, timeNight;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Integer taskId;
    boolean isEdit, isNightSleep;
    int mHour, mMinute, hourStart, hourFinish, minuteStart, minuteFinish;
    TimePickerDialog timePickerDialog;
    String nightSleepTime;
    String log_tag = "NewTaskActivity";

    ArrayAdapter<String> spinnerAdapter;
    List<String> list;
    Integer itemId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();

        addTaskTitle = findViewById(R.id.spinnerTaskTitle);
        taskStartTime = findViewById(R.id.taskStartTime);
        taskFinishTime = findViewById(R.id.taskFinishTime);
        addTask = findViewById(R.id.addTask);
        addCategory = findViewById(R.id.addCategory);
        timeStartTitle = findViewById(R.id.timeStartTitle);
        timeFinishTitle = findViewById(R.id.timeFinishTitle);
        timeNight = findViewById(R.id.timeNight);
        list = new ArrayList<String>();


        addToSpinner();

        taskStartTime.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(this,
                        (view12, hourOfDay, minute) -> {
                            taskStartTime.setText(hourOfDay + ":" + minute);
                            hourStart = hourOfDay;
                            timePickerDialog.dismiss();
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
            return true;
        });

        taskFinishTime.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(this,
                        (view12, hourOfDay, minute) -> {
                            taskFinishTime.setText(hourOfDay + ":" + minute);
                            hourFinish = hourOfDay;
                            timePickerDialog.dismiss();
                        }, mHour, mMinute, false);
                timePickerDialog.show();

            }
            return true;
        });

        taskId = getIntent().getIntExtra("taskId", -1);
       // Log.d(log_tag, "TASKID"+taskId);
        isEdit = getIntent().getBooleanExtra("isEdit", false);


        if(taskId!=null || taskId!=-1){
            setDataInFields(taskId);
        }
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addTaskTitle.getSelectedItem().toString().equals("Sleep")){
                    if(validateNightSleepFields())
                        saveTask();
                }
                else {
                    if (validateFields())
                        saveTask();
                }

            }
        });

        addTaskTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                /*itemId = (int)id;
                 itemId = position;*/

                itemId = findCategoryId(addTaskTitle.getSelectedItem().toString());
                //Log.d(log_tag, "id: "+String.valueOf(id));
                //Log.d(log_tag, "position: "+String.valueOf(position));
                isNightSleep = addTaskTitle.getSelectedItem().toString().equals("Sleep");
                if(isNightSleep){
                    showNightSleepFields();

                }
                else{
                    showTaskFields();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        timeNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        NewTaskActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minuteOfDay) {
                                mHour = hourOfDay;
                                mMinute = minuteOfDay;
                                String time = mHour + ":" + mMinute;
                                nightSleepTime = time;
                                SimpleDateFormat f24Hour = new SimpleDateFormat("HH:mm");
                                try{
                                    Date date = f24Hour.parse(time);
                                    timeNight.append(f24Hour.format(date));
                                }catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 24, 0, true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(mHour, mMinute);
                timePickerDialog.show();
            }
        });

    }

    public void addToSpinner() {

        String query = "select "+databaseHelper.COLUMN_CATEGORY_TITLE+" from " + databaseHelper.TABLE_CATEGORY;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        cursor.moveToFirst();


        if(cursor!=null && cursor.getCount()!=0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                String s = cursor.getString(0);
               // Log.d(log_tag, "list: "+ s);

                list.add(s.toString());
                cursor.moveToNext();
            }

            cursor.close();

            spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            addTaskTitle.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDataInFields(Integer id){
        String query = "select * from " + databaseHelper.TABLE_TASK + " where "+databaseHelper.COLUMN_TASK_ID + " = "+ String.valueOf(id);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        cursor.moveToFirst();

        if(cursor!=null && cursor.getCount()!=0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){

                for (int position = 0; position < addTaskTitle.getCount(); position++) {
                    if(addTaskTitle.getItemAtPosition(position).equals(findCategoryTitle(cursor.getString(1)))) {
                        addTaskTitle.setSelection(position);

                    }
                }
                if(findCategoryTitle(cursor.getString(1)).equals("Sleep")){
                    timeNight.append(cursor.getString(3));
                }else{
                    taskStartTime.setText(cursor.getString(2));
                    taskFinishTime.setText(cursor.getString(3));
                }

                cursor.moveToNext();
            }

            cursor.close();

        }

//        Log.d(log_tag, "ID i: "+strArgs);
//        if (cursor != null && cursor.getCount() != 0) {
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//
//                for(int i =0; i < spinnerAdapter.getCount(); i++){
//                    Log.d(log_tag, "ID i: "+i);
//                    Log.d(log_tag, "ID cursor: "+cursor.getString(1));
//                    Log.d(log_tag, "ID list: "+list.get(i));
//                    if(list.get(i).equals(cursor.getString(1))){
//                        //Log.d(log_tag, "ID cursor: "+cursor.getString(1));
//                        addTaskTitle.setSelection(i);
//                    }
//                }
//
//                if(cursor.getString(1).equals("Sleep")){
//                    timeNight.append(cursor.getString(3));
//                }else{
//                    taskStartTime.setText(cursor.getString(2));
//                    taskFinishTime.setText(cursor.getString(3));
//                }
//                cursor.moveToNext();
//            }
//        }
    }

    public String findCategoryTitle(String id){
        databaseHelper = new DatabaseHelper(getApplicationContext());
        String query = "select "+databaseHelper.COLUMN_CATEGORY_TITLE+" from " + databaseHelper.TABLE_CATEGORY + " where " + databaseHelper.COLUMN_CATEGORY_ID + " = "+id;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String s = "";
        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        cursor.moveToFirst();

        if(cursor!=null && cursor.getCount()!=0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){

                s = cursor.getString(0);
                cursor.moveToNext();
                //Log.d(log_tag, "find: "+ s);
            }

            cursor.close();
        }
        return s;
    }

    public Integer findCategoryId(String title){
        databaseHelper = new DatabaseHelper(getApplicationContext());
        String query = "select "+databaseHelper.COLUMN_CATEGORY_ID+" from " + databaseHelper.TABLE_CATEGORY + " where " + databaseHelper.COLUMN_CATEGORY_TITLE + " = \""+title+"\"";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Integer s = 0;
        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        cursor.moveToFirst();

        if(cursor!=null && cursor.getCount()!=0) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){

                s = cursor.getInt(0);
                cursor.moveToNext();
                //Log.d(log_tag, "find id: "+ s);
            }

            cursor.close();
        }
        return s;
    }

    private void saveTask() {
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                if (!isEdit) {

                    if(isNightSleep){
                        addTaskToDatabase(
                                itemId,
                                "0:0",
                                nightSleepTime);
                    }else{
                        addTaskToDatabase(
                                itemId,
                                taskStartTime.getText().toString(),
                                taskFinishTime.getText().toString());
                    }
                }else{
                    if(isNightSleep){
                        updateTaskToDatabase(
                                itemId,
                                "0:0",
                                nightSleepTime,
                                taskId);

                    }else{
                    updateTaskToDatabase(
                            itemId,

                            taskStartTime.getText().toString(),
                            taskFinishTime.getText().toString(),
                            taskId
                    );
                    }
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                goHome();
            }
        }
        SaveTask saveTask = new SaveTask();
        saveTask.execute();
    }

    public boolean validateFields() {
        if(addTaskTitle.getSelectedItem().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please choose a task title", Toast.LENGTH_SHORT).show();
            return false;
        }else if(taskStartTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter start time", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(taskFinishTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter finish time", Toast.LENGTH_SHORT).show();
            return false;
        }else if(hourStart == hourFinish && minuteStart > minuteFinish){
            Toast.makeText(this, "Finish time can't be less than start time", Toast.LENGTH_SHORT).show();
            return false;
        }else if(hourStart > hourFinish){
            Toast.makeText(this, "Finish time can't be less than start time", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
    public boolean validateNightSleepFields() {
        if(nightSleepTime == null){
            Toast.makeText(this, "Please enter how mich time you slept", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    public void addTaskToDatabase(Integer title, String startTime, String finishTime){
        try{
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_TITLE_ID, title);
            cv.put(DatabaseHelper.COLUMN_TIMESTART, startTime);
            cv.put(DatabaseHelper.COLUMN_TIMEFINISH, finishTime);
            Uri res = getContentResolver().insert(TASK_URI, cv);
            Log.d(log_tag, "inserted");
        }catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    public void updateTaskToDatabase(Integer title, String startTime, String finishTime, Integer taskId){
        try{
            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.COLUMN_TITLE_ID, title);
            cv.put(DatabaseHelper.COLUMN_TIMESTART, startTime);
            cv.put(DatabaseHelper.COLUMN_TIMEFINISH, finishTime);

            Uri uri = ContentUris.withAppendedId(TASK_URI, taskId);
            int rowCount = getContentResolver().update(uri, cv, null, null);
            Log.d(log_tag, "updated");
        }catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }
    public void goHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showNightSleepFields(){
        timeStartTitle.setVisibility(View.INVISIBLE);
        timeFinishTitle.setVisibility(View.INVISIBLE);
        taskFinishTime.setVisibility(View.INVISIBLE);
        taskStartTime.setVisibility(View.INVISIBLE);

        timeNight.setVisibility(View.VISIBLE);
    }

    public void showTaskFields(){
        timeStartTitle.setVisibility(View.VISIBLE);
        timeFinishTitle.setVisibility(View.VISIBLE);
        taskFinishTime.setVisibility(View.VISIBLE);
        taskStartTime.setVisibility(View.VISIBLE);

        timeNight.setVisibility(View.INVISIBLE);
    }

    public void addCategoryActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), NewCategoryActivity.class);
        intent.putExtra("actionKey", 0); //0 - from new task activity
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
