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

import com.propertyvista.domain.Document;
import com.propertyvista.domain.marketing.yield.Concession;
import com.propertyvista.domain.property.asset.Rentable;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.Pets;

public interface Lease extends IEntity {

    IPrimitive<String> leaseID();

    AptUnit unit();

    Application application();

    // Dates:
    IPrimitive<Date> leaseFrom();

    IPrimitive<Date> leaseTo();

    IPrimitive<Date> expectedMoveIn();

    IPrimitive<Date> expectedMoveOut();

    IPrimitive<Date> actualMoveIn();

    IPrimitive<Date> actualMoveOut();

    IPrimitive<Date> signDate();

    // Financial:
    IPrimitive<String> accountNumber();

    IPrimitive<Double> currentRent();

    IPrimitive<String> paymentAccepted();

    IList<ChargeLine> charges();

    IList<Concession> concessions();

    IPrimitive<String> specialStatus();

    // Lists:
    IList<Tenant> tenants();

    IList<Pets> pets();

    IList<Rentable> rentableItems();

    // TODO : there are utilities in the Unit already... is it the same? 
    IList<Utility> utilities();

    IList<Document> documents();

    IList<LeaseEvent> events();
}
