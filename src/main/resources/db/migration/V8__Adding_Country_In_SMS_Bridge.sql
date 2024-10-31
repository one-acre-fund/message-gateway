--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

-- Add country table
CREATE TABLE m_country (
                              id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
                              tenant_id               BIGINT(20)                                      NOT NULL,
                              name         VARCHAR(50)                                    NOT NULL,
                              country_code			  VARCHAR(3) 									  NOT NULL,
                              created_on              TIMESTAMP                                       NULL DEFAULT NULL,
                              last_modified_on        TIMESTAMP                                       NULL DEFAULT NULL,
                              UNIQUE INDEX UNQ_COUNTRY_NAME (name)
);

-- Add country id on sms_bridge table
ALTER TABLE m_sms_bridge
    ADD COLUMN country_id BIGINT(20),
    MODIFY COLUMN country_code VARCHAR(5) NULL,
    ADD CONSTRAINT CTR_SMS_BRIDGE_COUNTR FOREIGN KEY (country_id) REFERENCES m_country (id);

