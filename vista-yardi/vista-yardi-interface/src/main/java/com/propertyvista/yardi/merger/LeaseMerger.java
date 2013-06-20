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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final static Logger log = LoggerFactory.getLogger(LeaseMerger.class);

    public enum LeaseChargesMergeStatus {
        NoChange, DatesOnly, TotalAmount
    }

    private boolean isNew = false;

    public boolean isLeaseDatesChanged(YardiLease imported, Lease existing) {
        isNew = false;

        compare(existing.expectedMoveIn(), imported.getExpectedMoveInDate());
        compare(existing.actualMoveIn(), imported.getActualMoveIn());

        compare(existing.expectedMoveOut(), imported.getExpectedMoveOutDate());

        return isNew;
    }

    public Lease mergeLeaseDates(YardiLease imported, Lease existing) {
        existing.expectedMoveIn().setValue(getImportedDate(imported.getExpectedMoveInDate()));
        existing.actualMoveIn().setValue(getImportedDate(imported.getActualMoveIn()));

        existing.expectedMoveOut().setValue(getImportedDate(imported.getExpectedMoveOutDate()));

        return existing;
    }

    public boolean isTermDatesChanged(YardiLease imported, LeaseTerm existing) {
        isNew = false;

        compare(existing.termFrom(), YardiLeaseProcessor.guessFromDate(imported));
        compare(existing.termTo(), imported.getLeaseToDate());

        return isNew;
    }

    public LeaseTerm mergeTermDates(YardiLease imported, LeaseTerm existing) {
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

    public LeaseChargesMergeStatus mergeBillableItems(List<BillableItem> items, Lease lease) {
        LeaseChargesMergeStatus mergeStatus = LeaseChargesMergeStatus.NoChange;
        List<BillableItem> lookupList = new ArrayList<BillableItem>(lease.currentTerm().version().leaseProducts().featureItems());
        for (BillableItem item : items) {
            BillableItem leaseItem = null;
            LeaseChargesMergeStatus lookupStatus = compareBillableItems(item, lease.currentTerm().version().leaseProducts().serviceItem());
            if (LeaseChargesMergeStatus.NoChange.equals(lookupStatus)) {
                log.debug("      existing service: {} - {}", item.extraData().<YardiLeaseChargeData> cast().chargeCode().getValue(), item.agreedPrice()
                        .getValue());
                continue;
            } else if (lookupStatus == null) {
                leaseItem = findBillableItem(item, lookupList);
                lookupStatus = (leaseItem == null ? null : compareBillableItems(item, leaseItem));
                if (LeaseChargesMergeStatus.NoChange.equals(lookupStatus)) {
                    log.debug("      existing feature: {} - {}", item.extraData().<YardiLeaseChargeData> cast().chargeCode().getValue(), item.agreedPrice()
                            .getValue());
                    continue;
                }
            }
            log.debug("      new or modified item: {} - {}", item.extraData().<YardiLeaseChargeData> cast().chargeCode().getValue(), item.agreedPrice()
                    .getValue());
            // update mergeStatus to the highest lookupStatus
            if (lookupStatus == null) {
                // new product added
                mergeStatus = LeaseChargesMergeStatus.TotalAmount;
            } else if (lookupStatus.compareTo(mergeStatus) > 0) {
                mergeStatus = lookupStatus;
            }
        }
        // see if we have expired items
        if (!lookupList.isEmpty()) {
            mergeStatus = LeaseChargesMergeStatus.TotalAmount;
        }
        // if changes detected - set new lease products
        if (!LeaseChargesMergeStatus.NoChange.equals(mergeStatus)) {
            lease.currentTerm().version().leaseProducts().featureItems().clear();
            for (BillableItem item : items) {
                // process new item
                if (isServiceItem(item)) {
                    // replace if service
                    lease.currentTerm().version().leaseProducts().serviceItem().set(item);
                } else {
                    // new feature - add it
                    lease.currentTerm().version().leaseProducts().featureItems().add(item);
                }
            }
        }
        return mergeStatus;
    }

    public BillableItem findBillableItem(BillableItem item, List<BillableItem> items) {
        Iterator<BillableItem> it = items.iterator();
        while (it.hasNext()) {
            BillableItem leaseItem = it.next();
            if (item.uid().compareTo(leaseItem.uid()) == 0) {
                it.remove();
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

    private LeaseChargesMergeStatus compareBillableItems(BillableItem item1, BillableItem item2) {
        if (item1.uid().compareTo(item2.uid()) != 0) {
            return null;
        }

        LeaseChargesMergeStatus result = LeaseChargesMergeStatus.NoChange;
        if (item1.expirationDate().compareTo(item2.expirationDate()) != 0 || item1.effectiveDate().compareTo(item2.effectiveDate()) != 0) {
            result = LeaseChargesMergeStatus.DatesOnly;
        }
        if (item1.agreedPrice().compareTo(item2.agreedPrice()) != 0) {
            result = LeaseChargesMergeStatus.TotalAmount;
        }
        return result;
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
