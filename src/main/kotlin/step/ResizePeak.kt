package step

import util.*

/**
 * Resizes a region to a fixed width around its midpoint.
 * @param region the region to resize.
 * @param halfSize half the fixed width to which the region should be resized.
 */
fun resizeRegion(region: Region, halfSize: Int): Region {
    val midpoint: Int = (region.start + region.end) / 2
    return region.copy(start = midpoint - halfSize, end = midpoint + halfSize)
}

/**
 * Resizes a sequence of regions to a fixed width around their midpoints.
 * @param regions the regions to resize.
 * @param halfSize half the fixed width to which each region should be resized.
 */
fun resizeRegions(regions: List<Region>, halfSize: Int): List<Region> {
    return regions.map { resizeRegion(it, halfSize) }
}
