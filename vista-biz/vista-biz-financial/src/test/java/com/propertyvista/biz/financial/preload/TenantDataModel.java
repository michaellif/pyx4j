/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.preload;

import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.person.Person.Sex;
import com.propertyvista.domain.tenant.Customer;

public class TenantDataModel {

    private Customer tenant;

    public TenantDataModel(PreloadConfig config) {
    }

    public void generate() {
        tenant = EntityFactory.create(Customer.class);
        tenant.person().name().firstName().setValue("Robinson");
        tenant.person().name().lastName().setValue("Crusoe");
        tenant.person().sex().setValue(Sex.Male);
        tenant.person().mobilePhone().setValue("647-555-5555");
        tenant.person().homePhone().setValue("647-333-7777");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(calendar.YEAR, 1980);
        calendar.set(calendar.MONTH, 1);
        calendar.set(calendar.DAY_OF_MONTH, 1);
        tenant.person().birthDate().setValue(new LogicalDate(calendar.getTime()));
        Persistence.service().persist(tenant);
    }

    public IEntity getTenant() {
        return tenant;
    }

}
