package com.task;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class GitHubController {

	@Autowired
	private GitHubService gitHubService;

	@GetMapping("/users/{username}/repos")
	public Mono<ResponseEntity<Object>> getUserRepositories(
			@PathVariable String username,
			@RequestHeader("Accept") String acceptHeader) {

		return gitHubService.getUserRepos(username)
				.map(repos -> ResponseEntity.ok((Object) repos))
				.onErrorResume(e -> Mono.just(ResponseEntity.status(404)
						.body(new ErrorResponse(404, e.getMessage()))));
	}

	@Data
	public static class ErrorResponse {
		private final int status;
		private final String message;
	}
}
