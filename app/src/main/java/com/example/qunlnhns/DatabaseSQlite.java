package com.example.qunlnhns;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;


public class DatabaseSQlite extends SQLiteOpenHelper {
    public DatabaseSQlite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //tạo bảng ở các layout ban đầu và main
    public void CREATE_TABLE_MAIN() {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "CREATE TABLE IF NOT EXISTS main(stt INTEGER PRIMARY KEY, maNv VARCHAR(50), admin VARCHAR(50), tk VARCHAR(50))";
        database.execSQL(sql);
    }

    //thêm giá trị manv truyền vào ở các layout ban đầu và main
    public void INSERT_MANV_MAIN(String stt, String maNv, String admin, String tk) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO main(stt, maNv, admin, tk) VALUES (?, ?, ?, ?)";
        SQLiteStatement sqLiteStatement = database.compileStatement(sql);
        sqLiteStatement.clearBindings();

        // Kiểm tra giá trị của stt và thay thế bằng giá trị mặc định nếu cần
        if (stt == null) {
            stt = "1"; // Thay thế "default_value" bằng giá trị mặc định
        }
        sqLiteStatement.bindString(1, stt);
        sqLiteStatement.bindString(2, maNv);
        sqLiteStatement.bindString(3, admin);
        sqLiteStatement.bindString(4, tk);
        sqLiteStatement.executeInsert();
    }

    //lấy giá trị tk cho các layout ban đầu và main
    public String SELECT_TK_MAIN() {
        SQLiteDatabase database = getReadableDatabase();
        String tk = null;

        String sql = "SELECT tk FROM main WHERE stt = 1";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("tk");
            if (columnIndex >= 0) {
                tk = cursor.getString(columnIndex);
            } else {
                Log.e("Error", "Column 'tk' not found in the query result.");
            }
        }

        cursor.close();
        return tk;
    }
    //lấy giá trị manv cho các layout ban đầu và main
    public Pair<String, String> SELECT_MANV_MAIN() {
        SQLiteDatabase database = getReadableDatabase();
        String maNv = null;
        String admin = null;

        String sql = "SELECT maNv, admin FROM main WHERE stt = 1";
        Cursor cursor = database.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            int maNvIndex = cursor.getColumnIndex("maNv");
            int adminIndex = cursor.getColumnIndex("admin");
            if (maNvIndex >= 0) {
                maNv = cursor.getString(maNvIndex);
            } else {
                Log.e("Error", "Column 'maNv' not found in the query result.");
            }

            if (adminIndex >= 0) {
                admin = cursor.getString(adminIndex);
            } else {
                Log.e("Error", "Column 'admin' not found in the query result.");
            }
        }

        cursor.close();
        return Pair.create(maNv, admin);
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
