package org.skycastle.world

import java.util.{ArrayList, PriorityQueue}
import scala.collection.JavaConversions._

/**
 * Service that runs callbacks at specified times.
 */
object Scheduler {

  private trait Task extends Comparable[Task] {
    def time: Long
    final def compareTo(o: Task) = if (time < o.time) -1 else if (time > o.time) 1 else 0
    def invoke(currentTime: Long): Boolean
  }


  private final case class SingleTask(time: Long, callback: () => Unit) extends Task {
    def invoke(currentTime: Long) {
      callback()
      false
    }
  }

  private final case class RegularTask(var time: Long, interval: Long, callback: (Long) => Unit) extends Task {
    var lastTime: Long = 0
    def invoke(currentTime: Long) {
      val duration = if (lastTime == 0) 0 else currentTime - lastTime
      callback(duration)
      time = currentTime + interval
      true
    }
  }

  private final case class VariableTask(var time: Long, callback: (Long) => Long) extends Task {
    var lastTime: Long = 0
    def invoke(currentTime: Long) {
      val duration = if (lastTime == 0) 0 else currentTime - lastTime
      val interval = callback(duration)

      if (interval > 0) {
        time = currentTime + interval
        true
      }
      else false
    }

  }

  private val tasks: PriorityQueue[Task] = new PriorityQueue[Task]
  private var tasksToReAdd = new ArrayList[Task]()

  /**
   * Add a task that is called once at the specified time.
   * Returns a references to the task that can be used to remove it.
   */
  def scheduleCallback(time: Long, callback: () => Unit): Task = {
    require(callback != null, "Callback should not be null")

    val task = SingleTask(time, callback)
    tasks add task
    task
  }

  /**
   * Add a task that is called at regular intervals, starting from the specified time (pass in 0 to start immediately).
   * Returns a references to the task that can be used to remove it.
   */
  def scheduleRegularCallback(firstTime: Long, interval: Long, callback: (Long) => Unit): Task = {
    require(interval > 0, "Interval should be positive")
    require(callback != null, "Callback should not be null")

    val task = RegularTask(firstTime, interval, callback)
    tasks add task
    task
  }

  /**
   * Add a task that returns number of milliseconds until it should be called again, or <= 0 if it should not
   * be called anymore, starting from the specified time (pass in 0 to start immediately).
   * Returns a references to the task that can be used to remove it.
   */
  def scheduleVariableCallback(firstTime: Long, callback: (Long) => Long): Task = {
    require(callback != null, "Callback should not be null")

    val task = VariableTask(firstTime, callback)
    tasks add task
    task
  }

  /**
   * Removes the specified task from the Scheduler.
   */
  def removeTask(task: Task) {
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

    tasksToReAdd foreach tasks.add( _ )
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

}