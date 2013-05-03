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
 * @version $Id$
 */
package com.propertyvista.crm.rpc.dto.gadgets;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.billing.AgingBuckets;

@Transient
public interface ArrearsGadgetDataDTO extends IEntity {

    AgingBuckets buckets();

    /** used for presentation */
    @Caption(name = "Outstanding This Month")
    IPrimitive<String> outstandingThisMonth();

    IPrimitive<Integer> outstandingThisMonthCount();

    /** used for presentation */
    @Caption(name = "Outstanding 1 to 30 Days")
    IPrimitive<String> outstanding1to30Days();

    IPrimitive<Integer> outstanding1to30DaysCount();

    /** used for presentation */
    @Caption(name = "Outstanding 31 to 60 Days")
    IPrimitive<String> outstanding31to60Days();

    IPrimitive<Integer> outstanding31to60DaysCount();

    /** used for presentation */
    @Caption(name = "Outstanding 61 to 90 Days")
    IPrimitive<String> outstanding61to90Days();

    IPrimitive<Integer> outstanding61to90DaysCount();

    /** used for presentation */
    @Caption(name = "Outstanding 91+ Days")
    IPrimitive<String> outstanding91andMoreDays();

    IPrimitive<Integer> outstanding91andMoreDaysCount();

    /** used for presentation */
    IPrimitive<String> outstandingTotal();

    IPrimitive<Integer> delinquentLeases();

}
