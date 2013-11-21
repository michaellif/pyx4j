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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.ActionType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.property.asset.unit.AptUnit;
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

    public LeaseChargesMergeStatus mergeBillableItems(List<BillableItem> items, Lease lease, ExecutionMonitor executionMonitor) {
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

            boolean serviceItemRecived = false;

            for (BillableItem item : items) {
                // process new item
                if (isServiceItem(item)) {
                    if (serviceItemRecived) {
                        // This is wrong but we will the items to show.
                        lease.currentTerm().version().leaseProducts().featureItems().add(item);
                        String msg = SimpleMessageFormat.format("multiple serviceItems detected on lease {0}", lease.leaseId());
                        log.info("      " + msg);
                        if (executionMonitor != null) {
                            executionMonitor.addFailedEvent("chargesChanged", msg);
                        }
                    } else {
                        // replace if service
                        lease.currentTerm().version().leaseProducts().serviceItem().set(item);
                        serviceItemRecived = true;
                    }
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

    public Lease updateUnit(AptUnit unit, Lease lease) {
        LeaseTerm newTerm = ServerSideFactory.create(LeaseFacade.class).createOffer(lease, unit, LeaseTerm.Type.FixedEx);
        newTerm.termFrom().setValue(lease.currentTerm().termFrom().getValue());
        newTerm.termTo().setValue(lease.currentTerm().termTo().getValue());

        // process old term:
        if (lease.currentTerm().unit().isNull()) {
            lease.currentTerm().unit().set(lease.unit());
        }
        lease.currentTerm().status().setValue(LeaseTerm.Status.Historic);
        lease.currentTerm().version().setValueDetached(); // TRICK (saving just non-versioned part)!..
        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());

        // set new term:
        lease.unit().set(unit);
        lease.currentTerm().set(newTerm);
        lease.currentTerm().status().setValue(LeaseTerm.Status.Current);

        return ServerSideFactory.create(LeaseFacade.class).persist(lease);
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

    private static LogicalDate getImportedDate(Date date) {
        return (date != null ? new LogicalDate(date) : null);
    }

    /**
     * Sort list of leases by ascending by lease end date, so current and open ended ones go last...
     * 
     * @param rtCustomers
     *            - list to sort
     * @return - sorted input list
     */

    public static List<RTCustomer> sortRtCustomers(List<RTCustomer> rtCustomers) {
        Collections.sort(rtCustomers, new Comparator<RTCustomer>() {
            @Override
            public int compare(RTCustomer c1, RTCustomer c2) {
                int res = 0;

                LogicalDate ltd1 = getImportedDate(c1.getCustomers().getCustomer().get(0).getLease().getLeaseToDate());
                LogicalDate ltd2 = getImportedDate(c2.getCustomers().getCustomer().get(0).getLease().getLeaseToDate());

                if (ltd1 == null) {
                    if (ltd2 == null) {
                        res = 0; // both are null - assume they are the same
                    } else {
                        res = 1; // first is null - it is greater 
                    }
                } else {
                    if (ltd2 == null) {
                        res = -1; // second is null - first is less 
                    } else {
                        res = ltd1.compareTo(ltd2);
                    }
                }

                return res;
            }
        });

        return rtCustomers;
    }
}
