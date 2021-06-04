package ru.netology.cloud_service_app.services;

import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloud_service_app.entities.Role;
import ru.netology.cloud_service_app.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        val user = userRepository.findByLogin(login).orElseThrow(IllegalArgumentException::new);
        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(getUserAuthorities(user.getRoles()))
                .build();
    }

    private Set<? extends GrantedAuthority> getUserAuthorities(Set<Role> roles) {
        Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = roles.stream()
                .map(Role::getAuthorities)
                .reduce((authorities, authorities2)
                        -> Stream.concat(authorities.stream(), authorities2.stream())
                        .collect(Collectors.toSet())).orElseThrow(IllegalArgumentException::new)
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toSet());

        simpleGrantedAuthoritySet.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet()));

        return simpleGrantedAuthoritySet;
    }
}
