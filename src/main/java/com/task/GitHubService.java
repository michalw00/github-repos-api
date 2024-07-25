package com.task;

import com.task.repos.GitHubRepository;
import com.task.repos.GitHubUserRepositories;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class GitHubService {
	private final WebClient webClient;

	public GitHubService(WebClient webClientBuilder) {
		this.webClient = WebClient.create("https://api.github.com");
	}

	public GitHubService(WebClient webClientBuilder, String baseUrl) {
		this.webClient = WebClient.create(baseUrl);
	}

	public Mono<GitHubUserRepositories> getUserRepos(String username) {
		return webClient.get()
				.uri("/users/{username}/repos", username)
				.header("Accept", "application/json")
				.retrieve()
				.bodyToFlux(GitHubRepository.class)
				.filter(repo -> !repo.isFork())
				.flatMap(repo -> fetchBranches(repo))
				.collectList()
				.map(GitHubUserRepositories::new)
				.onErrorResume(WebClientResponseException.NotFound.class, e ->
						Mono.error(new UserNotFoundException("User not found", e)));
	}

	private Mono<GitHubRepository> fetchBranches(GitHubRepository repo) {
		return webClient.get()
				.uri("/repos/{owner}/{repo}/branches", repo.getOwner().getLogin(), repo.getName())
				.retrieve()
				.bodyToFlux(GitHubRepository.Branch.class)
				.collectList()
				.map(branches -> {
					repo.setBranches(branches);
					return repo;
				});

	}
}
