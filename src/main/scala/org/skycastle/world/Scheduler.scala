package org.skycastle.world

import java.util.{ArrayList, PriorityQueue}
import scala.collection.JavaConversions._

/**
 * Service that runs callbacks at specified times.
 */
object Scheduler {

  /**
   * Reference to a task that can be removed from the Scheduler.
   */
  trait TaskRef

  private val tasks: PriorityQueue[Task] = new PriorityQueue[Task]
  private val tasksToReAdd = new ArrayList[Task]()

  /**
   * Add a task that is called once after the specified time.
   * Returns a references to the task that can be used to remove it.
   */
  def addSingleTask(waitTime: Long = 0)(callback: => Unit): TaskRef = {
    require(callback _ != null, "Callback should not be null")

    val task = SingleTask(System.currentTimeMillis() + waitTime, callback _ )
    tasks add task
    task
  }

  /**
   * Add a task that is called at regular intervals, starting after the specified time (pass in 0 to start immediately).
   * Returns a references to the task that can be used to remove it.
   */
  def addRegularTask(waitTime: Long = 0, interval: Long = 1000)(callback: (Long) => Unit): TaskRef = {
    require(interval > 0, "Interval should be positive")
    require(callback != null, "Callback should not be null")

    val task = RegularTask(System.currentTimeMillis() + waitTime, interval, callback)
    tasks add task
    task
  }

  /**
   * Add a task that returns number of milliseconds until it should be called again, or <= 0 if it should not
   * be called anymore, starting after the specified time (pass in 0 to start immediately).
   * Returns a references to the task that can be used to remove it.
   */
  def addVariableTask(waitTime: Long = 0)(callback: (Long) => Long): TaskRef = {
    require(callback != null, "Callback should not be null")

    val task = VariableTask(System.currentTimeMillis() + waitTime, callback)
    tasks add task
    task
  }

  /**
   * Removes the specified task from the Scheduler.
   */
  def removeTask(task: TaskRef) {
    tasks remove task
  }

  /**
   * Invokes any tasks that were scheduled to be run now or earlier.
   */
  def update() {
    val currentTime = System.currentTimeMillis()

    while(tasks.peek != null && tasks.peek.time <= currentTime) {
      val task = tasks.poll
      val reAdd = task.invoke(currentTime)
      if (reAdd) tasksToReAdd.add(task)
    }

    tasksToReAdd foreach {t => tasks.add(t) }
    tasksToReAdd.clear()
  }

  /**
   * Constantly updates tasks, allowing other threads to do work in between updates.
   */
  def loop() {
    while (true) {
      update()
      Thread.`yield`()
    }
  }

  /**
   * Constantly updates tasks, using as much processor power as possible
   */
  def busyLoop() {
    while (true) {
      update()
    }
  }


  private trait Task extends Comparable[Task] with TaskRef {
    def time: Long
    final def compareTo(o: Task) = if (time < o.time) -1 else if (time > o.time) 1 else 0
    def invoke(currentTime: Long): Boolean

  }

  private abstract class RepeatingTask extends Task {
    var lastTime: Long = 0

    protected final def calculateDuration(currentTime: Long): Long = {
      val duration = if (lastTime == 0) 0 else currentTime - lastTime
      lastTime = currentTime
      duration
    }

  }
  
  private final case class SingleTask(time: Long, callback: () => Unit) extends Task {
    def invoke(currentTime: Long): Boolean = {
      callback()
      false
    }
  }

  private final case class RegularTask(var time: Long, interval: Long, callback: (Long) => Unit) extends RepeatingTask {
    def invoke(currentTime: Long): Boolean =  {
      val duration = calculateDuration(currentTime)

      callback(duration)

      time = currentTime + interval
      true
    }
  }

  private final case class VariableTask(var time: Long, callback: (Long) => Long) extends RepeatingTask {
    def invoke(currentTime: Long): Boolean =  {
      val duration = calculateDuration(currentTime)

      val interval = callback(duration)

      if (interval > 0) {
        time = currentTime + interval
        true
      }
      else {
        false
      }
    }

  }


}