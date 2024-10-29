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
package org.fineract.messagegateway.exception;

import org.fineract.messagegateway.helpers.ApiGlobalErrorResponse;
import org.fineract.messagegateway.helpers.MissingRequestValueExceptionMapper;
import org.fineract.messagegateway.helpers.PlatformApiDataValidationExceptionMapper;
import org.fineract.messagegateway.helpers.PlatformResourceNotFoundExceptionMapper;
import org.fineract.messagegateway.helpers.UnsupportedParameterExceptionMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MessageGatewayExceptionHandler {

    @ExceptionHandler({ AbstractPlatformResourceNotFoundException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handleAbstractPlatformResourceNotFoundException(AbstractPlatformResourceNotFoundException e) {
        return PlatformResourceNotFoundExceptionMapper.toResponse(e) ;
    }

    @ExceptionHandler({PlatformApiDataValidationException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handlePlatformApiDataValidationException(PlatformApiDataValidationException e) {
        return PlatformApiDataValidationExceptionMapper.toResponse(e) ;
    }

    @ExceptionHandler({UnsupportedParameterException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handleUnsupportedParameterException(UnsupportedParameterException e) {
        return UnsupportedParameterExceptionMapper.toResponse(e) ;
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public ResponseEntity<ApiGlobalErrorResponse> handleMissingRequestValueException(MissingRequestValueException e) {
        return MissingRequestValueExceptionMapper.toResponse(e) ;
    }

}
