package id.ac.sttccirebon.student.ui.isikehadiran

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Absen(
    val idjadwal: Int,
    val matakuliah: String,
    val namadosen: String,
    val namaasisten: String,
    val tanggal: String,
    val jammulai: String,
    val jamselesai: String,
    val ruangan: String,
    val jenis_pertemuan: String,
    val status: String
) : Parcelable