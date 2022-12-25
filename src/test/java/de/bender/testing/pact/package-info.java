/**
 * Pact is a consumer-driven contract testing framework - so to start in the proper order you should start with the
 *  {@link de.bender.testing.pact.PactConsumerTest} first. It defines the expectation from a consumer perspective and
 *  once executed will produce a contract file (in form of a JSON file in `/target/pacts`).<br/>
 *  Once that file exists - hand if over to the provider-team (the one that creates the server side) and they can put
 *  that file into their test-suite. This step is manually simulated in here by copying that file
 *  into `/resources/pact`. From there the {@link de.bender.testing.pact.PactProducerTest} will pick it up and will use
 *  it to verify the clients expectations against the actual service API.
 */
package de.bender.testing.pact;
