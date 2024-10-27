package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.AbstractIT;
import com.company.secureapispring.auth.AuthInfo;
import com.company.secureapispring.auth.AuthLibSpringBootTest;
import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.auth.interfaces.HasOrganization;
import com.company.secureapispring.auth.repositories.OrganizationRepository;
import com.company.secureapispring.auth.repositories.UserRepository;
import com.company.secureapispring.auth.utils.TestJWTUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;
import java.util.Collections;

@AuthLibSpringBootTest
public class AuthServiceIT extends AbstractIT {

    @Autowired
    private AuthService authService;

    @SpyBean
    private UserRepository userRepository;

    @SpyBean
    private OrganizationRepository organizationRepository;

    private AuthInfo mockAuthenticationContext() {
        User user = makeUser();
        Organization organization = makeOrganization();

        Jwt jwt = TestJWTUtils.createJwt(user, Collections.singletonList(organization.getAlias()), "ROLE_ADMIN");
        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);

        SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);

        return new AuthInfo(user, organization);
    }

    @Test
    public void testGetAuthInfo() {
        AuthInfo expectedAuthInfo = mockAuthenticationContext();

        AuthInfo authInfo = authService.getAuthInfo();

        Assertions.assertEquals(expectedAuthInfo, authInfo);

        Assertions.assertNotNull(authInfo.user().getId());
        Assertions.assertNotNull(authInfo.organization().getId());
    }

    @Test
    public void testGetAuthInfoWhenUserOrgAlreadyExistsAndInfoChanged() {
        AuthInfo expectedAuthInfo = mockAuthenticationContext();

        // make a new user to change the values that are going to be updated
        User user = makeUser();
        user.setUsername(expectedAuthInfo.user().getUsername());
        userRepository.save(user);

        authService.getAuthInfo();

        // when there are updates on the user info, the user on the database should be sync
        Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any());
        Mockito.verify(organizationRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testGetAuthInfoWhenUserOrgAlreadyExistsButInfoNotChanged() {
        AuthInfo expectedAuthInfo = mockAuthenticationContext();

        userRepository.save(expectedAuthInfo.user());
        organizationRepository.save(expectedAuthInfo.organization());

        authService.getAuthInfo();

        // the only calls to save should be the previous calls
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(organizationRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testGetAuthInfoWhenUnauthenticated() {
        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> authService.getAuthInfo()
        );
    }

    @Test
    public void testGetAuthInfoTheIsNoOrganizationInTheToken() {
        User user = makeUser();

        // no organization
        Jwt jwt = TestJWTUtils.createJwt(user, null, "ROLE_ADMIN");
        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> authService.getAuthInfo()
        );
    }

    @Test
    public void testGetAuthInfoTheIsMoreThanOrganizationInTheToken() {
        User user = makeUser();

        // more than 1 organization
        Jwt jwt = TestJWTUtils.createJwt(
                user,
                Arrays.asList(makeOrganization().getAlias(), makeOrganization().getAlias()),
                "ROLE_ADMIN"
        );
        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt);

        SecurityContextHolder.getContext().setAuthentication(jwtAuthToken);

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> authService.getAuthInfo()
        );
    }

    private HasOrganization mockImplOfHasOrganization() {
        return new HasOrganization() {
            private Organization organization;
            @Override
            public void setOrganization(Organization entity) {
                this.organization = entity;
            }
            @Override
            public Organization getOrganization() {
                return this.organization;
            }
        };
    }

    @Test
    public void testSetAuthOrganization() {
        HasOrganization belongsToOrg = mockImplOfHasOrganization();

        AuthInfo expectedAuthInfo = mockAuthenticationContext();

        authService.setAuthOrganization(belongsToOrg);

        Assertions.assertEquals(expectedAuthInfo.organization(), belongsToOrg.getOrganization());
    }

    @Test
    public void testValidateOwnership() {
        HasOrganization belongsToOrg = mockImplOfHasOrganization();
        belongsToOrg.setOrganization(makeOrganization());

        mockAuthenticationContext();

        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> authService.validateOwnership(belongsToOrg)
        );
    }
}
