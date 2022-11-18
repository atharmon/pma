package xyz.atharmon.pma.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import xyz.atharmon.pma.adapter.MapAreaAdapter
import xyz.atharmon.pma.databinding.FragmentMainBinding
import xyz.atharmon.pma.model.ViewModel

class MainFragment : Fragment() {

    private val viewModel: ViewModel by activityViewModels()

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
        mainFragmentBinding.webMapThumbnailPreview.setImageBitmap(
            viewModel.getThumbnailPreview(
                viewModel.webMap
            )
        )
        mainFragmentBinding.webMapLayout.setOnClickListener {
            val action: NavDirections = MainFragmentDirections.actionMainFragmentToMapFragment(-1)
            findNavController().navigate(action)
        }

        val directory = context?.getExternalFilesDir(null)?.path
        mainFragmentBinding.mapAreasRecyclerView.adapter =
            MapAreaAdapter(viewModel.mapAreas, directory)

    }

}