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

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.ref.ISOProvince;
import com.propertyvista.domain.tenant.lease.Tenant;

public class TenantSureTenantAdapter {

    private static final I18n i18n = I18n.get(TenantSureTenantAdapter.class);

    public static void setClientsAddress(Tenant tenantId, SimpleClient parameters) {

        Tenant tenant = Persistence.service().retrieve(Tenant.class, tenantId.getPrimaryKey());

        Persistence.service().retrieveMember(tenant.lease());
        Persistence.service().retrieveMember(tenant.lease().unit().building());

        InternationalAddress address = tenant.lease().unit().building().info().address();

        parameters.setAddress1(address.streetNumber().getStringView() + " " + address.streetName().getStringView());
        parameters.setAddress2(address.suiteNumber().getValue());

        parameters.setCity(tenant.lease().unit().building().info().address().city().getValue());
        parameters.setState(ISOProvince.forName(tenant.lease().unit().building().info().address().province().getValue(), tenant.lease().unit().building()
                .info().address().country().getValue()).code);
        parameters.setPostcode(tenant.lease().unit().building().info().address().postalCode().getValue());

        ISOCountry country = tenant.lease().unit().building().info().address().country().getValue();
        if (!ISOCountry.Canada.equals(country)) {
            throw new UserRuntimeException(i18n.tr("Country \"{0}\" is not supported by TenantSure!", country));
        }
        parameters.setCountryCode(country.iso2);

        parameters.setEmailAddress(tenant.customer().person().email().getValue());
    }
}
