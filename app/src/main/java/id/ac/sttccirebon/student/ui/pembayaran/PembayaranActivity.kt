package id.ac.sttccirebon.student.ui.pembayaran

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.ac.sttccirebon.student.databinding.ActivityPembayaranBinding
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class PembayaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var pembayaran: Pembayaran
    private lateinit var data : DataManager
    var id : String? = null


    companion object {
        const val EXTRA_PEMBAYARAN = "extra_pembayaran"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        data = DataManager(baseContext)
        val view = binding.root
        setContentView(view)

        pembayaran = intent.getParcelableExtra<Pembayaran>(PembayaranActivity.EXTRA_PEMBAYARAN) as Pembayaran
        id = pembayaran.id_kewajiban_pembayaran.toString()

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

        val loading = LoadingDialog(this)
        loading.startLoading()

        data = DataManager(baseContext)

        val token = data.getString("token")
        val queue = Volley.newRequestQueue(this@PembayaranActivity)
        val url = "${HPI.API_URL}/api/mahasiswa/get-detail-kewajiban-pembayaran"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val Result = responseJson.getJSONObject("result")
                    val kewajibanMasiswa = Result.getJSONObject("kewajiban_mahasiswa")
                    val detailKewajiban = Result.getJSONObject("detail_kewajiban_mahasiswa")

                    binding.jenisPembayaran.text = kewajibanMasiswa.getString("jenis_pembayaran")
                    binding.semester.text = "Semester ${kewajibanMasiswa.getString("semester")}"
                    binding.tanggal.text = detailKewajiban.getString("tanggal_lunas")
                    binding.nominal.text = kewajibanMasiswa.getString("nominal")
                    binding.metode.text = detailKewajiban.getString("metode")
                    binding.keterangan.text = kewajibanMasiswa.getString("Keterangan")

                    val Status = kewajibanMasiswa.getString("status")
                    if (Status.equals("Lunas")){
                        binding.lunas.text = "Lunas"
                    }else{
                        binding.belumLunas.text = "Belum Lunas"
                    }

                    loading.isDismiss()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener {
                Toast.makeText(this@PembayaranActivity, "Gagal ditampilkan", Toast.LENGTH_LONG).show()
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
                params["id_kewajiban_pembayaran"] = id.toString()
                Log.i("DATA_API", id.toString())
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
