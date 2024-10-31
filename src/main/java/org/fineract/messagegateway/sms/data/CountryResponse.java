package org.fineract.messagegateway.sms.data;

/**
 * CountryResponse class is used to represent the response of a country
 *
 * @author amy.muhimpundu
 */
public class CountryResponse {

    private Long id;
    private String name;
    private String countryCode;

    public CountryResponse() {
    }

    public CountryResponse(Long id, String name, String countryCode) {
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
