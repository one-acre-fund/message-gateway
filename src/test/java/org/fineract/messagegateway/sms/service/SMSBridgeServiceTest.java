package org.fineract.messagegateway.sms.service;

import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.ApiParameterError;
import org.fineract.messagegateway.service.SecurityService;
import org.fineract.messagegateway.sms.domain.Country;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.exception.CountryNotFoundException;
import org.fineract.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.fineract.messagegateway.sms.repository.SMSBridgeRepository;
import org.fineract.messagegateway.sms.serialization.SmsBridgeSerializer;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.fineract.messagegateway.tenants.exception.TenantNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SMSBridgeService}
 *
 * @author amy.muhimpundu
 */
@ExtendWith(MockitoExtension.class)
class SMSBridgeServiceTest {

    @Mock
    private SMSBridgeRepository smsBridgeRepository;

    @Mock
    private SmsBridgeSerializer smsBridgeSerializer;

    @Mock
    private SecurityService securityService;

    @Mock
    private CountryService countryService;


    private SMSBridgeService smsBridgeService;

    private Tenant tenant = new Tenant("default", "123", "Default Tenant");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        smsBridgeService = new SMSBridgeService(smsBridgeRepository, smsBridgeSerializer, securityService,countryService );
    }

    @Test
    @DisplayName("Test createSmsBridgeConfig with valid data, should return bridgeId")
    void createSmsBridgeConfig_withValidData_returnsBridgeId() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        String json = "{\"countryId\": 1, \"provider\": \"TestProvider\"}";
        SMSBridge smsBridge = new SMSBridge();
        smsBridge.setCountryId(1L);
        Country country = new Country();
        SMSBridge savedSmsBridge = mock(SMSBridge.class);

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeSerializer.validateCreate(json, tenant)).thenReturn(smsBridge);
        when(countryService.retrieveCountryById(tenantId, tenantAppKey, 1L)).thenReturn(country);
        when(smsBridgeRepository.save(smsBridge)).thenReturn(savedSmsBridge);
        when(savedSmsBridge.getId()).thenReturn(1L);

        Long bridgeId = smsBridgeService.createSmsBridgeConfig(tenantId, tenantAppKey, json);

        assertNotNull(bridgeId);
        assertEquals(1L, bridgeId);
    }

    @Test
    @DisplayName("Test createSmsBridgeConfig with invalid tenant, should throw exception")
    void createSmsBridgeConfig_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        String json = "{\"countryId\": 1, \"provider\": \"TestProvider\"}";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            smsBridgeService.createSmsBridgeConfig(tenantId, tenantAppKey, json);
        });
    }

    @Test
    @DisplayName("Test createSmsBridgeConfig with invalid country, should throw exception")
    void createSmsBridgeConfig_withInvalidCountry_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        String json = "{\"countryId\": 999, \"provider\": \"TestProvider\"}";
        SMSBridge smsBridge = new SMSBridge();
        smsBridge.setCountryId(999L);

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeSerializer.validateCreate(json, tenant)).thenReturn(smsBridge);
        when(countryService.retrieveCountryById(tenantId, tenantAppKey, 999L)).thenThrow(new CountryNotFoundException(999L));

        assertThrows(CountryNotFoundException.class, () -> {
            smsBridgeService.createSmsBridgeConfig(tenantId, tenantAppKey, json);
        });
    }

    @Test
    @DisplayName("Test createSmsBridgeConfig with invalid json, should throw exception")
    void createSmsBridgeConfig_withInvalidJson_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        String json = "{\"countryId\": \"\", \"provider\": \"\"}";
        List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        dataValidationErrors.add(ApiParameterError.parameterError("", "", null));

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeSerializer.validateCreate(json, tenant)).thenThrow(new PlatformApiDataValidationException(dataValidationErrors));

        assertThrows(PlatformApiDataValidationException.class, () -> {
            smsBridgeService.createSmsBridgeConfig(tenantId, tenantAppKey, json);
        });
    }

    @Test
    @DisplayName("Test updateSmsBridge with valid data, should update bridge")
    void updateSmsBridge_withValidData_updatesBridge() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 1L;
        String json = "{\"countryId\": 1, \"provider\": \"UpdatedProvider\"}";
        SMSBridge bridge = new SMSBridge();
        bridge.setCountryId(1L);
        Country country = new Country();

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(bridge);
        when(countryService.retrieveCountryById(tenantId, tenantAppKey, 1L)).thenReturn(country);

        smsBridgeService.updateSmsBridge(tenantId, tenantAppKey, bridgeId, json);

        verify(smsBridgeRepository).save(bridge);
    }

    @Test
    @DisplayName("Test updateSmsBridge with invalid tenant, should throw exception")
    void updateSmsBridge_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        Long bridgeId = 1L;
        String json = "{\"countryId\": 1, \"provider\": \"UpdatedProvider\"}";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            smsBridgeService.updateSmsBridge(tenantId, tenantAppKey, bridgeId, json);
        });
    }

    @Test
    @DisplayName("Test updateSmsBridge with non-existent bridge, should throw exception")
    void updateSmsBridge_withNonExistentBridge_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 999L;
        String json = "{\"countryId\": 1, \"provider\": \"UpdatedProvider\"}";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(null);

        assertThrows(SMSBridgeNotFoundException.class, () -> {
            smsBridgeService.updateSmsBridge(tenantId, tenantAppKey, bridgeId, json);
        });
    }

    @Test
    @DisplayName("Test updateSmsBridge with invalid country, should throw exception")
    void updateSmsBridge_withInvalidCountry_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 1L;
        String json = "{\"countryId\": 999, \"provider\": \"UpdatedProvider\"}";
        SMSBridge bridge = new SMSBridge();
        bridge.setCountryId(999L);

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(bridge);
        when(countryService.retrieveCountryById(tenantId, tenantAppKey, 999L)).thenThrow(new CountryNotFoundException(999L));

        assertThrows(CountryNotFoundException.class, () -> {
            smsBridgeService.updateSmsBridge(tenantId, tenantAppKey, bridgeId, json);
        });
    }

    @Test
    @DisplayName("Test updateSmsBridge with invalid json, should throw exception")
    void updateSmsBridge_withInvalidJson_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 1L;
        String json = "{\"countryId\": \"\", \"provider\": \"\"}";
        SMSBridge bridge = new SMSBridge();
        List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        dataValidationErrors.add(ApiParameterError.parameterError("", "", null));

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(bridge);
        doThrow(new PlatformApiDataValidationException(dataValidationErrors)).when(smsBridgeSerializer).validateUpdate(json, bridge);

        assertThrows(PlatformApiDataValidationException.class, () -> {
            smsBridgeService.updateSmsBridge(tenantId, tenantAppKey, bridgeId, json);
        });
    }

    @Test
    @DisplayName("Test deleteSmsBridge with valid data, should delete bridge")
    void deleteSmsBridge_withValidData_deletesBridge() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 1L;
        SMSBridge bridge = new SMSBridge();

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(bridge);

        Long deletedBridgeId = smsBridgeService.deleteSmsBridge(tenantId, tenantAppKey, bridgeId);

        assertNotNull(deletedBridgeId);
        assertEquals(bridgeId, deletedBridgeId);
        verify(smsBridgeRepository).delete(bridge);
    }

    @Test
    @DisplayName("Test deleteSmsBridge with invalid tenant, should throw exception")
    void deleteSmsBridge_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        Long bridgeId = 1L;

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            smsBridgeService.deleteSmsBridge(tenantId, tenantAppKey, bridgeId);
        });
    }

    @Test
    @DisplayName("Test deleteSmsBridge with non-existent bridge, should throw exception")
    void deleteSmsBridge_withNonExistentBridge_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 999L;

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(null);

        assertThrows(SMSBridgeNotFoundException.class, () -> {
            smsBridgeService.deleteSmsBridge(tenantId, tenantAppKey, bridgeId);
        });
    }

    @Test
    @DisplayName("Test retrieveSmsBridge with valid data, should return bridge")
    void retrieveSmsBridge_withValidData_returnsBridge() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 1L;
        SMSBridge bridge = mock(SMSBridge.class);
        when(bridge.getId()).thenReturn(bridgeId);

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(bridge);

        SMSBridge result = smsBridgeService.retrieveSmsBridge(tenantId, tenantAppKey, bridgeId);

        assertNotNull(result);
        assertEquals(bridgeId, result.getId());
    }

    @Test
    @DisplayName("Test retrieveSmsBridge with invalid tenant, should throw exception")
    void retrieveSmsBridge_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        Long bridgeId = 1L;

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            smsBridgeService.retrieveSmsBridge(tenantId, tenantAppKey, bridgeId);
        });
    }

    @Test
    @DisplayName("Test retrieveSmsBridge with non-existent bridge, should throw exception")
    void retrieveSmsBridge_withNonExistentBridge_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long bridgeId = 999L;

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByIdAndTenantId(bridgeId, tenant.getId())).thenReturn(null);

        assertThrows(SMSBridgeNotFoundException.class, () -> {
            smsBridgeService.retrieveSmsBridge(tenantId, tenantAppKey, bridgeId);
        });
    }

    @Test
    @DisplayName("Test retrieveProviderDetails with valid data, should return provider details")
    void retrieveProviderDetails_withValidData_returnsProviderDetails() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        String country = "TestCountry";
        SMSBridge bridge1 = new SMSBridge();

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByTenantIdAndCountryName(tenant.getId(), country)).thenReturn(List.of(bridge1));

        Collection<SMSBridge> result = smsBridgeService.retrieveProviderDetails(tenantId, tenantAppKey, country);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(bridge1));
    }

    @Test
    @DisplayName("Test retrieveProviderDetails with invalid tenant, should throw exception")
    void retrieveProviderDetails_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        String country = "TestCountry";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            smsBridgeService.retrieveProviderDetails(tenantId, tenantAppKey, country);
        });
    }

    @Test
    @DisplayName("Test retrieveProviderDetails with no providers, should return empty list")
    void retrieveProviderDetails_withNoProviders_returnsEmptyList() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        String country = "TestCountry";
        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(smsBridgeRepository.findByTenantIdAndCountryName(tenant.getId(), country)).thenReturn(Collections.emptyList());

        Collection<SMSBridge> result = smsBridgeService.retrieveProviderDetails(tenantId, tenantAppKey, country);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}