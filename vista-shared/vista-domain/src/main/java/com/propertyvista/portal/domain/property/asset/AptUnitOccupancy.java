/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.domain.property.asset;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.portal.domain.tenant.lease.Lease;

public interface AptUnitOccupancy extends IEntity {

    @Detached
    @Owner
    AptUnit unit();

    IPrimitive<java.sql.Date> dateFrom();

    IPrimitive<java.sql.Date> dateTo();

    IPrimitive<AptUnitStatusType> status();

    IPrimitive<AptUnitOffMarketType> offMarket();

    IPrimitive<String> description();

    Lease lease();

}
