package id.ac.sttccirebon.student.ui.isikehadiran

import android.content.pm.ActivityInfo
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import id.ac.sttccirebon.student.ui.helper.DataManager
import id.ac.sttccirebon.student.R
import id.ac.sttccirebon.student.ui.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_maps.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import java.util.*

class MapsHadirActivity : AppCompatActivity() {

    lateinit var mapController: MapController
    private lateinit var data : DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_maps)

        val loading = LoadingDialog(this)
        loading.startLoading()

        var addresses:List<Address>
        val geocoder = Geocoder(this@MapsHadirActivity, Locale.getDefault())

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        data = DataManager(baseContext)
        val Latitude: String = data.getString("latitude").toString()
        val Longitude: String = data.getString("longitude").toString()

        Log.i("DATA_API", Latitude.toString())
        Log.i("DATA_API", Longitude.toString())

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

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