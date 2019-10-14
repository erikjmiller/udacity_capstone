package com.gracenote.cae.sparkkuduload

import akka.actor.{ActorSystem, DeadLetter}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.SparkSession

object System {

  val conf: Config = ConfigFactory.load()
  val system: ActorSystem = ActorSystem("system")
  val http: HTTP = new HTTP(system)
  val spark: SparkSession = Spark.spark
  val supervisor = Utils.getOrCreateActor(system, Supervisor.props(), "/user/supervisor")
  system.eventStream.subscribe(supervisor, classOf[DeadLetter])

  def shutdown(): Unit = {
    system.terminate()
    spark.stop()
  }
}
