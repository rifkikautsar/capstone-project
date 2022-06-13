package alangsatinantongga.md14.kulitku.db

import alangsatinantongga.md14.kulitku.db.DatabaseContract.ScanColumns.Companion.TABLE_NAME
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbkulitku"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_SCAN = "CREATE TABLE $TABLE_NAME" +
                " (${DatabaseContract.ScanColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${DatabaseContract.ScanColumns.IMAGE} TEXT NOT NULL," +
                " ${DatabaseContract.ScanColumns.DATE} TEXT NOT NULL," +
                " ${DatabaseContract.ScanColumns.PREDICTION} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_SCAN)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}