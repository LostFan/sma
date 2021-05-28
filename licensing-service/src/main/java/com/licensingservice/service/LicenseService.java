package com.licensingservice.service;

import com.licensingservice.config.ServiceConfig;
import com.licensingservice.domain.License;
import com.licensingservice.domain.Organization;
import com.licensingservice.repository.LicenseRepository;
import com.licensingservice.service.client.OrganizationClient;
import com.licensingservice.service.client.OrganizationFeignClient;
import com.licensingservice.service.client.factory.OrganizationClientFactory;
import com.licensingservice.service.client.factory.OrganizationClientType;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeoutException;

@Service
public class LicenseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseService.class);

    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig config;
    private final OrganizationClientFactory organizationClientFactory;
    private final OrganizationFeignClient organizationFeignClient;

    public LicenseService(MessageSource messages, LicenseRepository licenseRepository, ServiceConfig config, OrganizationClientFactory organizationClientFactory, OrganizationFeignClient organizationFeignClient) {
        this.messages = messages;
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationClientFactory = organizationClientFactory;
        this.organizationFeignClient = organizationFeignClient;
    }

    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository
                .findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(
                    String.format(messages.getMessage(
                            "license.search.error.message", null, Locale.ENGLISH),
                            licenseId, organizationId));
        }
        return license.withComment(config.getProperty());
    }

    public License getLicense(String licenseId, String organizationId, OrganizationClientType organizationClientType) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId
                (organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(String.format(
                    messages.getMessage("license.search.error.message", null, Locale.ENGLISH),
                    licenseId, organizationId));
        }
        Organization organization;
        if (OrganizationClientType.FEIGN.equals(organizationClientType)) {
            organization = organizationFeignClient.getOrganization(organizationId);
        } else {
            OrganizationClient organizationClient = organizationClientFactory.getOrganizationClient(organizationClientType);
            organization = organizationClient.getOrganization(organizationId);
        }
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }
        return license.withComment(config.getProperty());
    }

    public License createLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license) {
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId) {
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messages.getMessage(
                "license.delete.message", null, null), licenseId);
        return responseMessage;
    }

    @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    @Bulkhead(name = "bulkheadLicenseService", type = Bulkhead.Type.THREADPOOL, fallbackMethod = "buildFallBackLicenseList")
    public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private void randomlyRunLong() throws TimeoutException {
        Random rand = new Random();
        int randomNum = rand.nextInt(3) + 1;
        if (randomNum <= 3) sleep();
    }

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(1);
            throw new java.util.concurrent.TimeoutException();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private List<License> buildFallbackLicenseList(String organizationId, Throwable t) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName(
                "Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }
}
