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
package com.propertyvista.preloader.leases;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.preloader.BaseVistaDevDataPreloader;
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.person.Person.Sex;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseProducts;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.marketing.LeaseParticipantMoveInAction.MoveInActionType;
import com.propertyvista.generator.LeaseGenerator;
import com.propertyvista.generator.TenantsEquifaxTestCasesGenerator;
import com.propertyvista.generator.TenantsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.preloader.helper.LeasePreloaderHelper;
import com.propertyvista.preloader.leases.LeaseLifecycleSimulator.LeaseLifecycleSimulatorBuilder;

public class LeasePreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {

        Random random = new Random(1);

        int numCreated = 0;
        int numCreatedWithBilling = 0;

        LeaseGenerator generator = new LeaseGenerator(config());

        AptUnitSource aptUnitSource = new AptUnitSource(1);

        Customer dualTenantCustomer = null;
        int tCoApplicantCount = 0;
        int tGuarantorCount = 0;

        for (int i = 0; i < config().numTenants; i++) {
            AptUnit unit = makeAvailable(aptUnitSource.next());
            final Lease lease = generator.createLeaseWithTenants(unit);
            LeaseGenerator.attachDocumentData(lease);

            if (i < DemoData.UserType.TENANT.getDefaultMax()) {
                LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().get(0);
                String email = DemoData.UserType.TENANT.getEmail(i + 1);
                mainTenant.leaseParticipant().customer().person().email().setValue(email);
                // Make one (Third) Customer with Two Leases
                if (i == 2) {
                    dualTenantCustomer = mainTenant.leaseParticipant().customer();
                }

            } else if (i == DemoData.UserType.TENANT.getDefaultMax()) {
                LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().get(0);
                mainTenant.leaseParticipant().customer().set(dualTenantCustomer);
            }

            //Set CoApplicant and Guarantors users that can login using UI
            if ((lease.currentTerm().version().tenants().size() > 1) && (tCoApplicantCount < DemoData.UserType.COAPPLICANT.getDefaultMax())) {
                tCoApplicantCount++;
                LeaseTermTenant participant = lease.currentTerm().version().tenants().get(1);
                String email = DemoData.UserType.COAPPLICANT.getEmail(tCoApplicantCount);
                participant.leaseParticipant().customer().person().email().setValue(email);

                if (participant.role().getValue().equals(LeaseTermParticipant.Role.Dependent)) {
                    ensureCoApplicantAttributes(participant);
                }

                // Make sure he is CoApplicant
                participant.role().setValue(LeaseTermParticipant.Role.CoApplicant);

            }

            if ((lease.currentTerm().version().guarantors().size() > 0) && (tGuarantorCount < DemoData.UserType.GUARANTOR.getDefaultMax())) {
                tGuarantorCount++;
                LeaseTermGuarantor participant = lease.currentTerm().version().guarantors().get(0);
                String email = DemoData.UserType.GUARANTOR.getEmail(tGuarantorCount);
                participant.leaseParticipant().customer().person().email().setValue(email);
            }

            // Create normal Active Lease first for Shortcut users
            if (i < config().numOfLeasesWithNoSimulation) {

                final int tenantsCount = i;

                final Date trDate = SystemDateManager.getDate();
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() throws RuntimeException {
                        Calendar cal = new GregorianCalendar();
                        cal.setTime(new LogicalDate(Math.min(new LogicalDate().getTime(), lease.currentTerm().termFrom().getValue().getTime())));
                        cal.add(Calendar.MONTH, -1);
                        SystemDateManager.setDate(cal.getTime());
                        ServerSideFactory.create(LeaseFacade.class).persist(lease);

                        for (LeaseTermTenant participant : lease.currentTerm().version().tenants()) {
                            participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                            Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
                            Persistence.service().persist(participant.leaseParticipant().customer().personScreening().creditChecks());
                        }
                        for (LeaseTermGuarantor participant : lease.currentTerm().version().guarantors()) {
                            participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                            Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
                            Persistence.service().persist(participant.leaseParticipant().customer().personScreening().creditChecks());
                        }

                        ServerSideFactory.create(LeaseFacade.class).approve(lease, null, "LeasePreloader");

                        if (lease.leaseFrom().getValue().compareTo(trDate) <= 0) {
                            SystemDateManager.setDate(lease.leaseFrom().getValue());
                            ServerSideFactory.create(LeaseFacade.class).activate(lease);
                        }

                        // First DEMO tenant
                        if (tenantsCount == 0) {
                            LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().get(0);
                            skipResidentWizard((LeaseTermTenant) mainTenant.duplicate());
                            LeaseProducts products = lease.currentTerm().version().leaseProducts();
                            createDefaultAutopay(mainTenant, products);
                        }

                        return null;
                    }
                });

                SystemDateManager.setDate(trDate);

            } else {
                LeaseLifecycleSimulatorBuilder simBuilder = LeaseLifecycleSimulator.sim(random);

                if (numCreatedWithBilling < config().numOfPseudoRandomLeasesWithSimulatedBilling) {
                    // create simulation events that happen between 4 years ago, and and the end of the previous month
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(new Date());
                    cal.add(Calendar.YEAR, -4);
                    simBuilder.start(new LogicalDate(cal.getTime()));

                    // Set lease to
                    {
                        Calendar leaseToCal = new GregorianCalendar();
                        leaseToCal.setTime(new Date());
                        int month;

                        if (config().mockupData) {
                            month = DataGenerator.randomInt(11);
                        } else {
                            month = DataGenerator.randomInt(4);
                        }
                        if (i % 2 != 0) {
                            // produce lease completed by the day of the preload run
                            leaseToCal.add(Calendar.MONTH, -1 - month);
                        } else {
                            // produce a lease that ends after the day of the preload run
                            leaseToCal.add(Calendar.MONTH, 1 + month);
                        }
                        simBuilder.leaseTo(new LogicalDate(leaseToCal.getTime()));
                    }

                    cal.setTime(new Date());
                    cal.add(Calendar.MONTH, -1);
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    simBuilder.end(new LogicalDate(cal.getTime()));

                    ++numCreatedWithBilling;
                    simBuilder.simulateBilling();

                    simBuilder.setNumOfBillsAndPayments(config().oneBillOnePayment ? 1 : -1);

                } else {
                    Calendar cal = new GregorianCalendar();
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                    if (config().mockupData) {
                        cal.add(Calendar.YEAR, -2);
                    } else {
                        cal.add(Calendar.YEAR, -1);
                    }
                    simBuilder.start(new LogicalDate(cal.getTime()));

                    // To
                    if (RandomUtil.randomBoolean()) {
                        Calendar leaseToCal = new GregorianCalendar();
                        leaseToCal.setTime(new Date());
                        int month = 0;

                        if (config().mockupData) {
                            month = DataGenerator.randomInt(11);
                        } else {
                            month = DataGenerator.randomInt(4);
                        }
                        if (i % 2 != 0) {
                            // produce lease completed by the day of the preload run
                            leaseToCal.add(Calendar.MONTH, -1 - month);
                        } else {
                            // produce a lease that ends after the day of the preload run
                            leaseToCal.add(Calendar.MONTH, 1 + month);
                        }
                        simBuilder.leaseTo(new LogicalDate(leaseToCal.getTime()));
                    }

                    simBuilder.end(new LogicalDate());

                    simBuilder.availabilityTermConstraints(0l, 0l);
                    simBuilder.reservedTermConstraints(0l, 0l);
                    simBuilder.approveImmidately();
                }

                simBuilder.create().generateRandomLifeCycle(lease);

                // Add Turnover
                if (lease.status().getValue().isFormer() && RandomUtil.randomBoolean()) {
                    // Create new Lease for the same unit
                    unit = makeAvailable(Persistence.service().retrieve(AptUnit.class, unit.getPrimaryKey()));
                    Lease lease2 = generator.createLeaseWithTenants(unit);

                    Calendar leaseFrom = new GregorianCalendar();
                    leaseFrom.setTime(lease.leaseTo().getValue());
                    leaseFrom.add(Calendar.MONTH, 2);
                    lease2.leaseFrom().setValue(new LogicalDate(leaseFrom.getTime()));

                    LeaseLifecycleSimulatorBuilder simBuilder2 = LeaseLifecycleSimulator.sim(random);
                    simBuilder2.start(new LogicalDate(leaseFrom.getTime()));
                    simBuilder2.end(new LogicalDate());
                    simBuilder2.create().generateRandomLifeCycle(lease2);
                }

                if (lease.status().getValue().isActive() && !lease.currentTerm().version().tenants().isEmpty()) {
                    new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                        @Override
                        public Void execute() throws RuntimeException {
                            LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().get(0);
                            LeaseProducts products = lease.currentTerm().version().leaseProducts();
                            createDefaultAutopay(mainTenant, products);
                            return null;
                        }
                    });
                }

            }

            numCreated++;
        }

        Customer dualPotentialCustomer = null;
        int pCoApplicantCount = 0;
        int pGuarantorCount = 0;
        for (int i = 0; i < config().numPotentialTenants; i++) {
            AptUnit unit = aptUnitSource.next();
            unit = makeAvailable(unit);

            Lease lease = generator.createLeaseWithTenants(unit);

            LeaseGenerator.ensureOneYearLeaseFromNextMonthOn(lease);
            LeaseGenerator.attachDocumentData(lease);

            //Set users that can login using UI
            boolean mustHaveOnlineApplication = false;
            boolean mustSelectUnitInApplication = false;
            if (i < DemoData.UserType.PTENANT.getDefaultMax()) {
                LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().get(0);
                String email = DemoData.UserType.PTENANT.getEmail(i + 1);
                mustHaveOnlineApplication = true;
                mainTenant.leaseParticipant().customer().person().email().setValue(email);

                // Make one (Third) Customer with Two Applications
                if (i == 2) {
                    dualPotentialCustomer = mainTenant.leaseParticipant().customer();
                }
                // Allow (Fifth) Customer unit selection
                if (i == 4) {
                    mustSelectUnitInApplication = true;
                }

            } else if (i == DemoData.UserType.PTENANT.getDefaultMax()) {
                mustHaveOnlineApplication = true;
                LeaseTermTenant mainTenant = lease.currentTerm().version().tenants().get(0);
                mainTenant.leaseParticipant().customer().set(dualPotentialCustomer);
            }

            //Set PCOAPPLICANT users that can login using UI
            if ((lease.currentTerm().version().tenants().size() > 1) && (pCoApplicantCount < DemoData.UserType.PCOAPPLICANT.getDefaultMax())) {
                pCoApplicantCount++;
                LeaseTermTenant participant = lease.currentTerm().version().tenants().get(1);
                String email = DemoData.UserType.PCOAPPLICANT.getEmail(pCoApplicantCount);
                participant.leaseParticipant().customer().person().email().setValue(email);

                if (participant.role().getValue().equals(LeaseTermParticipant.Role.Dependent)) {
                    ensureCoApplicantAttributes(participant);
                }

                // Make sure he is CoApplicant
                participant.role().setValue(LeaseTermParticipant.Role.CoApplicant);
            }

            if ((lease.currentTerm().version().guarantors().size() > 0) && (pGuarantorCount < DemoData.UserType.PGUARANTOR.getDefaultMax())) {
                pGuarantorCount++;
                LeaseTermGuarantor participant = lease.currentTerm().version().guarantors().get(0);
                String email = DemoData.UserType.PGUARANTOR.getEmail(pGuarantorCount);
                participant.leaseParticipant().customer().person().email().setValue(email);
            }

            if (lease.currentTerm().termFrom().getValue().before(new Date())) {
                SystemDateManager.setDate(lease.currentTerm().termFrom().getValue());
            }
            ServerSideFactory.create(LeaseFacade.class).persist(lease);

            for (LeaseTermTenant participant : lease.currentTerm().version().tenants()) {
                setPictureToTenantIfDemo(participant.leaseParticipant().customer());
                participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
            }
            for (LeaseTermGuarantor participant : lease.currentTerm().version().guarantors()) {
                setPictureToTenantIfDemo(participant.leaseParticipant().customer());
                participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
            }
            if (mustHaveOnlineApplication || RandomUtil.randomBoolean()) {
                if (mustSelectUnitInApplication) {
                    ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(lease, lease.unit().building(), null);
                } else {
                    ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(lease, null, null);
                }
            }

            ensureDepositsForDraftApplicationsIfDemo(lease);

            if (lease.leaseApplication().status().getValue() == LeaseApplication.Status.InProgress) {
                LeaseTermParticipant<? extends LeaseParticipant<?>> leaseTermParticipant = lease.currentTerm().version().tenants().get(0);
                LeasePreloaderHelper.addDefaultPaymentToLeaseApplication(leaseTermParticipant);
            }

            SystemDateManager.resetDate();
        }

        TenantsEquifaxTestCasesGenerator tenantsEquifaxTestCasesGenerator = new TenantsEquifaxTestCasesGenerator();
        for (int i = 0; i < config().numPotentialTenants2CreditCheck; i++) {
            AptUnit unit = aptUnitSource.next();
            unit = makeAvailable(unit);

            Lease lease = generator.createLease(unit);
            if (!tenantsEquifaxTestCasesGenerator.addTenants(lease)) {
                break;
            }
            LeaseGenerator.attachDocumentData(lease);
            ServerSideFactory.create(LeaseFacade.class).persist(lease);
            for (LeaseTermTenant participant : lease.currentTerm().version().tenants()) {
                Persistence.ensureRetrieve(participant.leaseParticipant().customer().paymentMethods(), AttachLevel.Attached);
                participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
            }
            for (LeaseTermGuarantor participant : lease.currentTerm().version().guarantors()) {
                participant.leaseParticipant().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(participant.leaseParticipant().customer().personScreening());
            }

            if (lease.leaseApplication().status().getValue() == LeaseApplication.Status.InProgress) {
                LeaseTermParticipant<? extends LeaseParticipant<?>> leaseTermParticipant = lease.currentTerm().version().tenants().get(0);
                LeasePreloaderHelper.addDefaultPaymentToLeaseApplication(leaseTermParticipant);
            }
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + numCreated + " leases");

        return b.toString();
    }

    private void ensureDepositsForDraftApplicationsIfDemo(Lease lease) {
        if (ApplicationMode.isDemo()) {
            LeaseGenerator.ensureDepositsForDraftApplications(lease);
        }
    }

    private void setPictureToTenantIfDemo(Customer customer) {
        if (ApplicationMode.isDemo()) {
            TenantsGenerator.setCustomerPicture(customer);
            Persistence.service().persist(customer.picture());
        }
    }

    private void skipResidentWizard(LeaseTermParticipant<?> tenant) {
        signAgreement(tenant);
        doLaterSetupAutoPay(tenant);
        doLaterSetupInsurance(tenant);
    }

    private void signAgreement(LeaseTermParticipant<?> tenant) {
        LeasePreloaderHelper.signDefaultAgreement(tenant.leaseParticipant());
    }

    private void doLaterSetupAutoPay(LeaseTermParticipant<?> tenant) {
        markDoLater(tenant, MoveInActionType.autoPay);
    }

    private void doLaterSetupInsurance(LeaseTermParticipant<?> tenant) {
        markDoLater(tenant, MoveInActionType.insurance);
    }

    private void markDoLater(LeaseTermParticipant<?> mainTenant, MoveInActionType action) {
        ServerSideFactory.create(CustomerFacade.class).skipMoveInAction(mainTenant.leaseParticipant(), action);
    }

    private void createDefaultAutopay(LeaseTermTenant tenant, LeaseProducts products) {
        BillableItem item = null;
        if (products != null && !products.isEmpty() && !products.featureItems().isEmpty()) {
            item = products.featureItems().get(0);
        } else {
            item = products.serviceItem();
        }
        Persistence.ensureRetrieve(tenant.leaseParticipant(), AttachLevel.Attached);
        LeasePreloaderHelper.createDefaultAutoPayment(tenant.leaseParticipant(), item);
    }

    @Override
    public String delete() {
        return null;
    }

    private AptUnit makeAvailable(final AptUnit unit) {
        if (unit.availability().availableForRent().isNull()) {
            SystemDateManager.setDate(getStatusFromDate(unit));
            try {
                new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

                    @Override
                    public Void execute() throws RuntimeException {
                        ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(unit.getPrimaryKey());
                        return null;
                    }
                });
            } finally {
                SystemDateManager.resetDate();
            }
        }
        return Persistence.service().retrieve(AptUnit.class, unit.getPrimaryKey());
    }

    private LogicalDate getStatusFromDate(AptUnit unit) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.desc(criteria.proto().dateFrom());
        AptUnitOccupancySegment segment = Persistence.service().retrieve(criteria);

        if (segment == null || segment.status().getValue() != AptUnitOccupancySegment.Status.pending) {
            throw new IllegalStateException("the unit must be pending");
        } else {
            return new LogicalDate(segment.dateFrom().getValue());
        }
    }

    private void ensureCoApplicantAttributes(LeaseTermTenant participant) {
        if (participant.leaseParticipant().customer().person().sex().getValue().equals(Sex.Male)) {
            participant.relationship().setValue(RandomUtil.random(LeaseGenerator.maleCoApplicantRelationships));
        } else {
            participant.relationship().setValue(RandomUtil.random(LeaseGenerator.femaleCoApplicantRelationships));
        }
        participant.leaseParticipant().customer().person().birthDate().setValue(RandomUtil.randomLogicalDate(1930, 1980));
    }

}
