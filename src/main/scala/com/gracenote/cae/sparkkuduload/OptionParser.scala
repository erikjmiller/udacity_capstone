package com.gracenote.cae.sparkkuduload

import scopt.RenderingMode.OneColumn

object OptionParser {

  /** Possible arguments to the Main object of SparkKuduLoad
   *
   *  @param action One of load, kudu, elasticsearch, init-stack
   *  @param `type` Type of object to load, currently only supports program
   *  @param dataSource Source of data to be loaded, currently only supports elasticsearch
   *  @param ids A comma-seperated list of integer ids used by load action
   *  @param min A minimum id of a range of integer ids to load
   *  @param max A maximum id of a range of integer ids to load
   *  @param op An operation specific to the defined action
   *  @param workers The number of simultaneous http requests to get objects
   *  @param sql A sql query to execute against kudu using spark sql
   *  @param keepalive A flag to the application (ActorSystem and Spark) running, useful for dev
   */
  case class Args(action:String = "",
                  `type`: String = "",
                  dataSource: String = "",
                  ids: List[Int] = List(),
                  min: Int = 0,
                  max: Int = 0,
                  op : String = "",
                  workers: Int = 4,
                  sql: String = "",
                  keepalive: Boolean = false)

  val parser = new scopt.OptionParser[Args]("sparkkuduload") {
    head("SparkKuduLoad", "An application for loading data into kudu using spark integration APIs")
    opt[Unit]("keepalive").abbr("k").action((_, c) => c.copy(keepalive = true))
        .text("development flag to keep ActorSystem and SparkSession running")
    cmd("load").action( (_, c) => c.copy(action = "load") ).
      text("load operations").
      children(
        opt[String]("type").abbr("t").action( (x, c) =>
          c.copy(`type` = x) )
          .text("object type to load (program, timeslot, person)"),
        opt[String]("data-source").abbr("d").action( (x, c) =>
          c.copy(dataSource = x) )
          .text("source of data to load"),
        opt[Seq[Int]]("ids").action( (x, c) =>
          c.copy(ids = x.toList) )
          .text("ids to load, min and max are ignored"),
        opt[Int]("min").action( (x, c) =>
          c.copy(min = x) )
          .text("minimum id of range to load"),
        opt[Int]("max").action( (x, c) =>
          c.copy(max = x) )
          .text("maximum id of range to load"),
        opt[Int]("workers").abbr("w").action( (x, c) =>
          c.copy(workers = x) )
          .text("number of concurrent nds requests")
      )
    cmd("kudu").action( (_, c) => c.copy(action = "kudu") ).
      text("kudu operations").
      children(
        opt[Unit]("reset-tables").action( (x, c) =>
          c.copy(op = "reset-tables") )
          .text("drop and recreate all kudu tables"),
        opt[String]("sql").action( (x, c) =>
          c.copy(op = "sql", sql = x))
          .text("execute sql against kudu using spark sql")
      )
    cmd("elasticsearch").action( (_, c) => c.copy(action = "elasticsearch") ).
      text("elasticsearch operations").
      children(
        opt[Unit]("reset-index").action( (x, c) =>
          c.copy(op = "reset-index") )
          .text("recreate all indexes and load seed data")
      )
    cmd("init-stack").action( (_, c) => c.copy(action = "init-stack") ).
      text("initialize dev stack")
    cmd("usage").action( (_, c) => c.copy(action = "usage") ).
      text("print this message")
  }

  def parse(args: Seq[String]): Option[Args] = {
    parser.parse(args, Args())
  }

  def usage() = println(parser.renderUsage(OneColumn))

}
