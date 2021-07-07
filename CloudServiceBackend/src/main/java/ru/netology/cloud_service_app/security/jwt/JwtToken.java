package ru.netology.cloud_service_app.security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonComponent
public class JwtToken implements Serializable {
    @JsonProperty("auth-token")
    private String token;
}
