package ru.netology.cloud_service_app.security;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.netology.cloud_service_app.entities.Role;
import ru.netology.cloud_service_app.exceptions.InvalidUserDetailsException;
import ru.netology.cloud_service_app.exceptions.UserNotFoundException;
import ru.netology.cloud_service_app.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String login) {
        val user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь %s не найден", login)));

        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(getUserAuthorities(user.getRoles()))
                .build();
    }

    private Set<? extends GrantedAuthority> getUserAuthorities(Set<Role> roles) {
        val msg = "Authorities are invalid";
        if (roles == null || roles.isEmpty()) throw new InvalidUserDetailsException(msg);
        Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = roles.stream()
                .flatMap(role -> role.getAuthorities().stream())
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toSet());

        simpleGrantedAuthoritySet.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet()));
        return simpleGrantedAuthoritySet;
    }
}
