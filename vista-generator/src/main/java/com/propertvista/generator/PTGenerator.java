/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 26, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertvista.generator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.gdo.ApplicationSummaryGDO;
import com.propertvista.generator.gdo.TenantSummaryGDO;
import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.LegalQuestions;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonGuarantor;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.PersonScreeningHolder;
import com.propertyvista.domain.tenant.Tenant.Type;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalAsset.AssetType;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.extradata.Pet;
import com.propertyvista.domain.tenant.lease.extradata.Pet.WeightUnit;
import com.propertyvista.domain.tenant.lease.extradata.Vehicle;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.misc.EquifaxApproval.Decision;
import com.propertyvista.misc.EquifaxResult;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.server.common.reference.SharedData;
import com.propertyvista.server.common.util.LeaseLifecycleSim;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class PTGenerator {

    public static boolean equifaxDemo = false;

    private final static Logger log = LoggerFactory.getLogger(PTGenerator.class);

    private static final long MAX_CREATE_WAIT = 1000L * 60L * 60L * 24L * 30L;

    private static final long MAX_RESERVED_DURATION = 1000L * 60L * 60L * 24L * 30L;

    private static final long MAX_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L * 3L;

    private static final long MIN_LEASE_DURATION = 1000L * 60L * 60L * 24L * 365L;

    private final long seed;

    private final VistaDevPreloadConfig config;

    private int reservedCoApplicantNumber = 0;

    private final Random rnd;

    public PTGenerator(VistaDevPreloadConfig config) {
        this.seed = config.ptGenerationSeed;
        DataGenerator.setRandomSeed(config.ptGenerationSeed);
        this.rnd = new Random(666l);
        this.config = config;
    }

    public TenantUser createUser(int number) {
        String email = DemoData.UserType.PTENANT.getEmail(number);
        TenantUser user = EntityFactory.create(TenantUser.class);
        user.name().setValue(email.substring(0, email.indexOf('@')));
        user.email().setValue(email);
        return user;
    }

    /**
     * 
     * @param user
     * @param selectedUnit
     * @return <code>null</code> if selectedUnit is not avaialble for rent, otherwise a generated application summary
     */
    public ApplicationSummaryGDO createSummary(TenantUser user, AptUnit selectedUnit) {
        ApplicationSummaryGDO summary = EntityFactory.create(ApplicationSummaryGDO.class);

        createTenantList(user, summary.tenants());

        // lease:
        // FIXME since it's a gnerator, not sure if accessing the db via LeaseLifecycleSim is a good idea.
        // FIXME generate a lease with all the underground occupancy related stuff via LeaseLifecycleSim
        LeaseLifecycleSim sim = new LeaseLifecycleSim();

        if (selectedUnit.availableForRent().isNull()) {
            return null;
        }
        LogicalDate effectiveAvailableForRent = new LogicalDate(Math.max(selectedUnit.availableForRent().getValue().getTime(),
                RandomUtil.randomLogicalDate(2012, 2012).getTime()));
        LogicalDate createdDate = new LogicalDate(effectiveAvailableForRent.getTime() + Math.abs(rnd.nextLong()) % MAX_CREATE_WAIT);

        LogicalDate leaseFrom = new LogicalDate(createdDate.getTime() + Math.abs(rnd.nextLong()) % MAX_RESERVED_DURATION);
        LogicalDate leaseTo = new LogicalDate(Math.max(new LogicalDate().getTime(), leaseFrom.getTime()) + MIN_LEASE_DURATION + Math.abs(rnd.nextLong())
                % (MAX_LEASE_DURATION - MIN_LEASE_DURATION));
        LogicalDate expectedMoveIn = leaseFrom; // for simplicity's sake

        Lease lease = sim.newLease(createdDate, RandomUtil.randomLetters(8), selectedUnit, leaseFrom, leaseTo, expectedMoveIn, PaymentFrequency.Monthly, null);
        summary.lease().set(lease);

        // This is actually update during save to match real unit data
//        summary.lease().type().setValue(Service.Type.residentialUnit);
//        summary.lease().status().setValue(Lease.Status.Created);
//        summary.lease().unit().set(selectedUnit);
//        summary.lease().leaseFrom().setValue(RandomUtil.randomLogicalDate(2012, 2012));
//        summary.lease().leaseTo().setValue(RandomUtil.randomLogicalDate(2013, 2015));
//        summary.lease().expectedMoveIn().setValue(summary.lease().leaseFrom().getValue());
//        summary.lease().createDate().setValue(RandomUtil.randomLogicalDate(2011, 2011));
//
        //summary.charges().set(createCharges(summary, selectedUnit));

        return summary;
    }

    public EmergencyContact createEmergencyContact() {
        EmergencyContact contact = EntityFactory.create(EmergencyContact.class);

        contact.name().set(CommonsGenerator.createName());

        contact.homePhone().setValue(CommonsGenerator.createPhone());
        contact.mobilePhone().setValue(CommonsGenerator.createPhone());

        contact.email().setValue(CommonsGenerator.createEmail(contact.name()));

        PriorAddress address = createAddress();
        contact.address().set(address);

        return contact;
    }

    public static LegalQuestions createLegalQuestions() {
        LegalQuestions lq = EntityFactory.create(LegalQuestions.class);

        lq.suedForDamages().setValue(RandomUtil.randomBoolean());
        lq.suedForRent().setValue(RandomUtil.randomBoolean());
        lq.defaultedOnLease().setValue(RandomUtil.randomBoolean());
        lq.convictedOfFelony().setValue(RandomUtil.randomBoolean());
        lq.everEvicted().setValue(RandomUtil.randomBoolean());
        lq.legalTroubles().setValue(RandomUtil.randomBoolean());
        lq.filedBankruptcy().setValue(RandomUtil.randomBoolean());

        return lq;
    }

    public static IncomeInfoEmployer createEmployer() {
        IncomeInfoEmployer employer = EntityFactory.create(IncomeInfoEmployer.class);

        populateAddress(employer.address());

        employer.name().setValue(RandomUtil.random(PreloadData.EMPLOYER_NAMES));
        employer.supervisorName().setValue("Mr. " + DataGenerator.randomLastName());
        employer.supervisorPhone().setValue(RandomUtil.randomPhone());
        employer.monthlyAmount().setValue(new BigDecimal(1000 + RandomUtil.randomInt(4000)));
        employer.position().setValue(RandomUtil.random(PreloadData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        employer.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        employer.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return employer;
    }

    public static IncomeInfoSelfEmployed createSelfEmployed() {
        IncomeInfoSelfEmployed selfEmpl = EntityFactory.create(IncomeInfoSelfEmployed.class);

        populateAddress(selfEmpl.address());

        selfEmpl.name().setValue(RandomUtil.random(PreloadData.EMPLOYER_NAMES));
        selfEmpl.supervisorName().setValue("Mr. " + DataGenerator.randomLastName());
        selfEmpl.supervisorPhone().setValue(RandomUtil.randomPhone());
        selfEmpl.monthlyAmount().setValue(new BigDecimal(1000 + RandomUtil.randomInt(4000)));
        selfEmpl.position().setValue(RandomUtil.random(PreloadData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        selfEmpl.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        selfEmpl.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return selfEmpl;
    }

    public ApplicationDocument createApplicationDocument(Application application, String fileName) {
        if (IOUtils.getResource("pt-docs/" + fileName, PTGenerator.class) == null) {
            throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
        }
        ApplicationDocument applicationDocument = EntityFactory.create(ApplicationDocument.class);
        applicationDocument.fileName().setValue(fileName);
        return applicationDocument;
    }

    public void attachDocumentData(ApplicationDocument applicationDocument) {
        String fileName = applicationDocument.fileName().getValue();
        ApplicationDocumentBlob applicationDocumentData;
        try {
            byte[] data = IOUtils.getBinaryResource("pt-docs/" + fileName, PTGenerator.class);
            if (data == null) {
                throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
            }
            String contentType = MimeMap.getContentType(FilenameUtils.getExtension(fileName));
            applicationDocumentData = EntityFactory.create(ApplicationDocumentBlob.class);
            applicationDocumentData.data().setValue(data);
            applicationDocumentData.contentType().setValue(contentType);

        } catch (IOException e) {
            throw new Error("Failed to read the file [" + fileName + "]", e);
        }

        Persistence.service().persist(applicationDocumentData);
        applicationDocument.fileSize().setValue(applicationDocumentData.data().getValue().length);
        applicationDocument.blobKey().set(applicationDocumentData.id());
    }

    private Pet createPet() {
        Pet pet = EntityFactory.create(Pet.class);

        pet.name().setValue(RandomUtil.random(PreloadData.PET_NAMES));
        pet.color().setValue(RandomUtil.random(PreloadData.PET_COLORS));
        pet.breed().setValue(RandomUtil.random(PreloadData.PET_BREEDS));

        if (RandomUtil.randomBoolean()) {
            pet.weightUnit().setValue(WeightUnit.kg);
            pet.weight().setValue(4 + RandomUtil.randomInt(20));
        } else {
            pet.weightUnit().setValue(WeightUnit.lb);
            pet.weight().setValue(10 + RandomUtil.randomInt(30));
        }

        pet.birthDate().setValue(RandomUtil.randomLogicalDate(1985, 2011));

        return pet;
    }

    private Vehicle createVehicle() {
        Vehicle vehicle = EntityFactory.create(Vehicle.class);

        vehicle.plateNumber().setValue("ML" + RandomUtil.randomInt(9999) + "K");
        vehicle.year().setValue(RandomUtil.randomYear(1992, 2012));
        vehicle.make().setValue(RandomUtil.random(PreloadData.CAR_MAKES));
        vehicle.model().setValue(RandomUtil.random(PreloadData.CAR_MODELS));
        vehicle.province().set(RandomUtil.random(SharedData.getProvinces()));
        vehicle.country().set(vehicle.province().country());

        return vehicle;
    }

    public void populateAddress(AddressSimple address) {

        String line1 = 100 + RandomUtil.randomInt(10000) + " " + RandomUtil.random(PreloadData.STREETS);

        address.street1().setValue(line1);
        address.street2().setValue("");

        Province province = RandomUtil.random(SharedData.getProvinces());
        address.province().set(province);
        address.country().set(province.country());

        address.city().setValue(RandomUtil.random(PreloadData.CITIES));

        // for now we support only two countries
        if (address.country().name().getValue().toLowerCase().startsWith("c")) {
            address.postalCode().setValue(RandomUtil.randomPostalCode());
        } else {
            address.postalCode().setValue(RandomUtil.randomZipCode());
        }
    }

    public static void populateAddress(AddressStructured address) {

        address.suiteNumber().setValue(Integer.toString(RandomUtil.randomInt(1000)));
        address.streetNumber().setValue(Integer.toString(RandomUtil.randomInt(10000)));
        address.streetNumberSuffix().setValue("");

        address.streetName().setValue(RandomUtil.random(PreloadData.STREETS));
        address.streetType().setValue(RandomUtil.random(StreetType.values()));
        address.streetDirection().setValue(RandomUtil.random(StreetDirection.values()));

        Province province = RandomUtil.random(SharedData.getProvinces());
        address.province().set(province);
        address.country().set(province.country());

        address.city().setValue(RandomUtil.random(PreloadData.CITIES));
        address.county().setValue("");

        // for now we support only two countries
        if (address.country().name().getValue().toLowerCase().startsWith("c")) {
            address.postalCode().setValue(RandomUtil.randomPostalCode());
        } else {
            address.postalCode().setValue(RandomUtil.randomZipCode());
        }
    }

    public PriorAddress createAddress() {
        PriorAddress address = EntityFactory.create(PriorAddress.class);

        populateAddress(address);

        address.moveInDate().setValue(RandomUtil.randomLogicalDate(2009, 2011));
        address.moveOutDate().setValue(RandomUtil.randomLogicalDate(2011, 2013));

        address.payment().setValue(new BigDecimal(1000 + RandomUtil.randomInt(1000)));

        address.phone().setValue(CommonsGenerator.createPhone());
        address.rented().setValue(RandomUtil.randomEnum(PriorAddress.OwnedRented.class));
        address.propertyCompany().setValue(DataGenerator.randomLastName() + " Inc.");
        address.managerName().setValue("Mr. " + DataGenerator.randomLastName());
        address.managerPhone().setValue(CommonsGenerator.createPhone());
        address.managerEmail().setValue(DataGenerator.randomFirstName().toLowerCase() + "@" + DataGenerator.random(PreloadData.EMAIL_DOMAINS)
        /* , Email.Type.work */);

        return address;
    }

    private void createTenantList(TenantUser user, IList<TenantSummaryGDO> list) {
        int maxTenants = config.numTenantsInLease;
        if (Math.abs(this.seed) > 1000) {
            maxTenants = 1 + RandomUtil.randomInt(5);
        }

        for (int i = 0; i < maxTenants; i++) {
            TenantSummaryGDO tenantInfo = createTenantSummary(user, i);
            list.add(tenantInfo);
        }
    }

    private TenantSummaryGDO createTenantSummary(TenantUser user, int index) {
        TenantSummaryGDO tenantSummary = EntityFactory.create(TenantSummaryGDO.class);

        // Tenant as person, first to have the same random names
        tenantSummary.tenant().type().setValue(Type.person);
        tenantSummary.tenant().person().set(CommonsGenerator.createPerson());

        // Join the objects
        tenantSummary.tenantInLease().tenant().set(tenantSummary.tenant());

        // first tenant must always be an applicant
        if (index == 0) {
            tenantSummary.tenantInLease().role().setValue(TenantInLease.Role.Applicant);
            tenantSummary.tenant().user().set(user);
        } else if (index == 1) {
            tenantSummary.tenantInLease().role().setValue(TenantInLease.Role.CoApplicant);
        } else if (index == 2) {
            tenantSummary.tenantInLease().role().setValue(TenantInLease.Role.CoApplicant);
        } else {
            tenantSummary.tenantInLease().role().setValue(TenantInLease.Role.Dependent);
        }

        if (index == 0) {
            // TODO set null when UI is fixed
            tenantSummary.tenantInLease().relationship().setValue(PersonRelationship.Other);
        } else {
            tenantSummary.tenantInLease().relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
        }

// TODO check %-ge correctness bettween tenants here:
        tenantSummary.tenantInLease().percentage().setValue(RandomUtil.randomInt(100));

        if (EnumSet.of(PersonRelationship.Daughter, PersonRelationship.Son).contains(tenantSummary.tenantInLease().relationship().getValue())) {
            tenantSummary.tenantInLease().role().setValue(TenantInLease.Role.Dependent);
        }
        tenantSummary.tenantInLease().takeOwnership().setValue(RandomUtil.randomBoolean());

        if (index == 0) {
            tenantSummary.tenant().person().name().namePrefix().setValue(null);
            tenantSummary.tenant().person().name().middleName().setValue("");
            tenantSummary.tenant().person().email().setValue(user.email().getValue());
            user.name().setValue(tenantSummary.tenant().person().name().getStringView());
        } else if (!tenantSummary.tenantInLease().takeOwnership().getValue()
                && (tenantSummary.tenantInLease().role().getValue() == TenantInLease.Role.CoApplicant)) {
            String email = DemoData.UserType.PCOAPPLICANT.getEmail(++reservedCoApplicantNumber);
            tenantSummary.tenant().person().email().setValue(email);
        }

        EmergencyContact ec1 = createEmergencyContact();
        tenantSummary.tenant().emergencyContacts().add(ec1);

        EmergencyContact ec2 = createEmergencyContact();
        tenantSummary.tenant().emergencyContacts().add(ec2);

        // Screening
        tenantSummary.tenantScreening().set(createScreening(tenantSummary.tenant(), tenantSummary.tenantInLease().application(), tenantSummary));
        if (equifaxDemo) {
            createEquifaxApproival(tenantSummary.tenantScreening());
        }

        return tenantSummary;
    }

    private PersonScreening createScreening(PersonScreeningHolder screene, Application application, TenantSummaryGDO tenantSummary) {
        PersonScreening screening = EntityFactory.create(PersonScreening.class);
        screening.screene().set(screene);

        screening.createDate().setValue(RandomUtil.randomLogicalDate(2012, 2013));
        screening.updateDate().setValue(RandomUtil.randomLogicalDate(2012, 2013));

        String driversLicense = "JTVMX" + RandomUtil.randomInt(10) + "VMIEK";

        screening.driversLicense().setValue(driversLicense);
        screening.driversLicenseState().set(RandomUtil.random(SharedData.getProvinces()));

        screening.notCanadianCitizen().setValue(RandomUtil.randomBoolean());
        if (screening.notCanadianCitizen().isBooleanTrue()) {
            String fileName = "doc-security" + RandomUtil.randomInt(3) + ".jpg";
            screening.documents().add(createApplicationDocument(application, fileName));
        } else {
            screening.secureIdentifier().setValue("649 951 282");
        }

        PriorAddress currentAddress = createAddress();
        currentAddress.moveOutDate().setValue(RandomUtil.randomLogicalDate(2013, 2014)); // this has to be in the future
        screening.currentAddress().set(currentAddress);

        PriorAddress previousAddress = createAddress();
        // moveOut date for previous address is 1 day before the moveIn date for current address
        Date moveOut = new Date();
        moveOut.setTime(currentAddress.moveInDate().getValue().getTime() - 86400000);
        // moveIn date for previous address is a few days/years back
        int years = RandomUtil.randomInt(10) + 1;
        years *= -1;
        Date moveIn = DateUtils.yearsAdd(moveOut, years);
        log.debug("Moving {} years back", years);
        log.debug("Moving from {} to {}", moveOut, moveIn);
        previousAddress.moveOutDate().setValue(new LogicalDate(moveOut.getTime()));
        previousAddress.moveInDate().setValue(new LogicalDate(moveIn.getTime()));
        screening.previousAddress().set(previousAddress);

        LegalQuestions legalQuestions = createLegalQuestions();
        screening.legalQuestions().set(legalQuestions);

        createFinancialInfo(screening, application, tenantSummary);

        return screening;
    }

    private void createFinancialInfo(PersonScreening screening, Application application, TenantSummaryGDO tenantSummary) {

        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            PersonalIncome income = EntityFactory.create(PersonalIncome.class);

            income.incomeSource().setValue(IncomeSource.fulltime);
            income.employer().set(createEmployer());
            //            income. monthlyAmount().setValue(DomainUtil.createMoney(300d + RandomUtil.randomInt(4000)).getValue());

            //income.active().setValue(RandomUtil.randomBoolean());

            ApplicationDocument applicationDocument = createApplicationDocument(application, "doc-income" + RandomUtil.randomInt(3) + ".jpg");
            income.documents().add(applicationDocument);

            screening.incomes().add(income);
        }

        int minAssets = 0;
        if (screening.incomes().size() == 0) {
            minAssets = 1;
        }

        for (int i = 0; i < minAssets + RandomUtil.randomInt(3); i++) {
            PersonalAsset asset = EntityFactory.create(PersonalAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.percent().setValue((double) RandomUtil.randomInt(100));
            asset.assetValue().setValue(BigDecimal.valueOf(500 + RandomUtil.randomDouble(500)));

            screening.assets().add(asset);
        }

        if (tenantSummary != null) {
            for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
                Guarantor guarantor = EntityFactory.create(Guarantor.class);
                guarantor.person().set(CommonsGenerator.createPerson());
                PersonGuarantor personGuarantor = EntityFactory.create(PersonGuarantor.class);
                personGuarantor.guarantor().set(guarantor);
                personGuarantor.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
                screening.guarantors().add(personGuarantor);

                tenantSummary.guarantorScreening().add(createScreening(guarantor, application, null));
            }
        }
    }

    private void createEquifaxApproival(PersonScreening ts) {
        // TODO: currently - just some mockup stuff:
        ts.equifaxApproval().suggestedDecision().setValue(RandomUtil.randomEnum(Decision.class));
        switch (ts.equifaxApproval().suggestedDecision().getValue()) {
        case Approve:
            ts.equifaxApproval().percenrtageApproved().setValue(80 + RandomUtil.randomDouble(40));
            ts.equifaxApproval().suggestedDecision().setValue(Decision.Approve);
            break;
        case RequestInfo:
            ts.equifaxApproval().percenrtageApproved().setValue(40 + RandomUtil.randomDouble(20));
            ts.equifaxApproval().suggestedDecision().setValue(Decision.RequestInfo);
            break;
        case Decline:
            ts.equifaxApproval().percenrtageApproved().setValue(RandomUtil.randomDouble(20));
            ts.equifaxApproval().suggestedDecision().setValue(Decision.Decline);
            break;

        default:
            ts.equifaxApproval().suggestedDecision().setValue(Decision.Pending);
        }

        ts.equifaxApproval().checkResultDetails().set(EntityFactory.create(EquifaxResult.class));
        ts.equifaxApproval().checkResultDetails().suggestedDecision().setValue(ts.equifaxApproval().suggestedDecision().getValue());
    }
}
