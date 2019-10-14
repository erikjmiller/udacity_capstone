package com.gracenote.cae.sparkkuduload

import wvlet.log.{LogFormatter, LogSupport, Logger}
import akka.actor.{ActorRef, ActorSystem, DeadLetter}
import com.gracenote.cae.sparkkuduload.OptionParser.Args
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{SparkSession}

/** The Main entry point to all application operations
 *
 */

object Main extends LogSupport {

  Logger.setDefaultFormatter(LogFormatter.SourceCodeLogFormatter)

  /** Primary processing entry, parses cli args, and performs specific action based on them
   *
   *  @param args
   */
  def main(args: Array[String]): Unit = {

    OptionParser.parse(args.toSeq) match{

      case None =>
        warn("Invalid Arguments")
        OptionParser.usage()
      case Some(cliArgs) =>

        Utils.pp(cliArgs)

        val keepalive: Boolean = cliArgs.action match{
          case "start" => start(); true
          case "load" => load(cliArgs); true
          case "kudu" => kudu(cliArgs); false
          case "init-stack" => initStack(); false
          case "elasticsearch" => elasticsearch(cliArgs); return
          case "usage" => OptionParser.usage(); return
        }

        if(!(cliArgs.keepalive || keepalive)){
          System.shutdown()
        }
    }
  }

  /** Not Implemented - Will eventually be the entry point to run application as a long running service
   *
   */
  def start(): Unit = ???

  /** Load some data into kudu
   *
   *  @param cliArgs
   */
  def load(cliArgs: Args): Unit = {
    info("Load Starting...")
    val supervisor = Utils.getOrCreateActor(System.system, Supervisor.props(), "/user/supervisor")

    val msg: CliMessage = (cliArgs.`type`, cliArgs.ids) match {
      case ("program", List()) => LoadPrograms(dataSource = cliArgs.dataSource, min = cliArgs.min, max = cliArgs.max, workers = cliArgs.workers)
      case ("program", ids) => LoadPrograms(dataSource = cliArgs.dataSource, ids = ids, workers = cliArgs.workers)
      case (_,_) => InvalidArgs()
    }

    (msg, supervisor) match {
      case (InvalidArgs(), _) => OptionParser.usage()
      case (m,s:ActorRef) => s ! m
    }
  }

  /** Perform kudu operation
   *
   *  @param args
   */
  def kudu(args: Args): Unit = {
    args.op match{
      case "reset-tables" => Kudu.resetTables()
      case "sql" => Kudu.sql(args.sql).show(100)
      case _ => OptionParser.usage()
    }
  }

  /** Perform elasticsearch operation
   *
   *  @param args
   */
  def elasticsearch(args: Args): Unit = {
    args.op match{
      case "reset-index" => Elasticsearch.resetIndex()
      case _ => OptionParser.usage()
    }
  }

  /** Initialize dev stack
   *    - Create elasticsearch index and load seed data
   *    - Create kudu tables
   */
  def initStack(): Unit = {
    Kudu.resetTables()
    Elasticsearch.resetIndex()
  }
}