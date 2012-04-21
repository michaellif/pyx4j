/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.util;

import java.util.List;
import java.util.Random;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.misc.VistaTODO;

//TODO refactor this to use LeaseFacade 
public class LeaseLifecycleSim {

    private final static Random RND = new Random(1);

    private static final long MIN_RESERVE_TIME = 0L;

    private static final long MAX_RESERVE_TIME = 1000L * 60L * 60L * 24L * 60L; // 60 days

    private static final long MIN_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L; // approx 1 Year

    private static final long MAX_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L * 5L; // approx 5 Years

    private static final long MIN_NOTICE_TERM = 1000L * 60L * 60L * 24L * 31L;

    private static final long MAX_NOTICE_TERM = 1000L * 60L * 60L * 24L * 60L;

    private static final long MIN_AVAILABLE_TERM = 0;

    private static final long MAX_AVAILABLE_TERM = 1000L * 60L * 60L * 24L * 30L;

    public void generateRandomLeaseLifeCycle(Lease lease) {
        LogicalDate now = new LogicalDate();

        LogicalDate simStart = new LogicalDate(Math.max(lease.unit()._availableForRent().getValue().getTime(), new LogicalDate(2008 - 1900, 1, 1).getTime()));

        LogicalDate eventDate = add(simStart, random(MIN_AVAILABLE_TERM, MAX_AVAILABLE_TERM));
        LogicalDate leaseFrom = add(eventDate, random(MIN_RESERVE_TIME, MAX_RESERVE_TIME));
        LogicalDate leaseTo = add(leaseFrom, random(MIN_LEASE_TERM, MAX_LEASE_TERM));

        lease.leaseFrom().setValue(leaseFrom);
        lease.leaseTo().setValue(leaseTo);

        try {
            do {
                // create lease
                if (eventDate.after(now)) {
                    break;
                }
                //updateLease(lease);
                save(lease, eventDate);

                // approve application
                eventDate = new LogicalDate(random(eventDate.getTime(), lease.leaseFrom().getValue().getTime()));
                if (eventDate.after(now)) {
                    break;
                }
                approveApplication(lease, eventDate);

                // lease starts here
                eventDate = lease.leaseFrom().getValue();
                if (eventDate.after(now)) {
                    break;
                }
                activate(lease.getPrimaryKey(), eventDate);

                // notice about lease end
                eventDate = new LogicalDate(Math.max(lease.leaseFrom().getValue().getTime(),
                        lease.leaseTo().getValue().getTime() - random(MIN_NOTICE_TERM, MAX_NOTICE_TERM)));
                if (eventDate.after(now)) {
                    break;
                }
                notice(lease.getPrimaryKey(), eventDate, lease.leaseTo().getValue());

                // lease complete, the unit is ready for another lease
                eventDate = lease.leaseTo().getValue();
                if (eventDate.after(now)) {
                    break;
                }
                complete(lease, lease.leaseTo().getValue());

            } while (false);
        } finally {
            Persistence.service().setTransactionSystemTime(null);
        }
    }

    // emulate invitation to online application:
    private static Lease startApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.ApplicationInProgress);
        Persistence.secureSave(lease);

        Persistence.service().retrieve(lease.leaseApplication().onlineApplication());
        lease.leaseApplication().onlineApplication().status().setValue(com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication.Status.Incomplete);
        Persistence.service().merge(lease.leaseApplication().onlineApplication());

        return lease;
    }

    private void approveApplication(Lease lease, LogicalDate eventDate) {
        leaseFacade(eventDate).approveApplication(lease, null, "simulation");
    }

    private void declineApplication(Lease leaseId, LogicalDate eventDate) {
        leaseFacade(eventDate).declineApplication(leaseId, null, "simulation");
    }

    private void cancelApplication(Lease leaseId, LogicalDate eventDate) {
        leaseFacade(eventDate).cancelApplication(leaseId, null, "simulation");
    }

    private void activate(Key leaseId, LogicalDate eventDate) {
        // confirm latest bill before activation :
        if (!VistaTODO.removedForProduction) {
            Persistence.service().setTransactionSystemTime(eventDate);
            Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
            BillingFacade billing = ServerSideFactory.create(BillingFacade.class);
            billing.runBilling(lease);
            billing.confirmBill(billing.getLatestBill(lease));
        }
        leaseFacade(eventDate).activate(leaseId);
    }

    private void notice(Key leaseId, LogicalDate noticeDay, LogicalDate moveOutDay) {
        leaseFacade(noticeDay).createCompletionEvent(leaseId, CompletionType.Notice, noticeDay, moveOutDay);
    }

    private void cancelNotice(Key leaseId, LogicalDate cancelDay) {
        leaseFacade(cancelDay).cancelCompletionEvent(leaseId);
    }

    private void evict(Key leaseId, LogicalDate evictionDay, LogicalDate moveOutDay) {
        leaseFacade(evictionDay).createCompletionEvent(leaseId, CompletionType.Eviction, evictionDay, moveOutDay);
    }

    private void cancelEvict(Key leaseId, LogicalDate cancellationDay) {
        leaseFacade(cancellationDay).cancelCompletionEvent(leaseId);
    }

    /** completes the lease and makes the unit "available" */
    private void complete(Lease lease, final LogicalDate completionDay) {
        leaseFacade(completionDay).complete(lease.getPrimaryKey());
        ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(lease.unit().getPrimaryKey());
    }

    private void close(Key leaseId, LogicalDate closingDay) {
        leaseFacade(closingDay).close(leaseId);
    }

    private static void save(Lease lease, LogicalDate eventDate) {
        leaseFacade(eventDate).createLease(lease);
    }

    private static void updateLease(Lease lease) {
        Building building = lease.unit().belongsTo();
        Persistence.service().retrieve(building);

        Service selectedService = null;
        // Proper way
//        {
//            EntityQueryCriteria<ServiceItem> serviceItemCriteria = EntityQueryCriteria.create(ServiceItem.class);
//            serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().element(), lease.unit()));
//            serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().sserviceOrFeatureervice().catalog(), building.serviceCatalog()));
//            ServiceItem item = Persistence.service().retrieve(serviceItemCriteria);
//            if (item != null) {
//                lease.serviceAgreement().serviceItem().set(createChargeItem(item));
//                selectedService = item.service();
//                Persistence.service().retrieve(selectedService);
//                lease.type().set(selectedService.type());
//            }
//        }
        // TODO use the code above when sserviceOrFeatureervice implemented

        if (true) {
            EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.productCatalog()));
            List<Service> services = Persistence.service().query(serviceCriteria);

            for (Service service : services) {
                Persistence.service().retrieve(service.version().items());
                for (ProductItem item : service.version().items()) {
                    if (lease.unit().equals(item.element())) {
                        lease.version().leaseProducts().serviceItem().set(createBillableItem(item, lease.leaseFrom().getValue()));
                        selectedService = service;
                        lease.type().set(selectedService.version().type());
                        break;
                    }
                }
            }
        }

        if (!lease.version().leaseProducts().serviceItem().isEmpty()) {
            Persistence.service().retrieve(building.productCatalog());
            // pre-populate utilities for the new service: 
            Persistence.service().retrieve(selectedService.version().features());
            for (Feature feature : selectedService.version().features()) {
                if (Feature.Type.utility.equals(feature.version().type().getValue())) {
                    Persistence.service().retrieve(feature.version().items());
                    for (ProductItem item : feature.version().items()) {
                        if (!building.productCatalog().includedUtilities().contains(item.type())
                                && !building.productCatalog().externalUtilities().contains(item.type())) {
                            lease.version().leaseProducts().featureItems().add(createBillableItem(item, lease.leaseFrom().getValue()));
                        }
                    }
                }

            }

            // pre-populate concessions for the new service: 
            Persistence.service().retrieve(selectedService.version().concessions());
            if (!selectedService.version().concessions().isEmpty()) {
                lease.version().leaseProducts().concessions().add(RandomUtil.random(selectedService.version().concessions()));
            }
        }
    }

    private static BillableItem createBillableItem(ProductItem serviceItem, LogicalDate effectiveDate) {
        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(serviceItem);
        newItem.effectiveDate().setValue(effectiveDate != null ? effectiveDate : new LogicalDate());
        return newItem;
    }

    private static LeaseFacade leaseFacade(final LogicalDate eventDate) {
        Persistence.service().setTransactionSystemTime(eventDate);
        return ServerSideFactory.create(LeaseFacade.class);
    }

    private long random(long min, long max) {
        assert min <= max;
        if (max == min) {
            return min;
        } else {
            return min + Math.abs(RND.nextLong()) % (max - min);
        }
    }

    private LogicalDate add(LogicalDate date, long term) {
        return new LogicalDate(date.getTime() + term);
    }

}
