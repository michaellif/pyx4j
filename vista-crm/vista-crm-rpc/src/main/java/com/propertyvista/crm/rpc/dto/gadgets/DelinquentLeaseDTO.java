/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.property.asset.building.Building;

@Transient
public interface DelinquentLeaseDTO extends IEntity {

    IPrimitive<Key> leasePrimaryKey();

    @Caption(name = "Building")
    IPrimitive<String> buildingPropertyCode();

    @Caption(name = "Unit")
    IPrimitive<String> unitNumber();

    @Caption(name = "Lease")
    IPrimitive<String> leaseId();

    @Caption(name = "Tenant ID")
    IPrimitive<String> participantId();

    @Caption(name = "Fist Name")
    IPrimitive<String> primaryApplicantsFirstName();

    @Caption(name = "Last Name")
    IPrimitive<String> primaryApplicantsLastName();

    IPrimitive<String> mobilePhone();

    IPrimitive<String> homePhone();

    IPrimitive<String> workPhone();

    IPrimitive<String> email();

    LeaseAgingBuckets arrears();

    // This is used to bind criteria
    IPrimitive<LogicalDate> asOf();

    Building building();

}
