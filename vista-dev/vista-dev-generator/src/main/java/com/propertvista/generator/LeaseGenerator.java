/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-10
 * @author vlads
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.EnumSet;

import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.misc.VistaDevPreloadConfig;

public class LeaseGenerator extends DataGenerator {

    private final CustomerGenerator customerGenerator;

    private final ScreeningGenerator screeningGenerator;

    protected static final long MAX_CREATE_WAIT = 1000L * 60L * 60L * 24L * 30L;

    protected static final long MAX_RESERVED_DURATION = 1000L * 60L * 60L * 24L * 30L;

    protected static final long MAX_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L * 3L;

    protected static final long MIN_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L;

    private final VistaDevPreloadConfig config;

    public LeaseGenerator(VistaDevPreloadConfig config) {
        this.config = config;
        setRandomSeed(config.leaseGenerationSeed);
        customerGenerator = new CustomerGenerator();
        screeningGenerator = new ScreeningGenerator();
    }

    public Lease createLease(AptUnit unit) {
        Lease lease = EntityFactory.create(Lease.class);

        lease.unit().set(unit);

        LogicalDate effectiveAvailableForRent = new LogicalDate(Math.max(unit._availableForRent().getValue().getTime(), RandomUtil
                .randomLogicalDate(2012, 2012).getTime()));
        LogicalDate createdDate = new LogicalDate(effectiveAvailableForRent.getTime() + Math.abs(random().nextLong()) % MAX_CREATE_WAIT);

        LogicalDate leaseFrom = new LogicalDate(createdDate.getTime() + Math.abs(random().nextLong()) % MAX_RESERVED_DURATION);
        LogicalDate leaseTo = new LogicalDate(Math.max(new LogicalDate().getTime(), leaseFrom.getTime()) + MIN_LEASE_DURATION + Math.abs(random().nextLong())
                % (MAX_LEASE_DURATION - MIN_LEASE_DURATION));
        LogicalDate expectedMoveIn = leaseFrom; // for simplicity's sake

        lease.type().setValue(Service.Type.residentialUnit);
        lease.leaseFrom().setValue(leaseFrom);
        lease.leaseTo().setValue(leaseTo);

        lease.creationDate().setValue(createdDate);
        lease.version().expectedMoveIn().setValue(expectedMoveIn);

        addTenants(lease);

        return lease;
    }

    private void addTenants(Lease lease) {
        Tenant mainTenant = EntityFactory.create(Tenant.class);
        mainTenant.customer().set(customerGenerator.createCustomer());
        mainTenant.customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
        mainTenant.customer()._PersonScreenings().add(screeningGenerator.createScreening());
        mainTenant.screening().set(mainTenant.customer()._PersonScreenings().iterator().next());
        mainTenant.role().setValue(LeaseParticipant.Role.Applicant);
        mainTenant.percentage().setValue(100);
        lease.version().tenants().add(mainTenant);

        addPreathorisedPaymentMethod(mainTenant);
        Guarantor guarantor = EntityFactory.create(Guarantor.class);
        guarantor.customer().set(customerGenerator.createCustomer());
        guarantor.screening().set(screeningGenerator.createScreening());
        guarantor.customer()._PersonScreenings().add(guarantor.screening());
        guarantor.role().setValue(LeaseParticipant.Role.Guarantor);
        guarantor.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
        guarantor.tenant().set(mainTenant);
        lease.version().guarantors().add(guarantor);

        int maxTenants = RandomUtil.randomInt(config.numTenantsInLease);
        for (int t = 0; t < maxTenants; t++) {
            Tenant tenant = EntityFactory.create(Tenant.class);
            tenant.customer().set(customerGenerator.createCustomer());
            tenant.customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
            tenant.screening().set(screeningGenerator.createScreening());
            tenant.customer()._PersonScreenings().add(tenant.screening());

            tenant.role().setValue(RandomUtil.random(EnumSet.of(LeaseParticipant.Role.CoApplicant, LeaseParticipant.Role.Dependent)));
            tenant.percentage().setValue(100);
            tenant.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
            tenant.takeOwnership().setValue(RandomUtil.randomBoolean());

            lease.version().tenants().add(tenant);
        }
    }

    private void addPreathorisedPaymentMethod(Tenant tenant) {

        PaymentMethod m = EntityFactory.create(PaymentMethod.class);
        m.type().setValue(PaymentType.Echeck);
        m.isDefault().setValue(Boolean.TRUE);
        m.isOneTimePayment().setValue(Boolean.FALSE);
        m.isDeleted().setValue(Boolean.FALSE);

        // create new payment method details:
        EcheckInfo details = EntityFactory.create(EcheckInfo.class);
        details.nameOn().setValue(tenant.customer().person().name().getStringView());
        details.bankId().setValue(Integer.toString(RandomUtil.randomInt(999)));
        details.branchTransitNumber().setValue(Integer.toString(RandomUtil.randomInt(99999)));
        details.accountNo().setValue("000000" + Integer.toString(RandomUtil.randomInt(999999)));
        m.details().set(details);

        m.leaseParticipant().set(tenant);
        m.sameAsCurrent().setValue(Boolean.FALSE);
        m.billingAddress().set(CommonsGenerator.createAddress());
        m.phone().setValue(CommonsGenerator.createPhone());

        tenant.paymentMethods().add(m);

    }

    public static void attachDocumentData(Lease lease) {
        for (Tenant tenant : lease.version().tenants()) {
            for (PersonScreening screening : tenant.customer()._PersonScreenings()) {
                ScreeningGenerator.attachDocumentData(screening);
            }
        }
    }

    public static void assigneLeaseProducts(Lease lease) {

        EntityQueryCriteria<ProductItem> serviceCriteria = EntityQueryCriteria.create(ProductItem.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ServiceItemType.class));
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().element(), lease.unit()));
        ProductItem serviceItem = Persistence.service().retrieve(serviceCriteria);
        if (serviceItem != null) {
            lease.version().leaseProducts().serviceItem().set(createBillableItem(serviceItem, lease.leaseFrom().getValue()));

            Persistence.service().retrieve(serviceItem.product());
            Service selectedService = ((Service.ServiceV) serviceItem.product().cast()).holder();

            Building building = lease.unit().belongsTo();
            Persistence.service().retrieve(building);

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
        newItem.agreedPrice().setValue(newItem.item().price().getValue());
        newItem.effectiveDate().setValue(effectiveDate != null ? effectiveDate : new LogicalDate());
        return newItem;
    }
}
