package com.licensingservice.service;

import com.licensingservice.config.ServiceConfig;
import com.licensingservice.domain.License;
import com.licensingservice.domain.Organization;
import com.licensingservice.repository.LicenseRepository;
import com.licensingservice.service.client.OrganizationClient;
import com.licensingservice.service.client.OrganizationFeignClient;
import com.licensingservice.service.client.factory.OrganizationClientFactory;
import com.licensingservice.service.client.factory.OrganizationClientType;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class LicenseService {

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

    public License getLicense(String licenseId, String organizationId){
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

    public License getLicense(String licenseId, String organizationId, OrganizationClientType organizationClientType){
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

    public License createLicense(License license){
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license){
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }
    public String deleteLicense(String licenseId){
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messages.getMessage(
                "license.delete.message", null, null),licenseId);
        return responseMessage;
    }
}
