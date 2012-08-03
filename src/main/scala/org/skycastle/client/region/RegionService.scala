package org.skycastle.client.region

import org.skycastle.client.ActionMethod
import org.skycastle.utils.Service


/**
 * A region is a space that uses an own coordinate system, and which is typically managed by just one server.
 * Regions can be rooms (or possibly sets of rooms), terrain blocks, or empty space (e.g. interplanetary).
 * There are typically portals in a region that allows players to see and to travel to other connected regions.
 * Each portal is defined by some (typically flat) polygon, and has a transformation to the linked region.
 *
 * A portal may be on an entity placed in the region (e.g. door on a house on a terrain, or planet surface on a planet in space).
 * or be part of the region itself (e.g. portals to neighbouring terrain blocks and the space from a terrain block).
 * The portal does not necessarily have to render the content behind it (e.g. a planet surface could be rendered as a texture
 * instead of as a terrain with entities, and a window could be rendered as black from afar instead of rendering the
 * interiors of all visible houses).
 * The server will typically tell when the region behind a portal is visible, but the client may choose to not display
 * it anyway for performance reasons.  (So the client should query for the visible content of a region when it wants it).
 *
 * In addition to a region being visible or not, entities in the region may or may not be visible, depending on line of
 * sight, hiding, etc.  The server will notify the client when it perceives new entities in the regions it can see
 * (or is following?).
 */
// TODO: Maybe most practical if portals between regions are special entities?
// TODO: For maximum non-navigability, make region id:s random when they appear, maintain mapping on server side.
trait RegionService extends Service {

  def getRegion(regionId: Symbol): Region

  /**
   * @return the currently available regions.
   */
  def regions: Map[Symbol, Region]

  /**
   * Indicates that a new region is visible.
   * Visible entities in the region will be specified with addEntity messages to EntityService.
   * @param regionId
   * @param regionData TODO: Should contain some basic info about the region, if needed?
   */
  @ActionMethod
  def regionAppeared(regionId: Symbol, regionData: Any)

  /**
   * Indicates that a region is no longer visible.  Any entities located in it are no longer visible either.
   */
  @ActionMethod
  def regionDisappeared(regionId: Symbol)


}