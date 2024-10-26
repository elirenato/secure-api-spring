package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.AbstractIT;
import com.company.secureapispring.auth.AuthLibSpringBootTest;
import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.auth.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

@AuthLibSpringBootTest
public class UserServiceIT extends AbstractIT {

    @Autowired
    private UserService userService;

    @SpyBean
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        userRepository.save(makeUser());
        User user = userRepository.save(makeUser());

        User found = userRepository.findByUsername(user.getUsername()).orElseThrow();

        Assertions.assertEquals(user, found);
    }

    @Test
    public void testFindByUsernameIsCacheable() {
        User user = userRepository.save(makeUser());

        userService.findByUsername(user.getUsername());
        userService.findByUsername(user.getUsername());

        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(user.getUsername());
    }

    @Test
    @Transactional
    public void testSyncNewUser() {
        User detachedUser = makeUser();
        User syncResponse = userService.sync(new User(), detachedUser);

        User persistedUser = userRepository.getReferenceById(syncResponse.getId());

        Assertions.assertEquals(syncResponse, persistedUser);

        Assertions.assertEquals(detachedUser, persistedUser);
    }
}
