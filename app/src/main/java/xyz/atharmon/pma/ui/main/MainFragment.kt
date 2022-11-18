package xyz.atharmon.pma.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import xyz.atharmon.pma.R
import xyz.atharmon.pma.adapter.MapAreaAdapter
import xyz.atharmon.pma.databinding.FragmentMainBinding
import xyz.atharmon.pma.model.PortalItemViewModel

class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
    }

    private val viewModel: PortalItemViewModel by viewModels()

    private val mainFragmentBinding by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return mainFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainFragmentBinding.apply {
            portalItemViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        // TODO: Need to check if any previously downloaded map areas exist

        // Need to load the preplanned map areas
        // How to set the adapter to an observable and have it populate upon emitting a value?
        mainFragmentBinding.mapAreasRecyclerView.adapter = MapAreaAdapter(viewModel.mapAreas)

    }

}