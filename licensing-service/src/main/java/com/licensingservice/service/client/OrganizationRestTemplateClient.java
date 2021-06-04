package com.licensingservice.service.client;

import com.licensingservice.domain.Organization;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("restClientBean")
public class OrganizationRestTemplateClient implements OrganizationClient {

    private final RestTemplate restTemplate;

    public OrganizationRestTemplateClient(KeycloakRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Organization getOrganization(String organizationId) {
        ResponseEntity<Organization> restExchange = restTemplate.exchange(
                "http://organization-service/v1/organization/{organizationId}",
                HttpMethod.GET, null,
                Organization.class, organizationId);
        return restExchange.getBody();
    }
}
