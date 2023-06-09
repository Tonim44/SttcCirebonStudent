package id.ac.sttccirebon.student.ui.khs

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
import id.ac.sttccirebon.student.databinding.ActivityDetailtablekhsBinding
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class TableKhsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailtablekhsBinding
    private lateinit var khs: Khs
    private lateinit var data : DataManager
    private var khsTable = arrayListOf<TableKHS>()

    companion object {
        const val EXTRA_TABLEKHS = "extra_tablekhs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailtablekhsBinding.inflate(layoutInflater)
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
        val Ipk = binding.nilaiIPK

        khs = intent.getParcelableExtra<Khs>(TableKhsActivity.EXTRA_TABLEKHS) as Khs

        val semester = khs.semester
        val tahun_ajaran = khs.tahun_ajaran
        val total_sks = khs.total_sks
        val id : Int = khs.id_khs
        val IPK : Double = khs.nilai_ips

        Semester.text = "Semester $semester"
        TahunAjaran.text = tahun_ajaran
        TotalSKS.text = total_sks.toString()
        Ipk.text = IPK.toString()

        data = DataManager(baseContext)

        val token = data.getString("token")
        val queue = Volley.newRequestQueue(this@TableKhsActivity)
        val url = "${HPI.API_URL}/api/mahasiswa/get-detail-khs"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val Result = responseJson.getJSONObject("result")
                    val Khs = Result.getJSONObject("khs")
                    val semester = Result.getJSONArray("detail_khs")
                    for (i in 0 until semester.length()) {
                        val detailKhs = semester.getJSONObject(i)
                        val listKhsdetail = TableKHS(
                            Khs.getString("semester"),
                            Khs.getString("tahun_ajaran"),
                            detailKhs.getString("mata_kuliah"),
                            detailKhs.getString("dosen"),
                            detailKhs.getInt("jumlah_pertemuan"),
                            detailKhs.getInt("sks"),
                            detailKhs.getDouble("nilai_uts"),
                            detailKhs.getDouble("nilai_uas"),
                            detailKhs.getDouble("nilai_tugas"),
                            detailKhs.getDouble("nilai_kehadiran"),
                            detailKhs.getDouble("nilai_akhir"),
                            detailKhs.getString("nilai_huruf"),
                            detailKhs.getString("status")
                        )

                        khsTable.add(listKhsdetail)
                    }
                    showKhsList()
                    loading.isDismiss()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener {
                Toast.makeText(this@TableKhsActivity, "Gagal ditampilkan", Toast.LENGTH_LONG).show()
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
                params["id_khs"] = id.toString()
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

    private fun showKhsList() {
        val recyclerView : RecyclerView = binding.listKhs
        recyclerView.layoutManager = LinearLayoutManager(this@TableKhsActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = TableKhsAdapter(khsTable)
    }

 }