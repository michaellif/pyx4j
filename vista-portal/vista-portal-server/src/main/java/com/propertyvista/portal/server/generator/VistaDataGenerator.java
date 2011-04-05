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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.AptUnit;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.VistaBehavior;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.IncomeInfoEmployer;
import com.propertyvista.portal.domain.pt.IncomeSource;
import com.propertyvista.portal.domain.pt.LegalQuestions;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.Pet.WeightUnit;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenant.Relationship;
import com.propertyvista.portal.domain.pt.PotentialTenant.Status;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantAsset.AssetType;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.server.preloader.PreloadPT;
import com.propertyvista.portal.server.preloader.RandomUtil;
import com.propertyvista.portal.server.pt.ChargesServerCalculation;
import com.propertyvista.portal.server.pt.services.ApplicationServiceImpl;
import com.propertyvista.portal.server.pt.util.PreloadUtil;
import com.propertyvista.server.common.security.PasswordEncryptor;
import com.propertyvista.server.domain.ApplicationDocumentData;
import com.propertyvista.server.domain.UserCredential;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.essentials.server.preloader.DataGenerator;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.gwt.server.IOUtils;

public class VistaDataGenerator {

    private final static Logger log = LoggerFactory.getLogger(VistaDataGenerator.class);

    public VistaDataGenerator() {
    }

    public VistaDataGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
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
        vehicle.year().setValue(new Date());
        vehicle.make().setValue(RandomUtil.random(DemoData.CAR_MAKES));
        vehicle.model().setValue(RandomUtil.random(DemoData.CAR_MODELS));
        vehicle.province().set(RandomUtil.random(SharedData.getProvinces()));
        vehicle.country().set(SharedData.findCountryCanada());
        //        vehicle.province().set(retrieveByMemeber(Province.class, vehicle.province().code(), RandomUtil.random(DemoData.PROVINCES)));
        //        vehicle.country().set(retrieveNamed(Country.class, "Canada"));

        return vehicle;
    }

    public EmergencyContact createEmergencyContact() {
        EmergencyContact contact = EntityFactory.create(EmergencyContact.class);

        contact.firstName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
        contact.middleName().setValue("");
        contact.lastName().setValue(RandomUtil.random(DemoData.LAST_NAMES));

        contact.homePhone().setValue(RandomUtil.randomPhone());
        contact.mobilePhone().setValue(RandomUtil.randomPhone());

        String email = contact.firstName().getStringView().toLowerCase() + "." + contact.lastName().getStringView().toLowerCase() + "@"
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

        employer.starts().setValue(DateUtils.createDate(startYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)));
        employer.ends().setValue(DateUtils.createDate(endYear, RandomUtil.randomInt(12), RandomUtil.randomInt(28)));

        return employer;
    }

    public ApplicationDocument createApplicationDocument(PotentialTenantInfo tenantInfo, String fileName, ApplicationDocument.DocumentType documentType) {
        assert (tenantInfo.application() != null);

        ApplicationDocument applicationDocument = EntityFactory.create(ApplicationDocument.class);
        applicationDocument.application().set(tenantInfo.application());
        applicationDocument.type().setValue(documentType);
        applicationDocument.filename().setValue(fileName);
        //InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(PreloadUtil.resourceFileName(PreloadPT.class, fileName));
        //byte[] data = IOUtils.toByteArray(in);
        String filename = PreloadUtil.resourceFileName(PreloadPT.class, fileName);
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
        String filename = PreloadUtil.resourceFileName(PreloadPT.class, fileName);
        try {
            byte[] data = IOUtils.getResource(filename);
            if (data == null) {
                log.error("Could not find picture [{}] in classpath", filename);
                throw new Error("Could not find picture [" + filename + "] in classpath");
            } else {
                ApplicationDocumentData applicationDocumentData = EntityFactory.create(ApplicationDocumentData.class);
                //applicationDocumentData.id().setValue(documentId);
                applicationDocumentData.tenant().set(tenantInfo);
                applicationDocumentData.application().set(tenantInfo.application());
                applicationDocumentData.data().setValue(data);
                return applicationDocumentData;
            }
        } catch (Exception e) {
            log.error("Failed to read the file [{}]", filename, e);
            throw new Error("Failed to read the file [" + filename + "]");
        }
    }

    private Pets createPets(Application application) {
        Pets pets = EntityFactory.create(Pets.class);
        pets.application().set(application);

        for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
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

            pet.birthDate().setValue(RandomUtil.randomDate(1985, 2010));

            // charge line
            pet.chargeLine().set(DomainUtil.createChargeLine(ChargeType.petCharge, 20d + RandomUtil.randomInt(100)));

            pets.pets().add(pet);
        }
        return pets;
    }

    public void populateAddress(IAddress address) {

        String line1 = 100 + RandomUtil.randomInt(10000) + " " + RandomUtil.random(DemoData.STREETS);

        String zip = "M2J 9V1";

        address.street1().setValue(line1);
        address.street2().setValue("");
        address.city().setValue(RandomUtil.random(DemoData.CITIES));
        address.province().set(RandomUtil.random(SharedData.getProvinces()));
        //        address.province().set(retrieveByMemeber(Province.class, address.province().code(), RandomUtil.random(DemoData.PROVINCES)));
        address.postalCode().setValue(zip);
        address.country().set(SharedData.findCountryCanada());
    }

    public Address createAddress() {
        Address address = EntityFactory.create(Address.class);

        populateAddress(address);

        address.moveInDate().setValue(RandomUtil.randomDate(2008, 2010));
        address.moveOutDate().setValue(RandomUtil.randomDate(2010, 2012));

        address.payment().setValue(1000d + RandomUtil.randomInt(1000));

        address.phone().setValue(RandomUtil.randomPhone());
        address.rented().setValue((OwnedRented) RandomUtil.random(Address.OwnedRented.class));
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

    private PotentialTenantFinancial createFinancialInfo(PotentialTenantInfo tenant) {
        assert (tenant.application() != null);
        PotentialTenantFinancial ptf = EntityFactory.create(PotentialTenantFinancial.class);

        ptf.application().set(tenant.application());
        ptf.id().set(tenant.id());

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

            ptf.incomes().add(income);
        }

        for (int i = 0; i < RandomUtil.randomInt(3); i++) {
            TenantAsset asset = EntityFactory.create(TenantAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.percent().setValue((double) RandomUtil.randomInt(100));
            asset.assetValue().setValue(DomainUtil.createMoney(100d + RandomUtil.randomInt(10000)).getValue());

            ptf.assets().add(asset);
        }

        if (tenant.relationship().getValue() == Relationship.Son || tenant.relationship().getValue() == Relationship.Daughter) {
            for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
                TenantGuarantor guarantor = EntityFactory.create(TenantGuarantor.class);
                guarantor.firstName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
                guarantor.lastName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
                guarantor.relationship().setValue(RandomUtil.random(TenantGuarantor.Relationship.values()));
                ptf.guarantors().add(guarantor);
            }
        }

        return ptf;
    }

    public PotentialTenantList createPotentialTenantList(Application application) {
        PotentialTenantList tenants = EntityFactory.create(PotentialTenantList.class);
        tenants.application().set(application);

        for (int i = 0; i < DemoData.NUM_POTENTIAL_TENANTS; i++) {
            PotentialTenantInfo tenantInfo = createPotentialTenantInfo(application, i);
            tenants.tenants().add(tenantInfo);
        }
        return tenants;
    }

    private void populatePotentialTenant(PotentialTenantInfo pt, Relationship relationship, Status status) {

        if (status == Status.Applicant) {
            pt.firstName().setValue("Jack");
            pt.middleName().setValue("");
            pt.lastName().setValue("London");
        } else {
            pt.firstName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
            pt.middleName().setValue(RandomUtil.randomInt(100) % 7 == 0 ? "M" : "");
            pt.lastName().setValue(RandomUtil.random(DemoData.LAST_NAMES));
        }

        pt.birthDate().setValue(RandomUtil.randomDate(1930, 1980));
        pt.homePhone().setValue(RandomUtil.randomPhone());
        pt.mobilePhone().setValue(RandomUtil.randomPhone());
        pt.workPhone().setValue(RandomUtil.randomPhone());

        String email = pt.firstName().getStringView().toLowerCase() + "." + pt.lastName().getStringView().toLowerCase() + "@"
                + RandomUtil.random(DemoData.EMAIL_DOMAINS);
        pt.email().setValue(email);

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
        currentAddress.moveOutDate().setValue(RandomUtil.randomDate(2012, 2013)); // this has to be in the future
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
        previousAddress.moveOutDate().setValue(moveOut);
        previousAddress.moveInDate().setValue(moveIn);
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

        Calendar avalableTo = new GregorianCalendar();
        avalableTo.setTime(new Date());
        avalableTo.add(Calendar.MONTH, 1);
        DateUtils.dayStart(avalableTo);

        criteria.availableFrom().setValue(new Date());
        criteria.availableTo().setValue(avalableTo.getTime());

        unitSelection.selectionCriteria().set(criteria);

        if (selectedUnit != null) {
            unitSelection.selectedUnitId().setValue(selectedUnit.getPrimaryKey());
            unitSelection.selectedLeaseTerm().set(RandomUtil.random(selectedUnit.marketRent()).leaseTerm());
        }

        return unitSelection;
    }
}
