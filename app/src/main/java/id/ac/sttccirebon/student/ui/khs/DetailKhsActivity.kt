package id.ac.sttccirebon.student.ui.khs

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.databinding.ActivityDetailkhsBinding
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog

class DetailKhsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailkhsBinding
    private lateinit var tableKhs: TableKHS
    private lateinit var data: DataManager

    companion object {
        const val EXTRA_KHS = "extra_khs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailkhsBinding.inflate(layoutInflater)
        data = DataManager(baseContext)
        val view = binding.root
        setContentView(view)

        val loading = LoadingDialog(this)
        loading.startLoading()

        val back = binding.back
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        val Semester = binding.detailSemester
        val TahunAjaran = binding.detailTahunajaran
        val Matakuliah = binding.detailMatakuliah
        val Namadosen = binding.detailDosen
        val Sks = binding.detailSks
        val Pertemuan = binding.detailPertemuan
        val UTS = binding.detailNilaiuts
        val UAS = binding.detailNilaiuas
        val Tugas = binding.detailNilaitugas
        val Absesnsi = binding.detailAbsensi
        val NA = binding.detailNilaiakhir
        val Mutlak = binding.detailNilaimutlak

        tableKhs = intent.getParcelableExtra<TableKHS>(DetailKhsActivity.EXTRA_KHS) as TableKHS

        val semester = tableKhs.semester
        val tahunajaran = tableKhs.tahun_ajaran
        val matakuliah = tableKhs.matakuliah
        val namadosen = tableKhs.dosen
        val sks = tableKhs.jumlah_sks
        val pertemuan = tableKhs.jumlah_pertemuan
        val nilai_uts = tableKhs.nilai_uts
        val nilai_uas = tableKhs.nilai_uas
        val nilai_tugas = tableKhs.nilai_tugas
        val jumlah_absesensi = tableKhs.nilai_kehadiran
        val nilai_akhir = tableKhs.nilai_akhir
        val nilai_mutlak = tableKhs.nilai_huruf
        val status = tableKhs.status

        Semester.text = semester.toString()
        TahunAjaran.text = tahunajaran
        Matakuliah.text = matakuliah
        Namadosen.text = namadosen
        Sks.text = sks.toString()
        Pertemuan.text = pertemuan.toString()
        UTS.text = nilai_uts.toString()
        UAS.text = nilai_uas.toString()
        Tugas.text = nilai_tugas.toString()
        Absesnsi.text = jumlah_absesensi.toString()
        NA.text = nilai_akhir.toString()

        if(nilai_mutlak.equals("null")){
            Mutlak.text = "-"
        }else{
            Mutlak.text = nilai_mutlak
        }

        if(status.equals("Tidak Lulus")){
            val Tidak_lulus = binding.TIDAKLULUS
            Tidak_lulus.text = "Tidak Lulus"
        }
        if(status.equals("Lulus")){
            val Lulus = binding.LULUS
            Lulus.text = "Lulus"
        }
        if(status.equals("Belum di Nilai")) {
            binding.PROSES.text = "Belum Ada Nilai"
        }

        loading.isDismiss()

    }
}