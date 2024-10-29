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
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;

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

    @Resource
    private CountryService countryService;

    public CountryServiceImpl(final CountryRepository countryRepository,
            final CountrySerializer countrySerializer,
            final SecurityService securityService, final CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countrySerializer = countrySerializer ;
        this.securityService = securityService ;
        this.countryMapper = countryMapper;
    }

    @Override
    @Transactional
    public CountryResponse createCountry(String tenantId, String tenantAppKey, String countryJson) {
        Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
        Country country = this.countrySerializer.validateCreate(countryJson, tenant) ;
        final Country newCountry = this.countryRepository.save(country);
        return countryMapper.mapToCountryResponse(newCountry);
    }

    @Override
    @Transactional
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
    @Transactional
    public void deleteCountry(String tenantId, String tenantAppKey, Long countryId) {
        Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
        final Country country = this.countryRepository.findByIdAndTenantId(countryId, tenant.getId()) ;
        if (country == null) {
            throw new CountryNotFoundException(countryId);
        }
        this.countryRepository.delete(country);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CountryResponse> getAllCountries(String tenantId, String tenantAppKey) {
        Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
        return countryRepository.findByTenantId(tenant.getId()).stream().map(countryMapper::mapToCountryResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CountryResponse retrieveCountry(String tenantId, String appKey, Long countryId) {
        Country country = countryService.retrieveCountryById(tenantId, appKey, countryId);
        return countryMapper.mapToCountryResponse(country);
    }

    @Override
    @Transactional(readOnly = true)
    public Country retrieveCountryById(String tenantId, String appKey, Long countryId) {
        Tenant tenant = this.securityService.authenticate(tenantId, appKey) ;
        Country country = this.countryRepository.findByIdAndTenantId(countryId, tenant.getId());
        if (country == null) {
            throw new CountryNotFoundException(countryId);
        }
        return country;
    }
}
