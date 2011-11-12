package org.skycastle.world.crafting

import org.skycastle.world.action.Action

/**
 * Describes some type of skill / action that needs to be applied to a target,
 * and the quality and precision targets for it.
 */
// TODO: E.g. drilling stone and wood require different sort of equipment, and drilling may require drills of different sizes, etc..  How to express those things?
// Maybe different work types for stone versus soft drilling, and ignore drillsizes etc.
// Or add material / tool hardness as parameter?
case class Work(workType:   Action, // E.g. cutting, sawing, screwing, hammering, carving, shaping.  Determines tools that can be used.
                skill:      Symbol, // Skill type used.  E.g. blacksmithing, carpentry, surgery.
                difficulty: Double = 1.0 // Skill level needed, e.g. high skill needed to cut glass without shattering it.  1 = normal.
                )
{

}