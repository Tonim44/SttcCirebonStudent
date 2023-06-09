package id.ac.sttccirebon.student.ui.isikehadiran

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.databinding.ActivityDetailketidakhadiranBinding
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog

class DetailAlpaActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailketidakhadiranBinding
    private lateinit var absen: Absen
    private lateinit var data : DataManager

    companion object {
        const val EXTRA_ABSEN = "extra_absen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailketidakhadiranBinding.inflate(layoutInflater)

        data = DataManager(baseContext)
        val view = binding.root
        setContentView(view)

        val loading = LoadingDialog(this)
        loading.startLoading()

        val Matakuliah = binding.detailMatkul
        val Namadosen = binding.detailDosen
        val Tanggal = binding.detailTanggal
        val JamKuliah = binding.detailJam
        val Ruangan = binding.detailRuangan
        val JenisPertemuan = binding.detailJenispertemuan
        val Status = binding.ALPA

        val back = binding.back
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        absen = intent.getParcelableExtra<Absen>(IsiKehadiranActvity.EXTRA_ABSEN) as Absen

        val matakuliah = absen.matakuliah
        val namadosen = absen.namadosen
        val tanggal = absen.tanggal
        val jammasuk = absen.jammulai
        val jamselesai = absen.jamselesai
        val jenispertemuan = absen.jenis_pertemuan
        val ruangan = absen.ruangan

        Matakuliah.text = matakuliah
        Namadosen.text = namadosen
        Tanggal.text = tanggal
        JamKuliah.text = "${jammasuk}-${jamselesai}"
        Ruangan.text = ruangan
        JenisPertemuan.text = jenispertemuan
        Status.text = "Anda dinyatakan tidak hadir pada perkuliahan hari ini"

        loading.isDismiss()
    }

}