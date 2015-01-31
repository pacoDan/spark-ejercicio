package org.dummy

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object ScalaApp extends App {
  val logFile = "OlympicAthletes.csv"
  val sc = new SparkContext("local", "Simple", "$SPARK_HOME"
    , List("target/spark-ejercicio-1.0.jar"))
  val file = sc.textFile(logFile)

  val olympicMedalRecordsRDD = file.map(x => {
    val arr = x.split(",")
    new OlympicMedalRecords(arr(0), Integer.parseInt(arr(1)), arr(2)
      , Integer.parseInt(arr(3)), arr(5), Integer.parseInt(arr(6)),
      Integer.parseInt(arr(7)), Integer.parseInt(arr(8)))
  }
  )

  val olympicCountriesRDD = olympicMedalRecordsRDD.map(x => (x.getCountry, x)).groupByKey()
    .map(x => {
    val country = x._1

    val medalByYear = x._2.groupBy(y => y.getOlympicGame).map(y =>
      (y._1, y._2.map(y => (y.getGoldMedals, y.getSilverMedals, y.getBronzeMedals))
        .reduce((y, z) => (y._1 + z._1, y._1 + z._1, y._1 + z._1))))

    val yearFirstMedal = medalByYear.map(_._1).min
    val yearLastMedal = medalByYear.map(_._1).max
    val totalYear = medalByYear.count(y => true)
    val maxMedals = (medalByYear.map(_._2._1).max, medalByYear.map(_._2._2).max,
      medalByYear.map(_._2._1).max)
    val totalMedals = medalByYear.map(y => (y._2._1, y._2._2, y._2._3))
      .reduce((y, z) => (y._1 + z._1, y._2 + z._2, y._3 + z._3))
    val avgMedals = (totalMedals._1 / totalYear, totalMedals._2 / totalYear, totalMedals._3 / totalYear)


    x._2.foreach(records => {

    })
    new OlympicCountries(country, yearFirstMedal, yearLastMedal, avgMedals._1,
      maxMedals._1, totalMedals._1, avgMedals._2, maxMedals._2,
      totalMedals._2, avgMedals._3, maxMedals._3, totalMedals._3)
  }).foreach(x => println(x.getName + "---" + x.getMaxGoldMedals))

  val olympicAthletesRDD = olympicMedalRecordsRDD.map(x => (x.getName, x)).groupByKey()
    .map(x => {
    val name = x._1
    val yearMedalsGroup = x._2.groupBy(_.getOlympicGame)
    val yearMedals = yearMedalsGroup.map(_._1)
    val lastYear = yearMedals.max
    val firstYear = yearMedals.min
    val totalMedals = x._2.map(y => (y.getGoldMedals, y.getSilverMedals, y.getBronzeMedals))
      .reduce((y, z) => (y._1 + z._1, y._2 + z._2, y._3 + z._3))
    new OlympicAthletes(name,firstYear,lastYear,totalMedals._1,totalMedals._2,totalMedals._3)
  }).foreach(x=>println(x.getName + "----" + x.getGoldMedals))
}

