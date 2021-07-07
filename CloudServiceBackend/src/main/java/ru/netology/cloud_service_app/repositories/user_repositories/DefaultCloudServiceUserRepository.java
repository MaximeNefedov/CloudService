package ru.netology.cloud_service_app.repositories.user_repositories;

import org.springframework.stereotype.Repository;
import ru.netology.cloud_service_app.entities.User;
import ru.netology.cloud_service_app.exceptions.UserNotFoundException;

@Repository
public class DefaultCloudServiceUserRepository implements CloudServiceUserRepository {
    private final UserRepository userRepository;

    public DefaultCloudServiceUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("Пользователь под именем " + login + " не найден"));
    }
}
