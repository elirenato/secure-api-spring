package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.constants.CacheName;
import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Cacheable(cacheNames = CacheName.USER_BY_USERNAME, unless = "#result == null")
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @CacheEvict(cacheNames = CacheName.USER_BY_USERNAME)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User sync(User user, User detachedUser) {
        BeanUtils.copyProperties(detachedUser, user, "id");
        user = userRepository.save(user);
        userRepository.flush();
        return user;
    }
}
