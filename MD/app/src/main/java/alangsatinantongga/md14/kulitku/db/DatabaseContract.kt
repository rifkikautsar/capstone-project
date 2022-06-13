package alangsatinantongga.md14.kulitku.db

import alangsatinantongga.md14.kulitku.db.DatabaseContract.ScanColumns.Companion.TABLE_NAME
import android.net.Uri
import android.provider.BaseColumns

internal class DatabaseContract {

    internal class ScanColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "user"
            const val _ID = "_id"
            const val IMAGE = "image"
            const val DATE = "date"
            const val PREDICTION = "prediction"
        }
    }
}