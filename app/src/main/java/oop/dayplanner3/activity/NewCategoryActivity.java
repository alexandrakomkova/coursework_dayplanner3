package oop.dayplanner3.activity;

import static oop.dayplanner3.CategoryProviderDatabase.CATEGORY_URI;
import static oop.dayplanner3.database.DatabaseHelper.COLUMN_CATEGORY_TITLE;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import oop.dayplanner3.R;
import oop.dayplanner3.database.DatabaseHelper;

public class NewCategoryActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    EditText categoryTitle;
    Button addCategory, updateCategory, deleteCategory;
    Integer actionKey;
    ListView categoriesListView;
    String log_tag = "NewCategoryActivity";
    ArrayAdapter<String> listAdapter;
    List<String> list;
    Integer itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        addCategory = findViewById(R.id.addCategory);
        updateCategory = findViewById(R.id.updateCategory);
        deleteCategory = findViewById(R.id.deleteCategory);

        categoryTitle = findViewById(R.id.categoryTitle);
        categoriesListView = findViewById(R.id.categoriesList);
        list = new ArrayList<String>();

        databaseHelper = new DatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();

        actionKey = getIntent().getExtras().getInt("actionKey");
        storeCategoryDataInArray();
    }

    void storeCategoryDataInArray()
    {
        list.clear();
        String query = "select "+ COLUMN_CATEGORY_TITLE+" from " + DatabaseHelper.TABLE_CATEGORY;
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
                Log.d(log_tag, "list: "+ s);

                list.add(s.toString());
                cursor.moveToNext();
            }

            cursor.close();

            listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoriesListView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();

        }

        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                itemId = (int)id;
                setUpdateCategoryTitleInField(itemId);
            }
        });

    }

    public void setUpdateCategoryTitleInField(int id) {
        addCategory.setVisibility(View.INVISIBLE);
        updateCategory.setVisibility(View.VISIBLE);
        deleteCategory.setVisibility(View.VISIBLE);
        categoryTitle.setText(list.get(id));
    }

    public void updateCategory(View v) {
        ContentValues cv = new ContentValues();

        itemId += 1;
        cv.put(COLUMN_CATEGORY_TITLE, categoryTitle.getText().toString().trim());
        Uri uri = ContentUris.withAppendedId(CATEGORY_URI, itemId);
        int rowCount = getContentResolver().update(uri, cv, null, null);
        if(rowCount > 0 ){
            Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
            storeCategoryDataInArray();
            categoryTitle.setText("");
        }
    }
    public void deleteCategory(View v) {
        ContentValues cv = new ContentValues();

        itemId += 1;
        cv.put(COLUMN_CATEGORY_TITLE, categoryTitle.getText().toString().trim());
        Uri uri = ContentUris.withAppendedId(CATEGORY_URI, itemId);
        int rowCount = getContentResolver().delete(uri, null, null);
        if(rowCount > 0 ){
            Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show();
            storeCategoryDataInArray();
            categoryTitle.setText("");
        }
    }

    public void addCategory(View v) {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CATEGORY_TITLE, categoryTitle.getText().toString().trim());
        Uri res = getContentResolver().insert(CATEGORY_URI, cv);

        goHome();
    }

    public void goHome(){
        Intent intent;
        switch (actionKey) {
            case 0:
                intent = new Intent(this, NewTaskActivity.class);
                startActivity(intent);

            case 1:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }
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
