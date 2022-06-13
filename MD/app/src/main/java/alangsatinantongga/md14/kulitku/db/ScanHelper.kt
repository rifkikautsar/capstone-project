package alangsatinantongga.md14.kulitku.db

import alangsatinantongga.md14.kulitku.db.DatabaseContract.ScanColumns.Companion.TABLE_NAME
import alangsatinantongga.md14.kulitku.db.DatabaseContract.ScanColumns.Companion._ID
import alangsatinantongga.md14.kulitku.entity.Scan
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ScanHelper(context: Context) {

    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: ScanHelper? = null

        fun getInstance(context: Context): ScanHelper {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) INSTANCE = ScanHelper(context)
                }
            }
            return INSTANCE as ScanHelper
        }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }
    fun close() {
        dataBaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC")
    }

    fun queryById(id: String): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$_ID = ?",
            arrayOf(id),
            null,
            null,
            null,
            null)
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$_ID = ?", arrayOf(id))
    }

    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }
}