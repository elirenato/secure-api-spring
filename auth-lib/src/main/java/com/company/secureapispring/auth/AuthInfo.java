package com.company.secureapispring.auth;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;

public record AuthInfo(User user, Organization organization) {
}
