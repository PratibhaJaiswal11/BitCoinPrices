package com.example.bitcoin.service

import org.springframework.stereotype.Service
import scala.collection.immutable._

import com.example.bitcoin.model._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import com.example.bitcoin.model.Price
import java.time.LocalDate
import org.apache.spark.sql.DataFrame
import scala.collection.mutable.ListBuffer
//@Service
//object BitcoinService {
class BitcoinService {  
  val PRICE_FEED = "https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=all"
  val MOVING_AVERAGE_TOLERANCE = 5
  var spark = SparkSession
                .builder().master("local")
                .appName("BitcoinApplication")
                .getOrCreate()
  val _spark = spark              
  import _spark.implicits._              
  val fileDF = _spark.read.json(Seq(get(PRICE_FEED)).toDS())    
  val priceDF = fileDF.select(explode($"data.prices"))
  val priceTimeDF = priceDF.select('col.getItem("price") as 'price , 
                                     to_date('col.getItem("time")) as 'date)
  def getPrices(timeframeFilter:String): Array[com.example.bitcoin.model.Price] = {
    var _timeframeFilter = timeframeFilter.toLowerCase()
    var toDate = LocalDate.now
    var fromDate = _timeframeFilter match {
        case "last_week" => toDate.minusDays(7)
        case "last_month" => toDate.minusMonths(1)
        case "last_year" => toDate.minusYears(1)
        case _ => toDate
    }
    var selectedPrices = getPriceInTimeFrame(fromDate.toString(), toDate.toString())
    selectedPrices.rdd.map {priceData=>
      new Price(priceData(0).toString(), priceData(1).toString())
    }.collect
  }
  
  def getPriceAt(date:String): Any = {
    var toDate = LocalDate.parse(date)
    var fromDate = toDate.minusDays(1)
    var selectedPrices = getPriceInTimeFrame(fromDate.toString(), toDate.toString())
    if (selectedPrices.count() > 0)
      selectedPrices.head.apply(0).toString().toDouble
    else
      null
  }
  
  def getMovingAverage(fromDate:String, toDate:String): Double = {
    var selectedPrices = getPriceInTimeFrame(fromDate, toDate)
    selectedPrices.agg(avg("price")).head.apply(0).toString().toDouble
  }
  
  def getDecision(days:Int) : String ={
    var lastXDayMovingAverage = getMovingAverage(
                                  LocalDate.now.minusDays(days).toString(), 
                                  LocalDate.now.toString())
    var latestPrice = getPriceAt(LocalDate.now.toString())
    // Really naive approach , but had to do something ;)
    if (latestPrice != null){
      if  (latestPrice.toString().toDouble > lastXDayMovingAverage*(1+(MOVING_AVERAGE_TOLERANCE/100))) {
        "Buy"
      }else if (latestPrice.toString().toDouble < lastXDayMovingAverage*(1 - (MOVING_AVERAGE_TOLERANCE/100))) {
        "Sell"
      }else {
        "Hold"
      }
    }else{
      "Cant decide because price not available"
    }
  }
  
  def getHighestPrice(fromDate: String, toDate:String, window: Int): Unit = {
    var startDate = LocalDate.parse(fromDate)
    var endDate = LocalDate.parse(toDate)
    val num = (endDate.toEpochDay() - startDate.toEpochDay())/window
    println("num of intervals :" + num)
    var selectedPrices = getPriceInTimeFrame(fromDate, toDate)
    // Fetch the max price for that interval here     
  }
  
  private[this] def getPriceInTimeFrame(fromDate: String, toDate: String): DataFrame = {
    priceTimeDF.select(col("price").cast(DoubleType), col("date"))
      .filter($"date" <= lit(toDate.toString()) && $"date" > lit(fromDate.toString()))
  }
  
  // used to download the prices
  @throws(classOf[java.io.IOException])
  private[this] def get(url: String):String = {
    scala.io.Source.fromURL(url).mkString
  }
  
//  def main (args: Array[String]){
//    getHighestPrice("2018-01-01","2018-09-01",10)
//  }
}