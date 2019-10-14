package com.gracenote.cae.sparkkuduload

/** Various messages that can be created from command line arguments
 *
 */
sealed trait CliMessage
final case class InvalidArgs() extends CliMessage
final case class Start() extends CliMessage
final case class LoadPrograms(dataSource: String, ids: List[Int] = List(), min: Int = 0, max: Int = 0, workers: Int = 4) extends CliMessage

