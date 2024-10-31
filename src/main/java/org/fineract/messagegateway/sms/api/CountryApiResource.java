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
package org.fineract.messagegateway.sms.api;

import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.sms.data.CountryResponse;
import org.fineract.messagegateway.sms.service.CountryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/country")
public class CountryApiResource {
    
    private final CountryService countryService;
    
    public CountryApiResource(final CountryService countryService) {
        this.countryService = countryService;
    }

    @PostMapping( consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<CountryResponse> createCountry(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
            @RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey,
            @RequestBody final String countryJson) {
        CountryResponse countryResponse = this.countryService.createCountry(tenantId, appKey, countryJson) ;
        return new ResponseEntity<>(countryResponse, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{countryId}",  consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long>updateCountry(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
            @RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String tenantAppKey,
            @PathVariable("countryId") final Long countryId, @RequestBody final String country) {
        this.countryService.updateCountry(tenantId, tenantAppKey, countryId, country);
        return new ResponseEntity<>(countryId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{countryId}", produces = {"application/json"})
    public ResponseEntity<Long>deleteCountry(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
            @RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String tenantAppKey,
            @PathVariable("countryId") final Long countryId) {
        this.countryService.deleteCountry(tenantId, tenantAppKey, countryId);
        return new ResponseEntity<>(countryId, HttpStatus.OK);
    }

    @GetMapping(  produces = {"application/json"})
    public ResponseEntity<Collection<CountryResponse>> getAllCountries(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
            @RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey) {
        Collection<CountryResponse> countries = this.countryService.getAllCountries(tenantId, appKey) ;
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

    @GetMapping(value = "/{countryId}",  produces = {"application/json"})
    public ResponseEntity<CountryResponse> retrieveCountry(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
            @RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey,
            @PathVariable("countryId") final Long countryId) {
        CountryResponse countryResponse = this.countryService.retrieveCountry(tenantId, appKey, countryId);
        return new ResponseEntity<>(countryResponse, HttpStatus.OK);
    }
}
