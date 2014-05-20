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

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantSureTenantAdapter {

    private static final I18n i18n = I18n.get(TenantSureTenantAdapter.class);

    public static void setClientsAddress(Tenant tenantId, SimpleClient parameters) {

        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());

        Persistence.service().retrieveMember(tenant.lease());
        Persistence.service().retrieveMember(tenant.lease().unit().building());

        InternationalAddress address = tenant.lease().unit().building().info().address();

        parameters.setAddress1(address.addressLine1().getValue());
        parameters.setAddress2(address.addressLine2().getValue());

        parameters.setCity(tenant.lease().unit().building().info().address().city().getValue());
        parameters.setState(tenant.lease().unit().building().info().address().province().getValue());
        parameters.setPostcode(tenant.lease().unit().building().info().address().postalCode().getValue());

        String country = tenant.lease().unit().building().info().address().country().name().getValue();
        if ("Canada".equals(country)) {
            parameters.setCountryCode("CA");
        } else {
            throw new UserRuntimeException(i18n.tr("Country \"{0}\" is not supported by TenantSure!", country));
        }

        parameters.setEmailAddress(tenant.customer().person().email().getValue());
    }

    private static String getStreetNumber(AddressStructured address) {
        String streetNumber = address.streetNumber().getValue();
        if (!address.streetNumberSuffix().isNull()) {
            streetNumber += address.streetNumberSuffix().getValue();
        }
        return streetNumber;
    }

    private static String getStreetName(AddressStructured address) {
        String fullStreetName = address.streetName().getValue();
        if (!address.streetType().isNull()) {
            fullStreetName += " " + address.streetType().getStringView();
        }
        if (!address.streetDirection().isNull()) {
            fullStreetName += " " + address.streetDirection().getValue();
        }
        return fullStreetName;
    }
}
