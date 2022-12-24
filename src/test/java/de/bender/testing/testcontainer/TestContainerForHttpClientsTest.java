package de.bender.testing.testcontainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class TestContainerForHttpClientsTest {

    @Container
    public GenericContainer httpBin = new GenericContainer(DockerImageName.parse("kennethreitz/httpbin")).withExposedPorts(80);

    HttpClient httpClient;

    @BeforeEach
    public void setUp() {
        httpClient = HttpClient.newBuilder().build();
    }

    @Test
    @DisplayName("You can interact with an external system that is started/stopped along your unit-test")
    void callingAnExternalSystemUsingTestcontainers() throws URISyntaxException, IOException, InterruptedException {
        // given
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("http://%s:%s", httpBin.getHost(), httpBin.getFirstMappedPort()) + "/anything"))
                .GET()
                .build();

        // when
        HttpResponse<String> send = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // then
        assertThat(send.statusCode()).isEqualTo(200);
        assertThat(send.body())
                .contains("\"method\": \"GET\"")
                .containsPattern(" \"User-Agent\": \"Java-http-client/.*\"");

    }
}
