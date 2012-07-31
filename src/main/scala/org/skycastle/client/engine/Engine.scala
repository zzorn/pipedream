package org.skycastle.client.engine

import com.jme3.asset.AssetManager
import org.skycastle.utils.Service

/**
 * The 3D display engine.
 */
trait Engine extends Service {

  def getAssetManager: AssetManager

}
