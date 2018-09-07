package com.example.bitcoin.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import com.example.bitcoin.service.BitcoinService
import scala.collection.immutable.List
import com.example.bitcoin.model.Price
import com.example.bitcoin.validators.Validation
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping(Array("/bitcoin"))
class BitcoinController {

  @Autowired
  var service: BitcoinService = _

  @RequestMapping(Array("/prices"))
  def getPrices(@RequestParam(value = "type", required = false) `type`: String)
    : Any = {
    if (Validation.validTypes().contains(`type`)) {
      var response = service.getPrices(`type`)
      ResponseEntity.ok(response)
    }else{
      ResponseEntity.badRequest()
                    .body("Please enter valid type, it could be one of "+ Validation.validTypes().mkString(" "))  
    }
  }

  @RequestMapping(Array("/moving_average"))
  def getMovingAverage(
      @RequestParam(value = "from_date") fromDate: String,
      @RequestParam(value = "to_date") toDate: String): ResponseEntity[Any] = {
    var fromDateValidations =  Validation.dateVaildations(fromDate)
    if (fromDateValidations == "") {
      var price = service.getPriceAt(fromDate)
      if (price == null)
        return ResponseEntity.status(404).body("From date Price not Found")
    }else{
      return ResponseEntity.badRequest().body(fromDateValidations)
    }
    
    var toDateValidations =  Validation.dateVaildations(toDate)
    if (toDateValidations == "") {
      var price = service.getPriceAt(toDate)
      if (price == null)
        return ResponseEntity.status(404).body("To date Price not Found")
    }else{
      return ResponseEntity.badRequest().body(toDateValidations)
    }
    
    if(Validation.dateCompare(fromDate, toDate)) {
      var response =  service.getMovingAverage(fromDate, toDate)
      return ResponseEntity.ok(response)
    } else{
      return ResponseEntity.badRequest().body("From date can not be greater than to date")
    }
    
  }
    

  @RequestMapping(Array("/price_at"))
  def getPriceAt(
      @RequestParam(value = "date") date: String): ResponseEntity[Any] = {
    val invalidDate = Validation.dateVaildations(date)
    if (invalidDate == "") {
      var price = service.getPriceAt(date)
      if (price != null)
        return ResponseEntity.ok(price)
      else
        return ResponseEntity.status(404).body("Price not Found")
    }else{
      return ResponseEntity.badRequest().body(invalidDate)
    }
  } 
    
  @RequestMapping(Array("/make_decision"))
  def getDecision(
      @RequestParam(value = "last_x_days") days: Int): ResponseEntity[Any] = {
      if (Validation.validDays(days)) {
        ResponseEntity.ok(service.getDecision(days))
      }else{
        ResponseEntity.badRequest().body("Please enter valid days")    
      }  
  }
}
