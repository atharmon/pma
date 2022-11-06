package xyz.atharmon.pma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView

import xyz.atharmon.pma.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val activityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        activityMainBinding.mapview
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        setApiKeyForApp()

        setupMap()
    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }

    private fun setupMap() {
        val map = ArcGISMap(BasemapStyle.ARCGIS_DARK_GRAY_BASE)

        mapView.map = map

        mapView.setViewpoint(Viewpoint(30.3884, -96.08821, 72000.0))
    }

    private fun setApiKeyForApp() {
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.ARCGIS_RUNTIME_API)
    }
}