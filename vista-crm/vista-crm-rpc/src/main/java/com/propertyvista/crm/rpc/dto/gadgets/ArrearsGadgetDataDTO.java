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

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.billing.AgingBuckets;

@Transient
public interface ArrearsGadgetDataDTO extends IEntity {

    IPrimitive<Integer> delinquentTenants();

    IPrimitive<BigDecimal> bucketThisMonth();

    AgingBuckets buckets();

    /** used for presentation */
    @Caption(name = "This month")
    IPrimitive<String> outstandingThisMonth();

    /** used for presentation */
    @Caption(name = "1 - 30")
    IPrimitive<String> outstanding1to30Days();

    /** used for presentation */
    @Caption(name = "31 - 60")
    IPrimitive<String> outstanding31to60Days();

    @Caption(name = "61 - 90")
    IPrimitive<String> outstanding61to90Days();

    @Caption(name = "90 +")
    IPrimitive<String> outstanding91andMoreDays();

    IPrimitive<String> outstandingTotal();

}
