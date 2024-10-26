package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.AuthInfo;
import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.auth.interfaces.HasOrganization;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService {
    private final OrganizationService organizationService;
    private final UserService userService;

    private User extractUser(Map<String, Object> attrs) {
        User detachedUser = new User();
        detachedUser.setGivenName((String) attrs.get("given_name"));
        detachedUser.setFamilyName((String) attrs.get("family_name"));
        detachedUser.setUsername((String) attrs.get("preferred_username"));
        detachedUser.setEmail((String) attrs.get("email"));
        detachedUser.setEmailVerified(Boolean.TRUE.equals(attrs.get("email_verified")));
        return userService.findByUsername(detachedUser.getUsername())
                .map(user -> {
                    boolean updated = !user.getEmail().equals(detachedUser.getEmail());
                    if (!user.getFamilyName().equals(detachedUser.getFamilyName())) {
                        updated = true;
                    }
                    if (!user.getGivenName().equals(detachedUser.getGivenName())) {
                        updated = true;
                    }
                    if (updated) {
                        return userService.sync(user, detachedUser);
                    } else {
                        return user;
                    }
                })
                .orElseGet(() -> userService.sync(new User(), detachedUser));
    }

    private Organization extractOrganization(Map<String, Object> attrs, User authUser) {
        List<String> organizations = (List<String>) attrs.get("organization");
        if (organizations == null || organizations.isEmpty()) {
            throw new AccessDeniedException("Authentication does not have organization token.");
        }
        if (organizations.size() != 1) {
            throw new AccessDeniedException("More than one organization in the token is not handled.");
        }
        Organization detachedOrganization = new Organization();
        detachedOrganization.setAlias(organizations.getFirst());
        return organizationService.findByAlias(detachedOrganization.getAlias())
                .orElseGet(() -> {
                    detachedOrganization.setCreatedDate(Instant.now());
                    detachedOrganization.setCreatedBy(authUser.getUsername());
                    return organizationService.sync(new Organization(), detachedOrganization);
                });
    }

    public AuthInfo getAuthInfo() {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (jwtAuthenticationToken == null) {
            throw new AccessDeniedException("Not authenticated.");
        }
        Map<String, Object> attrs = jwtAuthenticationToken.getTokenAttributes();
        User authUser = extractUser(attrs);
        Organization authOrganization = extractOrganization(attrs, authUser);
        return new AuthInfo(authUser, authOrganization);
    }

    public void setAuthOrganization(HasOrganization entity) {
        entity.setOrganization(getAuthInfo().organization());
    }

    public void validateOwnership(HasOrganization entity) {
        if (!getAuthInfo().organization().equals(entity.getOrganization())) {
            throw new AccessDeniedException("The record does not belongs to your organization.");
        }
    }
}
