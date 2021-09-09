package com.example.monty.menuvisuals;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Monty on 16-03-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    String DB_PATH = null;
    //String taste = "";
    private static String DB_NAME = "appdb1";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    //private Bitmap bitmap;
    //MainActivity mainActivity = new MainActivity();

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 10);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public /*byte[]*/Cursor takeString(String string) {
        String query = "Select * \n" +
                "FROM Main\n" +
                "WHERE _id = '" + string + "';";
        byte[] image = null;
        Cursor cursor;
        cursor = myDataBase.rawQuery(query, null);
        if(cursor.getCount()!=0) {
            int col = cursor.getColumnCount();
            int count = cursor.getCount();
            if (cursor.moveToNext()) {
                //String tst = cursor.getString(cursor.getColumnIndex("Taste"));
                //image = cursor.getBlob(cursor.getColumnIndex("Image"));
                //Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            return cursor;
        }
        else{
            cursor.close();
            String query2 = "Select * \n" +
                    "FROM Main\n";
            cursor = myDataBase.rawQuery(query2, null);
            LongestCommonSubsequence lcs;
            String ocrString = string;
             if(cursor.moveToFirst()){
                 do{
                     String dish = cursor.getString(cursor.getColumnIndex("_id"));
                     lcs = new LongestCommonSubsequence(dish,ocrString);
                     boolean c = lcs.check();
                     if(c == true){
                         //image = cursor.getBlob(cursor.getColumnIndex("Image"));
                         //taste = cursor.getString(cursor.getColumnIndex("Taste"));
                         return cursor;
                     }
                 }while(cursor.moveToNext());
             }
            return null;
        }
    }
   // String getTaste(){
    //    return taste;
//    }
}