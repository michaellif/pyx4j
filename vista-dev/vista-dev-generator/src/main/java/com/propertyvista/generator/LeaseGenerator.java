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
package com.propertyvista.generator;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.biz.tenant.LeaseFacade;
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
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
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
        LogicalDate effectiveAvailableForRent = new LogicalDate(Math.max(unit._availableForRent().getValue().getTime(), RandomUtil
                .randomLogicalDate(2012, 2012).getTime()));
        LogicalDate createdDate = new LogicalDate(effectiveAvailableForRent.getTime() + Math.abs(random().nextLong()) % MAX_CREATE_WAIT);

        LogicalDate leaseFrom = new LogicalDate(createdDate.getTime() + Math.abs(random().nextLong()) % MAX_RESERVED_DURATION);
        LogicalDate leaseTo = new LogicalDate(Math.max(new LogicalDate().getTime(), leaseFrom.getTime()) + MIN_LEASE_DURATION + Math.abs(random().nextLong())
                % (MAX_LEASE_DURATION - MIN_LEASE_DURATION));
        LogicalDate expectedMoveIn = leaseFrom; // for simplicity's sake

        Lease lease = ServerSideFactory.create(LeaseFacade.class).create(Lease.Status.Application);

        lease.currentTerm().termFrom().setValue(leaseFrom);
        lease.currentTerm().termTo().setValue(leaseTo);
        lease.expectedMoveIn().setValue(expectedMoveIn);

        ServerSideFactory.create(LeaseFacade.class).setUnit(lease, unit);
        addTenants(lease);

        lease.creationDate().setValue(createdDate);

        return lease;
    }

    private void addTenants(Lease lease) {
        Tenant mainTenant = EntityFactory.create(Tenant.class);
        mainTenant.leaseCustomer().customer().set(customerGenerator.createCustomer());
        mainTenant.leaseCustomer().customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
        mainTenant.leaseCustomer().customer().personScreening().set(screeningGenerator.createScreening());
        mainTenant.role().setValue(LeaseParticipant.Role.Applicant);
        mainTenant.percentage().setValue(new BigDecimal(1));
        lease.currentTerm().version().tenants().add(mainTenant);

        addPreathorisedPaymentMethod(mainTenant);
        Guarantor guarantor = EntityFactory.create(Guarantor.class);
        guarantor.leaseCustomer().customer().set(customerGenerator.createCustomer());
        guarantor.leaseCustomer().customer().personScreening().set(screeningGenerator.createScreening());
        guarantor.role().setValue(LeaseParticipant.Role.Guarantor);
        guarantor.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
        guarantor.tenant().set(mainTenant.leaseCustomer());
        lease.currentTerm().version().guarantors().add(guarantor);

        int maxTenants = RandomUtil.randomInt(config.numTenantsInLease);
        for (int t = 0; t < maxTenants; t++) {
            Tenant tenant = EntityFactory.create(Tenant.class);
            tenant.leaseCustomer().customer().set(customerGenerator.createCustomer());
            tenant.leaseCustomer().customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
            tenant.leaseCustomer().customer().personScreening().set(screeningGenerator.createScreening());

            tenant.role().setValue(RandomUtil.random(EnumSet.of(LeaseParticipant.Role.CoApplicant, LeaseParticipant.Role.Dependent)));
            tenant.percentage().setValue(BigDecimal.ZERO);
            tenant.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
            tenant.takeOwnership().setValue(RandomUtil.randomBoolean());

            lease.currentTerm().version().tenants().add(tenant);
        }
    }

    public static String paddZerro(int i, int lenght) {
        String value = String.valueOf(i);
        int len = value.length();
        if (len > lenght) {
            throw new IllegalArgumentException();
        }
        return CommonsStringUtils.padding(lenght - len, '0') + value;
    }

    private void addPreathorisedPaymentMethod(Tenant tenant) {
        PaymentMethod m = EntityFactory.create(PaymentMethod.class);
        m.type().setValue(PaymentType.Echeck);
        m.isOneTimePayment().setValue(Boolean.FALSE);
        m.isDeleted().setValue(Boolean.FALSE);

        // create new payment method details:
        EcheckInfo details = EntityFactory.create(EcheckInfo.class);
        details.nameOn().setValue(tenant.leaseCustomer().customer().person().name().getStringView());
        details.bankId().setValue(paddZerro(RandomUtil.randomInt(999), 3));
        details.branchTransitNumber().setValue(paddZerro(RandomUtil.randomInt(99999), 5));
        details.accountNo().number().setValue(Integer.toString(RandomUtil.randomInt(99999)) + Integer.toString(RandomUtil.randomInt(999999)));
        details.accountNo().obfuscatedNumber().setValue(DomainUtil.obfuscateAccountNumber(details.accountNo().number().getValue()));
        m.details().set(details);

        m.customer().set(tenant.leaseCustomer().customer());
        m.sameAsCurrent().setValue(Boolean.FALSE);
        m.billingAddress().set(CommonsGenerator.createAddress());

        tenant.leaseCustomer().customer().paymentMethods().add(m);
    }

    public static void attachDocumentData(Lease lease) {
        for (Tenant tenant : lease.currentTerm().version().tenants()) {
            ScreeningGenerator.attachDocumentData(tenant.leaseCustomer().customer().personScreening());
        }
    }

    public static void assigneLeaseProducts(Lease lease) {
        EntityQueryCriteria<ProductItem> serviceCriteria = EntityQueryCriteria.create(ProductItem.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ServiceItemType.class));
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().element(), lease.unit()));
        ProductItem serviceItem = Persistence.service().retrieve(serviceCriteria);
        if (serviceItem != null) {
            lease.currentTerm().version().leaseProducts().serviceItem().set(createBillableItem(serviceItem, lease.currentTerm().termFrom().getValue()));

            Persistence.service().retrieve(serviceItem.product());
            Service selectedService = ((Service.ServiceV) serviceItem.product().cast()).holder();

            Building building = lease.unit().building();
            Persistence.service().retrieve(building);
            Persistence.service().retrieve(building.productCatalog());

            // pre-populate mandatory features for the new service:
            Persistence.service().retrieve(selectedService.version().features());
            for (Feature feature : selectedService.version().features()) {
                if (feature.version().mandatory().isBooleanTrue()) {
                    Persistence.service().retrieve(feature.version().items());
                    for (ProductItem item : feature.version().items()) {
                        if (item.isDefault().isBooleanTrue()) {
                            lease.currentTerm().version().leaseProducts().featureItems()
                                    .add(createBillableItem(item, lease.currentTerm().termFrom().getValue()));
                        }
                    }
                }
            }

            // pre-populate concessions for the new service:
            Persistence.service().retrieve(selectedService.version().concessions());
            if (!selectedService.version().concessions().isEmpty()) {
                lease.currentTerm().version().leaseProducts().concessions().add(RandomUtil.random(selectedService.version().concessions()));
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
