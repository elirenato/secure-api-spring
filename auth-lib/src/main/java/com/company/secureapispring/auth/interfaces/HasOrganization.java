package com.company.secureapispring.auth.interfaces;

import com.company.secureapispring.auth.entities.Organization;

public interface HasOrganization {
    void setOrganization(Organization entity);
    Organization getOrganization();
}
