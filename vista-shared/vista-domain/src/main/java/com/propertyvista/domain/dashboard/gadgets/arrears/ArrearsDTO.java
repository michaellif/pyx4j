/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.arrears;

import java.math.BigDecimal;

import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.tenant.lease.Lease;

/**
 * 
 */
@Transient
public interface ArrearsDTO extends IEntity {

    Lease lease();

    AgingBuckets buckets();

    @Format("#0.00")
    IPrimitive<BigDecimal> arBalance();

    @Format("#0.00")
    IPrimitive<BigDecimal> prepayments();

    /** {@link #arBalance()} - {@link #prepayments()} */
    @Format("#0.00")
    IPrimitive<BigDecimal> totalBalance();

    /**
     * LMR - last months rent depost.
     */
    @Format("#0.00")
    IPrimitive<BigDecimal> lmrToUnitRentDifference();

}
