package id.ac.sttccirebon.student.ui.pengumuman

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import id.ac.sttccirebon.student.databinding.ActivityPengumumanBinding
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog

class PengumumanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengumumanBinding
    private lateinit var pengumuman: Pengumuman
    private lateinit var data : DataManager
    private lateinit var dokumen: CardView

    companion object {
        const val EXTRA_PENGUMUMAN = "extra_pengumuman"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPengumumanBinding.inflate(layoutInflater)
        data = DataManager(baseContext)
        val view = binding.root
        setContentView(view)

        val loading = LoadingDialog(this)
        loading.startLoading()

        dokumen = binding.lihatDokumen

        val Kegiatan = binding.pengumuman
        val TanggalPengumuman = binding.tanggalPengumuman
        val Isi = binding.isi

        pengumuman = intent.getParcelableExtra<Pengumuman>(PengumumanActivity.EXTRA_PENGUMUMAN) as Pengumuman

        val kegiatan = pengumuman.judul
        val tanggal_pengumuman = pengumuman.tanggal
        val isi = pengumuman.konten
        val file = pengumuman.file

        if (file.equals("null")) {
            dokumen.visibility = View.GONE
        }

        Kegiatan.text = kegiatan
        TanggalPengumuman.text = tanggal_pengumuman
        Isi.text = isi

       dokumen.setOnClickListener{
          val openURL = Intent(android.content.Intent.ACTION_VIEW)
                openURL.data = Uri.parse(pengumuman.file)
                startActivity(openURL)
        }

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

        loading.isDismiss()

    }
}