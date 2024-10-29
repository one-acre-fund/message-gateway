package org.fineract.messagegateway.sms.mapper;

import org.fineract.messagegateway.sms.data.CountryResponse;
import org.fineract.messagegateway.sms.domain.Country;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link CountryMapper}
 *
 * @author amy.muhimpundu
 */
@ExtendWith(MockitoExtension.class)
class CountryMapperTest {

    private final CountryMapper countryMapper = new CountryMapper() {};

    @Test
    void mapToCountryResponse_withValidCountry_returnsCountryResponse() {
        Country country = new Country();
        country.setCountryName("TestCountry");
        country.setCountryCode("TC");

        CountryResponse response = countryMapper.mapToCountryResponse(country);

        assertNotNull(response);
        assertEquals("TestCountry", response.getName());
        assertEquals("TC", response.getCountryCode());
    }

    @Test
    void mapToCountryResponse_withNullCountry_returnsNull() {
        CountryResponse response = countryMapper.mapToCountryResponse(null);

        assertNull(response);
    }

    @Test
    void mapToCountryResponse_withCountryHavingNullFields_returnsCountryResponseWithNullFields() {
        Country country = new Country();
        country.setCountryName(null);
        country.setCountryCode(null);

        CountryResponse response = countryMapper.mapToCountryResponse(country);

        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getCountryCode());
    }

}