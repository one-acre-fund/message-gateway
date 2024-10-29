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
package org.fineract.messagegateway.sms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name="m_country")
public class Country extends AbstractPersistableCustom<Long> {

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "country_code", nullable = false)
    private String countryCode ;

    @Column(name = "name", nullable = false)
    private String countryName;

    @Column(name = "created_on", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOnDate;

    @Column(name = "last_modified_on", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOnDate;

    public Country() {
    }

    public Country(Long tenantId, String countryCode, String countryName) {
        this.tenantId = tenantId;
        this.countryCode = countryCode;
        this.countryName = countryName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public Date getModifiedOnDate() {
        return modifiedOnDate;
    }

    public void setModifiedOnDate(Date modifiedOnDate) {
        this.modifiedOnDate = modifiedOnDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Country country = (Country) o;
        return Objects.equals(id, country.id) && Objects.equals(tenantId, country.tenantId) && Objects.equals(countryCode, country.countryCode) && Objects.equals(countryName,
                country.countryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, countryCode, countryName);
    }
}
