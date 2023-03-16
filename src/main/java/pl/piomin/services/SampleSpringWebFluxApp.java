package pl.piomin.services;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class SampleSpringWebFluxApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSpringWebFluxApp.class);

    public static void main(String[] args) {
        SpringApplication.run(SampleSpringWebFluxApp.class, args);
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("slow-");
        executor.initialize();
        return executor;
    }

    @Value("${target.uri}")
    private String targetUri;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(targetUri).build();
    }

    @PostConstruct
    public void init() {
        LOGGER.info("CPU: {}", Runtime.getRuntime().availableProcessors());
    }

}
