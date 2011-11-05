package org.skycastle.world.crafting

/**
 * Describes some type of skill / action that needs to be applied to a target,
 * and the quality and precision targets for it.
 */
// TODO: E.g. drilling stone and wood require different sort of equipment, and drilling may require drills of different sizes, etc..  How to express those things?
// Maybe different work types for stone versus soft drilling, and ignore drillsizes etc.
// Or add material / tool hardness as parameter?
case class Work(workType: Symbol,   // E.g. cutting, sawing, screwing, hammering, carving, shaping
                precision: Double,  // e.g. 1mm accuracy in cutting wood for cabinet, or 1m accuracy in digging ditch
                force: Double,      // typical force needed (per second?  Or max?)
                skillType: Symbol,  // Skill type used, in addition to the work.  E.g. cutting for joinery or
                                    //   surgery or battle have something in common but require knowledge of
                                    //   the subject matter to succeed well.  So the skill is usually a subject matter skill, as opposed to tool use skill, which is determined by the workType.
                quality: Double,    // Skill level needed, e.g. high skill needed to cut glass without shattering it.
                amount: Double)     // Amount of work to be done, specified in seconds it would take for normal professional to do it (not including breaks)
{

}