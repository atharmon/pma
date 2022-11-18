package xyz.atharmon.pma.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.portal.PortalItem
import com.esri.arcgisruntime.tasks.offlinemap.PreplannedMapArea
import xyz.atharmon.pma.R
import xyz.atharmon.pma.ui.main.MainFragmentDirections
import java.io.File

class MapAreaAdapter(
    private val mapAreas: MutableList<PreplannedMapArea>,
    val packageDirectory: String?
) :
    RecyclerView.Adapter<MapAreaAdapter.MapAreaViewHolder>() {

    class MapAreaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailPreview: ImageView = view.findViewById(R.id.map_area_thumbnail_preview)
        val title: TextView = view.findViewById(R.id.map_area_title)
        val snippet: TextView = view.findViewById(R.id.map_area_snippet)
        val checkmark: ImageView = view.findViewById(R.id.map_area_downloaded_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapAreaViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_map_area, parent, false)

        return MapAreaViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = mapAreas.size

    override fun onBindViewHolder(holder: MapAreaViewHolder, position: Int) {
        val mapArea = mapAreas[position]

        val thumbnailPreview = getThumbnailPreview(mapArea.portalItem)
        holder.thumbnailPreview.setImageBitmap(thumbnailPreview)
        holder.title.text = mapArea.portalItem.title
        holder.snippet.text = mapArea.portalItem.snippet

        val packagePath = packageDirectory + "PreplannedOfflineMap_" + mapArea.portalItem.itemId
        val existingMapPackage = File(packagePath)
        Log.d("Adapter", "Exists? ${existingMapPackage.exists()} -> ${mapArea.portalItem.itemId}")
        if (existingMapPackage.exists()) {
            holder.checkmark.visibility = ImageView.VISIBLE
            holder.checkmark.setImageResource(R.drawable.ic_check_circle_grey_24)
        }

        holder.view.setOnClickListener {
            val action: NavDirections =
                MainFragmentDirections.actionMainFragmentToMapFragment(position)
            holder.view.findNavController().navigate(action)
        }
    }

    private fun getThumbnailPreview(portalItem: PortalItem): Bitmap {
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
}