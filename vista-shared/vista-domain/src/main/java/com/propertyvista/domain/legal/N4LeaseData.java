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
 * @version $Id$
 */
package com.propertyvista.domain.legal;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.tenant.lease.Tenant;

@Transient
public interface N4LeaseData extends IEntity {

    IList<Tenant> leaseTenants();

    AddressStructured rentalUnitAddress();

    IPrimitive<LogicalDate> terminationDate();

    IPrimitive<BigDecimal> totalRentOwning();

    IList<N4RentOwingForPeriod> rentOwingBreakdown();

}
