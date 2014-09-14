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
package com.propertyvista.yardi.mergers;

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
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.OccupancyOperationException;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.ActionType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.YardiTrace;
import com.propertyvista.yardi.processors.YardiLeaseProcessor;
import com.propertyvista.yardi.services.ARCodeAdapter;

public class LeaseMerger {

    private final static Logger log = LoggerFactory.getLogger(LeaseMerger.class);

    public static boolean isLeaseDatesChanged(YardiLease imported, Lease existing) {
        boolean changed = false;

        changed |= isChanged(existing.expectedMoveIn(), imported.getExpectedMoveInDate());
        changed |= isChanged(existing.actualMoveIn(), imported.getActualMoveIn());

        changed |= isChanged(existing.expectedMoveOut(), imported.getExpectedMoveOutDate());

        return changed;
    }

    public Lease mergeLeaseDates(YardiLease imported, Lease existing) {
        existing.expectedMoveIn().setValue(getImportedDate(imported.getExpectedMoveInDate()));
        existing.actualMoveIn().setValue(getImportedDate(imported.getActualMoveIn()));

        boolean changed = isChanged(existing.expectedMoveOut(), imported.getExpectedMoveOutDate());
        existing.expectedMoveOut().setValue(getImportedDate(imported.getExpectedMoveOutDate()));
        // update unit availability date in case of noticed lease:
        if (changed && existing.completion().getValue() == CompletionType.Notice) {
            try {
                ServerSideFactory.create(OccupancyFacade.class).moveOut(existing.unit().getPrimaryKey(), existing.expectedMoveOut().getValue(), existing);
            } catch (OccupancyOperationException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }

        return existing;
    }

    public static boolean isTermDatesChanged(YardiLease imported, LeaseTerm existing) {
        boolean changed = false;

        LogicalDate date = YardiLeaseProcessor.guessFromDate(imported);
        if (date != null) {
            changed |= isChanged(existing.termFrom(), date);
        }
        changed |= isChanged(existing.termTo(), imported.getLeaseToDate());

        return changed;
    }

    public LeaseTerm mergeTermDates(YardiLease imported, LeaseTerm existing) {
        LogicalDate date = YardiLeaseProcessor.guessFromDate(imported);
        if (date == null) {
            date = SystemDateManager.getLogicalDate();
            log.warn("Empty Yardi 'Lease From' date - substitute with current date!");
        }
        existing.termFrom().setValue(date);
        existing.termTo().setValue(getImportedDate(imported.getLeaseToDate()));

        // correct term type if yardi changed lease end date:
        if (!existing.termTo().isNull() && existing.type().getValue() == LeaseTerm.Type.Periodic) {
            existing.type().setValue(LeaseTerm.Type.FixedEx);
        }

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

    public boolean mergeBillableItems(List<BillableItem> newItems, List<BillableItem> currentItems, Lease lease, ExecutionMonitor executionMonitor) {
        assert (executionMonitor != null);

        boolean mergeRequired = false;
        if (currentItems.size() != newItems.size()) {
            mergeRequired = true;
        } else {
            for (BillableItem newItem : newItems) {
                BillableItem existing = extractBillableItem(newItem, currentItems);
                if ((existing == null) || (!compareBillableItems(existing, newItem))) {
                    mergeRequired = true;
                    break;
                } else {
                    currentItems.remove(existing);
                }
            }

            if (!mergeRequired) {
                // See if Service item is changed
                boolean serviceItemRecived = false;
                for (BillableItem item : newItems) {
                    // process new item
                    if (isServiceItem(item)) {
                        serviceItemRecived = true;
                        if (!compareBillableItems(item, lease.currentTerm().version().leaseProducts().serviceItem())) {
                            mergeRequired = true;
                        }
                        break;
                    }
                }

                if ((!serviceItemRecived) && !lease.currentTerm().version().leaseProducts().serviceItem().isNull()) {
                    mergeRequired = true;
                }
            }
        }

        if (mergeRequired) {
            lease.currentTerm().version().leaseProducts().serviceItem().set(null);
            lease.currentTerm().version().leaseProducts().featureItems().clear();

            for (BillableItem item : newItems) {
                if (isServiceItem(item)) {
                    if (lease.currentTerm().version().leaseProducts().serviceItem().isNull()) {
                        lease.currentTerm().version().leaseProducts().serviceItem().set(item);
                        if (YardiTrace.trace) {
                            log.debug("set service item agreedPrice {}", item.agreedPrice());
                        }
                    } else {
                        // TODO: add multiple services as features now, then redesign:
                        lease.currentTerm().version().leaseProducts().featureItems().add(item);

                        String msg = SimpleMessageFormat.format("multiple serviceItems detected on lease {0} - added as features", lease.leaseId());
                        log.info("      " + msg);
                        if (executionMonitor != null) {
                            executionMonitor.addFailedEvent("ChargesProblems", msg);
                        }
                    }
                } else {
                    lease.currentTerm().version().leaseProducts().featureItems().add(item);
                }
            }
        }

        return mergeRequired;
    }

    public BillableItem extractBillableItem(BillableItem item, List<BillableItem> items) {
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

    public Lease updateUnit(AptUnit unit, Lease lease, boolean move) {
        // create new term:
        LeaseTerm term = EntityFactory.create(LeaseTerm.class);
        term.status().setValue(LeaseTerm.Status.Current);
        term.type().setValue(LeaseTerm.Type.FixedEx);
        term.lease().set(lease);

        // migrate participants:
        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            term.version().tenants().add(businessDuplicate(tenant));
        }
        Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);
        for (LeaseTermGuarantor guarantor : lease.currentTerm().version().guarantors()) {
            term.version().guarantors().add(businessDuplicate(guarantor));
        }

        // mark old term:
        lease.currentTerm().status().setValue(LeaseTerm.Status.Historic);
        lease.currentTerm().version().setValueDetached(); // TRICK (saving just non-versioned part)!..
        Persistence.service().merge(lease.currentTerm());

        // set new term:
        lease.currentTerm().set(term);

        // update lease unit (for new term):
        ServerSideFactory.create(LeaseFacade.class).setUnit(lease.currentTerm(), unit);
        ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());
        return lease;
    }

    protected <P extends LeaseTermParticipant<?>> P businessDuplicate(P leaseParticipant) {
        // There are no own entities for now,
        Persistence.retrieveOwned(leaseParticipant);
        P copy = EntityGraph.businessDuplicate(leaseParticipant);
        copy.screening().set(null);
        return copy;
    }

    // internals
    private boolean isServiceItem(BillableItem item) {
        ARCode arCode = new ARCodeAdapter().retrieveARCode(ActionType.Debit, item.yardiChargeCode().getValue());
        return arCode != null && ARCode.Type.Residential.equals(arCode.type().getValue());
    }

    private boolean compareBillableItems(BillableItem item1, BillableItem item2) {
        if (item1.uid().compareTo(item2.uid()) != 0) {
            return false;
        }
        if (item1.expirationDate().compareTo(item2.expirationDate()) != 0 || item1.effectiveDate().compareTo(item2.effectiveDate()) != 0) {
            return false;
        }
        if (item1.agreedPrice().compareTo(item2.agreedPrice()) != 0) {
            return false;
        }

        return true;
    }

    private static boolean isChanged(IPrimitive<LogicalDate> existing, Date imported) {
        LogicalDate importedDate = getImportedDate(imported);
        if ((existing.isNull() && importedDate != null) || (!existing.isNull() && importedDate == null)) {
            log.debug("date changed {} {} {}", existing.getFieldName(), existing.getValue(), importedDate);
            return true;
        } else if (!existing.isNull() && importedDate != null) {
            if (!importedDate.equals(existing.getValue())) {
                log.debug("date changed {} {} {}", existing.getFieldName(), existing.getValue(), importedDate);
                return true;
            }
        }
        return false;
    }

    private static LogicalDate getImportedDate(Date date) {
        return (date != null ? new LogicalDate(date) : null);
    }
}
