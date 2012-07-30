package org.skycastle.client

/**
 *
 */
object Client {

  def main(args: Array[String]) {

    val services: ClientServices = new ClientServicesImpl

    services.startup()
  }


  //todo: network, ui widgets, perceived objects, actions on controlled obj, environment visualization & update

}