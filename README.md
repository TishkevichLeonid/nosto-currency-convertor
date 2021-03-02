# Nosto currency convertor
***
# How to build
In this application **gradle** is used for building artifact. The build result will be a **jar** archive file.
In the root of the repository you can run the commands:  
`gradle clean`  
`gradle build`

***
# How to run
After building application you will get executable jar file.  
You need to move to the folder with the jar file using the command: `cd build/libs/`  
Then run command `java -jar nosto-currency-convertor-0.0.1-SNAPSHOT.jar`  
Or you can start the application by IDE.

***
# AWS
This service is deployed on AWS

***
# API
This application hase one endpoint:  

| HTTP METHOD | PATH | REQUEST PARAMETERS |
|     :---:    |     :---:      |    :---:      |
| GET   | /convert     | source, target, amount    |  
  
Example request: http://localhost:5000/convert?source=EUR&target=USD&amount=1000  
Or you can use AWS instanse: http://nostocurrencyconvertor-env.eba-t2vbqrfm.us-east-2.elasticbeanstalk.com/convert?source=EUR&target=USD&amount=1000
***
As for request parameters, you can choose currencies from here: https://www.ecb.europa.eu/stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html  

***
# Test

You can find tests in test folder for every layer in application.
