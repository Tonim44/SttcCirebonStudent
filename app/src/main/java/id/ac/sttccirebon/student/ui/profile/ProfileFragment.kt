package id.ac.sttccirebon.student.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.databinding.FragmentProfileBinding
import id.ac.sttccirebon.student.ui.uitel.LoadingDialogFragment
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var data : DataManager
    lateinit var PhotoProfil : ImageView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loading = LoadingDialogFragment(this)
        loading.startLoading()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        data = DataManager(root.context)

        val EditProfile = binding.edit
        EditProfile.setOnClickListener{
            startActivity(Intent(this.context, EditProfileActivity::class.java))
        }

        PhotoProfil = binding.fotoProfile
        val token = data.getString("token")
        val queue = Volley.newRequestQueue(this.context)
        val url = "${HPI.API_URL}/api/mahasiswa/get-profile"
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener<String?> { response ->
                try {
                    val responseJson = JSONObject(response)
                    val user = responseJson.getJSONObject("result")
                    val namaMahasiswa = user.getString("nama_mahasiswa")
                    Log.i("DATA_API", namaMahasiswa)
                    val nim = user.getString("nim")
                    Log.i("DATA_API", nim.toString())
                    val noTelp = user.getString("nomor_telepon")
                    val jurusan = user.getString("jurusan")
                    val angkatan = user.getString("angkatan")
                    val semesterAktif = user.getString("semester_aktif")
                    val kelas = user.getString("kelas")
                    val fotoProfil = user.getString("link_foto_profil")

                    data.putProfile(fotoProfil)

                    binding.namaMahasiwa.text = namaMahasiswa
                    binding.detailNim.text = nim
                    binding.detailNomertelepon.text = noTelp
                    binding.detailJurusan.text = jurusan
                    binding.detailAngkatan.text = angkatan
                    binding.detailSemesteraktif.text = semesterAktif
                    binding.detaiLKelas.text = kelas
                    Picasso.get().load(fotoProfil).into(PhotoProfil)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}