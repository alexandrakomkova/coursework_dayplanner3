package oop.dayplanner3.activity;

import static oop.dayplanner3.TaskProviderDatabase.TASK_URI;
import static oop.dayplanner3.TaskProviderDatabase.log_tag;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.List;

import oop.dayplanner3.R;
import oop.dayplanner3.database.DatabaseHelper;
import oop.dayplanner3.model.Category;

import oop.dayplanner3.model.Recommends;

public class ViewRecommendActivity extends AppCompatActivity {
    private PieChart pieChart;
    ArrayList<Integer> taskTitles;
    TextView textRecommends;
    String recommends = "";
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recommend);
        textRecommends = findViewById(R.id.textRecommend);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();

        pieChart = findViewById(R.id.piechart);
        setupPieChart();
        loadPieChartData();
    }
    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(14);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Analyse your day");
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(12);
        l.setEnabled(true);


    }
    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        addCategoriesTitlesToArray();

        for (int i=0; i < taskTitles.size();i++){
            Float percentValue = percentValueForTask(taskTitles.get(i));
            if(percentValue !=0.00){
                entries.add(new PieEntry(percentValue, findCategoryTitle(taskTitles.get(i).toString())));
            }
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "All categories");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

    }

    private Integer sumTimeForTask(Integer taskTitleId){
        Integer sum = 0;
        Integer totalHour=0;
        try{
            String[] strArgs = {taskTitleId.toString()};
            Cursor cursor = getContentResolver().query(
                    TASK_URI,
                    null,
                    DatabaseHelper.COLUMN_TITLE_ID+" =?",
                    strArgs,
                    null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {


                    String[] items_timeStart = cursor.getString(2).split(":");
                    Integer hourStart = Integer.parseInt(items_timeStart[0]);
                    Integer minuteStart = Integer.parseInt(items_timeStart[1]);

                    String[] items_timeFinish = cursor.getString(3).split(":");
                    Integer hourFinish = Integer.parseInt(items_timeFinish[0]);
                    Integer minuteFinish = Integer.parseInt(items_timeFinish[1]);

                    totalHour = hourFinish - hourStart;
                    Integer totalMinutes = minuteFinish - minuteStart;

                    if(totalMinutes < 0){
                        totalHour--;
                        totalMinutes+=60;
                    }
                    sum += totalHour * 60 + totalMinutes;
                    cursor.moveToNext();
                }

            }

            textRecommends.setText(analyseData(taskTitleId.toString(), totalHour));
        }catch (Exception e){
            Toast.makeText(ViewRecommendActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(log_tag, e.getMessage());
        }

        return sum;

    }
    private Float percentValueForTask(Integer taskTitleId){
        return Float.valueOf((sumTimeForTask(taskTitleId)*100)/(24*60));
    }

    private void addCategoriesTitlesToArray(){
//        List<Category> list = Data.getCategoryList();
//        taskTitles = new ArrayList<String>();
//        for(int i =0; i < list.size();i++){
//            taskTitles.add(list.get(i).getName());
//            Log.d(log_tag, i+"/"+list.get(i).getName());
//        }

        taskTitles = new ArrayList<Integer>();
        String query = "select "+databaseHelper.COLUMN_CATEGORY_ID+" from " + databaseHelper.TABLE_CATEGORY;
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
               Integer s = cursor.getInt(0);
                Log.d(log_tag, "list: "+ s);

                taskTitles.add(s);
                cursor.moveToNext();
            }

            cursor.close();

        }
    }

    public String analyseData(String titleId, Integer hour){
        switch (findCategoryTitle(titleId)) {
            case "Work":
                recommends += Recommends.analyseWork(hour);
                break;
           case "Sleep":
                recommends += Recommends.analyseNightSleep(hour);
                break;
           case "Study":
                recommends += Recommends.analyseStudy(hour);
                break;
            case "Eat Time":
                recommends += Recommends.analyseEatTime(hour);
                break;
            case "Nap":
                recommends += Recommends.analyseSleep(hour);
                break;
            default:
                break;
        }
        return recommends;
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
                Log.d(log_tag, "find: "+ s);
            }

            cursor.close();
        }
        return s;
    }

}
