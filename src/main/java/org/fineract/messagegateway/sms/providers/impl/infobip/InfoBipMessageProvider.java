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
package org.fineract.messagegateway.sms.providers.impl.infobip;

import java.util.Collections;
import java.util.HashMap;

import infobip.api.config.ApiKeyAuthConfiguration;
import infobip.api.config.Configuration;
import org.fineract.messagegateway.configuration.HostConfig;
import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.util.AuthorizationType;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.providers.SMSProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import infobip.api.client.SendMultipleTextualSmsAdvanced;
import infobip.api.config.BasicAuthConfiguration;
import infobip.api.model.Destination;
import infobip.api.model.sms.mt.send.Message;
import infobip.api.model.sms.mt.send.SMSResponse;
import infobip.api.model.sms.mt.send.SMSResponseDetails;
import infobip.api.model.sms.mt.send.textual.SMSAdvancedTextualRequest;
import org.springframework.util.StringUtils;

@Service(value = "InfoBip")
public class InfoBipMessageProvider extends SMSProvider {

	private static final Logger logger = LoggerFactory.getLogger(InfoBipMessageProvider.class);

	private HashMap<String, SendMultipleTextualSmsAdvanced> restClients = new HashMap<>() ; //tenantId, twilio clients
	private final String callBackUrl ;
	 
	private final StringBuilder builder ;
	  
	@Autowired
	public InfoBipMessageProvider(final HostConfig hostConfig) {
		callBackUrl = String.format("%s://%s:%d/infobip/report/", hostConfig.getProtocol(),  hostConfig.getHostName(), hostConfig.getPort());
    	logger.info("Registering call back to InfoBip: {}", callBackUrl);
    	builder = new StringBuilder() ;
	}

	@Override
	public void sendMessage(SMSBridge smsBridgeConfig, SMSMessage message) throws MessageGatewayException {
		String statusCallback = callBackUrl+message.getId() ;
		//Based on message id, register call back. so that we get notification from Infobip about message status
		SendMultipleTextualSmsAdvanced client = getRestClient(smsBridgeConfig) ;
		Destination destination = new Destination();
		builder.setLength(0);
        builder.append(smsBridgeConfig.getCountryCode()) ;
        builder.append(message.getMobileNumber()) ;
        String mobile = builder.toString() ;
        logger.info("Sending SMS to {} ...", mobile);
		destination.setTo(mobile);
		Message infoBipMessage = new Message();
		infoBipMessage.setDestinations(Collections.singletonList(destination));
		infoBipMessage.setFrom(smsBridgeConfig.getPhoneNo());
		infoBipMessage.setText(message.getMessage());
		infoBipMessage.setNotifyUrl(statusCallback);
		infoBipMessage.setNotifyContentType("application/json") ;
		infoBipMessage.setNotify(true) ;
		SMSAdvancedTextualRequest requestBody = new SMSAdvancedTextualRequest();
		requestBody.setMessages(Collections.singletonList(infoBipMessage));
		SMSResponse response = client.execute(requestBody);
		SMSResponseDetails sentMessageInfo = response.getMessages().get(0);
		message.setExternalId(sentMessageInfo.getMessageId());
		message.setDeliveryStatus(InfoBipStatus.smsStatus(sentMessageInfo.getStatus().getGroupId()).getValue());
		logger.debug("InfoBipMessageProvider.sendMessage():"+InfoBipStatus.smsStatus(sentMessageInfo.getStatus().getGroupId()).getValue());
	}

	@Override
	public void updateStatusByMessageId(SMSBridge bridge, String externalId) throws MessageGatewayException {

	}


	private SendMultipleTextualSmsAdvanced getRestClient(final SMSBridge smsBridge) {
		String authorizationKey = encodeBase64(smsBridge) ;
		 SendMultipleTextualSmsAdvanced client = this.restClients.get(authorizationKey) ;
		if(client == null) {
			client = this.get(smsBridge) ;
			this.restClients.put(authorizationKey, client) ;
		}
	    return client ;
	 }
	 
	SendMultipleTextualSmsAdvanced get(final SMSBridge smsBridgeConfig) {
    	logger.debug("Creating a new InfoBip Client ....");
		String baseURL = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_URL);
		String providerAuthType = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_AUTH_TYPE);
		String password = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_AUTH_TOKEN) ;

		AuthorizationType authType = (StringUtils.hasText(providerAuthType ))? AuthorizationType.valueOf(providerAuthType.toUpperCase()) : AuthorizationType.BASIC;

		Configuration configuration;

		switch (authType) {
			case API:
				configuration = StringUtils.hasText(baseURL) ? new ApiKeyAuthConfiguration(baseURL, password):  new ApiKeyAuthConfiguration(password);
				break;
			case CUSTOM:
				String customPrefix = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_AUTH_CUSTOM_PREFIX);
				configuration = StringUtils.hasText(baseURL) ? new CustomPrefixAuthConfiguration(baseURL, password, customPrefix): new CustomPrefixAuthConfiguration(password, customPrefix);
				break;
			default:
				String userName = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_ACCOUNT_ID) ;
				configuration = StringUtils.hasText(baseURL) ? new BasicAuthConfiguration(baseURL, userName, password):  new BasicAuthConfiguration(userName, password);
				break;
		}

		return new SendMultipleTextualSmsAdvanced(configuration);
    }
}
