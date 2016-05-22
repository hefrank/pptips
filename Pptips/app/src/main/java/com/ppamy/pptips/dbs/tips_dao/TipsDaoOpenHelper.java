package com.ppamy.pptips.dbs.tips_dao;

import com.ppamy.pptips.AppEnv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TipsDaoOpenHelper extends SQLiteOpenHelper {

    public TipsDaoOpenHelper(Context context, String name, CursorFactory factory) {
        super(context, name, factory, DaoMaster.SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (AppEnv.DEBUG) {
            Log.i("greenDAO", "Creating tables for schema version " + DaoMaster.SCHEMA_VERSION);
        }
        DaoMaster.createAllTables(db, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (AppEnv.DEBUG) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
        }
        DaoMaster.dropAllTables(db, true);
        onCreate(db);
    }

    /* 从3.0之后在继承SQLiteOpenHelper的类中，都需要重写该方法（该方法默认抛出异常），否则在数据库降级的时候会抛出异常。 */
    @SuppressLint("Override")
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (AppEnv.DEBUG) {
            Log.i("greenDAO", "Downgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
        }
        DaoMaster.dropAllTables(db, true);
        onCreate(db);
    }
}
