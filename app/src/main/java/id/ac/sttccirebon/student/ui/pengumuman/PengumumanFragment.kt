package id.ac.sttccirebon.student.ui.pengumuman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import id.ac.sttccirebon.student.databinding.FragmentPengumumanBinding
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.ui.uitel.LoadingDialogFragment
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class PengumumanFragment : Fragment() {

  private var _binding: FragmentPengumumanBinding? = null
  private val binding get() = _binding!!
  private lateinit var data: DataManager
  private lateinit var recyclerView: RecyclerView
  private lateinit var swipeRefreshLayout: SwipeRefreshLayout
  private lateinit var adapter: PengumumanAdapter
  private lateinit var pengumumanList: MutableList<Pengumuman>
  private lateinit var requestQueue: RequestQueue
  private var page = 1
  var isLoading = false

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    val loading = LoadingDialogFragment(this)
    loading.startLoading()

    _binding = FragmentPengumumanBinding.inflate(inflater, container, false)
    val root: View = binding.root
    data = DataManager(root.context)

    binding.lihatLainnya.setOnClickListener {
      startActivity(Intent(this.context, PengumumanLainnyaActivity::class.java))
    }

    pengumumanList = mutableListOf()
    adapter = PengumumanAdapter(pengumumanList)
    recyclerView = binding.pengumuman
    recyclerView.layoutManager = LinearLayoutManager(this.context)
    recyclerView.adapter = adapter

    swipeRefreshLayout = binding.swipeRefreshLayout
    swipeRefreshLayout.setOnRefreshListener {
      page = 1
      pengumumanList.clear()
      loadDataFromApi()
    }

    requestQueue = Volley.newRequestQueue(this.context)


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

    return root
  }

  private fun loadDataFromApi() {

    val token = data.getString("token")
    swipeRefreshLayout.isRefreshing = true
    val url = "${HPI.API_URL}/api/mahasiswa/get-pengumuman"
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
        Toast.makeText(this.context, "Gagal ditampilkan", Toast.LENGTH_LONG).show()
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

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}