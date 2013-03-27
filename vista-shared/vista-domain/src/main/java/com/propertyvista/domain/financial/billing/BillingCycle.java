/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author vladlouk
 * @version $Id$
 */

package com.propertyvista.domain.financial.billing;

//TODO - see @Comments for new labels as per VISTA-2605 - alexs

import com.propertyvista.domain.property.asset.building.Building;

@ToStringFormat("{0}, {1}")
@Table(prefix = "billing")
public interface BillingCycle extends IEntity {

    @ToString(index = 0)
    @ReadOnly
    @Owner
    @MemberColumn(notNull = true)
    @Detached
    @JoinColumn
    @Indexed(group = "DBT,2", uniqueConstraint = true)
    Building building();

    @ReadOnly
    @ToString(index = 1)
    @Indexed(group = "DBT,3")
    BillingType billingType();

    // @Comment(name = "Start Day",description = "First day of the Billing Period within Payment Frequency")
    @ReadOnly
    @Indexed(group = "DBT,1")
    IPrimitive<LogicalDate> billingCycleStartDate();

    @ReadOnly
    IPrimitive<LogicalDate> billingCycleEndDate();

    // @Comment(name = "Bill Processing Day", description = "Number of days between Bill Processing Day and Billing Period Start Date") 
    IPrimitive<LogicalDate> targetBillExecutionDate();

    // @Comment(name = "PAD Validation Day", description = "Number of days between PAD Validation Day and Billing Period Start Date") 
    IPrimitive<LogicalDate> targetPadGenerationDate();

    // @Comment(name = "PAD Processing Day", description = "Number of days between PAD Processing Day and Billing Period Start Date") 
    IPrimitive<LogicalDate> padExecutionDate();

    @ReadOnly(allowOverrideNull = true)
    IPrimitive<LogicalDate> actualBillExecutionDate();

    @ReadOnly(allowOverrideNull = true)
    IPrimitive<LogicalDate> actualPadGenerationDate();

    // Used as different entity to avoid transaction isolation problems in HDQLDB
    @Owned
    BillingCycleStats stats();

}
