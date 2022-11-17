package xyz.atharmon.pma.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.esri.arcgisruntime.mapping.view.MapView
import xyz.atharmon.pma.databinding.FragmentMainBinding
import xyz.atharmon.pma.model.PortalItemViewModel

class MainFragment : Fragment() {

    private val viewModel: PortalItemViewModel by viewModels()

    private val mainFragmentBinding by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    private val mapView: MapView by lazy {
        mainFragmentBinding.mapView
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mainFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainFragmentBinding.portalItemViewModel = viewModel

        mainFragmentBinding.lifecycleOwner = viewLifecycleOwner


        // Set the map to the map view
        mapView.map = viewModel.map
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        mapView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.dispose()
        super.onDestroy()
    }

}