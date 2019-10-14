package com.gracenote.cae.sparkkuduload

import java.io.InputStream
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import com.gracenote.cae.sparkkuduload.program.Program
import wvlet.log.LogSupport
import scala.io.Source
import play.api.libs.json._

/** Object to perform various operations against elasticsearch.
 *
 */
object Elasticsearch extends LogSupport {

  val host = System.conf.getString("elasticsearch.host")


  def resetIndex() = {
    val deleteRequest = HttpRequest(method=HttpMethods.DELETE, uri=s"${host}/programs")
    System.http.syncReq(deleteRequest)
    val putRequest = HttpRequest(method=HttpMethods.PUT, uri=s"${host}/programs")
    System.http.syncReq(putRequest)
    val stream: InputStream = getClass.getClassLoader.getResourceAsStream("programs.json")
    val lines: Iterator[String] = Source.fromInputStream( stream ).getLines
    val programs: List[String] = lines.toList
    stream.close()
    programs.map(p => {
      val pattern = "programId\":\\s*(\\d+)\\s*,".r
      var id = 0
      pattern.findAllIn(p).matchData foreach {
        m => id = m.group(1).toInt
      }
      val putRequest = HttpRequest(method=HttpMethods.PUT, uri=s"${host}/programs/_doc/${id.toString}",
                                   entity = HttpEntity(ContentTypes.`application/json`, p.getBytes()))
      System.http.syncReq(putRequest)
    })
  }

  def programFromJson(response: String): Program = {
    val json: JsValue = Json.parse(response)
    Program.fromJson(json("_source"))
  }
  def programFromJson(string: Option[String]): Option[Program] = string match {
    case None => None
    case Some(jsonStr: String) =>
      Some(programFromJson(jsonStr))
  }
}

