package com.gracenote.cae.sparkkuduload


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import wvlet.log.LogSupport
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


/** Wrapper around a akka-http for handling client requests to external services
 *
 * @param system
 */
class HTTP(system: ActorSystem) extends LogSupport{

  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))(system)
  implicit val executionContext: ExecutionContext = system.dispatchers.lookup("akka.blocking-dispatcher")

  val http = Http(system)

  /** Perform an http request synchronously, return an Option[String] with the reponse body
   *
   *  @param request
   *  @return
   */
  def syncReq(request: HttpRequest): Option[String] = {
    Try(Await.result(req(request), 5.seconds)) match {
      case Success(result) =>
        result
      case Failure(e) =>
        error(e.toString)
        None
    }
  }

  /** Perform an http request asynchronously, return an Future[Option[String]] with the response body
   *
   *  @param request
   *  @return
   */
  def req(request: HttpRequest): Future[Option[String]] = {
    info(s"Executing HTTP Request: ${request}")
    http.singleRequest(request)
      .flatMap { handleResponse }
  }

  /** Generic Handling for processing an http response
   *
   * @param response
   * @return
   */
  def handleResponse(response: HttpResponse): Future[Option[String]] = {
    response match {
      case resp@HttpResponse(StatusCodes.OK, _headers, entity, _protocol) =>
        info(s"Valid HttpResponse: ${resp}")
        entity.dataBytes
          .runReduce(_ ++ _)
          .map(x => {
            Some(x.utf8String)})
      case resp@HttpResponse(StatusCodes.Created, _headers, entity, _protocol) =>
        info(s"Valid HttpResponse: ${resp}")
        entity.dataBytes
          .runReduce(_ ++ _)
          .map(x => {
            Some(x.utf8String)})
      case resp@HttpResponse(code, _headers, entity, _protocol) =>
        warn(s"Invalid HttpResponse: ${resp}")
        entity.dataBytes
          .runReduce(_ ++ _)
          .map(x => None)
    }
  }

}