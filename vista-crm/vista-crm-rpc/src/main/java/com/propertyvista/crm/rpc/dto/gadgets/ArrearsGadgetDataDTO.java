/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;

@Transient
public interface ArrearsGadgetDataDTO extends IEntity {

    LeaseAgingBuckets buckets();

    @Caption(name = "Outstanding This Month")
    IPrimitive<Integer> outstandingThisMonthCount();

    @Caption(name = "Outstanding 1 to 30 Days")
    IPrimitive<Integer> outstanding1to30DaysCount();

    @Caption(name = "Outstanding 31 to 60 Days")
    IPrimitive<Integer> outstanding31to60DaysCount();

    @Caption(name = "Outstanding 61 to 90 Days")
    IPrimitive<Integer> outstanding61to90DaysCount();

    @Caption(name = "Outstanding 91+ Days")
    IPrimitive<Integer> outstanding91andMoreDaysCount();

    @Caption(name = "Delinquent Leases")
    IPrimitive<Integer> delinquentLeases();

}
