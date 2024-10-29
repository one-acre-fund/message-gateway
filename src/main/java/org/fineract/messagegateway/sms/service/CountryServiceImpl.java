package org.fineract.messagegateway.sms.service;

import org.fineract.messagegateway.service.SecurityService;
import org.fineract.messagegateway.sms.data.CountryResponse;
import org.fineract.messagegateway.sms.domain.Country;
import org.fineract.messagegateway.sms.exception.CountryNotFoundException;
import org.fineract.messagegateway.sms.mapper.CountryMapper;
import org.fineract.messagegateway.sms.repository.CountryRepository;
import org.fineract.messagegateway.sms.serialization.CountrySerializer;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service implementation for managing countries
 *
 * @author amy.muhimpundu
 */
@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    private final CountrySerializer countrySerializer ;

    private final SecurityService securityService ;

    private final CountryMapper countryMapper;

    public CountryServiceImpl(final CountryRepository countryRepository,
            final CountrySerializer countrySerializer,
            final SecurityService securityService, final CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countrySerializer = countrySerializer ;
        this.securityService = securityService ;
        this.countryMapper = countryMapper;
    }

    @Override
    public CountryResponse createCountry(String tenantId, String tenantAppKey, String countryJson) {
        Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
        Country country = this.countrySerializer.validateCreate(countryJson, tenant) ;
        final Country newCountry = this.countryRepository.save(country);
        return countryMapper.mapToCountryResponse(newCountry);
    }

    @Override
    public void updateCountry(String tenantId, String tenantAppKey, Long countryId, String country) {
        Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
        final Country countryToUpdate = this.countryRepository.findByIdAndTenantId(countryId, tenant.getId()) ;
        if (countryToUpdate == null) {
            throw new CountryNotFoundException(countryId);
        }
        this.countrySerializer.validateUpdate(country, countryToUpdate);
        this.countryRepository.save(countryToUpdate);
    }

    @Override
    public void deleteCountry(String tenantId, String tenantAppKey, Long countryId) {
        Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
        final Country country = this.countryRepository.findByIdAndTenantId(countryId, tenant.getId()) ;
        if (country == null) {
            throw new CountryNotFoundException(countryId);
        }
        this.countryRepository.delete(country);
    }

    @Override
    public Collection<CountryResponse> getAllCountries(String tenantId, String tenantAppKey) {
        Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
        return countryRepository.findByTenantId(tenant.getId()).stream().map(countryMapper::mapToCountryResponse).collect(Collectors.toList());
    }

    @Override
    public CountryResponse retrieveCountry(String tenantId, String appKey, Long countryId) {
        Tenant tenant = this.securityService.authenticate(tenantId, appKey) ;
        Country country = this.countryRepository.findByIdAndTenantId(countryId, tenant.getId());
        if (country == null) {
            throw new CountryNotFoundException(countryId);
        }
        return countryMapper.mapToCountryResponse(country);
    }
}
