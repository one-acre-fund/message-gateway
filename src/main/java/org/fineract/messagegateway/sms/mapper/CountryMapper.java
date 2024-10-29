package org.fineract.messagegateway.sms.mapper;

import org.fineract.messagegateway.sms.data.CountryResponse;
import org.fineract.messagegateway.sms.domain.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper {

    public CountryResponse mapToCountryResponse(Country country) {

        if(country == null) {
            return null;
        }

        CountryResponse response = new CountryResponse();
        response.setId(country.getId());
        response.setName(country.getCountryName());
        response.setCountryCode(country.getCountryCode());
        return response;
    }

}
