package xyz.atharmon.pma.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build.VERSION_CODES.P
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.tasks.offlinemap.*
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.atharmon.pma.BuildConfig

class PortalItemViewModel : ViewModel() {
//    private var _portalItems: MutableList<PortalItem> = mutableListOf()
//    val portalItems: MutableList<PortalItem>
//        get() = _portalItems
//
    private val _webMap: PortalItem = PortalItem(Portal("https://www.arcgis.com", false), "3bc3179f17da44a0ac0bfdac4ad15664")
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

        // create a portal pointing to ArcGIS Online
//        val portal = Portal("https://www.arcgis.com", false)

        // create a portal item for the specific web map id
//        val theirMapId = "3bc3179f17da44a0ac0bfdac4ad15664"

//        val mapItem = PortalItem(portal, theirMapId)

        // create the map from the item
        _map = ArcGISMap(_webMap)

        getPreplannedMapAreas()
    }

//    private fun addPortalItem(portalItem: PortalItem) {
//        portalItems.add(portalItem)
//    }

    private fun addMapArea(mapArea: PreplannedMapArea) {
        mapAreas.add(mapArea)
    }

    private fun getPreplannedMapAreas() {
        val offlineMapTask = OfflineMapTask(_webMap)

        val preplannedAreasFuture = offlineMapTask.preplannedMapAreasAsync

        // Make sure the Future is done before we access it
        while(!preplannedAreasFuture.isDone) {
            Log.d("ViewModel", "Fetching the preplanned map areas...")
        }

        val preplannedMapAreas = preplannedAreasFuture.get()

        Log.d("ViewModel", "Type ${offlineMapTask.portalItem.type.name}")
//        addPortalItem(offlineMapTask.portalItem)

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
                    Log.d("ViewModel", "preplanned area: ${it.portalItem.title} ${it.portalItem.itemId}")
//                    addPortalItem(it.portalItem)
                    addMapArea(it)
                } else {
                    Log.d("ViewModel", "preplanned area not loaded: ${it.portalItem.itemId} -> ${it.packagingStatus}")
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
        val bmp = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.size);

        return bmp
    }

//    fun createOfflineMapTask(position: Int, context: Context) {
//        val offlineMapTask = OfflineMapTask(_map)
//
//        val preplannedMapArea = mapAreas[position]
//
//        val createDefaultDownload = offlineMapTask.createDefaultDownloadPreplannedOfflineMapParametersAsync(preplannedMapArea)
//
//        createDefaultDownload.addDoneListener {
//            val params = createDefaultDownload.get().apply {
//                updateMode = PreplannedUpdateMode.NO_UPDATES
//            }
//            downloadMap(offlineMapTask, params, context, position)
//        }
//
//    }
//
//    private fun downloadMap(offlineMapTask: OfflineMapTask, params: DownloadPreplannedOfflineMapParameters, context: Context, position: Int) :  DownloadPreplannedOfflineMapJob {
//        // Build a folder path named with the portalItem's itemId property in the "My Documents" folder
//        val downloadLocation = context.getExternalFilesDir(null)?.path + "PreplannedOfflineMap_" + portalItems[position].itemId
//
//        return offlineMapTask.downloadPreplannedOfflineMap(params, downloadLocation).apply {
//            addProgressChangedListener {
//                Log.d("ViewModel", "$progress")
//            }
//
//            addJobDoneListener {
//                if (status == Job.Status.SUCCEEDED) {
//                    Toast.makeText(context, "Download complete", Toast.LENGTH_LONG).show()
//                    val result = result
//                    _map = result.offlineMap
//                }
//            }
//        }
//
//    }

}