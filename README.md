# GitHub Repositories API

## Description
Fetches non-forked repositories of a given GitHub user along with branch details and the last commit SHA.

## How to Run

### Prerequisites
- Java 11 or higher
- Maven or Gradle

### Build and Run
1. Clone the repository:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. Build the project:
    ```sh
    ./mvnw clean install
    ```

3. Run the application:
    ```sh
    ./mvnw spring-boot:run
    ```

### Usage
Send a GET request to:

GET /users/{username}/repos

Header: Accept: application/json

## Example
```sh
curl -H "Accept: application/json" http://localhost:8080/users/michalw00/repos