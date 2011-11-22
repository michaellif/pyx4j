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
import java.util.Date;
import java.util.EnumSet;

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
import com.propertyvista.domain.User;
import com.propertyvista.domain.charges.ChargeLine.ChargeType;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.contact.AddressStructured.StreetDirection;
import com.propertyvista.domain.contact.AddressStructured.StreetType;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.Pet.WeightUnit;
import com.propertyvista.domain.financial.offering.extradata.Vehicle;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.Tenant.Type;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeInfoSelfEmployed;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalAsset.AssetType;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.server.common.reference.SharedData;
import com.propertyvista.server.domain.ApplicationDocumentData;

public class PTGenerator {

    private final static Logger log = LoggerFactory.getLogger(PTGenerator.class);

    private final long seed;

    private final VistaDevPreloadConfig config;

    public PTGenerator(VistaDevPreloadConfig config) {
        this.seed = config.ptGenerationSeed;
        DataGenerator.setRandomSeed(config.ptGenerationSeed);
        this.config = config;
    }

    public User createUser(int number) {
        String email = DemoData.UserType.PTENANT.getEmail(number);
        User user = EntityFactory.create(User.class);
        user.name().setValue(email.substring(0, email.indexOf('@')));
        user.email().setValue(email);
        return user;
    }

    public ApplicationSummaryGDO createSummary(User user, AptUnit selectedUnit) {
        ApplicationSummaryGDO summary = EntityFactory.create(ApplicationSummaryGDO.class);

        createTenantList(user, summary.tenants());

        // lease:
        summary.lease().leaseID().setValue(RandomUtil.randomLetters(8));

        // This is actually update during save to match real unit data
        summary.lease().type().setValue(Service.Type.residentialUnit);

//        summary.lease().type().setValue(RandomUtil.randomEnum(Service.Type.class));
        summary.lease().status().setValue(Lease.Status.Draft);
        summary.lease().unit().set(selectedUnit);
        summary.lease().leaseFrom().setValue(RandomUtil.randomLogicalDate(2011, 2011));
        summary.lease().leaseTo().setValue(RandomUtil.randomLogicalDate(2012, 2012));
        summary.lease().expectedMoveIn().setValue(RandomUtil.randomLogicalDate(2011, 2011));
        summary.lease().actualMoveIn().setValue(RandomUtil.randomLogicalDate(2011, 2011));
        summary.lease().expectedMoveOut().setValue(RandomUtil.randomLogicalDate(2012, 2012));
        summary.lease().signDate().setValue(RandomUtil.randomLogicalDate(2012, 2012));

        summary.lease().createDate().setValue(new LogicalDate());

        //summary.charges().set(createCharges(summary, selectedUnit));

        return summary;
    }

    public EmergencyContact createEmergencyContact() {
        EmergencyContact contact = EntityFactory.create(EmergencyContact.class);

        contact.name().set(CommonsGenerator.createName());

        contact.homePhone().set(CommonsGenerator.createPhone());
        contact.mobilePhone().set(CommonsGenerator.createPhone());

        contact.email().set(CommonsGenerator.createEmail(contact.name()));

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
        employer.monthlyAmount().set(DomainUtil.createMoney(1000d + RandomUtil.randomInt(4000)));
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
        selfEmpl.monthlyAmount().set(DomainUtil.createMoney(1000d + RandomUtil.randomInt(4000)));
        selfEmpl.position().setValue(RandomUtil.random(PreloadData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        selfEmpl.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        selfEmpl.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return selfEmpl;
    }

    public ApplicationDocument createApplicationDocument(TenantInLease tenantInfo, String fileName, ApplicationDocument.DocumentType documentType) {
        assert (tenantInfo.application() != null);
        if (IOUtils.getResource("pt-docs/" + fileName, PTGenerator.class) == null) {
            throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
        }
        ApplicationDocument applicationDocument = EntityFactory.create(ApplicationDocument.class);
        applicationDocument.application().set(tenantInfo.application());
        applicationDocument.type().setValue(documentType);
        applicationDocument.filename().setValue(fileName);
        return applicationDocument;
    }

    public void attachDocumentData(ApplicationDocument applicationDocument) {
        String fileName = applicationDocument.filename().getValue();
        ApplicationDocumentData applicationDocumentData;
        try {
            byte[] data = IOUtils.getBinaryResource("pt-docs/" + fileName, PTGenerator.class);
            if (data == null) {
                throw new Error("Could not find DocumentData [" + fileName + "] in classpath");
            }
            String contentType = MimeMap.getContentType(FilenameUtils.getExtension(fileName));
            applicationDocumentData = EntityFactory.create(ApplicationDocumentData.class);
            //TODO VladS security settings !
            //applicationDocumentData.tenant().set();
            applicationDocumentData.application().set(applicationDocument.application());
            applicationDocumentData.data().setValue(data);
            applicationDocumentData.contentType().setValue(contentType);

        } catch (IOException e) {
            throw new Error("Failed to read the file [" + fileName + "]", e);
        }

        Persistence.service().persist(applicationDocumentData);
        applicationDocument.fileSize().setValue(applicationDocumentData.data().getValue().length);
        applicationDocument.dataId().set(applicationDocumentData.id());
    }

    private void createPets(IList<Pet> pets) {

        int maxPets;
        if (Math.abs(this.seed) < 1000) {
            maxPets = 1 + RandomUtil.randomInt(2);
        } else {
            maxPets = RandomUtil.randomInt(3);
        }

        for (int i = 0; i < maxPets; i++) {
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

            pet.birthDate().setValue(RandomUtil.randomLogicalDate(1985, 2010));

            // charge line
            pet.chargeLine().set(DomainUtil.createChargeLine(ChargeType.petCharge, 20d + RandomUtil.randomInt(100)));

            pets.add(pet);
        }
    }

    private void createVehicles(IList<Vehicle> vehicles) {

        int maxVehicles;
        if (Math.abs(this.seed) < 1000) {
            maxVehicles = 1 + RandomUtil.randomInt(2);
        } else {
            maxVehicles = RandomUtil.randomInt(3);
        }

        for (int i = 0; i < maxVehicles; i++) {
            Vehicle vehicle = EntityFactory.create(Vehicle.class);

            vehicle.plateNumber().setValue("ML" + RandomUtil.randomInt(9999) + "K");
            vehicle.year().setValue(RandomUtil.randomYear(1992, 2012));
            vehicle.make().setValue(RandomUtil.random(PreloadData.CAR_MAKES));
            vehicle.model().setValue(RandomUtil.random(PreloadData.CAR_MODELS));
            vehicle.province().set(RandomUtil.random(SharedData.getProvinces()));
            vehicle.country().set(vehicle.province().country());

            vehicles.add(vehicle);
        }
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

        address.moveInDate().setValue(RandomUtil.randomLogicalDate(2008, 2010));
        address.moveOutDate().setValue(RandomUtil.randomLogicalDate(2010, 2012));

        address.payment().setValue(1000d + RandomUtil.randomInt(1000));

        address.phone().set(CommonsGenerator.createPhone());
        address.rented().setValue(RandomUtil.randomEnum(PriorAddress.OwnedRented.class));
        address.managerName().setValue("Mr. " + DataGenerator.randomLastName());
        address.managerPhone().set(CommonsGenerator.createPhone());
        address.managerEmail().set(
                CommonsGenerator.createEmail(DataGenerator.randomFirstName().toLowerCase() + "@" + DataGenerator.random(PreloadData.EMAIL_DOMAINS),
                        Email.Type.work));

        return address;
    }

    private void createTenantList(User user, IList<TenantSummaryGDO> list) {
        int maxTenants = config.numTenantsInLease;
        if (Math.abs(this.seed) > 1000) {
            maxTenants = 1 + RandomUtil.randomInt(5);
        }

        for (int i = 0; i < maxTenants; i++) {
            TenantSummaryGDO tenantInfo = createTenantSummary(user, i);
            list.add(tenantInfo);
        }
    }

    private TenantSummaryGDO createTenantSummary(User user, int index) {
        TenantSummaryGDO tenantSummary = EntityFactory.create(TenantSummaryGDO.class);

        // Tenant as person, first to have the same random names
        tenantSummary.tenant().type().setValue(Type.person);
        tenantSummary.tenant().person().set(CommonsGenerator.createPerson());

        // Join the objects
        tenantSummary.tenantInLease().tenant().set(tenantSummary.tenant());
        tenantSummary.tenantScreening().tenant().set(tenantSummary.tenant());

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
            tenantSummary.tenantInLease().relationship().setValue(TenantInLease.Relationship.Other);
        } else {
            tenantSummary.tenantInLease().relationship().setValue(RandomUtil.randomEnum(TenantInLease.Relationship.class));
        }

// TODO check %-ge correctness bettween tenants here:
        tenantSummary.tenantInLease().percentage().setValue(RandomUtil.randomInt(100));

        if (EnumSet.of(TenantInLease.Relationship.Daughter, TenantInLease.Relationship.Son).contains(tenantSummary.tenantInLease().relationship().getValue())) {
            tenantSummary.tenantInLease().role().setValue(TenantInLease.Role.Dependent);
        }
        tenantSummary.tenantInLease().takeOwnership().setValue(RandomUtil.randomBoolean());

        if (index == 0) {
            tenantSummary.tenant().person().name().namePrefix().setValue(null);
            tenantSummary.tenant().person().name().middleName().setValue("");
            tenantSummary.tenant().person().email().address().setValue(user.email().getValue());
            user.name().setValue(tenantSummary.tenant().person().name().getStringView());
        }

        EmergencyContact ec1 = createEmergencyContact();
        tenantSummary.tenant().emergencyContacts().add(ec1);

        EmergencyContact ec2 = createEmergencyContact();
        tenantSummary.tenant().emergencyContacts().add(ec2);

        // Screening

        String driversLicense = "JTVMX" + RandomUtil.randomInt(10) + "VMIEK";

        tenantSummary.tenantScreening().driversLicense().setValue(driversLicense);
        tenantSummary.tenantScreening().driversLicenseState().set(RandomUtil.random(SharedData.getProvinces()));

        tenantSummary.tenantScreening().notCanadianCitizen().setValue(RandomUtil.randomBoolean());
        if (tenantSummary.tenantScreening().notCanadianCitizen().isBooleanTrue()) {
            ApplicationDocument.DocumentType documentType = ApplicationDocument.DocumentType.securityInfo;

            String fileName = "doc-security" + RandomUtil.randomInt(3) + ".jpg";
            tenantSummary.tenantScreening().documents().add(createApplicationDocument(tenantSummary.tenantInLease(), fileName, documentType));
        } else {
            tenantSummary.tenantScreening().secureIdentifier().setValue("649 951 282");
        }

        PriorAddress currentAddress = createAddress();
        currentAddress.moveOutDate().setValue(RandomUtil.randomLogicalDate(2012, 2013)); // this has to be in the future
        tenantSummary.tenantScreening().currentAddress().set(currentAddress);

        PriorAddress previousAddress = createAddress();
        // moveOut date for previous address is the same as the moveIn date for current address
        Date moveOut = currentAddress.moveInDate().getValue();
        // moveIn date for previous address is a few days/years back
        int years = RandomUtil.randomInt(10) + 1;
        years *= -1;
        Date moveIn = DateUtils.yearsAdd(moveOut, years);
        log.debug("Moving {} years back", years);
        log.debug("Moving from {} to {}", moveOut, moveIn);
        previousAddress.moveOutDate().setValue(new LogicalDate(moveOut.getTime()));
        previousAddress.moveInDate().setValue(new LogicalDate(moveIn.getTime()));
        tenantSummary.tenantScreening().previousAddress().set(previousAddress);

        LegalQuestions legalQuestions = createLegalQuestions();
        tenantSummary.tenantScreening().legalQuestions().set(legalQuestions);

        createFinancialInfo(tenantSummary.tenantScreening());

        return tenantSummary;
    }

    private void createFinancialInfo(TenantScreening tenantScreening) {

        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            PersonalIncome income = EntityFactory.create(PersonalIncome.class);

            income.incomeSource().setValue(IncomeSource.fulltime);
            income.employer().set(createEmployer());
            //            income. monthlyAmount().setValue(DomainUtil.createMoney(300d + RandomUtil.randomInt(4000)).getValue());

            //income.active().setValue(RandomUtil.randomBoolean());

            //TODO
//            if (IncomeSource.fulltime.equals(income.incomeSource().getValue())) {
//                ApplicationDocument applicationDocument = createApplicationDocument(tenant, "doc-income" + RandomUtil.randomInt(3) + ".jpg",
//                        ApplicationDocument.DocumentType.income);
//                income.documents().add(applicationDocument);
//            }

            tenantScreening.incomes().add(income);
        }

        int minAssets = 0;
        if (tenantScreening.incomes().size() == 0) {
            minAssets = 1;
        }

        for (int i = 0; i < minAssets + RandomUtil.randomInt(3); i++) {
            PersonalAsset asset = EntityFactory.create(PersonalAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.percent().setValue((double) RandomUtil.randomInt(100));
            asset.assetValue().setValue(DomainUtil.createMoney(100d + RandomUtil.randomInt(10000)).getValue());

            tenantScreening.assets().add(asset);
        }

        for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
            TenantGuarantor guarantor = EntityFactory.create(TenantGuarantor.class);
            guarantor.name().firstName().setValue(DataGenerator.randomFirstName());
            guarantor.name().lastName().setValue(DataGenerator.randomLastName());
            guarantor.relationship().setValue(RandomUtil.random(TenantGuarantor.Relationship.values()));
            guarantor.birthDate().setValue(RandomUtil.randomLogicalDate(1960, 2011 - 18));
            guarantor.sex().setValue(RandomUtil.randomEnum(Person.Sex.class));
            guarantor.email().set(CommonsGenerator.createEmail(guarantor.name()));
            tenantScreening.guarantors().add(guarantor);
        }

    }
}
