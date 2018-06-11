package com.hvantage2.money4driveeee.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hvantage2.money4driveeee.model.DashboardModel;
import com.hvantage2.money4driveeee.model.MediaModel;
import com.hvantage2.money4driveeee.model.ProjectModel;

import java.util.ArrayList;

/**
 * Created by Hvantage2 on 12/1/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    //database name
    private static final String DATABASE_NAME = "m4dmain.db";

    //column keys
    private static final String KEY_ID = "id";
    private static final String KEY_TOTAL_PROJECT = "total_project";
    private static final String KEY_COMPLETED_PROJECT = "completed_project";
    private static final String KEY_PENDING_PROJECT = "pending_project";
    private static final String KEY_NEWASSIGNED_PROJECT = "newassigned_project";
    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_PROJECT_TITLE = "project_title";
    private static final String KEY_CITY = "city";
    private static final String KEY_CREATED_DATE = "created_date";
    private static final String KEY_PROJECT_DESC = "project_desc";
    private static final String KEY_PROJECT_TYPE = "project_type";
    private static final String KEY_ALLO_MEDIA_ID = "allo_media_id";
    private static final String KEY_MEDIA_TYPE_NAME = "media_type_name";
    private static final String KEY_MEDIA_TYPE_ID = "media_type_id";
    private static final String KEY_MEDIA_OPTION_ID = "media_option_id";
    private static final String KEY_MEDIA_OPTION_NAME = "media_option_name";

    //tables
    private static final String TABLE_DASHBOARD = "dashboard";
    private static final String TABLE_PROJECTS = "projects";
    private static final String TABLE_PROJECTS_MEDIA_TYPES = "project_media_types";
    private static final String TABLE_PROJECTS_MEDIA_OPTIONS = "projects_media_options";

    private static final String TAG = "DBHelper";
    private static final int DATABASE_VERSION = 1;


    //create table structers
    String CREATE_TABLE_DASHBOARD = "CREATE TABLE " + TABLE_DASHBOARD +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TOTAL_PROJECT + " INTEGER, "
            + KEY_COMPLETED_PROJECT + " INTEGER, "
            + KEY_PENDING_PROJECT + " INTEGER, "
            + KEY_NEWASSIGNED_PROJECT + " INTEGER "
            + ")";

    String CREATE_TABLE_PROJECTS = "CREATE TABLE " + TABLE_PROJECTS +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PROJECT_ID + " TEXT, "
            + KEY_PROJECT_TITLE + " TEXT, "
            + KEY_CITY + " TEXT, "
            + KEY_CREATED_DATE + " TEXT, "
            + KEY_PROJECT_DESC + " TEXT, "
            + KEY_PROJECT_TYPE + " TEXT "
            + ")";

    String CREATE_TABLE_PROJECT_MEDIA_TYPES = "CREATE TABLE " + TABLE_PROJECTS_MEDIA_TYPES +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PROJECT_ID + " TEXT, "
            + KEY_ALLO_MEDIA_ID + " TEXT, "
            + KEY_MEDIA_TYPE_NAME + " TEXT, "
            + KEY_MEDIA_TYPE_ID + " TEXT "
            + ")";

    String CREATE_TABLE_PROJECT_MEDIA_OPTIONS = "CREATE TABLE " + TABLE_PROJECTS_MEDIA_OPTIONS +
            "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PROJECT_ID + " TEXT, "
            + KEY_ALLO_MEDIA_ID + " TEXT, "
            + KEY_MEDIA_TYPE_ID + " TEXT, "
            + KEY_MEDIA_OPTION_ID + " TEXT, "
            + KEY_MEDIA_OPTION_NAME + " TEXT "
            + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_DASHBOARD);
        sqLiteDatabase.execSQL(CREATE_TABLE_PROJECTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PROJECT_MEDIA_TYPES);
        sqLiteDatabase.execSQL(CREATE_TABLE_PROJECT_MEDIA_OPTIONS);
    }


    public void saveDashboard(DashboardModel modal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TOTAL_PROJECT, modal.getTotalProject());
        values.put(KEY_COMPLETED_PROJECT, modal.getCompleteProject());
        values.put(KEY_PENDING_PROJECT, modal.getPendingProject());
        values.put(KEY_NEWASSIGNED_PROJECT, modal.getNewaggignProject());
        Log.e(TAG, "saveDashboard: values >> " + values.toString());
        boolean bb = db.insert(TABLE_DASHBOARD, null, values) > 0;
        if (bb) {
            Log.e("saveDashboard : ", "Inserted");
        } else {
            Log.e("saveDashboard : ", "Not inserted");
        }
        db.close();
    }

    public DashboardModel getDashboard() {
        SQLiteDatabase db = this.getReadableDatabase();
        DashboardModel model = null;
        String queary = "SELECT * FROM " + TABLE_DASHBOARD;
        Cursor cursor = db.rawQuery(queary, null);
        if (cursor == null) {
            return model;
        }
        if (cursor.moveToFirst()) {
            do {
                model = new DashboardModel();
                model.setTotalProject(cursor.getInt(cursor.getColumnIndex(KEY_TOTAL_PROJECT)));
                model.setCompleteProject(cursor.getInt(cursor.getColumnIndex(KEY_COMPLETED_PROJECT)));
                model.setNewaggignProject(cursor.getInt(cursor.getColumnIndex(KEY_NEWASSIGNED_PROJECT)));
                model.setPendingProject(cursor.getInt(cursor.getColumnIndex(KEY_PENDING_PROJECT)));
            } while (cursor.moveToNext());
        }
        return model;
    }

    public int deleteDashboard() {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_DASHBOARD, "1", null);
        Log.e(TAG, "deleteDashboard: rowCount" + rowCount);
        return rowCount;
    }

    public void saveProject(ProjectModel modal, String type_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_ID, modal.getProjectId());
        values.put(KEY_PROJECT_TITLE, modal.getProjectTitle());
        values.put(KEY_CITY, modal.getCity());
        values.put(KEY_CREATED_DATE, modal.getCreatedDate());
        values.put(KEY_PROJECT_DESC, modal.getProjectDesc());
        values.put(KEY_PROJECT_TYPE, type_id);
        Log.e(TAG, "saveProject: values >> " + values.toString());
        boolean bb = db.insert(TABLE_PROJECTS, null, values) > 0;
        if (bb) {
            Log.e("saveProject : ", "Inserted");
        } else {
            Log.e("saveProject : ", "Not inserted");
        }
        db.close();
    }

    public ArrayList<ProjectModel> getProjects(String type_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ProjectModel> list = null;
        String query = "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + KEY_PROJECT_TYPE + "=" + type_id;//+ " ORDER BY " + KEY_PROJECT_ID + " ASC";
//        String query = "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + KEY_PROJECT_TYPE + "=" + type_id + " ORDER BY ID DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<ProjectModel>();
            do {
                ProjectModel d = new ProjectModel();
                d.setProjectId(cursor.getString(cursor.getColumnIndex(KEY_PROJECT_ID)));
                d.setProjectTitle(cursor.getString(cursor.getColumnIndex(KEY_PROJECT_TITLE)));
                d.setCity(cursor.getString(cursor.getColumnIndex(KEY_CITY)));
                d.setCreatedDate(cursor.getString(cursor.getColumnIndex(KEY_CREATED_DATE)));
                d.setProjectDesc(cursor.getString(cursor.getColumnIndex(KEY_PROJECT_DESC)));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public int deleteProjects(String type_id) {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_PROJECTS, KEY_PROJECT_TYPE + "=?", new String[]{type_id});
        Log.e(TAG, "deleteDashboard: deleteProjects" + rowCount);
        return rowCount;
    }

    public void saveMediaType(MediaModel modal, String project_id, String media_allo_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_ID, project_id);
        values.put(KEY_ALLO_MEDIA_ID, media_allo_id);
        values.put(KEY_MEDIA_TYPE_ID, modal.getMedia_id());
        values.put(KEY_MEDIA_TYPE_NAME, modal.getMedia_name());
        Log.e(TAG, "saveMediaType: values >> " + values.toString());
        boolean bb = db.insert(TABLE_PROJECTS_MEDIA_TYPES, null, values) > 0;
        if (bb) {
            Log.e("saveMediaType : ", "Inserted");
        } else {
            Log.e("saveMediaType : ", "Not inserted");
        }
        db.close();
    }

    public ArrayList<MediaModel> getMediaTypes(String project_id, String media_allo_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MediaModel> list = null;
        String query = "SELECT * FROM " + TABLE_PROJECTS_MEDIA_TYPES + " WHERE " + KEY_PROJECT_ID + "=" + project_id + " AND " + KEY_ALLO_MEDIA_ID + "=" + media_allo_id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<MediaModel>();
            do {
                MediaModel d = new MediaModel(cursor.getString(cursor.getColumnIndex(KEY_MEDIA_TYPE_ID)), cursor.getString(cursor.getColumnIndex(KEY_MEDIA_TYPE_NAME)));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public boolean isProjectExist(String project_id) {
        boolean isExist = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + KEY_PROJECT_ID + "=" + project_id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            isExist = false;
        } else if (cursor.moveToFirst() && cursor.getCount() > 0) {
            isExist = true;
        }
        return isExist;
    }

    public int deleteMediaTypes(String project_id, String media_allo_id) {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_PROJECTS_MEDIA_TYPES, KEY_PROJECT_ID + "=? AND " + KEY_ALLO_MEDIA_ID + "=?", new String[]{project_id, media_allo_id});
        Log.e(TAG, "deleteMediaTypes: rowCount" + rowCount);
        return rowCount;
    }

    public void saveMediaOption(MediaModel modal, String project_id, String media_type_id, String media_allo_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_ID, project_id);
        values.put(KEY_ALLO_MEDIA_ID, media_allo_id);
        values.put(KEY_MEDIA_TYPE_ID, media_type_id);
        values.put(KEY_MEDIA_OPTION_ID, modal.getMedia_id());
        values.put(KEY_MEDIA_OPTION_NAME, modal.getMedia_name());
        Log.e(TAG, "saveMediaOption: values >> " + values.toString());
        boolean bb = db.insert(TABLE_PROJECTS_MEDIA_OPTIONS, null, values) > 0;
        if (bb) {
            Log.e("saveMediaOption : ", "Inserted");
        } else {
            Log.e("saveMediaOption : ", "Not inserted");
        }
        db.close();
    }

    public ArrayList<MediaModel> getMediaOptions(String project_id, String media_type_id, String media_allo_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<MediaModel> list = null;
        String query = "SELECT * FROM " + TABLE_PROJECTS_MEDIA_OPTIONS + " WHERE " + KEY_PROJECT_ID + "=" + project_id + " AND " + KEY_ALLO_MEDIA_ID + "=" + media_allo_id + " AND " + KEY_MEDIA_TYPE_ID + "=" + media_type_id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            list = new ArrayList<MediaModel>();
            do {
                MediaModel d = new MediaModel(cursor.getString(cursor.getColumnIndex(KEY_MEDIA_OPTION_ID)), cursor.getString(cursor.getColumnIndex(KEY_MEDIA_OPTION_NAME)));
                list.add(d);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public int deleteMediaOptions(String project_id, String media_type_id, String media_allo_id) {
        int rowCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowCount = db.delete(TABLE_PROJECTS_MEDIA_OPTIONS, KEY_PROJECT_ID + "=? AND " + KEY_ALLO_MEDIA_ID + "=? AND " + KEY_MEDIA_TYPE_ID + "=?", new String[]{project_id, media_allo_id, media_type_id});
        Log.e(TAG, "deleteMediaTypes: rowCount" + rowCount);
        return rowCount;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SQLiteDatabase db = getWritableDatabase();
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DASHBOARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS_MEDIA_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS_MEDIA_OPTIONS);
        // Create tables again
        onCreate(db);
    }

    public void updateProjectStatus(String project_id, String status_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_TYPE, status_id);
        Log.e(TAG, "updateProjectStatus: values >> " + values.toString());
        boolean bb = db.update(TABLE_PROJECTS, values, KEY_PROJECT_ID + "=?", new String[]{project_id}) > 0;
        if (bb) {
            Log.e("updateProjectStatus : ", "updated");
        } else {
            Log.e("updateProjectStatus : ", "Not updated");
        }
        db.close();
    }
}
