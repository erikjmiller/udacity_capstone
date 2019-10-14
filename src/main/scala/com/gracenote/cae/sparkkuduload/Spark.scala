package com.gracenote.cae.sparkkuduload

import org.apache.spark.sql.SparkSession

/**
 * Singleton wrapper for the spark context
 */
object Spark {

  val spark: SparkSession = SparkSession.builder()
    .master("local")
    .appName("Spark Kudu Load")
    .getOrCreate()
  spark.sparkContext.setLogLevel("WARN")

}
