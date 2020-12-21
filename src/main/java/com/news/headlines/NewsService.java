package com.news.headlines;

import java.util.concurrent.CompletableFuture;

import com.news.jobs.grpc.server.Criteria;
import com.news.jobs.grpc.server.Job;
import com.news.jobs.grpc.server.JobsServiceGrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
@EnableAsync
public class NewsService {
    private static Logger log = LoggerFactory.getLogger(NewsService.class);

    @Autowired
    private RestTemplate restTemplate;

    @GrpcClient("local-grpc-server") 
    private JobsServiceGrpc.JobsServiceBlockingStub jobsServiceBlockingStub;

    @GrpcClient("local-grpc-server") 
    private JobsServiceGrpc.JobsServiceStub jobsStub;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    //override with java -jar target/headlines-0.0.1-SNAPSHOT.jar --weatherapp.url=http://localhost:8080/weather/v1/today/
    //mvn spring-boot:run -Drun.arguments=--spring.main.banner-mode=off,--customArgument=custom
    @Value( "${weatherapp.url:none}" )
    private String weatherUrl;

    @Async("asyncExecutor")
    public CompletableFuture<String> getAsyncCityWeather(String city) throws InterruptedException {
        log.info("exec starts");

        String data = restTemplate.getForObject(weatherUrl + city, String.class);

        log.info("completed {}", data);
        return CompletableFuture.completedFuture(data);
    }

    public String getCityWeather(String city) {
        ResponseEntity<String> response = restTemplate.getForEntity(weatherUrl + city, String.class);
        return response.getBody();
    }

    public String getJobs(String atLocation) {

        Criteria request = Criteria.newBuilder().setLocation(atLocation).build();
        return jobsServiceBlockingStub.getTopJob(request).getDescription(); 
    }

    public void getOpenJobs(String atLocation) {
        Criteria request = Criteria.newBuilder()
            .setLocation(atLocation)
            .setChunkSize(10)
            .build();
        
        StreamObserver<Job> responseObserver = new StreamObserver<Job>(){

            @Override
            public void onNext(Job value) {
                log.info(value.getDescription());
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage(), t);
            }

            @Override
            public void onCompleted() {
                log.info("Processing completed");
            }
        }; 
        jobsStub.getOpenJobs(request, responseObserver);
    } 
  
}
