package xyz.atharmon.pma.data

import xyz.atharmon.pma.R
import xyz.atharmon.pma.model.PreplannedMap

/**
 * An object to generate a static list of PreplannedMap for scaffolding the RecyclerView -> Adapter
 */
object DataSource {
    val preplannedMaps: List<PreplannedMap> = listOf(
        PreplannedMap(R.drawable.ic_launcher_background,
            "Acadia",
            "Acadia National Park is an American national park located in the state of Maine, southwest of Bar Harbor."),
        PreplannedMap(R.drawable.ic_launcher_background,
            "Boston",
            "Maine is a state in the New England region of the northeastern United States. Maine is the 12th smallest"),
        PreplannedMap(R.drawable.ic_launcher_background,
            "Baxter State Park",
            "Baxter State Park is a large wilderness area permanently preserved as a state park, located in Northeast Piscataquis,"),
        PreplannedMap(R.drawable.ic_launcher_background,
            "Greater Portland",
            "The Greater Portland metropolitan area is home to over half a million people, more than one-third of"),
        PreplannedMap(R.drawable.ic_launcher_background,
            "Caribou",
            "Caribou is the second largest city in Aroostook County, Maine, United States. Its population was 8,189 at"),
        PreplannedMap(R.drawable.ic_launcher_background,
            "Bangor",
            "Bangor is a city in the U.S. state of Maine, and the county seat of Penobscot County. The city proper")
    )
}