package org.skycastle.guice

import com.google.inject.AbstractModule
import org.skycastle.services.scheduler.{SingleThreadedScheduler, Scheduler}

/**
 *
 */
class SkycastleModule extends AbstractModule {

  def configure() {

    bind(classOf[Scheduler]).to(classOf[SingleThreadedScheduler])

  }

}