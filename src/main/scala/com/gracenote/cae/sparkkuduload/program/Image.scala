package com.gracenote.cae.sparkkuduload.program

import play.api.libs.json.JsValue

case class Image(
                  programId: Int,
                  relativePath: String,
                  language: String,
                  provider: String,
                  `type`: String,
                  tag: String
                )


object Image{

  def fromJsValue(jv: JsValue, programId: Int): Image ={
    new Image(
      programId = programId,
      relativePath = jv("relativePath").as[String],
      language = jv("language").as[String],
      provider = jv("provider").as[String],
      `type` = jv("type").as[String],
      tag = jv("aspectRatio")("tag").as[String]
    )
  }
}