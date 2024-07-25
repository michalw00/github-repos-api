package com.task;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class GitHubControllerTest {
	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private GitHubService gitHubService;

	@LocalServerPort
	private int port;

	private static final int WIREMOCK_PORT = 8089;
	private static WireMockServer wireMockServer;

	@BeforeAll
	static void startWireMockServer() {
		wireMockServer = new WireMockServer(WireMockConfiguration
				.wireMockConfig()
				.port(WIREMOCK_PORT));
		wireMockServer.start();
		WireMock.configureFor("localhost", WIREMOCK_PORT);
	}

	@AfterAll
	static void stopWireMockServer() {
		wireMockServer.stop();
	}

	@BeforeEach
	void setUp() {
		WebClient webClient = WebClient.create("http://localhost:" + WIREMOCK_PORT);
		gitHubService = new GitHubService(webClient);

		stubFor(get(urlPathEqualTo("/users/testuser/repos"))
				.withHeader("Accept", equalTo("application/json"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("[{\"name\":\"repo1\",\"owner\":{\"login\":\"testuser\"},\"fork\":false}]")));

		stubFor(get(urlPathEqualTo("/repos/testuser/repo1/branches"))
				.withHeader("Accept", equalTo("application/json"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("[{\"name\":\"main\",\"commit\":{\"sha\":\"123456\"}}]")));
	}

	@Test
	public void getUserRepos() throws IOException, InterruptedException {

		webTestClient.get().uri("users/testuser/repos")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.repositories[0].name").isEqualTo("repo1")
				.jsonPath("$.repositories[0].branches[0].name").isEqualTo("main")
				.jsonPath("$.repositories[0].branches[0].commit.sha").isEqualTo("123456");
	}

}
