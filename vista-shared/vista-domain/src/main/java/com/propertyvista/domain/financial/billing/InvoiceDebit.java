package com.propertyvista.domain.financial.billing;

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2012
 * @author michaellif
 * @version $Id$
 */

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

@AbstractEntity
public interface InvoiceDebit extends InvoiceLineItem {

    public enum DebitType {
        lease, parking, pet, addOn, utility, locker, booking, deposit, accountCharge, nsf, latePayment, target, other, total
    }

    ISet<DebitCreditLink> creditLinks();

    IPrimitive<DebitType> debitType();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> outstandingDebit();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> dueDate();

    @Owned
    IList<InvoiceChargeTax> taxes();

    @Format("#0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> taxTotal();
}
