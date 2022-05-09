//package oop.dayplanner3;
//
//import android.content.ContentProvider;
//import android.content.ContentUris;
//import android.content.ContentValues;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.net.Uri;
//import android.text.TextUtils;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import oop.dayplanner3.database.DatabaseHelper;
//
//public class CategotyProvider extends ContentProvider {
//    public static final String log_tag = "DayPlanner";
//    DatabaseHelper databaseHelper;
//    SQLiteDatabase db;
//
//    static final String AUTHORITY = "oop.dayplanner3.providers.CategoryList";
//    static final String PATH = "list";
//    public static final Uri CATEGORY_URI = Uri.parse("content://" + AUTHORITY + "/"+
//            PATH);
//
//    static final String CATEGORY_LIST_TYPE = "vnd.android.cursor.dir/vnd."+
//            AUTHORITY+"."+PATH;
//    static final String CATEGORY_TYPE = "vnd.android.cursor.item/vnd."+
//            AUTHORITY+"."+PATH;
//    static final int URI_CATEGORIES = 1;
//    static final int URI_CATEGORY_ID = 2;
//
//    private static UriMatcher uriMathcher;
//
//    static{
//        uriMathcher = new UriMatcher(UriMatcher.NO_MATCH);
//        uriMathcher.addURI(AUTHORITY, PATH, URI_CATEGORIES);
//        uriMathcher.addURI(AUTHORITY, PATH + "/#", URI_CATEGORY_ID);
//        uriMathcher.addURI(AUTHORITY, PATH, URI_CATEGORIES);
//        uriMathcher.addURI(AUTHORITY, PATH + "/#", URI_CATEGORY_ID);
//    }
//
//    @Override
//    public boolean onCreate() {
//        Log.d(log_tag, "onCreate");
//        databaseHelper = new DatabaseHelper(getContext());
//        return true;
//    }
//
//    @Nullable
//    @Override
//    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
//        switch (uriMathcher.match(uri)){
//            case URI_CATEGORIES:
//                Log.d(log_tag, "URI_CATEGORIES");
//                if(TextUtils.isEmpty(sortOrder)){
//                    sortOrder = databaseHelper.COLUMN_CATEGORYTITLE + " ASC";
//                }
//                break;
//            case URI_CATEGORY_ID:
//                String id = uri.getLastPathSegment();
//                Log.d(log_tag, "URI_CATEGORY_ID = "+ id);
//                if(TextUtils.isEmpty(selection)){
//                    selection = selection + " AND " +databaseHelper.COLUMN_IDCATEGORY + " = " + id;
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("wrong URI: " + uri);
//        }
//        db = databaseHelper.getWritableDatabase();
//        Cursor cursor = db.query(databaseHelper.TABLE_CATEGORY, projection, selection,
//                selectionArgs, null, null, sortOrder);
//
//        cursor.setNotificationUri(getContext().getContentResolver(),
//                CATEGORY_URI);
//        Log.d(log_tag, "query completed, "+ uri.toString());
//
//        return cursor;
//    }
//
//    @Nullable
//    @Override
//    public String getType(@NonNull Uri uri) {
//        switch(uriMathcher.match(uri)){
//            case URI_CATEGORY_ID:
//                return CATEGORY_TYPE;
//            case URI_CATEGORIES:
//                return CATEGORY_LIST_TYPE;
//        }
//        Log.d(log_tag, "getType, "+ uri.toString());
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
//        if(uriMathcher.match(uri) != URI_CATEGORIES){
//            throw new IllegalArgumentException("wrong uri: "+uri);
//        }
//        db = databaseHelper.getWritableDatabase();
//        long rowID = db.insert(databaseHelper.TABLE_CATEGORY, null, contentValues);
//        Uri result = ContentUris.withAppendedId(CATEGORY_URI, rowID);
//        getContext().getContentResolver().notifyChange(result, null);
//        Log.d(log_tag, "insert completed, "+ uri.toString());
//
//        return result;
//    }
//
//    @Override
//    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
//        switch (uriMathcher.match(uri)){
//            case URI_CATEGORIES:
//                Log.d(log_tag, "URI_CATEGORIES");
//
//                break;
//            case URI_CATEGORY_ID:
//                String id = uri.getLastPathSegment();
//                Log.d(log_tag, "URI_CATEGORY_ID = "+ id);
//                if(TextUtils.isEmpty(selection)){
//                    selection =  databaseHelper.COLUMN_IDCATEGORY + " = " + id;
//                }
//                else{
//                    selection = selection + " AND " +databaseHelper.COLUMN_IDCATEGORY + " = " + id;
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("wrong URI: " + uri);
//        }
//        db = databaseHelper.getWritableDatabase();
//
//        int rowCount = db.delete(databaseHelper.TABLE_TASK, selection, selectionArgs);
//
//        getContext().getContentResolver().notifyChange(uri, null);
//        Log.d(log_tag, "delete completed, "+ uri.toString());
//
//        return rowCount;
//    }
//
//    @Override
//    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
//        switch (uriMathcher.match(uri)){
//            case URI_CATEGORIES:
//                Log.d(log_tag, "URI_CATEGORIES");
//
//                break;
//            case URI_CATEGORY_ID:
//                String id = uri.getLastPathSegment();
//                Log.d(log_tag, "URI_CATEGORY_ID = "+ id);
//                if(TextUtils.isEmpty(selection)){
//                    selection =  databaseHelper.COLUMN_IDCATEGORY + " = " + id;
//                }
//                else{
//                    selection = selection + " AND " +databaseHelper.COLUMN_IDCATEGORY + " = " + id;
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("wrong URI: " + uri);
//        }
//        db = databaseHelper.getWritableDatabase();
//
//        int rowCount = db.update(databaseHelper.TABLE_CATEGORY, contentValues, selection, selectionArgs);
//
//        getContext().getContentResolver().notifyChange(uri, null);
//        Log.d(log_tag, "updated, "+ uri.toString());
//
//        return rowCount;
//    }
//
//}
