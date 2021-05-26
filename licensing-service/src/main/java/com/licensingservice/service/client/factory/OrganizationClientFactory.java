package com.licensingservice.service.client.factory;

import com.licensingservice.service.client.OrganizationClient;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class OrganizationClientFactory {

    private final EnumMap<OrganizationClientType, OrganizationClient> clientEnumMap;

    public OrganizationClientFactory(EnumMap<OrganizationClientType, OrganizationClient> clientEnumMap) {
        this.clientEnumMap = clientEnumMap;
    }

    public OrganizationClient getOrganizationClient (OrganizationClientType clientType) {
        return clientEnumMap.get(clientType);
    }
}
