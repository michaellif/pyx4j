/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.reports;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.tenant.lease.Tenant;

@Transient
public interface ResidentInsuranceStatusDTO extends IEntity {

    IPrimitive<Boolean> hasResidentInsurance();

    IPrimitive<String> namesOnLease();

    Tenant namesOnLease_();

    IPrimitive<String> building();

    IPrimitive<String> address();

    @Caption(name = "Postal/Zip")
    IPrimitive<String> postalCode();

    IPrimitive<String> unit();

    IPrimitive<String> provider();

    @Format("#,##0")
    IPrimitive<BigDecimal> liabilityCoverage();

    IPrimitive<LogicalDate> startDate();

    IPrimitive<LogicalDate> expiryDate();

    IPrimitive<String> certificate();

    Tenant certificate_();

    // TODO this cannot work beacuse file can have more than one url
    IPrimitive<String> certificateFileUrl();

}
