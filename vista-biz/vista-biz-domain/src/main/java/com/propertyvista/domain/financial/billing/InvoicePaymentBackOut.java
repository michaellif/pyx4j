/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial.billing;

import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.domain.financial.PaymentRecord;

@DiscriminatorValue("PaymentBackOut")
public interface InvoicePaymentBackOut extends InvoiceDebit {

    interface PaymentRecordColumnId extends ColumnId {
    }

    @JoinColumn(PaymentRecordColumnId.class)
    PaymentRecord paymentRecord();

    IPrimitive<Boolean> applyNSF();

}
