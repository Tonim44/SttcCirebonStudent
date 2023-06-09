package id.ac.sttccirebon.student.ui.isikehadiran

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.databinding.FragmentIsikehadiranBinding
import id.ac.sttccirebon.student.ui.uitel.LoadingDialogFragment
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class IsiKehadiranFragment : Fragment() {

    private var _binding: FragmentIsikehadiranBinding? = null
    private lateinit  var  data : DataManager
    private var absenList = arrayListOf<Absen>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loading = LoadingDialogFragment(this)
        loading.startLoading()

        _binding = FragmentIsikehadiranBinding.inflate(inflater, container, false)
        val root: View = binding.root
        data = DataManager(root.context)

        val back = binding.back
        back.visibility = View.GONE

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime: String = sdf.format(Date())
        binding.presentDate.text = currentDateandTime

        val token = data.getString("token")
        val queue = Volley.newRequestQueue(this.context)
        val url = "${HPI.API_URL}/api/mahasiswa/get-jadwal-kuliah"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val Result = responseJson.getJSONObject("result")
                    val jadwalKuliah = Result.getJSONArray("jadwal_kuliah")
                    absenList.clear()

                    for(i in 0 until jadwalKuliah.length()) {
                    val jadwalMatkul = jadwalKuliah.getJSONObject(i)
                    val listMatkul = Absen(
                        jadwalMatkul.getInt("id_detail_jadwal_kuliah"),
                        jadwalMatkul.getString("mata_kuliah"),
                        jadwalMatkul.getString("nama_dosen"),
                        jadwalMatkul.getString("nama_assisten"),
                        jadwalMatkul.getString("tanggal"),
                        jadwalMatkul.getString("jam_mulai"),
                        jadwalMatkul.getString("jam_selesai"),
                        jadwalMatkul.getString("ruangan"),
                        jadwalMatkul.getString("jenis_pertemuan"),
                        jadwalMatkul.getString("status")
                    )
                    absenList.add(listMatkul)
                }
                    showAbsenList()
                    loading.isDismiss()

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener {
                Toast.makeText(this.context, "Gagal ditampilkan", Toast.LENGTH_LONG).show()
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
                params["date"] = currentDateandTime.toString()
                Log.i("DATA_API", currentDateandTime.toString())
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

        return root
    }

    private fun showAbsenList() {
        val recyclerView : RecyclerView = binding.jadwalPerkuliahan
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = AbsenAdapter(absenList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}