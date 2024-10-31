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
package org.fineract.messagegateway.sms.constants;

import java.util.Set;

/**
 * The {@link CountryConstants} is a constant class that contains all the constants used in the country module.
 */
public class CountryConstants{

	private CountryConstants() {
		// Do nothing
	}

	public static final String TENANT_ID_PARAM_NAME = "tenantId";

	public static final String COUNTRY_CODE_PARAM_NAME = "countryCode" ;

	public static final String COUNTRY_NAME_PARAM_NAME = "countryName" ;

	public static final int COUNTRY_CODE_DB_COLUMN_SIZE = 3;

	public static final int COUNTRY_NAME_DB_COLUMN_SIZE = 50;

	public static final Set<String> SUPPORTED_PARAMETERS = Set.of(TENANT_ID_PARAM_NAME,
			COUNTRY_CODE_PARAM_NAME, COUNTRY_NAME_PARAM_NAME);
}
