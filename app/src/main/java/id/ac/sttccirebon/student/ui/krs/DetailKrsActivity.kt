package id.ac.sttccirebon.student.ui.krs

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.databinding.ActivityDetailtablekrsBinding
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class DetailKrsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailtablekrsBinding
    private lateinit var krs: Krs
    private lateinit var data : DataManager
    private var krsDetail = arrayListOf<DetailKrs>()

    companion object {
        const val EXTRA_KRS = "extra_krs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailtablekrsBinding.inflate(layoutInflater)
        data = DataManager(baseContext)
        val view = binding.root
        setContentView(view)

        val back = binding.back
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        val loading = LoadingDialog(this)
        loading.startLoading()

        val Semester = binding.semester
        val TahunAjaran = binding.tahunajaran
        val TotalSKS = binding.totalSKS

        krs = intent.getParcelableExtra<Krs>(DetailKrsActivity.EXTRA_KRS) as Krs

        val semester = krs.semester
        val tahun_ajaran = krs.tahun_ajaran
        val total_sks = krs.total_sks
        val id : Int = krs.id_krs

        Semester.text = "Semester $semester"
        TahunAjaran.text = tahun_ajaran
        TotalSKS.text = total_sks.toString()

        data = DataManager(baseContext)

        val token = data.getString("token")
        val queue = Volley.newRequestQueue(this@DetailKrsActivity)
        val url = "${HPI.API_URL}/api/mahasiswa/get-detail-krs"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val Result = responseJson.getJSONObject("result")
                    val semester = Result.getJSONArray("detail_krs")
                    for (i in 0 until semester.length()) {
                        val detailKrs = semester.getJSONObject(i)
                        val listKrsdetail = DetailKrs(
                            detailKrs.getString("mata_kuliah"),
                            detailKrs.getString("dosen"),
                            detailKrs.getInt("jumlah_pertemuan"),
                            detailKrs.getInt("sks")
                        )
                        krsDetail.add(listKrsdetail)
                    }
                    showKrsList()
                    loading.isDismiss()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener {
                Toast.makeText(this@DetailKrsActivity, "Gagal ditampilkan", Toast.LENGTH_LONG).show()
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
                params["id_krs"] = id.toString()
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

    private fun showKrsList() {
        val recyclerView : RecyclerView = binding.listKrs
        recyclerView.layoutManager = LinearLayoutManager(this@DetailKrsActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = DetailKrsAdapter(krsDetail)
    }

 }