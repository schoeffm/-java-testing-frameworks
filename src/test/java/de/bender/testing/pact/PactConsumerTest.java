package de.bender.testing.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;

/**
 * Step one of a consumer-driven contract test - the consumer side of the tests
 * <p/>
 * The consumer defines its expectation - first via a DSL approach the consumer precisely defines its input and the
 * subsequent output (see {@code createPact}).<br/>
 * The test-method (see {@code testAnything}) references (via {@code PactTestFor}-annotation) the defined expectation
 * and uses the included mock-server to actually call the fake-provider (which responds with the configured
 * expectation).<br/>
 * The client technology is actually yours ... so use whatever client code to interact with the mock-server
 * <p/>
 * Finally, after that test executed it'll provide a contract (in form of a JSON file) in `/target/pact` - that file
 * is the subsequent input for the provider side - it represents the contract!!
 */
@ExtendWith(PactConsumerTestExt.class)
public class PactConsumerTest {

    @Pact(provider="HttpBinProvider", consumer="test_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
            .given("test the anything endpoint of httpbin")
            .uponReceiving("ExampleJavaConsumerPactTest test interaction")
                .path("/anything")
                .method("GET")
            .willRespondWith()
                .status(200)
                .body(newJsonBody(o -> {
                    o.stringType("method");
                    o.stringType("origin");
                    o.stringType("url");
                    o.object("headers", h -> {
                        h.stringType("Accept-Encoding", "gzip, x-gzip, deflate");
                        h.stringType("Connection", "keep-alive");
                    });
                }).build())
            .toPact();
    }

    @Test
    @PactTestFor(providerName = "HttpBinProvider", pactVersion = PactSpecVersion.V3)
    void testAnything(MockServer mockServer) throws IOException, InterruptedException, URISyntaxException {
        // given
        var request = HttpRequest.newBuilder()
                .uri(new URI(mockServer.getUrl() + "/anything"))
                .GET()
                .build();

        // when
        var response = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.ofString());

        // then
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
    }
}
