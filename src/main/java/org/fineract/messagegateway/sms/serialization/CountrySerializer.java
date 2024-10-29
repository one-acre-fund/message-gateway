/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.fineract.messagegateway.sms.serialization;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.ApiParameterError;
import org.fineract.messagegateway.helpers.DataValidatorBuilder;
import org.fineract.messagegateway.helpers.FromJsonHelper;
import org.fineract.messagegateway.sms.constants.CountryConstants;
import org.fineract.messagegateway.sms.domain.Country;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.fineract.messagegateway.sms.constants.CountryConstants.COUNTRY_CODE_DB_COLUMN_SIZE;
import static org.fineract.messagegateway.sms.constants.CountryConstants.COUNTRY_NAME_DB_COLUMN_SIZE;

/**
 * Helper class to serialize the json request to create or update a country
 *
 * @author amy.muhimpundu
 */
@Component
public class CountrySerializer {

	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public CountrySerializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	/**
	 * Method to validate the json request to create a country.
	 *
	 * @param json the json request
	 * @param tenant the tenant object
	 * @return the country object
	 */
	public Country validateCreate(final String json, Tenant tenant) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CountryConstants.SUPPORTED_PARAMETERS);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("country");
		final JsonElement element = this.fromApiJsonHelper.parse(json);

		
		final String countryName = this.fromApiJsonHelper.extractStringNamed(CountryConstants.COUNTRY_NAME_PARAM_NAME, element);
		baseDataValidator.reset().parameter(CountryConstants.COUNTRY_NAME_PARAM_NAME).value(countryName).notBlank().notExceedingLengthOf(COUNTRY_NAME_DB_COLUMN_SIZE);
		 
		final String countryCode = this.fromApiJsonHelper.extractStringNamed(CountryConstants.COUNTRY_CODE_PARAM_NAME, element);
		baseDataValidator.reset().parameter(CountryConstants.COUNTRY_CODE_PARAM_NAME).value(countryCode).notBlank().notExceedingLengthOf(COUNTRY_CODE_DB_COLUMN_SIZE);

		
		Country country = new Country(tenant.getId(), countryCode, countryName) ;

		country.setCreatedOnDate(new Date());
		
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
		
		return country;
	}

	/**
	 * Method to validate the json request to update a country
	 * @param json the json request
	 * @param country the country object
	 */
	public void validateUpdate(final String json, final Country country) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, CountryConstants.SUPPORTED_PARAMETERS);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("country");
		 final JsonElement element = this.fromApiJsonHelper.parse(json);
		 if (this.fromApiJsonHelper.parameterExists(CountryConstants.TENANT_ID_PARAM_NAME, element)) {
			 final Long tenantId = this.fromApiJsonHelper.extractLongNamed(CountryConstants.TENANT_ID_PARAM_NAME, element);
			 baseDataValidator.reset().parameter(CountryConstants.TENANT_ID_PARAM_NAME).value(tenantId).notBlank();
			 country.setTenantId(tenantId);
		 }

		 
		 if (this.fromApiJsonHelper.parameterExists(CountryConstants.COUNTRY_NAME_PARAM_NAME, element)) {
			 final String countryName = this.fromApiJsonHelper.extractStringNamed(CountryConstants.COUNTRY_NAME_PARAM_NAME, element);
			 baseDataValidator.reset().parameter(CountryConstants.COUNTRY_NAME_PARAM_NAME).value(countryName).notBlank().notExceedingLengthOf(COUNTRY_NAME_DB_COLUMN_SIZE);
			 country.setCountryName(countryName);
		 }
		 
		 if(this.fromApiJsonHelper.parameterExists(CountryConstants.COUNTRY_CODE_PARAM_NAME, element)) {
			 final String countryCode = this.fromApiJsonHelper.extractStringNamed(CountryConstants.COUNTRY_CODE_PARAM_NAME, element);
				baseDataValidator.reset().parameter(CountryConstants.COUNTRY_CODE_PARAM_NAME).value(countryCode).notBlank().notExceedingLengthOf(COUNTRY_CODE_DB_COLUMN_SIZE);
				country.setCountryCode(countryCode);
				
		 }

		 
		 country.setModifiedOnDate(new Date()) ;
		 if (!dataValidationErrors.isEmpty()) {
				throw new PlatformApiDataValidationException(dataValidationErrors);
		 }
	}

}
