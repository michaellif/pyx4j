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

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.server.common.util.LeaseManager;
import com.propertyvista.server.common.util.LeaseManager.TimeContextProvider;
import com.propertyvista.server.financial.billing.BillingFacade;

public class LeaseLifecycleSim {

    public Lease newLease(final LogicalDate eventDate, String leaseId, AptUnit unit, LogicalDate leaseFrom, LogicalDate leaseTo, LogicalDate expectedMoveIn,
            PaymentFrequency paymentFrequency, Customer tenant) {
        Lease lease = leaseManager(eventDate).create(leaseId, Service.Type.residentialUnit, unit, leaseFrom, leaseTo);
        lease.createDate().setValue(eventDate);

        lease.version().expectedMoveIn().setValue(expectedMoveIn);

        if (tenant != null) {
            Tenant tenantInLease = EntityFactory.create(Tenant.class);
            tenantInLease.leaseV().set(lease.version());
            tenantInLease.customer().set(tenant);
            tenantInLease.orderInLease().setValue(1);
            tenantInLease.role().setValue(Tenant.Role.Applicant);
            lease.version().tenants().add(tenantInLease);
        }

        updateLease(lease);

        leaseManager(eventDate).save(lease);

        return lease;
    }

    // emulate invitation to online application:
    public static Lease startApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.ApplicationInProgress);
        Persistence.secureSave(lease);

        Persistence.service().retrieve(lease.application());
        lease.application().status().setValue(com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication.Status.Incomplete);
        Persistence.service().merge(lease.application());

        return lease;
    }

    public Lease approveApplication(Key leaseId, LogicalDate eventDate) {
        return leaseManager(eventDate).approveApplication(leaseId);
    }

    public Lease declineApplication(Key leaseId, LogicalDate eventDate) {
        return leaseManager(eventDate).declineApplication(leaseId);
    }

    public Lease cancelApplication(Key leaseId, LogicalDate eventDate) {
        return leaseManager(eventDate).cancelApplication(leaseId);
    }

    public Lease activate(Key leaseId, LogicalDate eventDate) {
        // confirm latest bill before activation :
        if (!VistaTODO.removedForProduction) {
            Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
            BillingFacade billing = ServerSideFactory.create(BillingFacade.class);
            billing.confirmBill(billing.getLatestBill(lease));
        }
        return leaseManager(eventDate).activate(leaseId);
    }

    public Lease notice(Key leaseId, LogicalDate noticeDay, LogicalDate moveOutDay) {
        return leaseManager(noticeDay).notice(leaseId, noticeDay, moveOutDay);
    }

    public Lease cancelNotice(Key leaseId, LogicalDate cancelDay) {
        return leaseManager(cancelDay).cancelNotice(leaseId);
    }

    public Lease evict(Key leaseId, LogicalDate evictionDay, LogicalDate moveOutDay) {
        return leaseManager(evictionDay).evict(leaseId, evictionDay, moveOutDay);
    }

    public Lease cancelEvict(Key leaseId, LogicalDate cancellationDay) {
        return leaseManager(cancellationDay).cancelEvict(leaseId);
    }

    /** completes the lease and makes the unit "available" */
    public Lease complete(Key leaseId, final LogicalDate completionDay) {
        Lease lease;
        Persistence.service().setTransactionSystemTime(completionDay);
        try {
            lease = leaseManager(completionDay).complete(leaseId);
            ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(lease.unit().getPrimaryKey());
        } finally {
            Persistence.service().setTransactionSystemTime(null);
        }
        return lease;
    }

    public Lease close(Key leaseId, LogicalDate closingDay) {
        return leaseManager(closingDay).close(leaseId);
    }

    private LeaseManager leaseManager(final LogicalDate eventDate) {
        return new LeaseManager(new TimeContextProvider() {
            @Override
            public LogicalDate getTimeContext() {
                return eventDate;
            }
        });
    }

    public static void updateLease(Lease lease) {
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

}
