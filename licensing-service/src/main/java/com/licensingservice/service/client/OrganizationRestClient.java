package com.licensingservice.service.client;

import com.licensingservice.domain.Organization;
import org.springframework.stereotype.Service;

@Service("restClientBean")
public class OrganizationRestClient implements OrganizationClient {

    @Override
    public Organization getOrganization(String organizationId) {
        return null;
    }
}
