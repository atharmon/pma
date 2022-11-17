package xyz.atharmon.pma.model

import android.os.Build.VERSION_CODES.P
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.portal.Portal
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedPackagingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import xyz.atharmon.pma.BuildConfig

class PortalItemViewModel : ViewModel() {
//    private val _uiState = MutableStateFlow()
//    private val _mapAreas = MutableLiveData<MutableList<PortalItem>>()
//    val mapAreas: LiveData<MutableList<PortalItem>> = _mapAreas
    private var _mapAreas: MutableList<PortalItem> = mutableListOf()
    val mapAreas: MutableList<PortalItem>
        get() = _mapAreas

    private var _numOfAreas = MutableLiveData(0)
    val numOfAreas: LiveData<Int>
        get() = _numOfAreas

    private val _map: ArcGISMap
    val map: ArcGISMap
        get() = _map

    init {
        // Pull the API key from the BuildConfig value set in build.gradle (app)
        ArcGISRuntimeEnvironment.setApiKey(BuildConfig.ARCGIS_RUNTIME_API)

        // create a portal pointing to ArcGIS Online
        val portal = Portal("https://www.arcgis.com", false)

        // create a portal item for the specific web map id
        val theirMapId = "3bc3179f17da44a0ac0bfdac4ad15664"

        val mapItem = PortalItem(portal, theirMapId)

        // create the map from the item
        _map = ArcGISMap(mapItem)

        getPreplannedMapAreas()
    }

    private fun addMapArea(portalItem: PortalItem) {
        mapAreas.add(portalItem)
        _numOfAreas.value = (_numOfAreas.value)?.inc()

    }

    private fun getPreplannedMapAreas() {
        val offlineMapTask = OfflineMapTask(_map)



        val preplannedAreasFuture = offlineMapTask.preplannedMapAreasAsync

        // Make sure the Future is done before we access it
        while(!preplannedAreasFuture.isDone) {
            Log.d("ViewModel", "Fetching the preplanned map areas...")
        }

        val preplannedMapAreas = preplannedAreasFuture.get()

        Log.d("ViewModel", "Type ${offlineMapTask.portalItem.type.name}")
        addMapArea(offlineMapTask.portalItem)

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
                    addMapArea(it.portalItem)
                } else {
                    Log.d("ViewModel", "preplanned area not loaded: ${it.portalItem.itemId} -> ${it.packagingStatus}")
                    it.retryLoadAsync()
                }
            }
        }

    }


}