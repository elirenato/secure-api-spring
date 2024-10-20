package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User syncUser(User user, User detachedUser) {
        BeanUtils.copyProperties(detachedUser, user);
        user = userRepository.save(user);
        userRepository.flush();
        return user;
    }
}
