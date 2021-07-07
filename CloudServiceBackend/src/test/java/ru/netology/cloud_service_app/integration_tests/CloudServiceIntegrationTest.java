package ru.netology.cloud_service_app.integration_tests;

import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloud_service_app.entities.UploadedFile;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.repositories.file_repositories.FileRepository;
import ru.netology.cloud_service_app.security.jwt.JwtTokenHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.netology.cloud_service_app.entities.UploadedFileStatus.ACTIVE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = CloudServiceIntegrationTest.Initializer.class)
@Testcontainers
public class CloudServiceIntegrationTest {
    private static final String HOST = "http://localhost:";
    private static final int PORT = 8888;
    private static final String GET_ALL_FILES_ENDPOINT = "/list";
    private static final String FILE_COMMON_ENDPOINT = "/file";
    private static final String VALID_TOKEN;

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

//    private static final Network NETWORK = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres")
//            .withNetwork(NETWORK)
            .withUsername("postgres")
            .withPassword("root")
            .withDatabaseName("postgres");

    @Container
    public static GenericContainer<?> application = new GenericContainer<>("cloud_service_app:1.0")
//            .withNetwork(NETWORK)
            .withExposedPorts(PORT)
            .withEnv(Map.of("SPRING_DATASOURCE_URL", "jdbc:postgresql://postgres:5432/postgres"))
            .dependsOn(postgresContainer);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresContainer.getUsername(),
                    "spring.datasource.password=" + postgresContainer.getPassword()
            ).applyTo(applicationContext);
        }
    }

    @Autowired
    private FileRepository fileRepository;

    @BeforeAll
    public static void setUpPostgres() {
        postgresContainer.start();
        application.start();
    }

    private void setFilesForTest() {
        val file1 = UploadedFile.builder()
                .name("111.jpeg")
                .contentType("image/jpeg")
                .hash("vsfv4wfdsvdscsdvs")
                .fileBody(new byte[100])
                .status(ACTIVE)
                .changeTime(LocalDateTime.now())
                .id(1)
                .user(User.builder().id(1).login("max@mail.ru").build())
                .build();
        fileRepository.save(file1);
    }

    @Test
    public void test() {
//        setFilesForTest();
//        val validStatusCode = 200;
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.set("auth-token", VALID_TOKEN);
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        val httpEntity = new HttpEntity<>(httpHeaders);
////        val url = "/list?limit=3";
//        val url = HOST + application.getMappedPort(PORT) + GET_ALL_FILES_ENDPOINT + "?limit=3";
//        val responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
//        val statusCodeValue = responseEntity.getStatusCodeValue();
//        Assertions.assertEquals(validStatusCode, statusCodeValue);
    }
}
