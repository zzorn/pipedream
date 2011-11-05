package org.skycastle

import world.artifact.Blade
import world.material.Iron

/**
 * 
 */

object ItemSpike {

  def main(args: Array[ String ]) {

    // Prototyping crafting scenario


    // Smith decides to make an epic vorpal blade
    // First he starts the design with a basic sword template
    val sword = new Blade()

    // which he customizes to his content
    sword.shape().undulation := 0.3
    sword.shape().width      := 0.1
    sword.shape().length     := 1.3
    sword.material            = Iron
    sword.name               := "Vorpal Serpent"

    // At this point the sword is just a design, not yet an item in the game world, although it uses the same class
    // (TODO: Or should it use a different one?  Why?)
    // The player can preview the sword in the client, as the client can render it as a normal game object
    // in a design world / window.

    // Now the ingredients for the sword are consumed, and actual work on the sword starts, the server marks it as concrete
    // After this, the design can no longer be customized, and is fixed.
    // The sword is not yet usable however, work is just beginning!
    sword.concrete := true

    // The smith works on the sword (using a hammer and other tools, as well as a forge) and the server updates the progress
    // The tools used can be a workshop, workbench, or just portable set of tools.
    // The most suitable tools available are used depending on the work phase
    // Several work phases can be next available ones.
    // A work phase needs its raw materials before it can begin.
    // Work phases can change depending on success of earlier ones, or other factors.
    // This way works can easily scale up to larger structures, e.g. houses, that consist of many parts, and that
    // can be worked on by many people.
    



    sword.progress := 0.1
    sword.progress := 0.3
    sword.progress := 0.7

    // The smith goes of doing something else, putting the half finished sword in his backpack.
    // Later he resumes work on it
    sword.progress := 0.9
    sword.progress := 0.95
    sword.progress := 0.999
    sword.progress := 1.0

    // The sword is now completed, and a quality of it is fixed, based on the success of the smiths skill
    // checks during the work progress.  If the quality is too low, the sword is scrap metal, and can not be used.
    sword.completed := true
    sword.quality   := 1.2 // Quality succeeded quite well, the sword will have 20% higher performance overall.

    // The can now be wielded and used!  Lets give it a swing:
    sword.slash

    // All right, ready for some heroics!

    // Dragon skin is hard, we got some chips
    sword.wear := 0.3

    // Which the smith can fix, the sword is almost like new!
    sword.wear := 0.1

    // Ack, the sword got toasted in dragonfire and chewed by lizards.  It's quite warped and worn, and beyond repair
    sword.wear := 1
    sword.broken := true

    // The smith melts it down for scrap metal, which he uses again in his next project
    // TODO: How?  Some take apart function?  Or just toss it in something hot?  Or use a hammer on it to take it apart?

  }

}