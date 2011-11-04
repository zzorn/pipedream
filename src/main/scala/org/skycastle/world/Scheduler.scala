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
   * Add a task that is called once after the specified seconds.
   *
   * Returns a references to the task that can be used to remove it.
   */
  def addSingleTask(waitTime: Float = 0)(callback: => Unit): TaskRef = {
    val task = SingleTask(calculateStartTime(waitTime), callback _ )
    tasks add task
    task
  }

  /**
   * Add a task that is called at regular intervals (specified in seconds),
   * starting after the specified time (pass in 0 to start immediately).
   * The callback is passed the number of seconds since the last call to it, or 0 if it has not been called before.
   *
   * Returns a references to the task that can be used to remove it.
   */
  def addRegularTask(waitTime: Float = 0, interval: Float = 1)(callback: (Float) => Unit): TaskRef = {
    require(interval > 0, "Interval should be positive")
    require(callback != null, "Callback should not be null")

    val task = RegularTask(calculateStartTime(waitTime), (interval * 1000).toLong, callback)
    tasks add task
    task
  }

  /**
   * Add a task that is passed the number of seconds since the last call to it, or 0 if it has not been called before,
   * and should return the number of seconds until it should be called again, or less than zero if it should not be called again.
   * Starts after the specified waittime in seconds (pass 0 to start immediately).
   *
   * Returns a references to the task that can be used to remove it.
   */
  def addVariableTask(waitTime: Float = 0)(callback: (Float) => Float): TaskRef = {
    require(callback != null, "Callback should not be null")

    val task = VariableTask(calculateStartTime(waitTime), callback)
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
   * Returns milliseconds to next event, or a fixed number if no next event is scheduled.
   */
  def update(): Long = {
    val currentTime = System.currentTimeMillis()

    while(tasks.peek != null && tasks.peek.time <= currentTime) {
      val task = tasks.poll
      val reAdd = task.invoke(currentTime)
      if (reAdd) tasksToReAdd.add(task)
    }

    tasksToReAdd foreach {t => tasks.add(t) }
    tasksToReAdd.clear()

    if (tasks.peek == null) 100 else tasks.peek.time - currentTime
  }

  /**
   * Updates tasks, sleeping between updates.
   */
  def loop() {
    while (true) {
      val timeToNextEvent = update()
      Thread.sleep(timeToNextEvent)
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

  private def calculateStartTime(waitTime: Float): Long = {
    System.currentTimeMillis() + (waitTime * 1000).toLong
  }


  private trait Task extends Comparable[Task] with TaskRef {
    def time: Long
    final def compareTo(o: Task) = if (time < o.time) -1 else if (time > o.time) 1 else 0
    def invoke(currentTime: Long): Boolean

  }

  private abstract class RepeatingTask extends Task {
    var lastTime: Long = 0

    protected final def calculateDuration(currentTime: Long): Float = {
      val duration = if (lastTime == 0) 0.0f else (currentTime - lastTime) / 1000.0f
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

  private final case class RegularTask(var time: Long, interval: Long, callback: (Float) => Unit) extends RepeatingTask {
    def invoke(currentTime: Long): Boolean =  {
      val duration = calculateDuration(currentTime)

      callback(duration)

      time = currentTime + interval
      true
    }
  }

  private final case class VariableTask(var time: Long, callback: (Float) => Float) extends RepeatingTask {
    def invoke(currentTime: Long): Boolean =  {
      val duration = calculateDuration(currentTime)

      val interval = callback(duration)

      if (interval > 0) {
        time = currentTime + (interval * 1000).toLong
        true
      }
      else {
        false
      }
    }

  }


}