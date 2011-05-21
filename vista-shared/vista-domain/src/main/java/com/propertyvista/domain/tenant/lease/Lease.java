/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.util.Date;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Rentable;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.Pets;

public interface Lease extends IEntity {

    IPrimitive<String> leaseID();

    AptUnit unit();

    IList<Concession> concessions();

    ISet<Tenant> tenants();

    ISet<Pets> pets();

    IList<Rentable> renableItems();

    Application application();

    IPrimitive<Double> currentRent();

    IPrimitive<Date> expectedMoveInDate();

    IPrimitive<Date> expectedMoveOutDate();

    IPrimitive<Date> leaseFromDate();

    IPrimitive<Date> leaseToDate();

    IPrimitive<Date> actualMoveIn();

    IPrimitive<Date> actualMoveOut();

    IPrimitive<Date> leaseSignDate();

    IPrimitive<String> specialStatus();

    IPrimitive<String> paymentAccepted();

    IPrimitive<String> accountNumber();
}
