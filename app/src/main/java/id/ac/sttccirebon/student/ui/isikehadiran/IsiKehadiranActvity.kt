package id.ac.sttccirebon.student.ui.isikehadiran

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.android.volley.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import id.ac.sttccirebon.student.DashboardActivity
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.databinding.ActivityIsikehadiranBinding
import id.ac.sttccirebon.student.ui.helper.HPI
import id.ac.sttccirebon.student.ui.helper.PrefHelper
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.concurrent.thread

class IsiKehadiranActvity : AppCompatActivity() {

    private lateinit var binding: ActivityIsikehadiranBinding
    private val FILE_NAME = "photo.jpg"
    private val REQUEST_CODE = 42
    private lateinit var photoFile: File
    private lateinit var absen: Absen
    private lateinit var data: DataManager
    lateinit var prefHelper: PrefHelper
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        const val EXTRA_ABSEN = "extra_absen"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityIsikehadiranBinding.inflate(layoutInflater)
        data = DataManager(baseContext)

        val view = binding.root
        prefHelper = PrefHelper(this)
        setContentView(view)

        val Matakuliah = binding.detailMatkul
        val Namadosen = binding.detailDosen
        val Tanggal = binding.detailTanggal
        val JamKuliah = binding.detailJam
        val Ruangan = binding.detailRuangan
        val JenisPertemuan = binding.detailJenispertemuan

        val back = binding.back
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        absen = intent.getParcelableExtra<Absen>(EXTRA_ABSEN) as Absen

        val matkul = absen.matakuliah
        val dosen = absen.namadosen
        val tanggal = absen.tanggal
        val jamawal = absen.jammulai
        val jamakhir = absen.jamselesai
        val ruangan = absen.ruangan
        val jenispertemuan = absen.jenis_pertemuan

        Matakuliah.text = matkul
        Namadosen.text = dosen
        Tanggal.text = tanggal
        JamKuliah.text = "${jamawal}-${jamakhir}"
        Ruangan.text = ruangan
        JenisPertemuan.text = jenispertemuan

        binding.simpanKehadiran.setOnClickListener{
            Toast.makeText(this@IsiKehadiranActvity, "Lakukan foto absen terlebih dahulu", Toast.LENGTH_LONG).show()
        }
        binding.sharelok.setOnClickListener{
            Toast.makeText(this@IsiKehadiranActvity, "Lakukan foto absen terlebih dahulu", Toast.LENGTH_LONG).show()
        }

        //TakePhoto
        binding.ambilfoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider =
                FileProvider.getUriForFile(this, "id.ac.sttccirebon.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val data = DataManager(baseContext)
            val imageView = binding.imageView
            val byteArrayOutputStream = ByteArrayOutputStream()
            var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            bitmap = resizeBitmap(bitmap, 800)
            imageView.setImageBitmap(bitmap)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            sendAbsen()
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun resizeBitmap(bitmap : Bitmap, maxSize: Int) : Bitmap {

        var width : Int = bitmap.getWidth();
        var height : Int = bitmap.getHeight();
        var x : Int = 0;

        if (width >= height && width > maxSize) {
            x = width / height;
            width = maxSize;
            height = maxSize / x
        } else if (height >= width && height > maxSize) {
            x = height / width;
            height = maxSize;
            width = maxSize / x
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    fun sendAbsen() {

        //TakeLocation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {

            val data = DataManager(baseContext)
            val id: Int = absen.idjadwal
            val Latitude: String = it.latitude.toString()
            val Longitude: String = it.longitude.toString()

//            val Latitude: String ="-6.740562"
//            val Longitude: String ="108.543294"

            Log.i("DATA_API", Latitude.toString())
            Log.i("DATA_API", Longitude.toString())

            val shareLokasi = binding.sharelok
            shareLokasi.setOnClickListener {
                val intent = Intent(this, MapsIsiActivity::class.java)
                startActivity(intent)
            }

            //SimpanKehadiran
            binding.simpanKehadiran.setOnClickListener {

                binding.simpanKehadiran.isEnabled = false
                val loading = LoadingDialog(this)
                loading.startLoading()
                val token = data.getString("token")

                thread {
                    try {
                        val client = OkHttpClient()
                        val file = File(photoFile.absolutePath)
                        val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "ImageCapture",
                                file.name,
                                RequestBody.create(MediaType.parse("image/jpg"), file)
                            )
                            .addFormDataPart("token", token)
                            .addFormDataPart("id_detail_jadwal_kuliah", id.toString())
                            .addFormDataPart("latitude", Latitude)
                            .addFormDataPart("longitude", Longitude)
                            .build()

                        val request = Request.Builder()
                            .url("${HPI.API_URL}/api/mahasiswa/set-absensi")
                            .post(requestBody)
                            .build()

                        val response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()
                            val jsonResponse = JSONObject(responseBody)
                            val results = jsonResponse.getJSONObject("result")
                            val message = results.getString("message")
                            runOnUiThread {
                                Toast.makeText(
                                    this@IsiKehadiranActvity,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                                moveIntent()
                            }
                        } else {
                            val responseBody = response.body()?.string()
                            val jsonResponse = JSONObject(responseBody)
                            val results = jsonResponse.getJSONObject("result")
                            val message = results.getString("message")
                            runOnUiThread {
                                Toast.makeText(
                                    this@IsiKehadiranActvity,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this@IsiKehadiranActvity,
                                "Terjadi kesalahan saat mengirim data : ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } finally {
                        runOnUiThread {
                            loading.isDismiss()
                            binding.simpanKehadiran.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun moveIntent(){
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
