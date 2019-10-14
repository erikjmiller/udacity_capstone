package com.gracenote.cae.sparkkuduload

import akka.actor.{Actor, ActorLogging, DeadLetter, Props}

object Supervisor {
  def props(): Props = Props(new Supervisor)
  val name: String = "supervisor"
}

/** Top-level actor for starting and monitoring other actors
 *
 */
class Supervisor extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("Supervisor started")
  override def postStop(): Unit = log.info("Supervisor stopped")

  val programPipeline = context.actorOf(Pipeline.props(), "programPipeline")

  override def receive = {
    case msg @ LoadPrograms(source, ids, min, max, httpWorkers) =>
      programPipeline ! msg
    case DeadLetter(msg, from, to) =>
      log.warning(s"Unhandled Message: ${msg.toString}")
  }
}
