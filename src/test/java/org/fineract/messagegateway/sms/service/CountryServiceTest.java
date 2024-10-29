package org.fineract.messagegateway.sms.service;

import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.ApiParameterError;
import org.fineract.messagegateway.service.SecurityService;
import org.fineract.messagegateway.sms.data.CountryResponse;
import org.fineract.messagegateway.sms.domain.Country;
import org.fineract.messagegateway.sms.exception.CountryNotFoundException;
import org.fineract.messagegateway.sms.mapper.CountryMapper;
import org.fineract.messagegateway.sms.repository.CountryRepository;
import org.fineract.messagegateway.sms.serialization.CountrySerializer;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CountryServiceImpl}
 *
 * @author amy.muhimpundu
 */
@ExtendWith(MockitoExtension.class)
class CountryServiceTest {


    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountrySerializer countrySerializer;

    @Mock
    private SecurityService securityService;

    @Mock
    private CountryMapper countryMapper;

    private CountryServiceImpl countryService;

    private Tenant tenant = new Tenant("default", "123", "Default Tenant");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        countryService = new CountryServiceImpl(countryRepository, countrySerializer, securityService, countryMapper);
    }

    @Test
    @DisplayName("Test createCountry with valid data, expect CountryResponse")
    void createCountry_withValidData_returnsCountryResponse() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        String countryJson = "{\"countryName\": \"TestCountry\", \"countryCode\": \"TC\"}";

        Country country = new Country();
        Country newCountry = new Country();
        CountryResponse countryResponse = new CountryResponse(1L, "TestCountry", "TC");

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countrySerializer.validateCreate(countryJson, tenant)).thenReturn(country);
        when(countryRepository.save(country)).thenReturn(newCountry);
        when(countryMapper.mapToCountryResponse(newCountry)).thenReturn(countryResponse);

        CountryResponse response = countryService.createCountry(tenantId, tenantAppKey, countryJson);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TestCountry", response.getName());
        assertEquals("TC", response.getCountryCode());
    }

    @Test
    @DisplayName("Test createCountry with invalid tenant, expect TenantNotFoundException")
    void createCountry_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        String countryJson = "{\"countryName\": \"TestCountry\", \"countryCode\": \"TC\"}";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            countryService.createCountry(tenantId, tenantAppKey, countryJson);
        });
    }

    @Test
    @DisplayName("Test createCountry with invalid country json, expect PlatformApiDataValidationException")
    void createCountry_withInvalidCountryJson_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        String countryJson = "{\"countryName\": \"\", \"countryCode\": \"\"}";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        dataValidationErrors.add( ApiParameterError.parameterError("", "", null));
        when(countrySerializer.validateCreate(countryJson, tenant)).thenThrow(new PlatformApiDataValidationException(dataValidationErrors));

        assertThrows(PlatformApiDataValidationException.class, () -> {
            countryService.createCountry(tenantId, tenantAppKey, countryJson);
        });
    }

    @Test
    @DisplayName("Test updateCountry with valid data, expect country updated")
    void updateCountry_withValidData_updatesCountry() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long countryId = 1L;
        String countryJson = "{\"countryName\": \"UpdatedCountry\", \"countryCode\": \"UC\"}";

        Country countryToUpdate = new Country();
        countryToUpdate.setName("OldCountry");
        countryToUpdate.setCode("OC");

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countryRepository.findByIdAndTenantId(countryId, tenant.getId())).thenReturn(countryToUpdate);

        countryService.updateCountry(tenantId, tenantAppKey, countryId, countryJson);

        verify(countrySerializer).validateUpdate(countryJson, countryToUpdate);
        verify(countryRepository).save(countryToUpdate);
    }

    @Test
    @DisplayName("Test updateCountry with invalid tenant, expect TenantNotFoundException")
    void updateCountry_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        Long countryId = 1L;
        String countryJson = "{\"countryName\": \"UpdatedCountry\", \"countryCode\": \"UC\"}";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            countryService.updateCountry(tenantId, tenantAppKey, countryId, countryJson);
        });
    }

    @Test
    @DisplayName("Test updateCountry with invalid country json, expect PlatformApiDataValidationException")
    void updateCountry_withNonExistentCountry_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long countryId = 999L;
        String countryJson = "{\"countryName\": \"UpdatedCountry\", \"countryCode\": \"UC\"}";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countryRepository.findByIdAndTenantId(countryId, tenant.getId())).thenReturn(null);

        assertThrows(CountryNotFoundException.class, () -> {
            countryService.updateCountry(tenantId, tenantAppKey, countryId, countryJson);
        });
    }

    @Test
    @DisplayName("Test updateCountry with invalid country json, expect PlatformApiDataValidationException")
    void updateCountry_withInvalidCountryJson_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long countryId = 1L;
        String countryJson = "{\"countryName\": \"\", \"countryCode\": \"\"}";

        Country countryToUpdate = new Country();
        countryToUpdate.setName("OldCountry");
        countryToUpdate.setCode("OC");

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countryRepository.findByIdAndTenantId(countryId, tenant.getId())).thenReturn(countryToUpdate);
        List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        dataValidationErrors.add(ApiParameterError.parameterError("", "", null));
        doThrow(new PlatformApiDataValidationException(dataValidationErrors)).when(countrySerializer).validateUpdate(countryJson, countryToUpdate);

        assertThrows(PlatformApiDataValidationException.class, () -> {
            countryService.updateCountry(tenantId, tenantAppKey, countryId, countryJson);
        });
    }

    @Test
    @DisplayName("Test deleteCountry with valid data, expect country deleted")
    void deleteCountry_withValidData_deletesCountry() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long countryId = 1L;

        Country country = new Country();

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countryRepository.findByIdAndTenantId(countryId, tenant.getId())).thenReturn(country);

        countryService.deleteCountry(tenantId, tenantAppKey, countryId);

        verify(countryRepository).delete(country);
    }

    @Test
    @DisplayName("Test deleteCountry with invalid tenant, expect TenantNotFoundException")
    void deleteCountry_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";
        Long countryId = 1L;

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            countryService.deleteCountry(tenantId, tenantAppKey, countryId);
        });
    }

    @Test
    @DisplayName("Test deleteCountry with invalid country, expect CountryNotFoundException")
    void deleteCountry_withNonExistentCountry_throwsException() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";
        Long countryId = 999L;

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countryRepository.findByIdAndTenantId(countryId, tenant.getId())).thenReturn(null);

        assertThrows(CountryNotFoundException.class, () -> {
            countryService.deleteCountry(tenantId, tenantAppKey, countryId);
        });
    }

    @Test
    @DisplayName("Test getAllCountries with valid data, expect a list of CountryResponse")
    void getAllCountries_withValidTenant_returnsCountryResponses() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";

        Country country1 = new Country(1L, "C1", "Country1" );
        Country country2 = new Country(1L, "C2", "Country2" );

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countryRepository.findByTenantId(tenant.getId())).thenReturn(List.of(country1, country2));
        when(countryMapper.mapToCountryResponse(country1)).thenReturn(new CountryResponse(1L, "Country1", "C1"));
        when(countryMapper.mapToCountryResponse(country2)).thenReturn(new CountryResponse(2L, "Country2", "C2"));

        Collection<CountryResponse> responses = countryService.getAllCountries(tenantId, tenantAppKey);

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    @DisplayName("Test getAllCountries with invalid tenant, expect TenantNotFoundException")
    void getAllCountries_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String tenantAppKey = "invalidAppKey";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenThrow(new TenantNotFoundException(tenantId, tenantAppKey));

        assertThrows(TenantNotFoundException.class, () -> {
            countryService.getAllCountries(tenantId, tenantAppKey);
        });
    }

    @Test
    @DisplayName("Test getAllCountries with no countries, expect empty list")
    void getAllCountries_withNoCountries_returnsEmptyList() {
        String tenantId = "tenant1";
        String tenantAppKey = "appKey1";

        when(securityService.authenticate(tenantId, tenantAppKey)).thenReturn(tenant);
        when(countryRepository.findByTenantId(tenant.getId())).thenReturn(Collections.emptyList());

        Collection<CountryResponse> responses = countryService.getAllCountries(tenantId, tenantAppKey);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Test retrieveCountry with valid data, expect CountryResponse")
    void retrieveCountry_withValidData_returnsCountryResponse() {
        String tenantId = "tenant1";
        String appKey = "appKey1";
        Long countryId = 1L;

        Country country = new Country();
        country.setName("TestCountry");
        country.setCode("TC");
        CountryResponse countryResponse = new CountryResponse(countryId, "TestCountry", "TC");

        when(securityService.authenticate(tenantId, appKey)).thenReturn(tenant);
        when(countryRepository.findByIdAndTenantId(countryId, tenant.getId())).thenReturn(country);
        when(countryMapper.mapToCountryResponse(country)).thenReturn(countryResponse);

        CountryResponse response = countryService.retrieveCountry(tenantId, appKey, countryId);

        assertNotNull(response);
        assertEquals(countryId, response.getId());
        assertEquals("TestCountry", response.getName());
        assertEquals("TC", response.getCountryCode());
    }

    @Test
    @DisplayName("Test retrieveCountry with invalid tenant, expect TenantNotFoundException")
    void retrieveCountry_withInvalidTenant_throwsException() {
        String tenantId = "invalidTenant";
        String appKey = "invalidAppKey";
        Long countryId = 1L;

        when(securityService.authenticate(tenantId, appKey)).thenThrow(new TenantNotFoundException(tenantId, appKey));

        assertThrows(TenantNotFoundException.class, () -> {
            countryService.retrieveCountry(tenantId, appKey, countryId);
        });
    }

    @Test
    @DisplayName("Test retrieveCountry with invalid country, expect CountryNotFoundException")
    void retrieveCountry_withNonExistentCountry_throwsException() {
        String tenantId = "tenant1";
        String appKey = "appKey1";
        Long countryId = 999L;

        when(securityService.authenticate(tenantId, appKey)).thenReturn(tenant);
        when(countryRepository.findByIdAndTenantId(countryId, tenant.getId())).thenReturn(null);

        assertThrows(CountryNotFoundException.class, () -> {
            countryService.retrieveCountry(tenantId, appKey, countryId);
        });
    }
}