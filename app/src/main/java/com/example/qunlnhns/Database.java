package com.example.qunlnhns;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.PreparedStatement;


public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //tạo bảng ở các layout ban đầu và main
    public void CREATE_TABLE_MAIN() {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS main(stt INTEGER PRIMARY KEY, maNv VARCHAR(50))";
        database.execSQL(sql);
    }

    //thêm giá trị manv truyền vào ở các layout ban đầu và main
    public void INSERT_MANV_MAIN(String stt, String maNv) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO main (stt, maNv) VALUES (?, ?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sql);
        sqLiteStatement.clearBindings();

        // Kiểm tra giá trị của stt và thay thế bằng giá trị mặc định nếu cần
        if (stt == null) {
            stt = "1"; // Thay thế "default_value" bằng giá trị mặc định
        }

        sqLiteStatement.bindString(1, stt);
        sqLiteStatement.bindString(2, maNv);
        sqLiteStatement.executeInsert();
    }


    //lấy giá trị manv cho các layout ban đầu và main
    public String SELECT_MANV_MAIN() {
        SQLiteDatabase database = getReadableDatabase();
        String maNv = null;

        String sql = "SELECT maNv FROM main WHERE stt = 1";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("maNv");
            if (columnIndex >= 0) {
                maNv = cursor.getString(columnIndex);
            } else {
                Log.e("Error", "Column 'maNv' not found in the query result.");
            }
        }

        cursor.close();
        return maNv;
    }

    //tạo bảng ở các layout cuối
    public void CREATE_TABLE_DETAIL() {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS detail(stt INTEGER PRIMARY KEY, maNv VARCHAR(50))";
        database.execSQL(sql);
    }

    //thêm giá trị manv truyền vào ở các layout trong cùng
    public void INSERT_MANV_DELTAIL(String stt, String maNv){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO detail (stt, maNv) VALUES (?, ?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sql);
        sqLiteStatement.clearBindings();

        // Kiểm tra giá trị của stt và thay thế bằng giá trị mặc định nếu cần
        if (stt == null) {
            stt = "1"; // Thay thế "default_value" bằng giá trị mặc định
        }

        sqLiteStatement.bindString(1, stt);
        sqLiteStatement.bindString(2, maNv);
        sqLiteStatement.executeInsert();
    }

    //lấy giá trị manv cho các layout ban đầu và main
    public String SELECT_MANV_DELTAIL() {
        SQLiteDatabase database = getReadableDatabase();
        String maNv = null;

        String sql = "SELECT maNv FROM detail WHERE stt = 1";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("maNv");
            if (columnIndex >= 0) {
                maNv = cursor.getString(columnIndex);
            } else {
                Log.e("Error", "Column 'maNv' not found in the query result.");
            }
        }

        cursor.close();
        return maNv;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
