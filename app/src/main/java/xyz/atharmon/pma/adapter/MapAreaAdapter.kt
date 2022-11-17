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
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.portal.PortalItem
import xyz.atharmon.pma.R
import xyz.atharmon.pma.data.DataSource

class MapAreaAdapter(val mapAreas: MutableList<PortalItem>) :
    RecyclerView.Adapter<MapAreaAdapter.MapAreaViewHolder>() {

//    val data = DataSource.preplannedMaps

    class MapAreaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailPreview = view.findViewById<ImageView>(R.id.map_area_thumbnail_preview)
        val title = view.findViewById<TextView>(R.id.map_area_title)
        val snippet = view.findViewById<TextView>(R.id.map_area_snippet)
//        val button = view.findViewById<Button>(R.id.download_map_area)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapAreaViewHolder {
//        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.adapter_map_area, parent)
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_map_area, parent, false)

        return MapAreaViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int = mapAreas.size

    override fun onBindViewHolder(holder: MapAreaViewHolder, position: Int) {
        val mapArea = mapAreas[position]

        val thumbnailPreview = getThumbnailPreview(mapArea)
        holder.thumbnailPreview?.setImageBitmap(thumbnailPreview)
        holder.title?.text = mapArea.title
        holder.snippet?.text = mapArea.snippet
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