/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 18, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.tenant.access;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.essentials.rpc.report.ReportColumn;

import com.propertyvista.dto.TenantPortalAccessInformationDTO;

@Transient
public interface TenantPortalAccessInformationPerLeaseDTO extends TenantPortalAccessInformationDTO {

    @Override
    @Caption(name = "Tenant Name Full 1")
    IPrimitive<String> tenantNameFull();

    @Override
    @Caption(name = "Portal Registration Token 1")
    IPrimitive<String> portalRegistrationToken();

    @ReportColumn(ignore = true)
    IPrimitive<Integer> tenantsCount();

    IPrimitive<String> tenantNameFull2();

    IPrimitive<String> portalRegistrationToken2();

    IPrimitive<String> tenantNameFull3();

    IPrimitive<String> portalRegistrationToken3();

    IPrimitive<String> tenantNameFull4();

    IPrimitive<String> portalRegistrationToken4();

    IPrimitive<String> tenantNameFull5();

    IPrimitive<String> portalRegistrationToken5();

    IPrimitive<String> tenantNameFull6();

    IPrimitive<String> portalRegistrationToken6();
}
