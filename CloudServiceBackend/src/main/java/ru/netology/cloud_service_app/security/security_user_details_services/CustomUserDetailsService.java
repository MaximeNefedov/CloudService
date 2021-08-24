package ru.netology.cloud_service_app.security.security_user_details_services;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.netology.cloud_service_app.entities.Role;
import ru.netology.cloud_service_app.exceptions.secutity_exceptions.InvalidUserDetailsException;
import ru.netology.cloud_service_app.repositories.CloudServiceUserRepository;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final CloudServiceUserRepository cloudServiceUserRepository;

    public CustomUserDetailsService(CloudServiceUserRepository cloudServiceUserRepository) {
        this.cloudServiceUserRepository = cloudServiceUserRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String login) {
        val user = cloudServiceUserRepository.findUserByLogin(login);

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
                .map(Role::getAuthorities)
                .reduce((authorities, authorities2)
                        -> Stream.concat(authorities.stream(), authorities2.stream())
                        .collect(Collectors.toSet())).orElseThrow(() -> new InvalidUserDetailsException(msg))
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toSet());

        simpleGrantedAuthoritySet.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet()));

        return simpleGrantedAuthoritySet;
    }
}
