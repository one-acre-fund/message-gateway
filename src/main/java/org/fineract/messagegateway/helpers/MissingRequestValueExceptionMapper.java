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
package org.fineract.messagegateway.helpers;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingRequestValueException;

/**
 * The {@link MissingRequestValueException} is typically thrown when an api request is missing a required parameter.
 */
@Component
@Scope("singleton")
public class MissingRequestValueExceptionMapper {

    private MissingRequestValueExceptionMapper() {
        super();
    }

    public static ResponseEntity<ApiGlobalErrorResponse> toResponse(final MissingRequestValueException exception) {
        final ApiGlobalErrorResponse dataValidationErrorResponse = ApiGlobalErrorResponse.badClientRequest(
                exception.getMessage(), exception.getLocalizedMessage(), null);
        return new ResponseEntity<>(dataValidationErrorResponse, HttpStatus.BAD_REQUEST) ;
    }
}