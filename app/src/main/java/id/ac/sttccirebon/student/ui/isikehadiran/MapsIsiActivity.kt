package id.ac.sttccirebon.student.ui.isikehadiran

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import id.ac.sttccirebon.student.R
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_maps.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import java.util.*

class MapsIsiActivity  : AppCompatActivity() {

    lateinit var mapController: MapController
    private lateinit var data : DataManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_maps)

        val loading = LoadingDialog(this)
        loading.startLoading()

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

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        task.addOnSuccessListener {

            val data = DataManager(baseContext)
            val Latitude: String = it.latitude.toString()
            val Longitude: String = it.longitude.toString()

            var addresses: List<Address>
            val geocoder = Geocoder(this@MapsIsiActivity, Locale.getDefault())

            Log.i("DATA_API", Latitude.toString())
            Log.i("DATA_API", Longitude.toString())

            Configuration.getInstance()
                .load(this, PreferenceManager.getDefaultSharedPreferences(this))

            addresses = geocoder.getFromLocation(
                Latitude.toDouble(),
                Longitude.toDouble(),
                1
            )

            var address: String = addresses[0].getAddressLine(0)
            Log.i("DATA_API", address)

            val geoPoint = GeoPoint(Latitude.toFloat().toDouble(), Longitude.toFloat().toDouble())
            mapView.setMultiTouchControls(true)
            mapView.controller.animateTo(geoPoint)
            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            mapController = mapView.controller as MapController
            mapController.setCenter(geoPoint)
            mapController.zoomTo(15)

            val strVicinity: String = address
            val latLoc = Latitude.toFloat().toDouble()
            val longLoc = Longitude.toFloat().toDouble()

            val tvAlamat = findViewById<TextView>(R.id.AlamatLokasi)
            tvAlamat.text = strVicinity

            val marker = Marker(mapView)
            marker.icon = resources.getDrawable(R.drawable.map_icon)
            marker.position = GeoPoint(latLoc, longLoc)
            marker.infoWindow = CustomInfoWindow(mapView)
            marker.setOnMarkerClickListener { item, arg1 ->
                item.showInfoWindow()
                true
            }
            mapView.overlays.add(marker)
            mapView.invalidate()

            loading.isDismiss()
        }
    }

    public override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (mapView != null) {
            mapView.onResume()
        }
    }

    public override fun onPause() {
        super.onPause()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (mapView != null) {
            mapView.onPause()
        }
    }

}