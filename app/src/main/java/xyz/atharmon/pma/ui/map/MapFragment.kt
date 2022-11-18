package xyz.atharmon.pma.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.portal.PortalItem
import xyz.atharmon.pma.databinding.FragmentMapBinding
import xyz.atharmon.pma.model.PortalItemViewModel
import xyz.atharmon.pma.ui.main.MainFragment
import kotlin.properties.Delegates

class MapFragment : Fragment() {

    companion object {
        const val TAG = "MapFragment"
    }

    private val viewModel: PortalItemViewModel by viewModels()
    val args: MapFragmentArgs by navArgs()

    private val mapFragmentBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        mapFragmentBinding.mapView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mapFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragmentBinding.apply {
            portalItemViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        getPortalItemIdFromArgsPosition()

        // Set map to the map view
        mapView.map = viewModel.map
    }

    fun getPortalItemIdFromArgsPosition() {
        val text = "PortalItem id: ${viewModel.mapAreas[args.position].itemId}"
        mapFragmentBinding.textfield.text = text
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"mapView.resume()")
        mapView.resume()
    }

    override fun onPause() {
        mapView.pause()
        Log.d(TAG,"mapView.pause()")
        super.onPause()
    }

    override fun onDestroy() {
        mapView.dispose()
        Log.d(TAG,"mapView.dispose()")
        super.onDestroy()
    }
}