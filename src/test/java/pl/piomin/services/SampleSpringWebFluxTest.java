package pl.piomin.services;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import pl.piomin.services.model.Person;
import reactor.core.publisher.Flux;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SampleSpringWebFluxTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSpringWebFluxTest.class);
    final WebClient client = WebClient.builder().baseUrl("http://localhost:8080").build();

    @Test
    public void testFindPersonsJson() {
        Flux<Person> persons = client.get().uri("/persons/json").retrieve().bodyToFlux(Person.class);
        persons.subscribe(person -> LOGGER.info("Get: {}", person));
    }

    @Test
    public void testFindPersonsStream() {
        Flux<Person> persons = client.get().uri("/persons/stream").retrieve().bodyToFlux(Person.class);
        persons.subscribe(person -> LOGGER.info("Get: {}", person));
    }

}
