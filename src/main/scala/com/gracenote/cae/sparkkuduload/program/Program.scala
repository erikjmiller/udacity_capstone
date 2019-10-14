package com.gracenote.cae.sparkkuduload.program

import akka.NotUsed
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl._
import com.gracenote.cae.sparkkuduload._
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import play.api.libs.json._
import wvlet.log.LogSupport


/** A program IE, movie, tv show, online video, etc
 *
 *  @param programId An integer id
 *  @param programType Movie, Show, Episode, etc
 *  @param programSubType Preview, Series, Mini-Series, etc
 *  @param parentProgramId Typically an episode will have a show as the parent.
 *  @param versions Programs could have sight variations in content, indicating different versions
 *  @param images List of images for the program
 *  @param titles A program can have many titles in different languages
 *  @param episodicTitles An episodic program can have multiple titles in different languages
 *  @param descriptions Program descriptions in different languages and lengths.
 */
case class Program(
                  programId: Int,
                  programType: String,
                  programSubType: String,
                  parentProgramId: Option[Int],
                  versions: List[Version],
                  images: List[Image],
                  titles: List[Title],
                  episodicTitles: List[Title],
                  descriptions: List[Description]
                  )


/** The companion object for a program
 *
 */
object Program extends LogSupport with KuduData[Program]{

  override val kuduTable = "programs"
  override val kuduSchema = StructType(
    StructField("program_id", IntegerType, false) ::
    StructField("program_type", StringType, false) ::
    StructField("program_subtype", StringType, false) ::
    StructField("parent_program_id", IntegerType, true) :: Nil
  )
  val conf = System.conf
  val nds_endpoint = s"${conf.getString("nds.url")}/programs/"
  val es_endpoint = s"${conf.getString("elasticsearch.host")}/programs/"

  def fromJson(program: String): Program = {
    val json: JsValue = Json.parse(program)
    fromJson(json)
  }
  def fromJson(json: JsValue): Program = {
    val programId = json("programId").as[Int]
    val versions = Utils.jsonSafeMap(json("versions"),{ x =>
      Version.fromJsValue(x, programId)
    })
    val titles = Utils.jsonSafeMap(json("titles"),{ x =>
      Title.fromJsValue(x, programId)
    })
    val episodicTitles = Utils.jsonSafeMap(json("episodicTitles"),{ x =>
      Title.fromJsValue(x, programId)
    })
    val descriptions = Utils.jsonSafeMap(json("descriptions"),{ x =>
      Description.fromJsValue(x, programId)
    })
    val images = Utils.jsonSafeMap(json("images"),{ x =>
      Image.fromJsValue(x, programId)
    })
    new Program(
      programId = programId,
      programType = json("programType").as[String],
      programSubType = json("programSubType").as[String],
      parentProgramId = json("parentProgramId").asOpt[Int],
      versions = versions,
      images = images,
      titles = titles,
      episodicTitles = episodicTitles,
      descriptions = descriptions
    )
  }
  def fromJson(string: Option[String]): Option[Program] = string match {
    case None => None
    case Some(jsonStr: String) =>
      Some(fromJson(jsonStr))
  }

  def getSourceIds(ids: List[Int], min: Int, max: Int): Source[Int, NotUsed] = {
    (ids, min, max) match{
      case (Nil, mi, ma) => Source(mi to ma)
      case (i, 0, 0) => Source(i)
      case (_,_,_) => error("Invalid Program.getSource arguments"); Source.empty
    }
  }

  override def createDFRows(program: Program): Seq[Row] = {
    Seq(Row(
      program.programId,
      program.programType,
      program.programSubType,
      program.parentProgramId.orNull))
  }

  def createRequest(source: String, id: Int): HttpRequest = {
    source match {
      case "elasticsearch" => HttpRequest(uri = s"${Program.es_endpoint}_doc/${id}")
      case "nds" => HttpRequest(uri = s"${Program.nds_endpoint}${id}")
    }
  }

}