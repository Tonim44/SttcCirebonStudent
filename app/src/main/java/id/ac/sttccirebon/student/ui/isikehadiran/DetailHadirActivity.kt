package id.ac.sttccirebon.student.ui.isikehadiran

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.databinding.ActivityDetailkehadiranBinding
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class DetailHadirActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailkehadiranBinding
    private lateinit var absen: Absen
    private lateinit var data : DataManager

    companion object {
        const val EXTRA_ABSEN = "extra_absen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailkehadiranBinding.inflate(layoutInflater)
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
        val Status = binding.HADIR

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
        JenisPertemuan.text = jenispertemuan
        Ruangan.text = ruangan
        Status.text = "Anda dinyatakan hadir pada perkuliahan hari ini"

        val shareLokasi = binding.sharelok
        shareLokasi.setOnClickListener {
            val intent = Intent(this, MapsHadirActivity::class.java)
            startActivity(intent)
        }

        val token = data.getString("token")
        val id_jadwal = absen.idjadwal
        val PhotoAbsen = binding.fotoAbsen
        Log.i("DATA_API", id_jadwal.toString())

        val queue = Volley.newRequestQueue(this@DetailHadirActivity)
        val url = "${HPI.API_URL}/api/mahasiswa/get-detail-jadwal-kuliah"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val Result = responseJson.getJSONObject("result")
                    val id_detail_jadwal = Result.getJSONObject("detail_jadwal_kuliah")
                    val kehadiran = id_detail_jadwal.getJSONObject("kehadiran")
                    val fotoAbsen = kehadiran.getString("foto_absensi")
                    Log.i("DATA_API", fotoAbsen)
                    val Latitude = kehadiran.getString("latitude")
                    Log.i("DATA_API", Latitude)
                    val Longitude = kehadiran.getString("longitude")
                    Log.i("DATA_API", Longitude)

                    Picasso.get().load(fotoAbsen).into(PhotoAbsen)

                    data.putLatitude(Latitude)
                    data.putLongitude(Longitude)

                    loading.isDismiss()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener {
                Toast.makeText(this@DetailHadirActivity, "Gagal ditampilkan", Toast.LENGTH_LONG).show()
                loading.isDismiss()
            }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val mStatusCode = response.statusCode
                Log.i("DATA_API", Integer.toString(mStatusCode))
                return super.parseNetworkResponse(response)
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Accept"] = "application/json"
                return params
            }

            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["token"] = token.toString()
                Log.i("DATA_API", token.toString())
                params["id_detail_jadwal_kuliah"] = id_jadwal.toString()
                Log.i("DATA_API", id_jadwal.toString())
                return params
            }
        }

        stringRequest.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        }

        queue.add(stringRequest)

    }
}