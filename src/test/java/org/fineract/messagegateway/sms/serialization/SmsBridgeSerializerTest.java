package org.fineract.messagegateway.sms.serialization;

import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.ApiParameterError;
import org.fineract.messagegateway.helpers.FromJsonHelper;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Unit tests for {@link SmsBridgeSerializer}
 *
 * @author amy.muhimpundu
 */
@ExtendWith(MockitoExtension.class)
class SmsBridgeSerializerTest {

    private FromJsonHelper fromApiJsonHelper = new FromJsonHelper();

    private SmsBridgeSerializer smsBridgeSerializer = new SmsBridgeSerializer(fromApiJsonHelper);

    Tenant tenant = new Tenant("default", "123", "Default Tenant");


    @Test
    @DisplayName("Test validateCreate with valid json returns SMSBridge")
    void validateCreate_withValidData_returnsSmsBridge() {
        String json = "{\"phoneNo\": \"1234567890\", \"providerName\": \"TestProvider\", \"countryCode\": \"US\", \"providerKey\": \"key123\", \"providerDescription\": \"Test Description\", \"countryId\": 1, \"bridgeConfigurations\": [{\"configName\": \"config1\", \"configValue\": \"value1\"}]}";
        SMSBridge expectedBridge = new SMSBridge(tenant.getId(), "1234567890", "TestProvider", "key123", "US", "Test Description", 1L);
        expectedBridge.setCreatedDate(new Date());
        expectedBridge.setSMSBridgeToBridgeConfigs();

        SMSBridge result = smsBridgeSerializer.validateCreate(json, tenant);

        assertNotNull(result);
        assertEquals(expectedBridge.getPhoneNo(), result.getPhoneNo());
        assertEquals(expectedBridge.getProviderName(), result.getProviderName());
        assertEquals(expectedBridge.getCountryCode(), result.getCountryCode());
        assertEquals(expectedBridge.getProviderKey(), result.getProviderKey());
        assertEquals(expectedBridge.getProviderDescription(), result.getProviderDescription());
        assertEquals(expectedBridge.getCountryId(), result.getCountryId());
    }

    /**
     * Method that generate arguments for validate create SMS bridge with invalid JSON
     *
     * @return a set of test arguments
     */
    static Stream<Arguments> invalidJsonForValidateCreateSMSProvider() {

        return Stream.of(
                // Missing 6 required fields and empty bridge configurations
                arguments("{\"phoneNo\": \"\", \"providerName\": \"\", \"countryCode\": \"\", \"providerKey\": \"\", \"providerDescription\": \"\", \"countryId\": null, \"bridgeConfigurations\": []}", 6, List.of("phoneNo", "providerName", "bridgeConfigurations", "providerKey", "providerDescription", "countryId")),
                // empty bridge configurations
                arguments("{\"phoneNo\": \"1234567890\", \"providerName\": \"TestProvider\", \"countryCode\": \"US\", \"providerKey\": \"key123\", \"providerDescription\": \"Test Description\", \"countryId\": 1, \"bridgeConfigurations\": []}", 1, List.of("bridgeConfigurations")),
                // Empty country id
                arguments("{\"phoneNo\": \"1234567890\", \"providerName\": \"TestProvider\", \"countryCode\": \"\", \"providerKey\": \"key123\", \"providerDescription\": \"Test Description\", \"countryId\": null, \"bridgeConfigurations\": [{\"configName\": \"config1\", \"configValue\": \"value1\"}]}", 1, List.of("countryId"))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidJsonForValidateCreateSMSProvider")
    @DisplayName("Test validateCreate with invalid json throws exception")
    void validateCreate_withInvalidJson_throwsException(String json, int expectedParameterErrorsSize, List<String> expectedParameterErrorNames) {


        Consumer<PlatformApiDataValidationException> assertException = e -> {
            assertThat(e.getErrors()).hasSize(expectedParameterErrorsSize);
            assertThat(e.getErrors()).extracting(ApiParameterError::getParameterName).containsExactlyInAnyOrderElementsOf(expectedParameterErrorNames);
        };
        assertThatThrownBy(() -> smsBridgeSerializer.validateCreate(json, tenant))
                .isInstanceOfSatisfying(PlatformApiDataValidationException.class, assertException);
    }

    @Test
    @DisplayName("Test validateCreate with missing required fields throws exception")
    void validateCreate_withMissingRequiredFields_throwsException() {
        String json = "{\"providerName\": \"TestProvider\", \"countryCode\": \"US\", \"providerKey\": \"key123\", \"providerDescription\": \"Test Description\", \"countryId\": 1, \"bridgeConfigurations\": [{\"configName\": \"config1\", \"configValue\": \"value1\"}]}";

        Consumer<PlatformApiDataValidationException> assertException = e -> {
            assertThat(e.getErrors()).hasSize(1);
            ApiParameterError error = e.getErrors().get(0);
            assertThat(error.getParameterName()).isEqualTo("phoneNo");
            assertThat(error.getDefaultUserMessage()).isEqualTo("The parameter phoneNo is mandatory.");
        };
        assertThatThrownBy(() -> smsBridgeSerializer.validateCreate(json, tenant))
                .isInstanceOfSatisfying(PlatformApiDataValidationException.class, assertException);
    }

    @Test
    @DisplayName("Test validateUpdate with valid json, updates SMSBridge")
    void validateUpdate_withValidData_updatesSmsBridge() {
        String json = "{\"phoneNo\": \"1234567890\", \"providerName\": \"UpdatedProvider\", \"countryCode\": \"US\", \"providerDescription\": \"Updated Description\", \"countryId\": 1, \"bridgeConfigurations\": [{\"configName\": \"config1\", \"configValue\": \"value1\"}]}";
        SMSBridge bridge = new SMSBridge(tenant.getId(), "0987654321", "OldProvider", "key123", "IN", "Old Description", 2L);

        smsBridgeSerializer.validateUpdate(json, bridge);

        assertEquals("1234567890", bridge.getPhoneNo());
        assertEquals("UpdatedProvider", bridge.getProviderName());
        assertEquals("US", bridge.getCountryCode());
        assertEquals("Updated Description", bridge.getProviderDescription());
        assertEquals(1L, bridge.getCountryId());
    }

    /**
     * Method that generate arguments for validate create SMS bridge with invalid JSON
     *
     * @return a set of test arguments
     */
    static Stream<Arguments> invalidJsonForValidateUpdateSMSProvider() {

        return Stream.of(
                // 5 mandatory fields passed as empty
                arguments("{\"phoneNo\": \"\", \"providerName\": \"\", \"countryCode\": \"\", \"providerKey\": \"\", \"providerDescription\": \"\", \"countryId\": null, \"bridgeConfigurations\": []}", 5, List.of("phoneNo", "providerName", "bridgeConfigurations", "providerDescription", "countryId")),
                // empty phone number
                arguments("{\"phoneNo\": \"\"}", 1, List.of("phoneNo")),
                // Empty bridge configurations
                arguments("{\"bridgeConfigurations\": []}", 1, List.of("bridgeConfigurations")),
                // Empty country id
                arguments("{\"countryId\": \"\"}", 1, List.of("countryId"))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidJsonForValidateUpdateSMSProvider")
    @DisplayName("Test validateUpdate with invalid json throws exception")
    void validateUpdate_withInvalidJson_throwsException(String json, int expectedParameterErrorsSize, List<String> expectedParameterErrorNames) {
        SMSBridge bridge = new SMSBridge(tenant.getId(), "0987654321", "OldProvider", "oldKey", "IN", "Old Description", 2L);

        Consumer<PlatformApiDataValidationException> assertException = e -> {
            assertThat(e.getErrors()).hasSize(expectedParameterErrorsSize);
            assertThat(e.getErrors()).extracting(ApiParameterError::getParameterName).containsExactlyInAnyOrderElementsOf(expectedParameterErrorNames);
        };

        assertThatThrownBy( () -> smsBridgeSerializer.validateUpdate(json, bridge))
                .isInstanceOfSatisfying(PlatformApiDataValidationException.class, assertException);
    }


}