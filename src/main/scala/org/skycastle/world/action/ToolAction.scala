package org.skycastle.world.action

import org.skycastle.util.{OnTarget, AnyTarget, Over, Target}

/**
 * For various actions using different kinds of mechanical tools, e.g. knifes, hammers, saws.
 */
case class ToolAction(useType: UseType,
                      force: Target = Over(1),
                      precision: Target = Over(1),
                      size: Target = OnTarget(0.1, 0.5),
                      sharpness: Target = Over(1),
                      hardness: Target = Over(1)
                     ) extends Action {

}

trait UseType

case object Hit extends UseType // with hammer etc

case object Cut extends UseType // with knife etc
case object Saw extends UseType // with saw..

case object Stab extends UseType // With needle, spear etc
case object Drill extends UseType // with drill..

case object Rip extends UseType // E.g. with claw, sharp marker
case object Scrape extends UseType // E.g. with file(sp?)
case object Grind extends UseType // E.g. with grindstones

case object Carve extends UseType // E.g. carve wood with carving iron
case object Shape extends UseType  // E.g. shape clay with hands

