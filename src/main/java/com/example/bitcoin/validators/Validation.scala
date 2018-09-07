package com.example.bitcoin.validators
import java.time.format.DateTimeParseException
import java.text.SimpleDateFormat
import java.time.LocalDate

object Validation {
  def dateVaildations(date: String): String = {
    var message = ""
    if(! isValidDate(date)) {
      message = "Please provide valid date in yyyy-MM-dd format" 
    }else{
      
      if (LocalDate.parse(date).isAfter(LocalDate.now())) {
        message = "Future dates are not accepted."
      }
    }
    println(message)
    message
  }
  
  def isValidDate(inDate: String): Boolean = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    try {
      dateFormat.parse(inDate.trim())
    }catch {
      case pe: Exception => return false
    }
    true
  } 
  
  def validDays(days: Int): Boolean = {
    days >= 0
  }
  
  def validTypes(): Array[String] = {
    Array("last_week","last_month","last_year")  
  }
  
  def dateCompare(fromDate: String, toDate: String): Boolean = {
    LocalDate.parse(fromDate).isBefore(LocalDate.parse(toDate))   
  }
}