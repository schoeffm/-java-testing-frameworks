package de.bender.testing.pact;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Provider("HttpBinProvider")        // references one specific provider-config in the pact file
@PactFolder("pacts")                // default-folder in `/target` (gets filled by consumer-test)
@Testcontainers                     // used to simulate an actual server to verify the contract agains
public class PactProducerTest {

    @Container
    public GenericContainer httpBin = new GenericContainer(DockerImageName
            .parse("kennethreitz/httpbin")).withExposedPorts(80);

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget(httpBin.getHost(), httpBin.getFirstMappedPort()));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("test the anything endpoint of httpbin")
    void toProductsExistState() {
    }

}
