/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 11, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.BillingAccount;

@Table(prefix = "billing")
public interface ArrearsSnapshot extends IEntity {

    @Owner
    @Detached
    @NotNull
    @Indexed
    @JoinColumn
    BillingAccount billingAccount();

    IPrimitive<LogicalDate> fromDate();

    IPrimitive<LogicalDate> toDate();

    IPrimitive<BigDecimal> arrearsAmount();

    IPrimitive<BigDecimal> creditAmount();

    AgingBuckets totalAgingBuckets();

    @Owned
    IList<AgingBuckets> agingBuckets();

}
