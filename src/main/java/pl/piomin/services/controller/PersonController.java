package pl.piomin.services.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piomin.services.model.Person;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Stream;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    @GetMapping("/json")
    public Flux<Person> findPersonsJson() {
        return Flux.fromStream(this::prepareStream).delayElements(Duration.ofMillis(100))
                .doOnNext(person -> LOGGER.info("Send: {}", person));
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Person> findPersonsStream() {
        return Flux.fromStream(this::prepareStream).delayElements(Duration.ofMillis(100))
                .doOnNext(person -> LOGGER.info("Send: {}", person));
    }

    private Stream<Person> prepareStream() {
        return Stream.of(
            new Person(1, "Name01", "Surname01", 11),
            new Person(2, "Name02", "Surname02", 22),
            new Person(3, "Name03", "Surname03", 33),
            new Person(4, "Name04", "Surname04", 44),
            new Person(5, "Name05", "Surname05", 55),
            new Person(6, "Name06", "Surname06", 66),
            new Person(7, "Name07", "Surname07", 77),
            new Person(8, "Name08", "Surname08", 88),
            new Person(9, "Name09", "Surname09", 99)
        );
    }
}
