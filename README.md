#note the server ports in application.yml; change as needed

#download and run zipkin 

#launch server; letting the service know how to reach the Weather API

java -jar target/headlines-0.0.1-SNAPSHOT.jar --weatherapp.url=http://localhost:8080/weather/v1/today/

#visit http://localhost:8080/swagger-ui.html
