package com.licensingservice.service.client;

import com.licensingservice.domain.Organization;
import com.licensingservice.repository.OrganizationRedisRepository;
import com.licensingservice.utils.UserContextHolder;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("restClientBean")
public class OrganizationRestTemplateClient implements OrganizationClient {

    private final RestTemplate restTemplate;

    private final OrganizationRedisRepository redisRepository;

    public OrganizationRestTemplateClient(KeycloakRestTemplate restTemplate, OrganizationRedisRepository organizationRedisRepository) {
        this.restTemplate = restTemplate;
        this.redisRepository = organizationRedisRepository;
    }

    public static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    @Override
    public Organization getOrganization(String organizationId){
        logger.debug("In Licensing Service.getOrganization: {}",  UserContextHolder.getContext().getCorrelationId());

        Organization organization = checkRedisCache(organizationId);

        if (organization != null){
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}", organizationId, organization);
            return organization;
        }

        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);

        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://gateway:8072/organization/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        /*Save the record from cache*/
        organization = restExchange.getBody();
        if (organization != null) {
            cacheOrganizationObject(organization);
        }

        return restExchange.getBody();
    }

    private Organization checkRedisCache(String organizationId) {
        try {
            return redisRepository.findById(organizationId).orElse(null);
        }catch (Exception ex){
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache.  Exception {}", organizationId, ex);
            return null;
        }
    }

    private void cacheOrganizationObject(Organization organization) {
        try {
            redisRepository.save(organization);
        }catch (Exception ex){
            logger.error("Unable to cache organization {} in Redis. Exception {}", organization.getId(), ex);
        }
    }
}
