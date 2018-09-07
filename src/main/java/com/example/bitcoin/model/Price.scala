package com.example.bitcoin.model

import scala.beans.BeanProperty
case class Price(
    @BeanProperty price: String, 
    @BeanProperty time: String
)