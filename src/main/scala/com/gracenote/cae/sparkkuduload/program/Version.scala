package com.gracenote.cae.sparkkuduload.program

import play.api.libs.json.JsValue

final case class Version(
                  versionId: Int,
                  programId: Int,
                  runtime: Int,
                  doNotUse: Boolean,
                  versionMappings: List[VersionMapping]
                  )

object Version{

  def fromJsValue(jv: JsValue, programId: Int): Version = {
    val versionId = jv("versionId").as[Int]
    new Version(
      programId = programId,
      versionId = versionId,
      runtime = jv("runtime").as[Int],
      doNotUse = jv("doNotUse").as[Boolean],
      versionMappings = jv("versionMappings").as[List[JsValue]].map{
        vm => VersionMapping.fromJsValue(vm, versionId, programId)
      }
    )
  }
}