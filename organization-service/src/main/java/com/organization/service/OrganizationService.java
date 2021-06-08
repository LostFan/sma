package com.organization.service;

import java.util.Optional;
import java.util.UUID;

import com.organization.events.source.SimpleSourceBean;
import com.organization.utils.ActionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organization.model.Organization;
import com.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository repository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    public Organization findById(String organizationId) {
        Optional<Organization> opt = repository.findById(organizationId);
        simpleSourceBean.publishOrganizationChange(
                ActionEnum.GET.name(),
                organizationId);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Organization create(Organization organization){
        organization.setId( UUID.randomUUID().toString());
        organization = repository.save(organization);
        simpleSourceBean.publishOrganizationChange(
                ActionEnum.CREATED.name(),
                organization.getId());
        return organization;

    }

    public void update(Organization organization){
        repository.save(organization);
        simpleSourceBean.publishOrganizationChange(
                ActionEnum.UPDATED.name(),
                organization.getId());
    }

    public void delete(Organization organization){
        repository.deleteById(organization.getId());
        simpleSourceBean.publishOrganizationChange(
                ActionEnum.DELETED.name(),
                organization.getId());
    }
}