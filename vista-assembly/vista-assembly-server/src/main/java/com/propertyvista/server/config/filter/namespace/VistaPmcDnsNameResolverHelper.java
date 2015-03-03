/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 3, 2015
 * @author ernestog
 */
package com.propertyvista.server.config.filter.namespace;

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.PmcDNSUtils;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.security.common.VistaApplication;

public class VistaPmcDnsNameResolverHelper {

    public static PmcDnsName getCustomPmcDNSName(String domain) {
        EntityQueryCriteria<PmcDnsName> criteria = EntityQueryCriteria.create(PmcDnsName.class);
        criteria.eq(criteria.proto().enabled(), Boolean.TRUE);
        criteria.eq(criteria.proto().dnsName(), domain);
        criteria.eq(criteria.proto().pmc().status(), PmcStatus.Active);

        return Persistence.service().retrieve(criteria);
    }

    public static PmcDnsName getCustomerPmcDnsNameForApplication(HttpServletRequest httpRequest, VistaApplication app) {
        PmcDnsName customerDnsName = getCustomPmcDNSName(httpRequest.getServerName());
        if (customerDnsName != null && (customerDnsName.target().getValue() == PmcDNSUtils.getDnsNameTargetByVistaApplication(app))) {
            return customerDnsName;
        } else {
            return null;
        }
    }

    public static boolean isCustomerDNSActive(PmcDnsName customerDnsName) {
        if (customerDnsName == null) {
            return false;
        }

        return (customerDnsName.enabled().getValue());
    }
}
