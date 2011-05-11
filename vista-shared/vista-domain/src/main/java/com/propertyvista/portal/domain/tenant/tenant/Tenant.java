/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.domain.tenant.tenant;

import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.portal.domain.Person;
import com.propertyvista.portal.domain.property.asset.AptUnit;
import com.propertyvista.portal.domain.tenant.lease.Lease;

public interface Tenant extends IEntity {

    // TODO make a RentingEntity (May be Company or Government)
    Person rentingEntity();

    AptUnit unit();

    /**
     * Used for DB Denormalization
     */
    Lease lease();

}
