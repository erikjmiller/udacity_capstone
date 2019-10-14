package com.gracenote.cae.sparkkuduload.program

import com.gracenote.cae.sparkkuduload._
import org.apache.spark.sql.{Row, types}
import org.apache.spark.sql.types._
import play.api.libs.json.JsValue
import wvlet.log.LogSupport

/** Title of a program
 *
 *  @param programId
 *  @param titleSetId
 *  @param `type`
 *  @param language
 *  @param text
 */
case class Title(
                programId: Int,
                titleSetId: Int,
                `type`: String,
                language: String,
                text: String
                )

/** Companion object of a Title
 *
 */
object Title extends LogSupport with KuduData[Program]{

  override val kuduTable = "titles"
  override val kuduSchema = types.StructType(
    StructField("program_id", IntegerType, false) ::
    StructField("titleset_id", IntegerType, false) ::
    StructField("type", StringType, false) ::
    StructField("language", StringType, false) ::
    StructField("text", StringType, false) :: Nil
  )

  def fromJsValue(jv: JsValue, programId: Int): Title = {
    new Title(
      programId = programId,
      titleSetId = jv("titlesetId").as[Int],
      `type` = jv("type").as[String],
      language = jv("language").as[String],
      text = jv("text").as[String]
    )
  }

  override def createDFRows(program: Program): Seq[Row] = {
    val rows: Seq[Row] = program.titles.foldLeft(Seq.empty[Row])((acc: Seq[Row], t: Title) => {
      acc :+ Row(program.programId,
          t.titleSetId,
          t.`type`,
          t.language,
        t. text)
    })
    rows
  }
}