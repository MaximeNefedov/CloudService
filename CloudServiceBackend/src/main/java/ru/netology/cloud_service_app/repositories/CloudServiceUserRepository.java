package ru.netology.cloud_service_app.repositories;

import ru.netology.cloud_service_app.entities.User;

public interface CloudServiceUserRepository {
    User findUserByLogin(String login);
}
