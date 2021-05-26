package com.licensingservice.service.client.factory;

import com.licensingservice.service.client.OrganizationClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;

@Configuration
public class OrganizationClientConfig {

    @Qualifier("discoveryClientBean")
    private final OrganizationClient discoveryClientBean;
    @Qualifier("restClientBean")
    private final OrganizationClient restClientBean;

    public OrganizationClientConfig(OrganizationClient discoveryClientBean, OrganizationClient restClientBean) {
        this.discoveryClientBean = discoveryClientBean;
        this.restClientBean = restClientBean;
    }

    @Bean
    public EnumMap<OrganizationClientType, OrganizationClient> getOrganizationServerMap() {
        EnumMap<OrganizationClientType, OrganizationClient> map = new EnumMap<>(OrganizationClientType.class);
        map.put(OrganizationClientType.DISCOVERY, discoveryClientBean);
        map.put(OrganizationClientType.REST_TEMPLATE, restClientBean);
        return map;
    }
}
