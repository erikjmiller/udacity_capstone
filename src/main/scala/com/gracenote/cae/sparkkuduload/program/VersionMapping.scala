package com.gracenote.cae.sparkkuduload.program

import play.api.libs.json.{JsNumber, JsValue}

case class VersionMapping(
                         versionMappingId: Int,
                         programId: Int,
                         versionId: Int,
                         tmsId: String,
                         titleSetId: Int,
                         episodicTitlesetId: Int,
                         descriptionLanguage: String
                         )


object VersionMapping {
  def fromJsValue(jv: JsValue, versionId: Int, programId: Int): VersionMapping  = {
    val epTitle = jv("episodicTitlesetId") match {
      case JsNumber(_) => jv("episodicTitlesetId").as[Int]
      case _ => 0
    }
    new VersionMapping(
      versionMappingId = jv("versionMappingId").as[Int],
      programId = programId,
      versionId = versionId,
      tmsId = jv("tmsId").as[String],
      titleSetId = jv("titlesetId").as[Int],
      episodicTitlesetId = epTitle,
      descriptionLanguage = jv("descriptionLanguage").as[String]
    )
  }
}