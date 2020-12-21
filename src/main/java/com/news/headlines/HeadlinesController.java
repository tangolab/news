package com.news.headlines;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/news/v1")
@RestController
public class HeadlinesController {

    private static Logger log = LoggerFactory.getLogger(HeadlinesController.class);
 
    @Autowired
    private NewsService service;
    
    @GetMapping("/headlines")
    public List<Headlines> home() {
        return Arrays.asList(
            new Headlines("RestTemplate rules", "@user1"),
            new Headlines("WebClient is better", "@user2"), 
            new Headlines("OK, both are useful", "@user1"));
    }

    @GetMapping("/weather/sync")
    public List<String> newssync(){
        return Arrays.asList(
            service.getCityWeather("New York"),
            service.getCityWeather("Philadelphia"),
            service.getCityWeather("Montreal"));
    }

    @GetMapping("/weather")
    public List<String> news() throws InterruptedException, ExecutionException {
        CompletableFuture<String> nyData = service.getAsyncCityWeather("New York");
        CompletableFuture<String> philaData = service.getAsyncCityWeather("Philadelphia");
        CompletableFuture<String> montrealData = service.getAsyncCityWeather("Montreal");


        log.info("waiting...");
        CompletableFuture.allOf(nyData,philaData,montrealData).join();
        log.info("done");

        return Arrays.asList(nyData.get(),philaData.get(),montrealData.get());
    }

    @GetMapping("/jobs")
    public String jobs(){
        return service.getJobs("NYC");
    }

    @GetMapping("/openjobs")
    public void openJobs(){
        service.getOpenJobs("NYC");
    }
}