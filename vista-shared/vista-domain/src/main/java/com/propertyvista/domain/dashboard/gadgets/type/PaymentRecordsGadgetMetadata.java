/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 29, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.dashboard.gadgets.type;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;

@Caption(name = "Payment Records", description = "TBD")
@DiscriminatorValue("PaymentRecordsGadgetMetadata")
public interface PaymentRecordsGadgetMetadata extends ListerGadgetBaseMetadata {

    /**
     * Payment type filter.
     * When <code>null</code> the gadget should display all payment records.
     */
    IPrimitiveSet<PaymentType> paymentMethodFilter();

    /**
     * Payment status filter.
     */
    IPrimitiveSet<PaymentRecord.PaymentStatus> paymentStatusFilter();

    IPrimitive<Boolean> customizeTargetDate();

    @NotNull
    IPrimitive<LogicalDate> targetDate();

}
