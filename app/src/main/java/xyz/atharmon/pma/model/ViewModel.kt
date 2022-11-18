package xyz.atharmon.pma.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedPackagingStatus
import xyz.atharmon.pma.BuildConfig

private const val webMapId = "3bc3179f17da44a0ac0bfdac4ad15664"

class ViewModel : ViewModel() {
    private val _webMap: PortalItem =
        PortalItem(Portal("https://www.arcgis.com", false), webMapId)
    val webMap: PortalItem
        get() = _webMap

    private var _mapAreas: MutableList<PreplannedMapArea> = mutableListOf()
    val mapAreas: MutableList<PreplannedMapArea>
        get() = _mapAreas

    private var _map: ArcGISMap
    val map: ArcGISMap
        get() = _map

    init {
        Log.d("ViewModel", "init called on PortalItemViewModel")
        // Pull the API key from the BuildConfig value set in build.gradle (app)
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.ARCGIS_RUNTIME_API)

        // create the map from the item
        _map = ArcGISMap(_webMap)

        // fetch any preplanned map areas
        getPreplannedMapAreas()
    }

    private fun addMapArea(mapArea: PreplannedMapArea) {
        mapAreas.add(mapArea)
    }

    private fun getPreplannedMapAreas() {
        val offlineMapTask = OfflineMapTask(_webMap)

        val preplannedAreasFuture = offlineMapTask.preplannedMapAreasAsync

        // Make sure the Future is done before we access it
        while (!preplannedAreasFuture.isDone) {
            Log.d("ViewModel", "Fetching the preplanned map areas...")
        }

        val preplannedMapAreas = preplannedAreasFuture.get()

        Log.d("ViewModel", "Type ${offlineMapTask.portalItem.type.name}")

        // Call loadAsync() on each preplanned map area
        preplannedMapAreas.apply {
            Log.d("ViewModel", "# of preplanned map areas: ${this.size}")
            this.onEach { mapArea ->
                mapArea.loadAsync()
            }
        }

        // For each preplanned map area, check its packaging status, if COMPLETE, add to mapAreas list
        preplannedMapAreas.apply {
            this.onEach {
                if (it.packagingStatus === PreplannedPackagingStatus.COMPLETE) {
                    Log.d(
                        "ViewModel",
                        "preplanned area: ${it.portalItem.title} ${it.portalItem.itemId}"
                    )
                    addMapArea(it)
                } else {
                    Log.d(
                        "ViewModel",
                        "preplanned area not loaded: ${it.portalItem.itemId} -> ${it.packagingStatus}"
                    )
                    it.retryLoadAsync()
                }
            }
        }

    }

    fun getThumbnailPreview(portalItem: PortalItem): Bitmap {
        val thumbnailFuture = portalItem.fetchThumbnailAsync()

        // Make sure the Future is done before we access it
        while (!thumbnailFuture.isDone) {
            Log.d("ViewModel", "Fetching the thumbnail...")
        }

        val thumbnail = thumbnailFuture.get()

        // Decode byte array into a bitmap for the ImageView
        return BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.size)
    }


}