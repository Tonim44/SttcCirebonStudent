package id.ac.sttccirebon.student.ui.krs

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
import id.ac.sttccirebon.student.databinding.FragmentKrsBinding
import id.ac.sttccirebon.student.ui.uitel.LoadingDialogFragment
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class KrsFragment : Fragment() {

    private var _binding: FragmentKrsBinding? = null
    private lateinit var data: DataManager
    private var krsList = arrayListOf<Krs>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loading = LoadingDialogFragment(this)
        loading.startLoading()

        _binding = FragmentKrsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        data = DataManager(root.context)

        val token = data.getString("token")
        val queue = Volley.newRequestQueue(this.context)
        val url = "${HPI.API_URL}/api/mahasiswa/get-krs"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val Result = responseJson.getJSONObject("result")
                    val semester = Result.getJSONArray("krs")
                    for (i in 0 until semester.length()) {
                        val jadwalKrs = semester.getJSONObject(i)
                        val listKrs = Krs(
                            jadwalKrs.getInt("id_krs"),
                            jadwalKrs.getInt("semester"),
                            jadwalKrs.getString("tahun_ajaran"),
                            jadwalKrs.getInt( "total_sks")
                        )
                        krsList.add(listKrs)
                    }
                    showKrsList()
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

    private fun showKrsList() {
        val recyclerView : RecyclerView = binding.krsSemester
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = KrsAdapter(krsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}