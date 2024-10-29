package org.fineract.messagegateway.sms.service;

import org.fineract.messagegateway.sms.data.CountryResponse;
import org.fineract.messagegateway.sms.domain.Country;

import java.util.Collection;

/**
 * Service interface for managing countries
 *
 * @author amy.muhimpundu
 */
public interface CountryService {

    /**
     * Create a new country.
     *
     * @param tenantId the tenant identifier
     * @param appKey the tenant app key
     * @param countryJson the country json
     * @return the created country
     */
    CountryResponse createCountry(String tenantId, String appKey, String countryJson);

    /**
     * Update an existing country.
     *
     * @param tenantId the tenant identifier
     * @param tenantAppKey the tenant app key
     * @param countryId the country identifier
     * @param country the country json
     */
    void updateCountry(String tenantId, String tenantAppKey, Long countryId, String country);

    /**
     * Delete an existing country.
     *
     * @param tenantId the tenant identifier
     * @param tenantAppKey the tenant app key
     * @param countryId the country identifier
     */
    void deleteCountry(String tenantId, String tenantAppKey, Long countryId);

    /**
     * Retrieve all countries.
     *
     * @param tenantId the tenant identifier
     * @param appKey the tenant app key
     * @return a collection of countries
     */
    Collection<CountryResponse> getAllCountries(String tenantId, String appKey);

    /**
     * Retrieve a country.
     *
     * @param tenantId the tenant identifier
     * @param appKey the tenant app key
     * @param countryId the country identifier
     * @return the country
     */
    CountryResponse retrieveCountry(String tenantId, String appKey, Long countryId);

    /**
     * Retrieve a country by its identifier.
     *
     * @param tenantId the tenant identifier
     * @param appKey the tenant app key
     * @param countryId the country identifier
     * @return the country
     */
    Country retrieveCountryById(String tenantId, String appKey, Long countryId);
}
