//package oop.dayplanner3.model;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import oop.dayplanner3.R;
//import oop.dayplanner3.activity.NewTaskActivity;
//import oop.dayplanner3.database.DatabaseHelper;
//
//public class Data {
//
//
//    public static List<Category> getCategoryList(Context activity) {
//        List<Category> categoryList = new ArrayList<>();
//        Category category = new Category();
//        DatabaseHelper databaseHelper;
//        SQLiteDatabase db;
//
//        databaseHelper = new DatabaseHelper(activity);
//        db = databaseHelper.getWritableDatabase();
//        Cursor cursor = databaseHelper.readAllCategoryData();
//
//        if(cursor.getCount()==0)
//        {
//            Toast.makeText(this, "no data", Toast.LENGTH_LONG).show();
//        }else{
//            headerGroup.setText("РќР°Р№РґРµРЅРѕ СЌР»РµРјРµРЅС‚РѕРІ: " +  cursor.getCount());
//            CursorAdapter cursorAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
//                    cursor,
//                    new String[]{DatabaseHelper.COLUMN_IDGROUP, DatabaseHelper.COLUMN_GROUPNAME},
//                    new int[]{android.R.id.text1,android.R.id.text2},
//                    0);
//            groupListView.setAdapter(cursorAdapter);
//
//            groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//                @Override
//                public void onItemClick(AdapterView<?> parent, View v, int position, long id)
//                {
//                    updateGroupActivity(id);
//                }
//            });
//        }
//
//        category.setName(i);
//        categoryList.add(category);
//
//
//
////        Category work = new Category();
////        work.setName("Work");
////        work.setImage(R.drawable.work);
////        categoryList.add(work);
////
////        Category sleep = new Category();
////        sleep.setName("Sleep");
////        sleep.setImage(R.drawable.sleep);
////        categoryList.add(sleep);
////
////        Category nightSleep = new Category();
////        nightSleep.setName("Night Sleep");
////        nightSleep.setImage(R.drawable.nightsleep);
////        categoryList.add(nightSleep);
////
////        Category study = new Category();
////        study.setName("Study");
////        study.setImage(R.drawable.study);
////        categoryList.add(study);
////
////        Category eat = new Category();
////        eat.setName("Eat Time");
////        eat.setImage(R.drawable.eattime);
////        categoryList.add(eat);
////
////        Category breakTime = new Category();
////        breakTime.setName("Break Time");
////        breakTime.setImage(R.drawable.breaktime);
////        categoryList.add(breakTime);
////
////        Category shopping = new Category();
////        shopping.setName("Shopping");
////        shopping.setImage(R.drawable.shop);
////        categoryList.add(shopping);
////
////        Category family = new Category();
////        family.setName("Family");
////        family.setImage(R.drawable.family);
////        categoryList.add(family);
////
////        Category friends = new Category();
////        friends.setName("Friends");
////        friends.setImage(R.drawable.friends);
////        categoryList.add(friends);
//
//        return categoryList;
//    }
//}
