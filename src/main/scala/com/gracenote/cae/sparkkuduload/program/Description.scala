package com.gracenote.cae.sparkkuduload.program

import play.api.libs.json.JsValue

case class Description(
                        programId: Int,
                        descriptionId: Int,
                        language: String,
                        text: String,
                        `type`: String
                      )

object Description {
  def fromJsValue(jv: JsValue, programId: Int): Description = {
    new Description(
      descriptionId = jv("descriptionId").as[Int],
      text = jv("text").as[String],
      language = jv("language").as[String],
      `type` = jv("type").as[String],
      programId = programId
    )
  }
}
