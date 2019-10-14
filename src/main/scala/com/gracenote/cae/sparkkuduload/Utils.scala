package com.gracenote.cae.sparkkuduload

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import play.api.libs.json._
import java.lang.reflect.Field
import java.sql.Timestamp
import akka.actor.{ActorContext, ActorRef, ActorSystem, Props}
import wvlet.log.LogSupport
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/** General object for utility functions
 *
 */
object Utils extends LogSupport{

  /** Simple datetime parsing from string to LocalDateTime
   *
   *  @param date
   *  @return
   */
  def parseDT(date: String): LocalDateTime = {
    val trimmed = date.replaceAll("\\.\\d+.*", "")  // remove anything less than a second
    val formatter = trimmed match {
      case s if s.matches("""^\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\d$$""") => DateTimeFormatter.ISO_LOCAL_DATE_TIME
      case s if s.matches("""^\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d$$""") => DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      case s if s.matches("""^\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\d.\d\d\dZ$$""") => DateTimeFormatter.ofPattern("""yyyy-MM-dd\THH:mm:ss.SSS\Z""")
      case s if s.matches("""^\d\d\d\d-\d\d-\d\dT\d\d:\d\d:\d\d.\d{6}[+-]\d\d:\d\d$$""") => DateTimeFormatter.ISO_INSTANT
      case s if s.matches("""^\d\d\d\d-\d\d-\d\d$$""") => DateTimeFormatter.ISO_LOCAL_DATE
      case s if s.matches("""^\d\d\d\d:\d\d:\d\d$$""") => DateTimeFormatter.ISO_LOCAL_TIME
    }
    LocalDateTime.parse(trimmed, formatter)
  }

  /** Function to map over json list return List[A]
   *
   *  @param jv
   *  @param mapFunc
   *  @tparam A
   *  @return List of type A
   */
  def jsonSafeMap[A](jv: JsValue, mapFunc: (JsValue => A)): List[A] = {
    jv match{
      case JsNull => List()
      case x => x.as[List[JsValue]].map{mapFunc(_)}
    }
  }

  /** Wrapper for prettyPrint to output nice looking data
   *
   *  @param a
   *  @param stdout
   *  @return
   */
  def pp(a: Any, stdout: Boolean = true): String = {
    val output = prettyPrint(a)
    stdout match {
      case true => println(output)
      case false => info(output)
    }
    output
  }

  /** Generate a pretty string of the input variable for output
   *
   *  @param a
   *  @param indentSize
   *  @return
   */
  def prettyPrint(a: Any, indentSize: Int = 0): String = {
    // Recursively get all the fields; this will grab vals declared in parents of case classes.
    def getFields(cls: Class[_]): List[Field] =
      Option(cls.getSuperclass).map(getFields).getOrElse(Nil) ++
        cls.getDeclaredFields.toList.filterNot(f =>
          f.isSynthetic || java.lang.reflect.Modifier.isStatic(f.getModifiers))

    val indent = List.fill(indentSize)(" ").mkString

    val newIndentSize = indentSize + 2
    (a match {
      // Make Strings look similar to their literal form.
      case string: String =>
        val conversionMap = Map('\n' -> "\\n", '\r' -> "\\r", '\t' -> "\\t", '\"' -> "\\\"", '\\' -> "\\\\")
        string.map(c => conversionMap.getOrElse(c, c)).mkString("\"", "", "\"")
      case xs: Seq[_] =>
        xs.map(prettyPrint(_, newIndentSize)).toString
      case xs: Array[_] =>
        s"Array(${xs.map(prettyPrint(_, newIndentSize)).mkString(", ")})"
      case map: Map[_, _] =>
        s"Map(\n" + map.map {
          case (key, value) => "  " + prettyPrint(key, newIndentSize) + " -> " + prettyPrint(value, newIndentSize)
        }.mkString(",\n") + "\n)"
      case None => "None"
      case Some(x) => "Some(" + prettyPrint(x, newIndentSize) + ")"
      case timestamp: Timestamp => "new Timestamp(" + timestamp.getTime + "L)"
      case p: Product =>
        s"${p.productPrefix}(\n${
          getFields(p.getClass).map { f =>
            f.setAccessible(true)
            s"  ${f.getName} = ${prettyPrint(f.get(p), newIndentSize)}"
          }
            .mkString(",\n")
        }\n)"
      case q =>
        Option(q).map(_.toString).getOrElse("null")
    })
      .split("\n", -1).mkString("\n" + indent)
  }

  /** Get a specific actor by name or create it using passed in Props and ActorSystem
   *
   *  @param system
   *  @param props
   *  @param path
   *  @return
   */
  def getOrCreateActor(system: ActorSystem, props: Props, path: String): ActorRef = {
    val selector = system.actorSelection(path)
    val name = path.split("/").last

    Try(Await.result(selector.resolveOne((5).seconds), 1.seconds)) match {
      case Success(result) =>
        info(result.toString())
        result
      case Failure(e) =>
        info(e.toString)
        system.actorOf(props, name)
    }
  }

  /** Get a specific actor by name or create it using passed in Props and ActorContext
   *
   *  @param context
   *  @param props
   *  @param path
   *  @return
   */
  def getOrCreateActor(context: ActorContext, props: Props, path: String): ActorRef = {
    val selector = context.actorSelection(path)
    val name = path.split("/").last

    Try(Await.result(selector.resolveOne((5).seconds), 1.seconds)) match {
      case Success(result) =>
        info(result.toString())
        result
      case Failure(e) =>
        info(e.toString)
        context.actorOf(props, name)
    }
  }

}
