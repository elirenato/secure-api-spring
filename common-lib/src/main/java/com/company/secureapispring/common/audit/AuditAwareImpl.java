package com.company.secureapispring.common.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Optional;

public class AuditAwareImpl implements AuditorAware<String> {
  @Override
  public Optional<String> getCurrentAuditor() {
    JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    if (jwtAuthenticationToken == null) {
      return Optional.empty();
    }
    Map<String, Object> attrs = jwtAuthenticationToken.getTokenAttributes();
    return Optional.of((String) attrs.get("preferred_username"));
  }
}
