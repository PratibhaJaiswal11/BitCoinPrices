package com.example.bitcoin
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class BitcoinAppConfig
object BitcoinApp extends App {
  SpringApplication.run(classOf[BitcoinAppConfig]);
}