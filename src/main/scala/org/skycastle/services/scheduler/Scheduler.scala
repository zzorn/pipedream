package org.skycastle.services.scheduler

import java.util.{ArrayList, PriorityQueue}
import scala.collection.JavaConversions._

/**
 * Service that runs callbacks at specified times.
 */
trait Scheduler {

  /**
   * Reference to a task that can be removed from the Scheduler.
   */
  trait TaskRef

  /**
   * Add a task that is called once after the specified seconds.
   *
   * Returns a references to the task that can be used to remove it.
   */
  def addSingleTask(waitTime: Float = 0)(callback: => Unit): TaskRef

  /**
   * Add a task that is called at regular intervals (specified in seconds),
   * starting after the specified time (pass in 0 to start immediately).
   * The callback is passed the number of seconds since the last call to it, or 0 if it has not been called before.
   *
   * Returns a references to the task that can be used to remove it.
   */
  def addRegularTask(waitTime: Float = 0, interval: Float = 1)(callback: (Float) => Unit): TaskRef


  /**
   * Add a task that is passed the number of seconds since the last call to it, or 0 if it has not been called before,
   * and should return the number of seconds until it should be called again, or less than zero if it should not be called again.
   * Starts after the specified waittime in seconds (pass 0 to start immediately).
   *
   * Returns a references to the task that can be used to remove it.
   */
  def addVariableTask(waitTime: Float = 0)(callback: (Float) => Float): TaskRef

  /**
   * Removes the specified task from the Scheduler.
   */
  def removeTask(task: TaskRef)

  /**
   * Invokes any tasks that were scheduled to be run now or earlier.
   * Returns milliseconds to next event, or a fixed number if no next event is scheduled.
   */
  def update(): Long

  /**
   * Updates tasks, sleeping between updates.
   */
  def loop()

  /**
   * Constantly updates tasks, using as much processor power as possible
   */
  def busyLoop()


}