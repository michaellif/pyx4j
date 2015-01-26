/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-09-20
 * @author ArtyomB
 */
package com.propertyvista.domain.legal.n4.pdf;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.contact.InternationalAddress;
import com.propertyvista.domain.contact.LegalAddress;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

@Transient
public interface N4PdfLeaseData extends IEntity {

    IPrimitive<String> landlordName();

    InternationalAddress landlordAddress();

    IList<LeaseTermTenant> leaseTenants();

    LegalAddress rentalUnitAddress();

    IPrimitive<LogicalDate> terminationDate();

    IPrimitive<BigDecimal> totalRentOwning();

    IList<N4PdfRentOwingForPeriod> rentOwingBreakdown();

}
