/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.merger;

import java.util.Date;

import com.yardi.entity.mits.YardiLease;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.PaymentAccepted;

public class LeaseMerger {

    boolean isNew = false;

    public Lease mergeLease(YardiLease imported, Lease existing) {
        existing.actualMoveIn().setValue(getImportedDate(imported.getActualMoveIn()));
        existing.actualMoveOut().setValue(getImportedDate(imported.getActualMoveOut()));
        existing.expectedMoveIn().setValue(getImportedDate(imported.getExpectedMoveInDate()));
        // TODO expected move out is automatically calculated for our leases, what to do with one we get from yardi?
//        existing.expectedMoveOut().setValue(getImportedDate(imported.getExpectedMoveOutDate()));
        return existing;
    }

    public LeaseTerm updateTerm(YardiLease imported, LeaseTerm existing) {
        existing.termFrom().setValue(getImportedDate(imported.getLeaseFromDate()));
        existing.termTo().setValue(getImportedDate(imported.getLeaseToDate()));
        return existing;
    }

    public boolean validateTermChanges(YardiLease imported, LeaseTerm existing) {
        compare(existing.termFrom(), imported.getLeaseFromDate());
        compare(existing.termTo(), imported.getLeaseToDate());
        return isNew;
    }

    public boolean validateLeaseChanges(YardiLease imported, Lease existing) {
        compare(existing.actualMoveIn(), imported.getActualMoveIn());
        compare(existing.actualMoveOut(), imported.getActualMoveOut());
        compare(existing.expectedMoveIn(), imported.getExpectedMoveInDate());
        // TODO expected move out is automatically calculated for our leases, what to do with one we get from yardi?
//        compare(existing.expectedMoveOut(), imported.getExpectedMoveOutDate());
        return isNew;
    }

    private void compare(IPrimitive<LogicalDate> existing, Date imported) {
        LogicalDate importedDate = getImportedDate(imported);
        if ((existing.isNull() && importedDate != null) || (!existing.isNull() && importedDate == null)) {
            isNew = true;
            return;
        } else if (!existing.isNull() && importedDate != null) {
            if (!importedDate.equals(existing.getValue())) {
                isNew = true;
            }
        }
    }

    private LogicalDate getImportedDate(Date date) {
        if (date != null) {
            return new LogicalDate(date);
        } else {
            return null;
        }
    }

    public boolean validatePaymentTypeChanger(String paymentAccepted, LeaseTerm currentTerm) {
        if (!currentTerm.paymentAccepted().getValue().equals(getPaymentType(paymentAccepted))) {
            return true;
        }
        return false;
    }

    public PaymentAccepted getPaymentType(String type) {
        if (type.equals("0")) {
            return PaymentAccepted.Any;
        } else if (type.equals("1")) {
            return PaymentAccepted.DoNotAccept;
        } else if (type.equals("2")) {
            return PaymentAccepted.CashEquivalent;
        } else {
            return PaymentAccepted.Any;
        }

    }
}
