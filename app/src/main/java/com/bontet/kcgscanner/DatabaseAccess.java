package com.bontet.kcgscanner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open(){
        this.db = openHelper.getWritableDatabase();
    }

    public void close() {
        if(db!=null){
            this.db.close();
        }
    }

    public Cursor getArtikel(String barcode) {
        Cursor c;
        c =db.rawQuery("SELECT * from m_barcode where barcode='"+barcode+"'", null);
        return c;
    }

    public boolean insert(){
        String sql = "INSERT INTO t_SO (idBranch,socode,sodate,barcode,artikel,color,size,qty,noscan,idScanner,isDelete)\n" +
                "VALUES('001','SO', date('now'),'123456789','SATEBABI2KG','BLK',45,1,'ur-001', 'ur-001', 'N' )";
        db.execSQL(sql);
        return true;
    }
}

