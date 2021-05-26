package com.licensingservice.service.client;

import com.licensingservice.domain.Organization;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service("discoveryClientBean")
public class OrganizationDiscoveryClient {

    private final DiscoveryClient discoveryClient;

    public OrganizationDiscoveryClient(DiscoveryClient discoveryClient) {

        this.discoveryClient = discoveryClient;
    }

    public Organization getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();
        List<ServiceInstance> instances =
                discoveryClient.getInstances("organization-service");
        if (instances.size() == 0) return null;
        String serviceUri = String.format
                ("%s/v1/organization/%s", instances.get(0)
                                .getUri().toString(),
                        organizationId);

        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        serviceUri, HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
