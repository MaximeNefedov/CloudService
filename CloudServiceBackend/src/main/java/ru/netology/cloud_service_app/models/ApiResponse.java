package ru.netology.cloud_service_app.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ApiResponse {
    private final String message;
    private final int statusCodeId;
}
