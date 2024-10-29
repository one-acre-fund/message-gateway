package org.fineract.messagegateway.sms.serialization;

import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.FromJsonHelper;
import org.fineract.messagegateway.sms.domain.Country;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Unit tests for {@link CountrySerializer}
 *
 * @author amy.muhimpundu
 */
@ExtendWith(MockitoExtension.class)
class CountrySerializerTest {

    private FromJsonHelper fromJsonHelper = new FromJsonHelper();

    private CountrySerializer countrySerializer = new CountrySerializer(fromJsonHelper);

    private Tenant tenant = new Tenant("default", "123", "Default Tenant");

    @Test
    void validateCreate_withValidJson_returnsCountry() {
        String json = "{\"countryName\": \"TestCountry\", \"countryCode\": \"TC\"}";

        Country country = countrySerializer.validateCreate(json, tenant);

        assertNotNull(country);
        assertEquals("TestCountry", country.getCountryName());
        assertEquals("TC", country.getCountryCode());
    }

    /**
     * Method that generate arguments for create country with invalid JSON
     *
     * @return a set of test arguments
     */
    static Stream<Arguments> invalidCreateCountryJsonProvider() {

        return Stream.of(
                // Blank country name & country code
                arguments("{\"countryName\": \"\", \"countryCode\": \"\"}"),
                // Blank country code
                arguments("{\"countryName\": \"Test\", \"countryCode\": \"\"}"),
                // Country code with more than 3 characters
                arguments("{\"countryName\": \"Test\", \"countryCode\": \"ABCDE\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCreateCountryJsonProvider")
    void validateCreate_withInvalidJson_throwsException(String json) {
        assertThrows(PlatformApiDataValidationException.class, () -> countrySerializer.validateCreate(json, tenant));
    }

    @Test
    void validateUpdate_withValidJson_updatesCountry() {
        String json = "{\"countryName\": \"UpdatedCountry\", \"countryCode\": \"UC\"}";
        Country country = new Country();
        country.setCountryName("OldCountry");
        country.setCountryCode("OC");


        countrySerializer.validateUpdate(json, country);

        assertEquals("UpdatedCountry", country.getCountryName());
        assertEquals("UC", country.getCountryCode());
    }

    @Test
    void validateUpdate_withInvalidJson_throwsException() {
        String json = "{\"countryName\": \"\", \"countryCode\": \"\"}";
        Country country = new Country();
        country.setCountryName("OldCountry");
        country.setCountryCode("OC");

        assertThrows(PlatformApiDataValidationException.class, () -> {
            countrySerializer.validateUpdate(json, country);
        });
    }
}