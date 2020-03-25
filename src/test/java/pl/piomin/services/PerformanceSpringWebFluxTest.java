package pl.piomin.services;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestRule;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MockServerContainer;
import pl.piomin.services.model.Person;

import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PerformanceSpringWebFluxTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceSpringWebFluxTest.class);

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();
    @Rule
    public MockServerContainer mockServer = new MockServerContainer();

    @Autowired
    TestRestTemplate template;

    @Before
    public void init() {
        System.setProperty("target.uri", "http://192.168.99.100:" + mockServer.getServerPort());
        MockServerClient client = new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort());
        client.when(HttpRequest.request().withPath("/slow"))
                .respond(response()
                        .withBody("SLOW")
                        .withDelay(TimeUnit.MILLISECONDS, 200));
//                        .withHeader("Content-Type", "application/json"));
    }

    @Test
    @BenchmarkOptions(warmupRounds = 0, concurrency = 20, benchmarkRounds = 100)
    public void testPerformance() {
        ResponseEntity<Person[]> r = template.exchange("/persons/integration", HttpMethod.GET, null, Person[].class);
    }
}
