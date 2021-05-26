package com.licensingservice.service.client;

import com.licensingservice.domain.Organization;
import org.springframework.stereotype.Service;

@Service
public interface OrganizationClient {
    public Organization getOrganization(String organizationId);
}
