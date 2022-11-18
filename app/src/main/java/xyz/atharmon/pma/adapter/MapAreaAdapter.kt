package xyz.atharmon.pma.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorDestinationBuilder
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.portal.PortalItem
import kotlinx.coroutines.NonDisposableHandle.parent
import xyz.atharmon.pma.R
import xyz.atharmon.pma.data.DataSource
import xyz.atharmon.pma.ui.main.MainFragment
import xyz.atharmon.pma.ui.main.MainFragmentDirections

class MapAreaAdapter(private val mapAreas: MutableList<PortalItem>) :
    RecyclerView.Adapter<MapAreaAdapter.MapAreaViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }
//    val data = DataSource.preplannedMaps

    class MapAreaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailPreview: ImageView = view.findViewById(R.id.map_area_thumbnail_preview)
        val title: TextView = view.findViewById(R.id.map_area_title)
        val snippet: TextView = view.findViewById(R.id.map_area_snippet)
    }

//    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val title = view.findViewById<TextView>(R.id.adapter_header_text)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapAreaViewHolder {
        Log.d("Adapter", "viewType $viewType")
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_map_area, parent, false)

        return MapAreaViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = mapAreas.size

    override fun getItemViewType(position: Int): Int {
        val portalItem = mapAreas[position]

        return if (portalItem.type === PortalItem.Type.WEBMAP) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }

    }

    override fun onBindViewHolder(holder: MapAreaViewHolder, position: Int) {
        val mapArea = mapAreas[position]

        val thumbnailPreview = getThumbnailPreview(mapArea)
        holder.thumbnailPreview.setImageBitmap(thumbnailPreview)
        holder.title.text = mapArea.title
        holder.snippet.text = mapArea.snippet

        holder.view.setOnClickListener {
            val action: NavDirections = MainFragmentDirections.actionMainFragmentToMapFragment(position)
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