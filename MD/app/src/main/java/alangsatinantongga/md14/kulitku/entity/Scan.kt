package alangsatinantongga.md14.kulitku.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Scan(
    var id: Int = 0,
    var image : String? = null,
    var date : String? = null,
    var predict : String? = null
) : Parcelable
