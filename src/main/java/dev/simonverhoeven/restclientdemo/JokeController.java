package dev.simonverhoeven.restclientdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@RestController
public class JokeController {
    private final RestClient restClient;
    private final JokeClient jokeClient;

    public JokeController(RestClient.Builder restClientBuilder, @Value("${jokeserviceUrl}") String jokeserviceUrl) {
        this.restClient = restClientBuilder.baseUrl(jokeserviceUrl).build();

        var factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        jokeClient = factory.createClient(JokeClient.class);
    }

    @GetMapping("/joke")
    Joke getJoke() {
        return this.restClient
                .get()
                .uri("/j/{jokeId}", "R7UfaahVfFd")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(Joke.class);
    }

    @GetMapping("/joke-entity")
    ResponseEntity<Joke> getJokeEntity() {
        return this.restClient
                .get()
                .uri("/j/{jokeId}", "MRZ0LJtHQCd")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Joke.class);
    }

    @GetMapping("/joke-bodiless")
    ResponseEntity<Void> getBodilessJoke() {
        return this.restClient
                .get()
                .uri("/j/{jokeId}", "R7UfaahVfFd")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity();
    }

    interface JokeClient {
        @GetExchange(url = "/j/{jokeId}", accept = "application/json")
        Joke getJoke(@PathVariable String jokeId);
    }

    @GetMapping("/joke-using-interface")
    Joke getJokeUsingInterface() {
        return this.jokeClient.getJoke("M7wPC5wPKBd");
    }
}
