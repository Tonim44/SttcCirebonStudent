package id.ac.sttccirebon.student.ui.pembayaran

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pembayaran(val id_kewajiban_pembayaran: Int,
                      val jenis_pembayaran: String,
                      val semester: String,
                      val nominal: String,
                      val status: String,
                      val Keterangan: String) : Parcelable