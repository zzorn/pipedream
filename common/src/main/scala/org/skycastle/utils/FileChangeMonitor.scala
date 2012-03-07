package org.skycastle.utils

import java.io.File
import java.awt.Toolkit
import javax.swing.SwingUtilities

/**
 * Looks for changes to a file, and notifies a listener.
 * Only reports the change when the file size is not changing between two checks (indicating that the file has been completely written)
 */
class FileChangeMonitor(file: File, listener: File => Unit, interval: Long = 500) extends Logging {

  private var started = false
  
  private class Checker(file: File, listener: File => Unit) extends Runnable {
    private var endLoop = false
    private var lastTimestamp = 0L
    private var lastSize = 0L

    def end() {
      synchronized {
        endLoop = true
      }
    }
    
    private def isEnded: Boolean = {
      var ended: Boolean = false
      synchronized {
        ended = endLoop
      }
      ended
    }
    
    def run() {
      log.debug("Started monitoring " + file)

      while (!isEnded) {
        if (file.exists()) {
          val timestamp = file.lastModified()
          if (timestamp != lastTimestamp) {

            // Check if writing is still ongoing (file size changes)
            val size = file.length()
            if (lastSize != size) {
              log.debug("File '"+file+"' still changing size, waiting for size to stabilize")
              //println("size changed")
              // Wait for write to complete
              lastSize = size
            }
            else {
              // Size didn't change since the last time we checked, so report file change
              lastTimestamp = timestamp
              
              // Notify listener
              log.debug("File '"+file+"' changed, notifying listener")
              listener(file)
            }
          }
        }

        Thread.sleep(interval)
      }

      log.debug("Stopped monitoring " + file)
    }
    
  }

  private val checker = new Checker(file, listener)
  private val thread: Thread = new Thread(checker, "File change monitor thread for '"+file.getName+"'")


  def start() {
    if (!started) {
      started = true
      thread.setDaemon(true) // End the thread when the application ends
      thread.start()
    }
  }
  
  def stop() {
    if (started) {
      started = false
      checker.end()
    }
  }
  
  
}
