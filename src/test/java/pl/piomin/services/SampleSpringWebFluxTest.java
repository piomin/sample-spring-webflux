package pl.piomin.services;

import java.util.concurrent.TimeoutException;

import net.jodah.concurrentunit.Waiter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.piomin.services.model.Person;
import reactor.core.publisher.Flux;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SampleSpringWebFluxTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleSpringWebFluxTest.class);
    final WebClient client = WebClient.builder().baseUrl("http://localhost:8080").build();

    @Test
    public void testFindPersonsJson() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        Flux<Person> persons = client.get().uri("/persons/json").retrieve().bodyToFlux(Person.class);
        persons.subscribe(person -> {
            waiter.assertNotNull(person);
            LOGGER.info("Client subscribes: {}", person);
            waiter.resume();
        });
        waiter.await(3000, 9);
    }

    @Test
    public void testFindPersonsStream() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        Flux<Person> persons = client.get().uri("/persons/stream").retrieve().bodyToFlux(Person.class);
        persons.subscribe(person -> {
            LOGGER.info("Client subscribes: {}", person);
            waiter.assertNotNull(person);
            waiter.resume();
        });
        waiter.await(3000, 9);
    }

    @Test
    public void testFindPersonsStreamBackPressure() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        Flux<Person> persons = client.get().uri("/persons/stream/back-pressure").retrieve().bodyToFlux(Person.class);
        persons.map(this::doSomeSlowWork).subscribe(person -> {
            waiter.assertNotNull(person);
            LOGGER.info("Client subscribes: {}", person);
            waiter.resume();
        });
        waiter.await(3000, 9);
    }

    private Person doSomeSlowWork(Person person) {
        try {
            Thread.sleep(90);
        }
        catch (InterruptedException e) { }
        return person;
    }
}
