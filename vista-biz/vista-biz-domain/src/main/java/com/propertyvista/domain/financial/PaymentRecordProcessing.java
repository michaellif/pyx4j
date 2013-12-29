/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.core.IEntity;

public interface PaymentRecordProcessing extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    PaymentRecord paymentRecord();

    interface RejectedBatchAggregatedTransferId extends ColumnId {
    }

    @JoinColumn(RejectedBatchAggregatedTransferId.class)
    @ReadOnly(allowOverrideNull = true)
    AggregatedTransfer aggregatedTransfer();
}
