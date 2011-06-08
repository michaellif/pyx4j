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

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.common.domain.IAddressFull;
import com.propertyvista.common.domain.IAddressFull.StreetDirection;
import com.propertyvista.common.domain.IAddressFull.StreetType;
import com.propertyvista.common.domain.User;
import com.propertyvista.common.domain.VistaBehavior;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.domain.ptapp.Address;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.ApplicationDocument;
import com.propertyvista.portal.domain.ptapp.ApplicationProgress;
import com.propertyvista.portal.domain.ptapp.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.EmergencyContact;
import com.propertyvista.portal.domain.ptapp.IncomeInfoEmployer;
import com.propertyvista.portal.domain.ptapp.IncomeSource;
import com.propertyvista.portal.domain.ptapp.LegalQuestions;
import com.propertyvista.portal.domain.ptapp.Pet;
import com.propertyvista.portal.domain.ptapp.Pet.WeightUnit;
import com.propertyvista.portal.domain.ptapp.Pets;
import com.propertyvista.portal.domain.ptapp.PotentialTenant.Relationship;
import com.propertyvista.portal.domain.ptapp.PotentialTenant.Status;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.TenantAsset;
import com.propertyvista.portal.domain.ptapp.TenantAsset.AssetType;
import com.propertyvista.portal.domain.ptapp.TenantGuarantor;
import com.propertyvista.portal.domain.ptapp.TenantIncome;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.ptapp.UnitSelectionCriteria;
import com.propertyvista.portal.domain.ptapp.Vehicle;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.ptapp.ApplicationDocumentServletParameters;
import com.propertyvista.portal.server.preloader.PreloadPT;
import com.propertyvista.portal.server.preloader.RandomUtil;
import com.propertyvista.portal.server.ptapp.ChargesServerCalculation;
import com.propertyvista.portal.server.ptapp.services.ApplicationServiceImpl;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.ApplicationDocumentData;
import com.propertyvista.server.domain.UserCredential;

public class PTGenerator {

    private final static Logger log = LoggerFactory.getLogger(PTGenerator.class);

    private final long seed;

    public PTGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
        this.seed = seed;
    }

    public User createUser(int number) {
        String email = DemoData.CRM_CUSTOMER_USER_PREFIX + CommonsStringUtils.d000(number) + DemoData.USERS_DOMAIN;
        User user = EntityFactory.create(User.class);
        user.name().setValue(email.substring(0, email.indexOf('@')));
        user.email().setValue(email);
        return user;
    }

    public Summary createSummary(Application application, AptUnit selectedUnit) {
        Summary summary = EntityFactory.create(Summary.class);
        summary.application().set(application);
        summary.unitSelection().set(createUnitSelection(application, selectedUnit));
        summary.tenantList().set(createPotentialTenantList(application));
        createTenantFinancials(summary.tenantFinancials(), summary.tenantList());
        summary.pets().set(createPets(application));
        summary.charges().set(createCharges(summary, selectedUnit));

        return summary;
    }

    private Charges createCharges(Summary summary, AptUnit selectedUnit) {
        assert (summary.application() != null);

        Charges charges = EntityFactory.create(Charges.class);
        charges.application().set(summary.application());
        ChargesServerCalculation.updateChargesFromObjects(charges, summary.unitSelection(), selectedUnit, summary.tenantList(), summary.pets());
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

        Address address = createAddress();
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

    private Pets createPets(Application application) {
        Pets pets = EntityFactory.create(Pets.class);
        pets.application().set(application);

        int maxPets;
        if (Math.abs(this.seed) < 1000) {
            maxPets = 1 + RandomUtil.randomInt(2);
        } else {
            maxPets = RandomUtil.randomInt(3);
        }

        for (int i = 0; i < maxPets; i++) {
            Pet pet = EntityFactory.create(Pet.class);

            pet.type().setValue(Pet.PetType.dog);
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

            pets.pets().add(pet);
        }
        return pets;
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

    public Address createAddress() {
        Address address = EntityFactory.create(Address.class);

        populateAddress(address);

        address.moveInDate().setValue(RandomUtil.randomLogicalDate(2008, 2010));
        address.moveOutDate().setValue(RandomUtil.randomLogicalDate(2010, 2012));

        address.payment().setValue(1000d + RandomUtil.randomInt(1000));

        address.phone().setValue(RandomUtil.randomPhone());
        address.rented().setValue(RandomUtil.randomEnum(Address.OwnedRented.class));
        address.managerName().setValue("Mr. " + RandomUtil.random(DemoData.LAST_NAMES));

        return address;
    }

    private void createTenantFinancials(IList<SummaryPotentialTenantFinancial> tenantFinancials, PotentialTenantList tenants) {
        for (PotentialTenantInfo tenantInfo : tenants.tenants()) {
            SummaryPotentialTenantFinancial summaryTenantFinancial = EntityFactory.create(SummaryPotentialTenantFinancial.class);
            summaryTenantFinancial.tenantFinancial().set(createFinancialInfo(tenantInfo));
            tenantFinancials.add(summaryTenantFinancial);
        }
    }

    public PotentialTenantFinancial createFinancialInfo(PotentialTenantInfo tenant) {
        assert (tenant.application() != null);
        PotentialTenantFinancial tenantFinancial = EntityFactory.create(PotentialTenantFinancial.class);

        tenantFinancial.application().set(tenant.application());
        tenantFinancial.id().set(tenant.id());
        tenantFinancial.setPrimaryKey(tenant.getPrimaryKey());

        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            TenantIncome income = EntityFactory.create(TenantIncome.class);

            income.incomeSource().setValue(IncomeSource.fulltime);
            income.employer().set(createEmployer());
            //            income. monthlyAmount().setValue(DomainUtil.createMoney(300d + RandomUtil.randomInt(4000)).getValue());

            //income.active().setValue(RandomUtil.randomBoolean());

            if (IncomeSource.fulltime.equals(income.incomeSource().getValue())) {
                ApplicationDocument applicationDocument = createApplicationDocument(tenant, "doc-income" + RandomUtil.randomInt(3) + ".jpg",
                        ApplicationDocument.DocumentType.income);
                income.documents().add(applicationDocument);
            }

            tenantFinancial.incomes().add(income);
        }

        int minAssets = 0;
        if (tenantFinancial.incomes().size() == 0) {
            minAssets = 1;
        }
        for (int i = 0; i < minAssets + RandomUtil.randomInt(3); i++) {
            TenantAsset asset = EntityFactory.create(TenantAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.percent().setValue((double) RandomUtil.randomInt(100));
            asset.assetValue().setValue(DomainUtil.createMoney(100d + RandomUtil.randomInt(10000)).getValue());

            tenantFinancial.assets().add(asset);
        }

        if (tenant.relationship().getValue() == Relationship.Son || tenant.relationship().getValue() == Relationship.Daughter) {
            for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
                TenantGuarantor guarantor = EntityFactory.create(TenantGuarantor.class);
                guarantor.name().firstName().setValue(DataGenerator.randomFirstName());
                guarantor.name().lastName().setValue(DataGenerator.randomLastName());
                guarantor.relationship().setValue(RandomUtil.random(TenantGuarantor.Relationship.values()));
                guarantor.birthDate().setValue(RandomUtil.randomLogicalDate(1960, 2011 - 18));
                guarantor.email().setValue(RandomUtil.randomPersonEmail(guarantor));
                tenantFinancial.guarantors().add(guarantor);
            }
        }

        return tenantFinancial;
    }

    public PotentialTenantList createPotentialTenantList(Application application) {
        PotentialTenantList tenants = EntityFactory.create(PotentialTenantList.class);
        tenants.application().set(application);

        int maxTenants = DemoData.NUM_POTENTIAL_TENANTS;
        if (Math.abs(this.seed) > 1000) {
            maxTenants = 1 + RandomUtil.randomInt(5);
        }

        for (int i = 0; i < maxTenants; i++) {
            PotentialTenantInfo tenantInfo = createPotentialTenantInfo(application, i);
            tenants.tenants().add(tenantInfo);
        }
        return tenants;
    }

    private void populatePotentialTenant(PotentialTenantInfo pt, Relationship relationship, Status status) {

        pt.name().firstName().setValue(DataGenerator.randomFirstName());
        pt.name().lastName().setValue(DataGenerator.randomLastName());
        if (status == Status.Applicant) {
            pt.name().middleName().setValue("");
        } else {
            pt.name().middleName().setValue(RandomUtil.randomInt(100) % 7 == 0 ? "M" : "");
        }

        pt.birthDate().setValue(RandomUtil.randomLogicalDate(1930, 1980));
        pt.homePhone().setValue(RandomUtil.randomPhone());
        pt.mobilePhone().setValue(RandomUtil.randomPhone());
        pt.workPhone().setValue(RandomUtil.randomPhone());

        pt.email().setValue(RandomUtil.randomPersonEmail(pt));

        pt.payment().setValue(1.0d + RandomUtil.randomInt(3000));

        if (relationship == Relationship.Daughter || relationship == Relationship.Son) {
            pt.status().setValue(Status.Dependant);
        }
        pt.takeOwnership().setValue(RandomUtil.randomBoolean());
    }

    private PotentialTenantInfo createPotentialTenantInfo(Application application, int index) {
        PotentialTenantInfo pti = EntityFactory.create(PotentialTenantInfo.class);
        pti.application().set(application);

        // first tenant must always be an applicant
        if (index == 0) {
            pti.status().setValue(Status.Applicant);
        } else if (index == 1) {
            pti.status().setValue(Status.CoApplicant);
        } else if (index == 2) {
            pti.status().setValue(Status.CoApplicant);
        } else {
            pti.status().setValue(Status.Dependant);
        }

        pti.relationship().setValue(RandomUtil.random(DemoData.RELATIONSHIPS));

        populatePotentialTenant(pti, pti.relationship().getValue(), pti.status().getValue());

        if (index == 0) {
            pti.email().setValue(application.user().email().getValue());
        }

        String driversLicense = "JTVMX" + RandomUtil.randomInt(10) + "VMIEK";
        pti.driversLicense().setValue(driversLicense);
        pti.driversLicenseState().set(RandomUtil.random(SharedData.getProvinces()));

        pti.secureIdentifier().setValue("649 951 282");

        pti.notCanadianCitizen().setValue(RandomUtil.randomBoolean());

        if (pti.notCanadianCitizen().isBooleanTrue()) {
            ApplicationDocument.DocumentType documentType = ApplicationDocument.DocumentType.securityInfo;
            pti.documents().add(createApplicationDocument(pti, "doc-security" + RandomUtil.randomInt(3) + ".jpg", documentType));
        }

        Address currentAddress = createAddress();
        currentAddress.moveOutDate().setValue(RandomUtil.randomLogicalDate(2012, 2013)); // this has to be in the future
        pti.currentAddress().set(currentAddress);

        Address previousAddress = createAddress();
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
        pti.previousAddress().set(previousAddress);

        for (int i = 0; i < RandomUtil.randomInt(3); i++) {
            Vehicle vehicle = createVehicle();
            pti.vehicles().add(vehicle);
        }

        LegalQuestions legalQuestions = createLegalQuestions();
        pti.legalQuestions().set(legalQuestions);

        EmergencyContact ec1 = createEmergencyContact();
        pti.emergencyContacts().add(ec1);

        EmergencyContact ec2 = createEmergencyContact();
        pti.emergencyContacts().add(ec2);

        return pti;
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

    public Application createApplication(User user) {
        Application application = EntityFactory.create(Application.class);
        application.user().set(user);
        return application;
    }

    public ApplicationProgress createApplicationProgress(Application application) {
        ApplicationProgress progress = ApplicationServiceImpl.createApplicationProgress();
        progress.application().set(application);
        return progress;
    }

    public UnitSelection createUnitSelection(Application application, AptUnit selectedUnit) {
        UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
        unitSelection.application().set(application);

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

        return unitSelection;
    }
}
