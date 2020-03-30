package pl.piomin.services;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import pl.piomin.services.model.Person;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;

public class SendingApp {

	private static int ix = 0;
	private static MockWebServer mockBackEnd;
	private static ObjectMapper mapper = new ObjectMapper();
	private static Random r = new Random();

	public static void main(String[] args) throws IOException {
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
		mockBackEnd.start(8082);
		System.setProperty("target.uri", "http://localhost:" + mockBackEnd.getPort());
		TestRestTemplate template = new TestRestTemplate();
		for (int i = 0; i < 20; i++) {
			new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					template.exchange("http://localhost:" + mockBackEnd.getPort() + "/persons/integration/{param}", HttpMethod.GET, null, Person[].class, ++ix);
				}
			}).start();
		}
	}
}
