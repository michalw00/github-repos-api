package com.task.repos;

import lombok.Data;
import java.util.List;

@Data
public class GitHubRepository {
	private String name;
	private Owner owner;
	private boolean fork;
	private List<Branch> branches;

	@Data
	public static class Owner {
		private String login;
	}

	@Data
	public static class Branch {
		private String name;
		private Commit commit;
	}

	@Data
	public static class Commit {
		private String sha;
	}
}
