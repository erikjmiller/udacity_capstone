package com.gracenote.cae.sparkkuduload

import akka.stream.scaladsl.{Flow, Keep, Sink}
import com.gracenote.cae.sparkkuduload.program._
import org.apache.kudu.client.{CreateTableOptions, KuduTable}
import org.apache.kudu.spark.kudu
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.StructType
import wvlet.log.LogSupport
import scala.collection.JavaConverters._
import scala.concurrent.Future


trait KuduData[A]{
  val kuduTable: String
  val kuduSchema: StructType
  def createDFRows(a: A): Seq[Row]
}

/** Object for interacting with Kudu cluster
 *
 */
object Kudu extends LogSupport {

  val kuduBatchSize = 1000
  val spark: SparkSession = Spark.spark
  val masters: String = System.conf.getString("kudu.masters")
  val context = new kudu.KuduContext(masters, spark.sparkContext)
  val wo = new kudu.KuduWriteOptions(ignoreNull = true)

  def resetTables(): Unit = {
    if(context.tableExists(Program.kuduTable)) context.deleteTable(Program.kuduTable)
    createProgramTable()
    if(context.tableExists(Title.kuduTable)) context.deleteTable(Title.kuduTable)
    createTitleTable()
  }

  def createProgramTable(): KuduTable = {
    context.createTable(Program.kuduTable, Program.kuduSchema,
      Seq("program_id"), new CreateTableOptions()
        .setNumReplicas(3)
        .addHashPartitions(List("program_id").asJava, 9))
  }

  def createTitleTable(): KuduTable = {
    context.createTable(Title.kuduTable, Title.kuduSchema,
      Seq("program_id","titleset_id"), new CreateTableOptions()
        .setNumReplicas(3)
        .addHashPartitions(List("program_id").asJava, 9))
  }

  def loadRows(rows: Seq[Row], schema: StructType, table: String): Unit = {
    warn(s"Inserting into Kudu -- Table: ${table} -- Count -- ${rows.length}")
    val df = spark.createDataFrame(
      spark.sparkContext.parallelize(rows),
      schema
    )
    context.upsertRows(df, table, wo)
  }

  def kuduSink[A](kuduData: KuduData[A]): Sink[A, Future[Int]] = {
    Flow[A].map{kuduData.createDFRows _}
      .mapConcat{titles: Seq[Row] => titles.to[collection.immutable.Seq]}
      .grouped(Kudu.kuduBatchSize)
      .map{rows => Kudu.loadRows(rows, kuduData.kuduSchema, kuduData.kuduTable)
        rows.length}
      .toMat(Sink.fold(0)(_ + _))(Keep.right)
  }

  def sql(sql: String): DataFrame = {
    createTableViews()
    spark.sql(sql)
  }

  def createTableViews(): Unit = {
    val programsDf = spark.read.options(Map("kudu.master" -> masters, "kudu.table" -> s"${Program.kuduTable}")).format("kudu").load
    val titlesDf = spark.read.options(Map("kudu.master" -> masters, "kudu.table" -> s"${Title.kuduTable}")).format("kudu").load
    programsDf.createOrReplaceTempView(Program.kuduTable)
    titlesDf.createOrReplaceTempView(Title.kuduTable)
    ()
  }
}
