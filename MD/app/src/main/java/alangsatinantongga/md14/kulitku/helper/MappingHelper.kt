package alangsatinantongga.md14.kulitku.helper

import alangsatinantongga.md14.kulitku.db.DatabaseContract
import alangsatinantongga.md14.kulitku.entity.Scan
import android.database.Cursor

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Scan> {
        val scansList = ArrayList<Scan>()
        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.ScanColumns._ID))
                val image = getString(getColumnIndexOrThrow(DatabaseContract.ScanColumns.IMAGE))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.ScanColumns.DATE))
                val prediction = getString(getColumnIndexOrThrow(DatabaseContract.ScanColumns.PREDICTION))
                scansList.add(Scan(id, image, date, prediction))
            }
        }
        return scansList
    }
}