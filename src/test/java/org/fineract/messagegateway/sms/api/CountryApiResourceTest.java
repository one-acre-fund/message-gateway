package org.fineract.messagegateway.sms.api;

import org.fineract.messagegateway.sms.data.CountryResponse;
import org.fineract.messagegateway.sms.service.CountryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CountryApiResource}
 *
 * @author amy.muhimpundu
 */
@ExtendWith(MockitoExtension.class)
class CountryApiResourceTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryApiResource countryApiResource;

    @Test
    void createCountry_withValidData_returnsCreatedStatus() {
        String tenantId = "tenant1";
        String appKey = "appKey1";
        String countryJson = "{\"countryName\": \"TestCountry\", \"countryCode\": \"TC\"}";
        CountryResponse countryResponse = new CountryResponse(1L, "TestCountry", "TC");

        when(countryService.createCountry(tenantId, appKey, countryJson)).thenReturn(countryResponse);

        ResponseEntity<CountryResponse> response = countryApiResource.createCountry(tenantId, appKey, countryJson);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(countryResponse, response.getBody());
    }

    @Test
    void updateCountry_withValidData_returnsOkStatus() {
        String tenantId = "tenant1";
        String appKey = "appKey1";
        Long countryId = 1L;
        String countryJson = "{\"countryName\": \"UpdatedCountry\", \"countryCode\": \"UC\"}";

        ResponseEntity<Long> response = countryApiResource.updateCountry(tenantId, appKey, countryId, countryJson);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(countryId, response.getBody());
    }

    @Test
    void deleteCountry_withValidData_returnsOkStatus() {
        String tenantId = "tenant1";
        String appKey = "appKey1";
        Long countryId = 1L;

        ResponseEntity<Long> response = countryApiResource.deleteCountry(tenantId, appKey, countryId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(countryId, response.getBody());
    }

    @Test
    void getAllCountries_withValidTenant_returnsOkStatus() {
        String tenantId = "tenant1";
        String appKey = "appKey1";
        CountryResponse countryResponse1 = new CountryResponse(1L, "Country1", "C1");
        CountryResponse countryResponse2 = new CountryResponse(2L, "Country2", "C2");

        when(countryService.getAllCountries(tenantId, appKey)).thenReturn(List.of(countryResponse1, countryResponse2));

        ResponseEntity<Collection<CountryResponse>> response = countryApiResource.getAllCountries(tenantId, appKey);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void retrieveCountry_withValidData_returnsOkStatus() {
        String tenantId = "tenant1";
        String appKey = "appKey1";
        Long countryId = 1L;
        CountryResponse countryResponse = new CountryResponse(countryId, "TestCountry", "TC");

        when(countryService.retrieveCountry(tenantId, appKey, countryId)).thenReturn(countryResponse);

        ResponseEntity<CountryResponse> response = countryApiResource.retrieveCountry(tenantId, appKey, countryId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(countryResponse, response.getBody());
    }
}