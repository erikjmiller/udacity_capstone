package com.gracenote.cae.sparkkuduload

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream._
import akka.stream.scaladsl.Keep
import com.gracenote.cae.sparkkuduload.program._
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._


object Pipeline {
  def props(): Props = Props(new Pipeline)
}

/** Primary processing pipeline for getting data and pushing it to Kudu
 *
 */
class Pipeline extends Actor with ActorLogging {

  override def preStart(): Unit = log.info("Pipeline started")
  override def postStop(): Unit = log.info("Pipeline stopped")

  // Setup Actor System and ExecutionContext
  implicit val system: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  implicit val executionContext: ExecutionContext = context.system.dispatcher

  // Handle messages to start pipelines
  override def receive: Receive = {
    case lp @ LoadPrograms(dataSource, ids, min, max, workers) =>
      Utils.pp(lp)

      //Return count of rows inserted
      val (tMat, pMat) = Program.getSourceIds(ids, min, max)
      .map{ x => Program.createRequest(dataSource, x) }
      .mapAsyncUnordered(workers)(System.http.req)
        .map(Elasticsearch.programFromJson)
        .filter { _.isDefined}
        .map{p => p.get}
        .alsoToMat(Kudu.kuduSink[Program](Title))(Keep.right)
        .toMat(Kudu.kuduSink[Program](Program))(Keep.both)
        .run()

      val tCount = Await.result(tMat, Duration.Inf)
      val pCount = Await.result(pMat, Duration.Inf)
      log.info(s"Load Complete -- $pCount Programs processed - $pCount program rows inserted - $tCount title rows inserted")

      System.system.terminate()
      System.spark.stop()

    case other => log.error(s"Unknown message: ${other.toString}")
  }

}
