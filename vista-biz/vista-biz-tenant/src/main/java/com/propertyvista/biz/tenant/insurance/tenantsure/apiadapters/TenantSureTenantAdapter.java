/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.apiadapters;

import com.cfcprograms.api.SimpleClient;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantSureTenantAdapter {

    public static void fillClient(Tenant tenantId, SimpleClient parameters) {
        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());

        parameters.setCompanyName(tenant.customer().person().name().getStringView());
        parameters.setContactName(tenant.customer().person().name().getStringView());

        Persistence.service().retrieveMember(tenant.lease());
        Persistence.service().retrieveMember(tenant.lease().unit().building());
        AddressStructured address = tenant.lease().unit().building().info().address();

        String streetNumber = address.streetNumber().getValue();
        if (!address.streetNumberSuffix().isNull()) {
            streetNumber += address.streetNumberSuffix().getValue();
        }
        String fullStreetName = address.streetName().getValue();
        if (!address.streetType().isNull()) {
            fullStreetName += " " + address.streetType().getStringView();
        }
        if (!address.streetDirection().isNull()) {
            fullStreetName += " " + address.streetDirection().getValue();
        }

        parameters.setAddress1(SimpleMessageFormat.format("{0} {1}", streetNumber, fullStreetName));

        parameters.setAddress2("unit " + tenant.lease().unit().info().number().getStringView());

        parameters.setCity(tenant.lease().unit().building().info().address().city().getValue());

        parameters.setState(tenant.lease().unit().building().info().address().province().code().getValue());

        parameters.setPostcode(tenant.lease().unit().building().info().address().postalCode().getValue());

        String countryName = tenant.lease().unit().building().info().address().country().name().getValue();
        if ("Canada".equals(countryName)) {
            parameters.setCountryCode("CA");
        } else {
            throw new Error(SimpleMessageFormat.format("tenant's country ({0}) is not supported!", countryName));
        }

        parameters.setEmailAddress(tenant.customer().person().email().getValue());
        parameters.setTelephoneNumber(tenant.customer().person().homePhone().getValue());
    }
}
