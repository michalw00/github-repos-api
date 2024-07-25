package com.task.repos;

import lombok.Data;

import java.util.List;

@Data
public class GitHubUserRepositories {
	private List<GitHubRepository> repositories;

	public GitHubUserRepositories(List<GitHubRepository> repositories) {
		this.repositories = repositories;
	}
}
