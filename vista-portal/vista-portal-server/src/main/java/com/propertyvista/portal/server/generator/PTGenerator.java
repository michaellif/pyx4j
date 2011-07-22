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
package com.propertyvista.portal.server.generator;

import gwtupload.server.exceptions.UploadActionException;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.essentials.rpc.report.DownloadFormat;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.ApplicationDocument;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.LegalQuestions;
import com.propertyvista.domain.Pet;
import com.propertyvista.domain.Pet.WeightUnit;
import com.propertyvista.domain.PetType;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.User;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.charges.ChargeLine.ChargeType;
import com.propertyvista.domain.contact.IAddress;
import com.propertyvista.domain.contact.IAddressFull;
import com.propertyvista.domain.contact.IAddressFull.StreetDirection;
import com.propertyvista.domain.contact.IAddressFull.StreetType;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.domain.tenant.Tenant.Type;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantScreening;
import com.propertyvista.domain.tenant.income.IncomeInfoEmployer;
import com.propertyvista.domain.tenant.income.IncomeSource;
import com.propertyvista.domain.tenant.income.TenantAsset;
import com.propertyvista.domain.tenant.income.TenantAsset.AssetType;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.tenant.income.TenantIncome;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.ptapp.ApplicationDocumentServletParameters;
import com.propertyvista.portal.server.preloader.PreloadPT;
import com.propertyvista.portal.server.preloader.RandomUtil;
import com.propertyvista.server.common.reference.SharedData;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.ApplicationDocumentData;
import com.propertyvista.server.domain.UserCredential;

public class PTGenerator {

    private final static Logger log = LoggerFactory.getLogger(PTGenerator.class);

    private final long seed;

    private final PreloadConfig config;

    // TODO this seed might be an optional thing (come from config)
    public PTGenerator(long seed, PreloadConfig config) {
        DataGenerator.setRandomSeed(seed);
        this.config = config;
        this.seed = seed;
    }

    public User createUser(int number) {
        String email = DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(number) + DemoData.USERS_DOMAIN;
        User user = EntityFactory.create(User.class);
        user.name().setValue(email.substring(0, email.indexOf('@')));
        user.email().setValue(email);
        return user;
    }

    public ApplicationSummaryDTO createSummary(User user, AptUnit selectedUnit) {
        ApplicationSummaryDTO summary = EntityFactory.create(ApplicationSummaryDTO.class);
        summary.lease().status().setValue(Lease.Status.ApplicationCreated);

        createUnitSelection(summary.unitSelection(), selectedUnit);
        createTenantList(user, summary.tenants());

        createPets(summary.lease().pets());
        //summary.charges().set(createCharges(summary, selectedUnit));

        return summary;
    }

    private Charges createCharges(Summary summary, AptUnit selectedUnit) {
        assert (summary.application() != null);

        Charges charges = EntityFactory.create(Charges.class);
        charges.application().set(summary.application());
        ///ChargesServerCalculation.updateChargesFromObjects(charges, summary.unitSelection(), selectedUnit, summary.tenantList(), summary.pets());
        return charges;
    }

    private Vehicle createVehicle() {
        Vehicle vehicle = EntityFactory.create(Vehicle.class);

        vehicle.plateNumber().setValue("ML" + RandomUtil.randomInt(9999) + "K");
        vehicle.year().setValue(RandomUtil.randomYear(1992, 2012));
        vehicle.make().setValue(RandomUtil.random(DemoData.CAR_MAKES));
        vehicle.model().setValue(RandomUtil.random(DemoData.CAR_MODELS));
        vehicle.province().set(RandomUtil.random(SharedData.getProvinces()));
        vehicle.country().set(vehicle.province().country());

        return vehicle;
    }

    public EmergencyContact createEmergencyContact() {
        EmergencyContact contact = EntityFactory.create(EmergencyContact.class);

        contact.name().set(CommonsGenerator.createName());

        contact.homePhone().setValue(RandomUtil.randomPhone());
        contact.mobilePhone().setValue(RandomUtil.randomPhone());

        String email = contact.name().firstName().getStringView().toLowerCase() + "." + contact.name().lastName().getStringView().toLowerCase() + "@"
                + RandomUtil.random(DemoData.EMAIL_DOMAINS);
        contact.email().setValue(email);

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

    private IncomeInfoEmployer createEmployer() {
        IncomeInfoEmployer employer = EntityFactory.create(IncomeInfoEmployer.class);

        populateAddress(employer);

        employer.name().setValue(RandomUtil.random(DemoData.EMPLOYER_NAMES));
        employer.supervisorName().setValue("Mr. " + RandomUtil.random(DemoData.LAST_NAMES));
        employer.supervisorPhone().setValue(RandomUtil.randomPhone());
        employer.monthlyAmount().set(DomainUtil.createMoney(1000d + RandomUtil.randomInt(4000)));
        employer.position().setValue(RandomUtil.random(DemoData.OCCUPATIONS));

        int startYear = 1990 + RandomUtil.randomInt(20);
        int endYear = startYear + 1 + RandomUtil.randomInt(8);

        employer.starts().setValue(new LogicalDate(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));
        employer.ends().setValue(new LogicalDate(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)).getTime()));

        return employer;
    }

    public ApplicationDocument createApplicationDocument(PotentialTenantInfo tenantInfo, String fileName, ApplicationDocument.DocumentType documentType) {
        assert (tenantInfo.application() != null);

        ApplicationDocument applicationDocument = EntityFactory.create(ApplicationDocument.class);
        applicationDocument.application().set(tenantInfo.application());
        applicationDocument.type().setValue(documentType);
        applicationDocument.filename().setValue(fileName);

        String filename = IOUtils.resourceFileName(fileName, PreloadPT.class);
        try {
            byte[] data = IOUtils.getResource(filename);
            if (data == null) {
                log.error("Could not find picture [{}] in classpath", filename);
                throw new Error("Could not find picture [" + filename + "] in classpath");
            } else {
                applicationDocument.fileSize().setValue((long) data.length);
                //applicationDocument.dataId().set(createApplicationDocumentData(filename).id());
                return applicationDocument;
            }
        } catch (Exception e) {
            log.error("Failed to read the file [{}]", filename, e);
            throw new Error("Failed to read the file [" + filename + "]");
        }
    }

    public ApplicationDocumentData createApplicationDocumentData(PotentialTenantInfo tenantInfo, String fileName) {
        String filename = IOUtils.resourceFileName(fileName, PreloadPT.class);
        try {
            byte[] data = IOUtils.getResource(filename);
            if (data == null) {
                log.error("Could not find picture [{}] in classpath", filename);
                throw new Error("Could not find picture [" + filename + "] in classpath");
            }

            int t = fileName.lastIndexOf(".");
            if (t == -1)
                throw new IllegalArgumentException("There's no extension in file name:" + fileName);
            String extension = fileName.substring(t + 1).trim();
            try {
                if (!ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.valueByExtension(extension))) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new UploadActionException("Unsupported file extension in file name:" + fileName + ". List of supported extensions: "
                        + ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS);
            }
            String contentType = MimeMap.getContentType(extension);
            if (contentType == null)
                throw new UploadActionException("Unknown file extension in file name:" + fileName);

            ApplicationDocumentData applicationDocumentData = EntityFactory.create(ApplicationDocumentData.class);
            //applicationDocumentData.id().setValue(documentId);
            applicationDocumentData.tenant().set(tenantInfo);
            applicationDocumentData.application().set(tenantInfo.application());
            applicationDocumentData.data().setValue(data);
            applicationDocumentData.contentType().setValue(contentType);
            return applicationDocumentData;
        } catch (Exception e) {
            log.error("Failed to read the file [{}]", filename, e);
            throw new Error("Failed to read the file [" + filename + "]");
        }
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

            pet.type().setValue(PetType.dog);
            pet.name().setValue(RandomUtil.random(DemoData.PET_NAMES));
            pet.color().setValue(RandomUtil.random(DemoData.PET_COLORS));
            pet.breed().setValue(RandomUtil.random(DemoData.PET_BREEDS));

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

    public void populateAddress(IAddress address) {

        String line1 = 100 + RandomUtil.randomInt(10000) + " " + RandomUtil.random(DemoData.STREETS);

        address.street1().setValue(line1);
        address.street2().setValue("");

        Province province = RandomUtil.random(SharedData.getProvinces());
        address.province().set(province);
        address.country().set(province.country());

        address.city().setValue(RandomUtil.random(DemoData.CITIES));

        // for now we support only two countries
        if (address.country().name().getValue().toLowerCase().startsWith("c")) {
            address.postalCode().setValue(RandomUtil.randomPostalCode());
        } else {
            address.postalCode().setValue(RandomUtil.randomZipCode());
        }
    }

    public void populateAddress(IAddressFull address) {

        address.unitNumber().setValue(Integer.toString(RandomUtil.randomInt(1000)));
        address.streetNumber().setValue(Integer.toString(RandomUtil.randomInt(10000)));
        address.streetNumberSuffix().setValue("");

        address.streetName().setValue(RandomUtil.random(DemoData.STREETS));
        address.streetType().setValue(RandomUtil.random(StreetType.values()));
        address.streetDirection().setValue(RandomUtil.random(StreetDirection.values()));

        Province province = RandomUtil.random(SharedData.getProvinces());
        address.province().set(province);
        address.country().set(province.country());

        address.city().setValue(RandomUtil.random(DemoData.CITIES));
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

        address.phone().setValue(RandomUtil.randomPhone());
        address.rented().setValue(RandomUtil.randomEnum(PriorAddress.OwnedRented.class));
        address.managerName().setValue("Mr. " + RandomUtil.random(DemoData.LAST_NAMES));

        return address;
    }

    private void createTenantList(User user, IList<TenantSummaryDTO> list) {
        int maxTenants = config.getNumPotentialTenants();
        if (Math.abs(this.seed) > 1000) {
            maxTenants = 1 + RandomUtil.randomInt(5);
        }

        for (int i = 0; i < maxTenants; i++) {
            TenantSummaryDTO tenantInfo = createTenantSummary(user, i);
            list.add(tenantInfo);
        }
    }

    private TenantSummaryDTO createTenantSummary(User user, int index) {
        TenantSummaryDTO tenantSummary = EntityFactory.create(TenantSummaryDTO.class);

        // Join the objects
        tenantSummary.tenantInLease().tenant().set(tenantSummary.tenant());
        tenantSummary.tenantScreening().tenant().set(tenantSummary.tenant());

        // first tenant must always be an applicant
        if (index == 0) {
            tenantSummary.tenantInLease().status().setValue(TenantInLease.Status.Applicant);
            tenantSummary.tenant().user().set(user);
        } else if (index == 1) {
            tenantSummary.tenantInLease().status().setValue(TenantInLease.Status.CoApplicant);
        } else if (index == 2) {
            tenantSummary.tenantInLease().status().setValue(TenantInLease.Status.CoApplicant);
        } else {
            tenantSummary.tenantInLease().status().setValue(TenantInLease.Status.Dependant);
        }
        tenantSummary.tenantInLease().relationship().setValue(RandomUtil.randomEnum(TenantInLease.Relationship.class));
        tenantSummary.tenantInLease().payment().setValue(1.0d + RandomUtil.randomInt(3000));

        if (EnumSet.of(TenantInLease.Relationship.Daughter, TenantInLease.Relationship.Son).contains(tenantSummary.tenantInLease().relationship().getValue())) {
            tenantSummary.tenantInLease().status().setValue(TenantInLease.Status.Dependant);
        }
        tenantSummary.tenantInLease().takeOwnership().setValue(RandomUtil.randomBoolean());

        // Tenant as person
        tenantSummary.tenant().type().setValue(Type.person);
        Person person = tenantSummary.tenant().person();
        person.name().firstName().setValue(DataGenerator.randomFirstName());
        person.name().lastName().setValue(DataGenerator.randomLastName());
        if (index == 0) {
            person.name().middleName().setValue("");
        } else {
            person.name().middleName().setValue(RandomUtil.randomInt(100) % 7 == 0 ? "M" : "");
        }

        person.birthDate().setValue(RandomUtil.randomLogicalDate(1930, 1980));
        person.homePhone().setValue(RandomUtil.randomPhone());
        person.mobilePhone().setValue(RandomUtil.randomPhone());
        person.workPhone().setValue(RandomUtil.randomPhone());

        person.email().setValue(RandomUtil.randomPersonEmail(person));

        if (index == 0) {
            person.email().setValue(user.email().getValue());
        }

        EmergencyContact ec1 = createEmergencyContact();
        tenantSummary.tenant().emergencyContacts().add(ec1);

        EmergencyContact ec2 = createEmergencyContact();
        tenantSummary.tenant().emergencyContacts().add(ec2);

        for (int i = 0; i < RandomUtil.randomInt(3); i++) {
            Vehicle vehicle = createVehicle();
            tenantSummary.tenant().vehicles().add(vehicle);
        }

        // Screening

        String driversLicense = "JTVMX" + RandomUtil.randomInt(10) + "VMIEK";

        tenantSummary.tenantScreening().driversLicense().setValue(driversLicense);
        tenantSummary.tenantScreening().driversLicenseState().set(RandomUtil.random(SharedData.getProvinces()));

        tenantSummary.tenantScreening().notCanadianCitizen().setValue(RandomUtil.randomBoolean());
        if (tenantSummary.tenantScreening().notCanadianCitizen().isBooleanTrue()) {
            ApplicationDocument.DocumentType documentType = ApplicationDocument.DocumentType.securityInfo;
            //TODO
            //tenantSummary.tenantScreening().documents().add(createApplicationDocument(pti, "doc-security" + RandomUtil.randomInt(3) + ".jpg", documentType));
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
            TenantIncome income = EntityFactory.create(TenantIncome.class);

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
            TenantAsset asset = EntityFactory.create(TenantAsset.class);

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
            guarantor.email().setValue(RandomUtil.randomPersonEmail(guarantor));
            tenantScreening.guarantors().add(guarantor);
        }

    }

    public static User createUser() {
        User user = EntityFactory.create(User.class);
        user.name().setValue("Gregory Holmes");
        user.email().setValue("gregory@221b.com");

        UserCredential credential = EntityFactory.create(UserCredential.class);
        credential.setPrimaryKey(user.getPrimaryKey());

        credential.user().set(user);
        credential.credential().setValue(PasswordEncryptor.encryptPassword("london"));
        credential.enabled().setValue(Boolean.TRUE);
        credential.behavior().setValue(VistaBehavior.POTENTIAL_TENANT);

        return user;
    }

    private void createUnitSelection(UnitSelection unitSelection, AptUnit selectedUnit) {
        // unit selection criteria
        UnitSelectionCriteria criteria = EntityFactory.create(UnitSelectionCriteria.class);
        criteria.floorplanName().setValue(DemoData.REGISTRATION_DEFAULT_FLOORPLAN);
        criteria.propertyCode().setValue(DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE);

        // from
        Calendar availableFrom = DateUtils.calRoundedNow();
        DateUtils.dayStart(availableFrom);
        criteria.availableFrom().setValue(new LogicalDate(availableFrom.getTime()));

        // to will be one month in the future
        Calendar avalableTo = new GregorianCalendar();
        avalableTo.setTime(availableFrom.getTime());
        avalableTo.add(Calendar.MONTH, 1);
        DateUtils.dayStart(avalableTo);
        criteria.availableTo().setValue(new LogicalDate(avalableTo.getTime()));

        unitSelection.selectionCriteria().set(criteria);

        if (selectedUnit != null) {
            unitSelection.selectedUnitId().setValue(selectedUnit.getPrimaryKey());
// TODO: there is no list of MarketRent in Unit now!?.             
//            unitSelection.selectedLeaseTerm().set(RandomUtil.random(selectedUnit.marketRent()).leaseTerm());
        }
    }
}
