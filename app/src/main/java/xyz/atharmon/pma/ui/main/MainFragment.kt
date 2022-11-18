package xyz.atharmon.pma.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import xyz.atharmon.pma.R
import xyz.atharmon.pma.adapter.MapAreaAdapter
import xyz.atharmon.pma.databinding.FragmentMainBinding
import xyz.atharmon.pma.model.PortalItemViewModel

class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
    }

    private val viewModel: PortalItemViewModel by activityViewModels()

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

        mainFragmentBinding.webMapTitle.text = viewModel.webMap.title
        mainFragmentBinding.webMapSnippet.text = viewModel.webMap.snippet
        mainFragmentBinding.webMapThumbnailPreview.setImageBitmap(viewModel.getThumbnailPreview(viewModel.webMap))

        mainFragmentBinding.mapAreasRecyclerView.adapter = MapAreaAdapter(viewModel.mapAreas)

    }

}