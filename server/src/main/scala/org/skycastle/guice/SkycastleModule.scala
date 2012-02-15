package org.skycastle.guice

import com.google.inject.AbstractModule
import org.skycastle.services.scheduler.{SingleThreadedScheduler, Scheduler}
import org.skycastle.services.serializer.{ProtostuffSerializer, Serializer}
import org.skycastle.services.storage.{RedisStorage, Storage}

/**
 *
 */
class SkycastleModule extends AbstractModule {

  def configure() {

    bind(classOf[Scheduler]).to(classOf[SingleThreadedScheduler])
    bind(classOf[Serializer]).to(classOf[ProtostuffSerializer])
    bind(classOf[Storage]).to(classOf[RedisStorage])

  }

}