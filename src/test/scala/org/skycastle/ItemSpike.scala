package org.skycastle

import util.{Over, Under}
import world.action.{ChangeTemperatureAction, ToolAction, Hit}
import world.artifact.Blade
import world.crafting.{Completed, Planned, Work}
import world.material.Iron

/**
 * 
 */

object ItemSpike {

  def main(args: Array[ String ]) {

    // Prototyping crafting scenario

    // TODO: Rewrite with work approach



    // Smith decides to make an epic vorpal blade
    // First he starts the design with a basic sword template
    val sword = new Blade()

    // which he customizes to his content
    sword.shape().undulation := 0.3
    sword.shape().width      := 0.1
    sword.shape().length     := 1.3
    sword.material            = Iron
    sword.name               := "Vorpal Serpent"

    // TODO: How needed work is calculated? - make it possible to have the project update needed work, and maybe some pseudoagile system for estimating remaining work (speed so far etc?)
    // TODO: How to calculate channeling time for actions?
    sword.addRequiredWork(Work('smithing, ToolAction(Hit, force = Over(2), sharpness = Under(0.5)), 2), 4)
    sword.addRequiredWork(Work('smithing, ChangeTemperatureAction(400)), 3)
//TODO: Leatherworking action    sword.addRequiredWork(Work('knot, 'leatherworking, time = 2), 2)

    // TODO: Add needed parts

    // At this point the sword is just a design, not yet an item in the game world, although it uses the same class
    // (TODO: Or should it use a different one?  Why?)
    // The player can preview the sword in the client, as the client can render it as a normal game object
    // in a design world / window.

    // Now the ingredients for the sword are consumed, and actual work on the sword starts, the server marks it as concrete
    // After this, the design can no longer be customized, and is fixed.
    // The sword is not yet usable however, work is just beginning!
    //sword.concrete := true

    // The smith works on the sword (using a hammer and other tools, as well as a forge) and the server updates the progress
    // The tools used can be a workshop, workbench, or just portable set of tools.
    // The most suitable tools available are used depending on the work phase
    // Several work phases can be next available ones.
    // A work phase needs its raw materials before it can begin.
    // Work phases can change depending on success of earlier ones, or other factors.
    // This way works can easily scale up to larger structures, e.g. houses, that consist of many parts, and that
    // can be worked on by many people.

    // TODO: Tools, skills, required parts

    assert(sword.status == Planned)

    var needed = sword.nextNeeded
    while (needed != None) {
      val skill = needed.get.skill
      val skillCheck = 0.4 // TODO: Check
      println("Using skill " + skill + " to " + needed.get.workType + " the blade, with success " + skillCheck)
      val used = sword.provideWork(needed.get, skillCheck)
      println("Work used: " + used)
      Thread.sleep(1000)
      needed = sword.nextNeeded
    }

    // (The smith goes of doing something else, putting the half finished sword in his backpack.
    // Later he resumes work on it)

    // The sword is now completed, [and a quality of it is fixed, based on the success of the smiths skill
    // checks during the work progress.  If the quality is too low, the sword is scrap metal, and can not be used. - quality design still open]
    assert(sword.status.isFunctional)
    assert(sword.status == Completed)

    // The can now be wielded and used!  Lets give it a swing:
    sword.slash

    // All right, ready for some heroics!

    // Dragon skin is hard, we got some chips
    sword.wear := 0.3

    // Which the smith can fix, the sword is almost like new!
    sword.wear := 0.1

    // Ack, the sword got toasted in dragonfire and chewed by lizards.  It's quite warped and worn, and beyond repair
    sword.wear := 1
    //sword.broken := true

    // The smith melts it down for scrap metal, which he uses again in his next project
    // TODO: How?  Some take apart function?  Or just toss it in something hot?  Or use a hammer on it to take it apart?

  }

}