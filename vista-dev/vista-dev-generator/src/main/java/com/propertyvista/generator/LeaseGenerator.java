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
 */
package com.propertyvista.generator;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.EnumSet;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.person.Person.Sex;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.ReferenceSource;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.shared.util.AccountNumberFormatter;

public class LeaseGenerator extends DataGenerator {

    private final CustomerGenerator customerGenerator;

    private final ScreeningGenerator screeningGenerator;

    protected static final long MAX_CREATE_WAIT = 1000L * 60L * 60L * 24L * 30L;

    protected static final long MAX_RESERVED_DURATION = 1000L * 60L * 60L * 24L * 30L;

    protected static final long MAX_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L * 3L;

    protected static final long MIN_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L;

    public static final EnumSet<PersonRelationship> maleDependentRelationships = EnumSet.of(PersonRelationship.Son, PersonRelationship.Friend,
            PersonRelationship.Other);

    public static final EnumSet<PersonRelationship> femaleDependentRelationships = EnumSet.of(PersonRelationship.Daughter, PersonRelationship.Friend,
            PersonRelationship.Other);

    public static final EnumSet<PersonRelationship> coApplicantRelationships = EnumSet.of(PersonRelationship.Spouse, PersonRelationship.Mother,
            PersonRelationship.Father, PersonRelationship.Grandfather, PersonRelationship.Grandmother, PersonRelationship.Uncle, PersonRelationship.Aunt,
            PersonRelationship.Friend, PersonRelationship.Other);

    private final VistaDevPreloadConfig config;

    public LeaseGenerator(VistaDevPreloadConfig config) {
        this.config = config;
        setRandomSeed(config.leaseGenerationSeed);
        customerGenerator = new CustomerGenerator();
        screeningGenerator = new ScreeningGenerator();
    }

    public Lease createLease(AptUnit unit) {
        LogicalDate effectiveAvailableForRent = new LogicalDate(Math.max(unit.availability().availableForRent().getValue().getTime(), RandomUtil
                .randomLogicalDate(2013, 2013).getTime()));
        LogicalDate createdDate = new LogicalDate(effectiveAvailableForRent.getTime() + Math.abs(random().nextLong()) % MAX_CREATE_WAIT);

        LogicalDate leaseFrom = new LogicalDate(createdDate.getTime() + Math.abs(random().nextLong()) % MAX_RESERVED_DURATION);
        LogicalDate leaseTo = new LogicalDate(Math.max(new LogicalDate().getTime(), leaseFrom.getTime()) + MIN_LEASE_DURATION + Math.abs(random().nextLong())
                % (MAX_LEASE_DURATION - MIN_LEASE_DURATION));
        LogicalDate expectedMoveIn = leaseFrom; // for simplicity's sake

        Lease lease = ServerSideFactory.create(LeaseFacade.class).create(Lease.Status.Application);
        lease.leaseApplication().referenceSource().setValue(RandomUtil.randomEnum(ReferenceSource.class));

        lease.currentTerm().termFrom().setValue(leaseFrom);
        lease.currentTerm().termTo().setValue(leaseTo);
        lease.expectedMoveIn().setValue(expectedMoveIn);

        lease.billingAccount().billingPeriod().setValue(BillingPeriod.Monthly);

        ServerSideFactory.create(LeaseFacade.class).setUnit(lease, unit);

        ensureProductPrices(lease);

        lease.creationDate().setValue(createdDate);
        return lease;
    }

    public Lease createLeaseWithTenants(AptUnit unit) {
        Lease lease = createLease(unit);
        addTenants(lease);
        return lease;
    }

    private void addTenants(Lease lease) {
        LeaseTermTenant mainTenant = EntityFactory.create(LeaseTermTenant.class);

        mainTenant.leaseParticipant().customer().set(customerGenerator.createCustomer());
        mainTenant.leaseParticipant().customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
        mainTenant.leaseParticipant().customer().personScreening().set(screeningGenerator.createScreening(lease, mainTenant.leaseParticipant()));
        mainTenant.role().setValue(LeaseTermParticipant.Role.Applicant);

        lease.currentTerm().version().tenants().add(mainTenant);

        addPreathorisedPaymentMethod(mainTenant);

        int maxTenants = RandomUtil.randomInt(config.numTenantsInLease);
        for (int t = 0; t <= maxTenants; t++) {
            LeaseTermTenant tenant = EntityFactory.create(LeaseTermTenant.class);

            tenant.leaseParticipant().customer().set(customerGenerator.createCustomer());
            tenant.leaseParticipant().customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
            tenant.leaseParticipant().customer().personScreening().set(screeningGenerator.createScreening(lease, tenant.leaseParticipant()));
            tenant.role().setValue(RandomUtil.random(EnumSet.of(LeaseTermParticipant.Role.CoApplicant, LeaseTermParticipant.Role.Dependent)));
            tenant.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));

            if (tenant.role().getValue().equals(LeaseTermParticipant.Role.Dependent)) {
                ensureDependantAttributes(tenant);
            }

            lease.currentTerm().version().tenants().add(tenant);
        }

        if (maxTenants == 0) {
            LeaseTermGuarantor guarantor = EntityFactory.create(LeaseTermGuarantor.class);

            guarantor.leaseParticipant().customer().set(customerGenerator.createCustomer());
            guarantor.leaseParticipant().customer().personScreening().set(screeningGenerator.createScreening(lease, guarantor.leaseParticipant()));
            guarantor.role().setValue(LeaseTermParticipant.Role.Guarantor);
            guarantor.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
            guarantor.tenant().set(mainTenant.leaseParticipant());

            lease.currentTerm().version().guarantors().add(guarantor);
        }
    }

    private static void addPreathorisedPaymentMethod(LeaseTermTenant tenant) {
        LeasePaymentMethod m = EntityFactory.create(LeasePaymentMethod.class);
        m.type().setValue(PaymentType.Echeck);
        m.isProfiledMethod().setValue(Boolean.TRUE);
        m.isDeleted().setValue(Boolean.FALSE);

        // create new payment method details:
        EcheckInfo details = EntityFactory.create(EcheckInfo.class);
        details.nameOn().setValue(tenant.leaseParticipant().customer().person().name().getStringView());
        details.bankId().setValue(CommonsStringUtils.paddZerro(RandomUtil.randomInt(999), 3));
        details.branchTransitNumber().setValue(CommonsStringUtils.paddZerro(RandomUtil.randomInt(99999), 5));
        details.accountNo().number().setValue(Integer.toString(RandomUtil.randomInt(99999)) + Integer.toString(RandomUtil.randomInt(999999)));
        details.accountNo().newNumber().setValue(details.accountNo().number().getValue());
        details.accountNo().obfuscatedNumber().setValue(new AccountNumberFormatter().obfuscate(details.accountNo().number().getValue()));
        m.details().set(details);

        m.customer().set(tenant.leaseParticipant().customer());
        m.sameAsCurrent().setValue(Boolean.FALSE);
        m.billingAddress().set(CommonsGenerator.createInternationalAddress());

        tenant.leaseParticipant().customer().paymentMethods().add(m);
    }

    public static LeasePaymentMethod createPaymentMethod(LeaseTermTenant tenant) {
        addPreathorisedPaymentMethod(tenant);
        return tenant.leaseParticipant().customer().paymentMethods().get(0);
    }

    public static void attachDocumentData(Lease lease) {
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            ScreeningGenerator.attachDocumentData(tenant.leaseParticipant().customer().personScreening());
        }
    }

    public static void ensureProductPrices(Lease lease) {
        ensureAgreedPrice(lease.currentTerm().version().leaseProducts().serviceItem());

        for (BillableItem billableItem : lease.currentTerm().version().leaseProducts().featureItems()) {
            ensureAgreedPrice(billableItem);
        }
    }

    private static void ensureAgreedPrice(BillableItem billableItem) {
        if (billableItem.item().price().getValue().compareTo(BigDecimal.ZERO) == 0) {
            if (ARCode.Type.services().contains(billableItem.item().product().holder().code().type().getValue())) {
                billableItem.agreedPrice().setValue(new BigDecimal(500 + RandomUtil.randomInt(500)));
            } else if (ARCode.Type.features().contains(billableItem.item().product().holder().code().type().getValue())) {
                switch (billableItem.item().product().holder().code().type().getValue()) {
                case Parking:
                    billableItem.agreedPrice().setValue(new BigDecimal(5 + RandomUtil.randomInt(50)));
                    break;
                case Locker:
                    billableItem.agreedPrice().setValue(new BigDecimal(5 + RandomUtil.randomInt(10)));
                    break;
                case Pet:
                    billableItem.agreedPrice().setValue(new BigDecimal(20 + RandomUtil.randomInt(20)));
                    break;
                case AddOn:
                    billableItem.agreedPrice().setValue(new BigDecimal(30 + RandomUtil.randomInt(50)));
                    break;
                case OneTime:
                    billableItem.agreedPrice().setValue(new BigDecimal(20 + RandomUtil.randomInt(20)));
                    break;
                case Utility:
                    billableItem.agreedPrice().setValue(new BigDecimal(80 + RandomUtil.randomInt(50)));
                    break;
                }
            }
        }
    }

    private static void ensureDependantAttributes(LeaseTermTenant tenant) {
        if (tenant.leaseParticipant().customer().person().sex().equals(Sex.Male)) {
            tenant.relationship().setValue(RandomUtil.random(maleDependentRelationships));
        } else {
            tenant.relationship().setValue(RandomUtil.random(femaleDependentRelationships));
        }

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        tenant.leaseParticipant().customer().person().birthDate().setValue(RandomUtil.randomLogicalDate(thisYear - 17, thisYear - 13));
    }
}
