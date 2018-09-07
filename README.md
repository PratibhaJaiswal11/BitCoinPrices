# Bitcoin Price Tracke

Bitcoin Price Tracke allow you to track the price movement of Bitcion. Following are the features:
  - Last week,month, year prices
  - Price on a specific date
  - Moving/Rolling Average for a specific timerange
  - Simple algorithm indicating buy, sell or hold
  
### Tech
  - /prices?type=last_week
        (available types are last_week, last_month, last_year)
  - /price?date=2018-09-01
        (Dates should be yyyy-mm-dd format)
  - /moving_average?from_date=2018-08-01&to_date=2018-09-01
  - /make_decision?last_x_days=50
        (Number of days which algorithm take care into account for moving average, 50 considered better)
  
### Installation
clone the project and run BitcoinApp.scala as scala application, hit the apis at http://localhost:8080