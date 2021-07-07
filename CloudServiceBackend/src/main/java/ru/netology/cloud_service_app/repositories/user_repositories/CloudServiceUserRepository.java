package ru.netology.cloud_service_app.repositories.user_repositories;

import ru.netology.cloud_service_app.entities.User;

public interface CloudServiceUserRepository {
    User findUserByLogin(String login);
}
