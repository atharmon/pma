package xyz.atharmon.pma.ui.map

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.esri.arcgisruntime.concurrent.Job
import com.esri.arcgisruntime.loadable.LoadStatus
import com.esri.arcgisruntime.mapping.MobileMapPackage
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.tasks.offlinemap.DownloadPreplannedOfflineMapParameters
import com.esri.arcgisruntime.tasks.offlinemap.OfflineMapTask
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedUpdateMode
import xyz.atharmon.pma.R
import xyz.atharmon.pma.databinding.FragmentMapBinding
import xyz.atharmon.pma.model.ViewModel
import java.io.File

class MapFragment : Fragment() {

    companion object {
        const val TAG = "MapFragment"
        const val WEB_MAP = -1
    }

    private val viewModel: ViewModel by activityViewModels()
    private val args: MapFragmentArgs by navArgs()

    private val mapFragmentBinding by lazy {
        FragmentMapBinding.inflate(layoutInflater)
    }

    private val progressBar: ProgressBar by lazy {
        mapFragmentBinding.mapDownloadProgressBar
    }

    private val mapView: MapView by lazy {
        mapFragmentBinding.mapView
    }

    private lateinit var packagePath: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mapFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragmentBinding.apply {
            portalItemViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Determine if web map or map area
        val isWebMap = args.position == WEB_MAP

        // Setup the menu
        val menuHost: MenuHost = requireActivity()
        if (!isWebMap) {
            menuHost.addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.mapmenu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_delete_mappackage -> {
                            deleteDownloadedMapPackage(context)
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        }


        // Set portalItem.title as the action/toolbar title
        setActionBarTitle(isWebMap)

        // Set map to the map view
        if (isWebMap) {
            mapView.map = viewModel.map
        } else {
            // Set the packagePath for use in download / delete map package functions
            packagePath =
                context?.getExternalFilesDir(null)?.path + "PreplannedOfflineMap_" + viewModel.mapAreas[args.position].portalItem.itemId

            context?.let { createOfflineMapTask(args.position, it) }
        }

    }


    private fun setActionBarTitle(isWebMap: Boolean) {
        if (isWebMap) {
            (activity as AppCompatActivity).supportActionBar?.title = viewModel.webMap.title
        } else {
            (activity as AppCompatActivity).supportActionBar?.title =
                viewModel.mapAreas[args.position].portalItem.title
        }

    }

    private fun deleteDownloadedMapPackage(context: Context?) {
        Log.d(TAG, "Deleting map package at: $packagePath")
        val packageDir = File(packagePath)
        if (packageDir.deleteRecursively()) {
            Toast.makeText(context, "Map area successfully removed", Toast.LENGTH_LONG).show()

            val action: NavDirections = MapFragmentDirections.actionMapFragmentToMainFragment()
            findNavController().navigate(action)
        } else {
            Toast.makeText(context, "Could not remove map area", Toast.LENGTH_LONG).show()
        }


    }

    private fun createOfflineMapTask(position: Int, context: Context) {
        val offlineMapTask = OfflineMapTask(viewModel.map)

        val preplannedMapArea = viewModel.mapAreas[position]

        val createDefaultDownload =
            offlineMapTask.createDefaultDownloadPreplannedOfflineMapParametersAsync(
                preplannedMapArea
            )

        createDefaultDownload.addDoneListener {
            val params = createDefaultDownload.get().apply {
                updateMode = PreplannedUpdateMode.NO_UPDATES
            }
            downloadMap(offlineMapTask, params, context)
        }

    }

    private fun downloadMap(
        offlineMapTask: OfflineMapTask,
        params: DownloadPreplannedOfflineMapParameters,
        context: Context
    ) {
        progressBar.visibility = ProgressBar.VISIBLE

        val job = offlineMapTask.downloadPreplannedOfflineMap(params, packagePath).apply {
            addProgressChangedListener {
                Log.d(TAG, "$progress")
                progressBar.progress = progress
            }

            addJobDoneListener {
                progressBar.visibility = ProgressBar.GONE

                if (status == Job.Status.SUCCEEDED) {
                    Toast.makeText(context, "Download complete", Toast.LENGTH_LONG).show()
                    val result = result
                    mapView.map = result.offlineMap
                } else {
                    // Looks like we've downloaded this one already, lets open it
                    val mapPackage = MobileMapPackage(packagePath)
                    mapPackage.loadAsync()

                    mapPackage.addDoneLoadingListener {
                        // check load status and that the mobile map package has maps
                        if (mapPackage.loadStatus === LoadStatus.LOADED && mapPackage.maps.isNotEmpty()) {
                            // add the map from the mobile map package to the MapView
                            mapView.map = mapPackage.maps[0]
                        }
                    }
                }
            }

        }

        job.start()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "mapView.resume()")
        mapView.resume()
    }

    override fun onPause() {
        mapView.pause()
        Log.d(TAG, "mapView.pause()")
        super.onPause()
    }

    override fun onDestroy() {
        mapView.dispose()
        Log.d(TAG, "mapView.dispose()")
        super.onDestroy()
    }
}