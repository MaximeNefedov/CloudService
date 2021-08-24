//package ru.netology.cloud_service_app.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import lombok.val;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//import ru.netology.cloud_service_app.entities.UploadedFile;
//import ru.netology.cloud_service_app.models.FileData;
//import ru.netology.cloud_service_app.models.NewFilename;
//import ru.netology.cloud_service_app.security.jwt.JwtTokenHandler;
//import ru.netology.cloud_service_app.services.FileService;
//
//import java.util.*;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(FileController.class)
//@ContextConfiguration(classes = FileControllerTestConfig.class)
//class FileControllerTest {
//    private static final String GET_ALL_FILES_ENDPOINT = "/list";
//    private static final String FILE_COMMON_ENDPOINT = "/file";
//    private static final String USERNAME = "max@mail.ru";
//    private static final String TOKEN_HEADER = "auth-token";
//    private static final String VALID_TOKEN;
//    private static final String INVALID_TOKEN = "Bearer token";
//    private static final String VALID_FILENAME = "111.jpeg";
//    private static final String INVALID_FILENAME = "111a";
//
//    static {
//        val secretKey = "{secret_key_for_application_cloud_service}";
//        val tokenPrefix = "Bearer ";
//        VALID_TOKEN = tokenPrefix + JwtTokenHandler
//                .singJwtToken(
//                        USERNAME, secretKey,
//                        List.of(
//                                new SimpleGrantedAuthority("READ"),
//                                new SimpleGrantedAuthority("DELETE"),
//                                new SimpleGrantedAuthority("WRITE")), 7
//                );
//    }
//
//    @MockBean
//    FileService fileService;
//
//    @Autowired
//    WebApplicationContext webApplicationContext;
//
//    MockMvc mockMvc;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
//    }
//
//    // Сохранение файла
//
//    @Test
//    @SneakyThrows
//    void saveFileShouldReturnOk() {
//        Map<String, String> contentTypeParams = new HashMap<>();
//        contentTypeParams.put("boundary", "265001916915724");
//        val mediaType = new MediaType("multipart", "form-data", contentTypeParams);
//        mockMvc.perform(multipart(FILE_COMMON_ENDPOINT).file("file", new byte[100])
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .param("filename", VALID_FILENAME)
//                .accept(MULTIPART_FORM_DATA)
//                .contentType(mediaType))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    void saveFileShouldReturnBadForbidden() {
//        mockMvc.perform(post(FILE_COMMON_ENDPOINT)
//                .header(TOKEN_HEADER, INVALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @SneakyThrows
//    void saveFileShouldReturnBadRequest() {
//        mockMvc.perform(post(FILE_COMMON_ENDPOINT)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    // Вывод всех файлов
//
//    @Test
//    @SneakyThrows
//    void getAllFilesShouldReturnOk() {
//        int limit = 3;
//        mockMvc.perform(get(GET_ALL_FILES_ENDPOINT)
//                .param("limit", String.valueOf(limit))
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    void getAllFilesShouldReturnBadRequest() {
//        int limit = 0;
//        mockMvc.perform(get(GET_ALL_FILES_ENDPOINT)
//                .param("limit", String.valueOf(limit))
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @SneakyThrows
//    void getAllFilesShouldReturnForbidden() {
//        int limit = 1;
//        mockMvc.perform(get(GET_ALL_FILES_ENDPOINT)
//                .param("limit", String.valueOf(limit))
//                .header(TOKEN_HEADER, INVALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    // Загрузка файла из хранилища
//
//    @Test
//    @SneakyThrows
//    void downloadFileShouldReturnOk() {
//        when(fileService.downloadFile(VALID_FILENAME, USERNAME))
//                .thenReturn(UploadedFile.builder().fileBody(new byte[1000]).contentType("image/jpeg").build());
//        mockMvc.perform(get(FILE_COMMON_ENDPOINT)
//                .param("filename", VALID_FILENAME)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    void downloadFileShouldReturnForbidden() {
//        mockMvc.perform(get(FILE_COMMON_ENDPOINT)
//                .param("filename", VALID_FILENAME)
//                .header(TOKEN_HEADER, INVALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @SneakyThrows
//    void downloadFileShouldReturnBadRequest() {
//        mockMvc.perform(get(FILE_COMMON_ENDPOINT)
//                .param("filename", INVALID_FILENAME)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    // Изменение файла
//
//    @Test
//    @SneakyThrows
//    void editFilenameShouldReturnOk() {
//        val validNewFilename = "222.jpeg";
//        mockMvc.perform(put(FILE_COMMON_ENDPOINT)
//                .param("filename", VALID_FILENAME)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(new NewFilename(validNewFilename)))
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    void editFilenameShouldReturnBadRequest() {
//        val invalidNewFilename = "222jpeg";
//        val validNewFilename = "222.jpeg";
//        mockMvc.perform(put(FILE_COMMON_ENDPOINT)
//                .param("filename", VALID_FILENAME)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(new NewFilename(invalidNewFilename)))
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        mockMvc.perform(put(FILE_COMMON_ENDPOINT)
//                .param("filename", INVALID_FILENAME)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(new NewFilename(validNewFilename)))
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    @SneakyThrows
//    void editFilenameShouldReturnForbidden() {
//        val validNewFilename = "222.jpeg";
//        mockMvc.perform(put(FILE_COMMON_ENDPOINT)
//                .param("filename", VALID_FILENAME)
//                .header(TOKEN_HEADER, INVALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(new NewFilename(validNewFilename)))
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    // Удаление файла
//
//    @Test
//    @SneakyThrows
//    void deleteFileShouldReturnOk() {
//        mockMvc.perform(delete(FILE_COMMON_ENDPOINT)
//                .param("filename", VALID_FILENAME)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    void deleteFileShouldReturnForbidden() {
//        mockMvc.perform(delete(FILE_COMMON_ENDPOINT)
//                .param("filename", VALID_FILENAME)
//                .header(TOKEN_HEADER, INVALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    @SneakyThrows
//    void deleteFileShouldReturnBadRequest() {
//        mockMvc.perform(delete(FILE_COMMON_ENDPOINT)
//                .param("filename", INVALID_FILENAME)
//                .header(TOKEN_HEADER, VALID_TOKEN)
//                .accept(APPLICATION_JSON)
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//}