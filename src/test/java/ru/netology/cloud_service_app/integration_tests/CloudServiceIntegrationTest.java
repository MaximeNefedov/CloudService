package ru.netology.cloud_service_app.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.models.FileData;
import ru.netology.cloud_service_app.models.NewFilename;
import ru.netology.cloud_service_app.repositories.FileRepository;
import ru.netology.cloud_service_app.security.jwt.JwtTokenHandler;
import ru.netology.cloud_service_app.utils.FileUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.netology.cloud_service_app.entities.UploadedFileStatus.ACTIVE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = CloudServiceIntegrationTest.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
public class CloudServiceIntegrationTest {
    private static final int BACKEND_PORT = 8888;
    private static final int FRONTEND_PORT = 8080;
    private static final Network NETWORK = Network.newNetwork();

    private static final String GET_ALL_FILES_ENDPOINT = "/list";
    private static final String FILE_COMMON_ENDPOINT = "/file";
    private static final String VALID_TOKEN;
    private static final String TOKEN_HEADER = "auth-token";

    static {
        val secretKey = "{secret_key_for_application_cloud_service}";
        val tokenPrefix = "Bearer ";
        VALID_TOKEN = tokenPrefix + JwtTokenHandler
                .singJwtToken(
                        "max@mail.ru", secretKey,
                        List.of(
                                new SimpleGrantedAuthority("READ"),
                                new SimpleGrantedAuthority("DELETE"),
                                new SimpleGrantedAuthority("WRITE")), 7
                );
    }

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private FileRepository fileRepository;

    private final UploadedFile uploadedFile;

    {
        val fileBody = new byte[100];
        val filename = "111.jpeg";
        val login = "max@mail.ru";
        uploadedFile = UploadedFile.builder()
                .name(filename)
                .contentType("image/jpeg")
                .hash(FileUtils.getFileHash(filename, login))
                .fullFileHash(FileUtils.getFullFileHash(filename, login, fileBody))
                .fileBody(fileBody)
                .size(fileBody.length)
                .status(ACTIVE)
                .changeTime(LocalDateTime.now())
                .user(User.builder().login(login).build())
                .build();
    }

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withNetwork(NETWORK)
            .withNetworkAliases("postgres_db")
            .withUsername("postgres")
            .withPassword("root")
            .withDatabaseName("postgres")
            .withNetwork(NETWORK);


    @Container
    public static GenericContainer<?> backendApplication = new GenericContainer<>("cloud_service_app:1.1")
            .withNetwork(NETWORK)
            .withExposedPorts(BACKEND_PORT)
            .withEnv(Map.of("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres_db:5432/postgres"))
            .dependsOn(postgresContainer);

    @Container
    public static GenericContainer<?> frontendApplication = new GenericContainer<>("front_app:1.0")
            .withNetwork(NETWORK)
            .withExposedPorts(FRONTEND_PORT)
            .dependsOn(backendApplication);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresContainer.getUsername(),
                    "spring.datasource.password=" + postgresContainer.getPassword()
            ).applyTo(applicationContext.getEnvironment());
        }
    }

    @Test
    void testPostgresContainerShouldWork() {
        Assertions.assertTrue(postgresContainer.isRunning());
    }


    @Test
    void testBackendContainerShouldWork() {
        Assertions.assertTrue(backendApplication.isRunning());
    }

    @Test
    void testFrontendContainerShouldWork() {
        Assertions.assertTrue(frontendApplication.isRunning());
    }

    private void setFileForTest() {
        fileRepository.save(uploadedFile);
    }

    private void removeTestFile() {
        fileRepository.delete(uploadedFile);
    }

    @Test
    @SneakyThrows
    public void backendApplicationGetAllFilesMethodShouldReturnOkAndValidJsonResponse() {
        setFileForTest();
        val validJsonResponse = new ObjectMapper().writeValueAsString(List.of(new FileData("111.jpeg", 100)));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("auth-token", VALID_TOKEN);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        val httpEntity = new HttpEntity<>(httpHeaders);
        val url = "http://" + backendApplication.getHost() + ":" + backendApplication.getMappedPort(BACKEND_PORT) + GET_ALL_FILES_ENDPOINT + "?limit=1";
        val responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> Assertions.assertEquals(validJsonResponse, responseEntity.getBody())
        );
        removeTestFile();
    }

    @Test
    @SneakyThrows
    public void backendApplicationEditFilenameMethodShouldReturnOk() {
        setFileForTest();
        val newFilenameJson = new ObjectMapper().writeValueAsString(new NewFilename("222.jpeg"));
        val validResponse = "Success upload";
        val httpHeaders = new HttpHeaders();
        httpHeaders.set(TOKEN_HEADER, VALID_TOKEN);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        val httpEntity = new HttpEntity<>(newFilenameJson, httpHeaders);
        val url = "http://" + backendApplication.getHost() + ":" + backendApplication.getMappedPort(BACKEND_PORT) + FILE_COMMON_ENDPOINT + "?filename=111.jpeg";
        val responseEntity = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, String.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> Assertions.assertEquals(validResponse, responseEntity.getBody())
        );
        removeTestFile();
    }

    @Test
    public void backendApplicationDeleteFileMethodShouldReturnOk() {
        setFileForTest();
        val validResponse = "Success deleted";
        val httpHeaders = new HttpHeaders();
        httpHeaders.set(TOKEN_HEADER, VALID_TOKEN);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        val httpEntity = new HttpEntity<>(httpHeaders);
        val url = "http://" + backendApplication.getHost() + ":" + backendApplication.getMappedPort(BACKEND_PORT) + FILE_COMMON_ENDPOINT + "?filename=111.jpeg";
        val responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode()),
                () -> Assertions.assertEquals(validResponse, responseEntity.getBody())
        );
        removeTestFile();
    }

    @Test
    @SneakyThrows
    public void backendApplicationSaveFileMethodShouldReturnOk() {
        setFileForTest();
        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new ClassPathResource("static/1.jpeg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(TOKEN_HEADER, VALID_TOKEN);
        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);
        val url = "http://" + backendApplication.getHost() + ":" + backendApplication.getMappedPort(BACKEND_PORT) + FILE_COMMON_ENDPOINT + "?filename=1.jpeg";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        removeTestFile();
    }

    @Test
    public void backendApplicationDownloadFileMethodShouldReturnOk() {
        setFileForTest();
        val httpHeaders = new HttpHeaders();
        httpHeaders.set(TOKEN_HEADER, VALID_TOKEN);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        val httpEntity = new HttpEntity<>(httpHeaders);
        val url = "http://" + backendApplication.getHost() + ":" + backendApplication.getMappedPort(BACKEND_PORT) + FILE_COMMON_ENDPOINT + "?filename=111.jpeg";
        val responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        removeTestFile();
    }
}
