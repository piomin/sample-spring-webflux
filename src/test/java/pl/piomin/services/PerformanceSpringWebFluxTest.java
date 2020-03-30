package pl.piomin.services;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.piomin.services.model.Person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class PerformanceSpringWebFluxTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceSpringWebFluxTest.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static Random r = new Random();
    private static int i = 0;

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();
    @Autowired
    TestRestTemplate template;

    public static MockWebServer mockBackEnd;

    @BeforeClass
    public static void setUp() throws IOException {
        final Dispatcher dispatcher = new Dispatcher() {

            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) throws InterruptedException {
                String pathParam = recordedRequest.getPath().replaceAll("/slow/", "");
                List<Person> personsPart2 = List.of(new Person(r.nextInt(100000), "Name" + pathParam, "Surname" + pathParam, r.nextInt(100)),
                        new Person(r.nextInt(100000), "Name" + pathParam, "Surname" + pathParam, r.nextInt(100)),
                        new Person(r.nextInt(100000), "Name" + pathParam, "Surname" + pathParam, r.nextInt(100)));
                try {
                    return new MockResponse()
                            .setResponseCode(200)
                            .setBody(mapper.writeValueAsString(personsPart2))
                            .setHeader("Content-Type", "application/json")
                            .setBodyDelay(200, TimeUnit.MILLISECONDS);
                }
                catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        mockBackEnd = new MockWebServer();
        mockBackEnd.setDispatcher(dispatcher);
        mockBackEnd.start();
        System.setProperty("target.uri", "http://localhost:" + mockBackEnd.getPort());
    }

    @AfterClass
    public static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @BenchmarkOptions(warmupRounds = 10, concurrency = 50, benchmarkRounds = 300)
    public void testPerformance() throws InterruptedException {
        ResponseEntity<Person[]> r = template.exchange("/persons/integration/{param}", HttpMethod.GET, null, Person[].class, ++i);
        Assert.assertEquals(200, r.getStatusCodeValue());
        Assert.assertNotNull(r.getBody());
        Assert.assertEquals(6, r.getBody().length);
    }

    @Test
    @BenchmarkOptions(warmupRounds = 10, concurrency = 50, benchmarkRounds = 30000)
    public void testPerformanceInDifferentPool() throws InterruptedException {
        ResponseEntity<Person[]> r = template.exchange("/persons/integration-in-different-pool/{param}", HttpMethod.GET, null, Person[].class, ++i);
        Assert.assertEquals(200, r.getStatusCodeValue());
        Assert.assertNotNull(r.getBody());
        Assert.assertEquals(6, r.getBody().length);
    }


}
