package dev.simonverhoeven.restclientdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
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

    @GetMapping("/joke-not-found")
    String getJokeNotFound() {
        return this.restClient
                .get()
                .uri("/j/{jokeId}", "UNKNOWN")
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
                    throw new PunException();
                }))
                .body(String.class);
    }

    static class PunException extends RuntimeException {
        public PunException() {
            super("Sorry, your joke could not be found. Here's a consolation one: 'If a pig loses its voice, does it become disgruntled?'");
        }
    }

    @GetMapping("/joke-exchange")
    String getJokeExchange() {
        return this.restClient
                .get()
                .uri("/j/{jokeId}", "UNKNOWN")
                .accept(MediaType.APPLICATION_JSON)
                .exchange((clientRequest, clientResponse) ->  {
                    // some criteria to determine our joke's too punny
                    return "Singing in the shower is fun until you get soap in your mouth. Then it's a soap opera";
                });
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
