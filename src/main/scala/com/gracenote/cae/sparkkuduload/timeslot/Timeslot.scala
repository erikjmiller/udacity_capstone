package com.gracenote.cae.sparkkuduload.timeslot

import java.time.{LocalDateTime,LocalDate,LocalTime}

final case class Timeslot(
                           status: String,
                           channelDay: String,
                           createdBy: String,
                           createdDate: LocalDateTime,
                           dayOfMonth: Int,
                           duration: Int,
                           gmtDateTime: LocalDateTime,
                           gmtOffset: Int,
                           lastUpdateDate: LocalDateTime,
                           loadTime: LocalDateTime,
                           localDateTime: LocalDateTime,
                           logLook: Boolean,
                           logSource: Int,
                           modifiedBy: String,
                           modifiedDate: LocalDateTime,
                           month: Int,
                           programId: Int,
                           progservId: Int,
                           qualifiers: List[String],
                           schedDate: LocalDate,
                           time: LocalTime,
                           tmsId: String,
                           tsId: String,
                           versionMappingId: Int,
                           weekDay: Int
                         )

object Timeslot{

}
