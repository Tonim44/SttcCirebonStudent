package id.ac.sttccirebon.student.ui.pengumuman

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.ac.sttccirebon.student.databinding.ActivityPengumumanlainnyaBinding
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.ui.helper.PrefHelper
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class PengumumanLainnyaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengumumanlainnyaBinding
    private lateinit var data: DataManager
    lateinit var prefHelper: PrefHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: PengumumanAdapter
    private lateinit var pengumumanList: MutableList<Pengumuman>
    private lateinit var requestQueue: RequestQueue
    private var page = 1
    var isLoading = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPengumumanlainnyaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data = DataManager(baseContext)
        prefHelper = PrefHelper(this)

        val loading = LoadingDialog(this)
        loading.startLoading()

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

        pengumumanList = mutableListOf()
        adapter = PengumumanAdapter(pengumumanList)
        recyclerView = binding.pengumuman
        recyclerView.layoutManager = LinearLayoutManager(this@PengumumanLainnyaActivity)
        recyclerView.adapter = adapter

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            page = 1
            pengumumanList.clear()
            loadDataFromApi()
        }

        requestQueue = Volley.newRequestQueue(this@PengumumanLainnyaActivity)


        var isLastPage = false

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItemPosition == adapter.itemCount - 1 && !isLoading && !isLastPage) {
                    isLoading = true
                    page++
                    loadDataFromApi()
                }
            }
        })

        loadDataFromApi()
        isLoading = false

        loading.isDismiss()

    }

    private fun loadDataFromApi() {

        val token = data.getString("token")
        swipeRefreshLayout.isRefreshing = true
        val url = "${HPI.API_URL}/api/mahasiswa/get-semua-pengumuman"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val Result = responseJson.getJSONArray("result")
                    for (i in 0 until Result.length()) {
                        val pengumuman = Result.getJSONObject(i)
                        val listPengumuman = Pengumuman(
                            pengumuman.getString("judul"),
                            pengumuman.getString("konten"),
                            pengumuman.getString("tanggal"),
                            pengumuman.getString( "file")
                        )
                        pengumumanList.add(listPengumuman)
                    }

                    if (page == 1) {
                        adapter = PengumumanAdapter(pengumumanList)
                        recyclerView.adapter = adapter
                    } else {
                        adapter.notifyDataSetChanged()
                    }

                    swipeRefreshLayout.isRefreshing = false
                    isLoading = false

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this@PengumumanLainnyaActivity, "Gagal ditampilkan", Toast.LENGTH_LONG).show()
                swipeRefreshLayout.isRefreshing = false
                isLoading = false
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
                params["page"] = page.toString()
                Log.i("DATA_API", page.toString())
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
            override fun retry(error: VolleyError) {}
        }

        requestQueue.add(stringRequest)
    }
}
