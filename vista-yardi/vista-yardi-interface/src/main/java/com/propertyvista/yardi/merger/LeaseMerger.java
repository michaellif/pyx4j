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
import java.util.List;

import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.ActionType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.extradata.YardiLeaseChargeData;
import com.propertyvista.yardi.services.ARCodeAdapter;
import com.propertyvista.yardi.services.YardiLeaseProcessor;

public class LeaseMerger {

    private boolean isNew = false;

    public boolean isLeaseChanged(YardiLease imported, Lease existing) {
        isNew = false;

        compare(existing.actualMoveIn(), imported.getActualMoveIn());
        compare(existing.actualMoveOut(), imported.getActualMoveOut());
        compare(existing.expectedMoveIn(), imported.getExpectedMoveInDate());
        // TODO expected move out is automatically calculated for our leases, what to do with one we get from yardi?
        //        compare(existing.expectedMoveOut(), imported.getExpectedMoveOutDate());

        return isNew;
    }

    public Lease mergeLease(YardiLease imported, Lease existing) {
        existing.actualMoveIn().setValue(getImportedDate(imported.getActualMoveIn()));
        existing.actualMoveOut().setValue(getImportedDate(imported.getActualMoveOut()));
        existing.expectedMoveIn().setValue(getImportedDate(imported.getExpectedMoveInDate()));
        // TODO expected move out is automatically calculated for our leases, what to do with one we get from yardi?
//        existing.expectedMoveOut().setValue(getImportedDate(imported.getExpectedMoveOutDate()));
        return existing;
    }

    public boolean isTermChanged(YardiLease imported, LeaseTerm existing) {
        isNew = false;

        compare(existing.termFrom(), YardiLeaseProcessor.guessFromDateNoThrow(imported));
        compare(existing.termTo(), imported.getLeaseToDate());

        return isNew;
    }

    public LeaseTerm mergeTerm(YardiLease imported, LeaseTerm existing) {
        existing.termFrom().setValue(YardiLeaseProcessor.guessFromDate(imported));
        existing.termTo().setValue(getImportedDate(imported.getLeaseToDate()));
        return existing;
    }

    public boolean isPaymentTypeChanged(RTCustomer rtCustomer, Lease lease) {
        if (!lease.billingAccount().paymentAccepted().getValue().equals(BillingAccount.PaymentAccepted.getPaymentType(rtCustomer.getPaymentAccepted()))) {
            return true;
        }
        return false;
    }

    public void mergePaymentType(RTCustomer rtCustomer, Lease lease) {
        lease.billingAccount().paymentAccepted().setValue(BillingAccount.PaymentAccepted.getPaymentType(rtCustomer.getPaymentAccepted()));
    }

    public boolean mergeBillableItem(BillableItem item, Lease lease) {
        if (//@formatter:off
                itemsEqual(item, lease.currentTerm().version().leaseProducts().serviceItem()) ||
                findBillableItem(item, lease.currentTerm().version().leaseProducts().featureItems()) != null
        ) {//@formatter:on
            return false;
        }
        // add new item
        if (isServiceItem(item)) {
            lease.currentTerm().version().leaseProducts().serviceItem().set(item);
        } else {
            lease.currentTerm().version().leaseProducts().featureItems().add(item);
        }
        return true;
    }

    public BillableItem findBillableItem(BillableItem item, List<BillableItem> items) {
        for (BillableItem leaseItem : items) {
            if (itemsEqual(item, leaseItem)) {
                return leaseItem;
            }
        }
        return null;
    }

    // internals
    private boolean isServiceItem(BillableItem item) {
        ARCode arCode = new ARCodeAdapter().retrieveARCode(ActionType.Debit, item.extraData().<YardiLeaseChargeData> cast().chargeCode().getValue());
        return arCode != null && ARCode.Type.Residential.equals(arCode.type().getValue());
    }

    private boolean itemsEqual(BillableItem item1, BillableItem item2) {
        return// @formatter:off
                item1.uid().equals(item2.uid()) &&
                item1.agreedPrice().equals(item2.agreedPrice()) &&
                ((item1.expirationDate().isNull() && item2.expirationDate().isNull()) || item1.expirationDate().equals(item2.expirationDate())) &&
                ((item1.effectiveDate().isNull() && item2.effectiveDate().isNull()) || item1.effectiveDate().equals(item2.effectiveDate()));
        // @formatter:on
    }

    private void compare(IPrimitive<LogicalDate> existing, Date imported) {
        if (!isNew) {
            LogicalDate importedDate = getImportedDate(imported);
            if ((existing.isNull() && importedDate != null) || (!existing.isNull() && importedDate == null)) {
                isNew = true;
            } else if (!existing.isNull() && importedDate != null) {
                if (!importedDate.equals(existing.getValue())) {
                    isNew = true;
                }
            }
        }
    }

    private LogicalDate getImportedDate(Date date) {
        return (date != null ? new LogicalDate(date) : null);
    }
}
