/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.ApartmentUnit;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.ChargeLineList;
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
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantAsset.AssetType;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.pt.TenantChargeList;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.domain.ref.Country;
import com.propertyvista.portal.domain.ref.Province;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.domain.util.PrintUtil;
import com.propertyvista.portal.server.pt.ChargesServerCalculation;
import com.propertyvista.portal.server.pt.services.ApartmentServicesImpl;
import com.propertyvista.portal.server.pt.services.ApplicationDebug;
import com.propertyvista.portal.server.pt.services.ApplicationServicesImpl;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

public class PreloadPT extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadPT.class);

    private User user;

    private Pets pets;

    //    private Building building;

    private UnitSelection unitSelection;

    private Application application;

    private static String resourceFileName(String fileName) {
        return PreloadPT.class.getPackage().getName().replace('.', '/') + "/" + fileName;
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

    private void loadEmployer(IncomeInfoEmployer employer, StringBuilder sb) {
        sb.append(" Employer: ").append(employer.name().getStringView());
        sb.append(" \t").append(employer.starts().getStringView()).append(" - ").append(employer.ends().getStringView());

        sb.append(" Supervisor: ").append(employer.supervisorName().getStringView());
        sb.append(" at ").append(employer.supervisorPhone().getStringView());

        sb.append(", Monthly salary ").append(employer.monthlyAmount().getValue());
        sb.append(", Poisiton ").append(employer.position().getStringView());

        sb.append(", \tAddress: ");
        loadAddress(employer, sb);
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

    public void populateAddress(IAddress address) {

        String line1 = 100 + RandomUtil.randomInt(10000) + " " + RandomUtil.random(DemoData.STREETS);

        String zip = "M2J 9V1";

        address.street1().setValue(line1);
        address.street2().setValue("");
        address.city().setValue(RandomUtil.random(DemoData.CITIES));
        address.province().set(retrieveByMemeber(Province.class, address.province().code(), RandomUtil.random(DemoData.PROVINCES)));
        address.postalCode().setValue(zip);
        address.country().set(retrieveNamed(Country.class, "Canada"));
    }

    public static void loadAddress(IAddress address, StringBuilder sb) {
        sb.append(address.street1().getValue());
        sb.append(", ").append(address.city().getStringView());
        sb.append(", ").append(address.province().getStringView());
        sb.append(" ").append(address.postalCode().getStringView());
    }

    private Vehicle createVehicle() {
        Vehicle vehicle = EntityFactory.create(Vehicle.class);

        vehicle.plateNumber().setValue("ML" + RandomUtil.randomInt(9999) + "K");
        vehicle.year().setValue(new Date());
        vehicle.make().setValue(RandomUtil.random(DemoData.CAR_MAKES));
        vehicle.model().setValue(RandomUtil.random(DemoData.CAR_MODELS));
        vehicle.province().set(retrieveByMemeber(Province.class, vehicle.province().code(), RandomUtil.random(DemoData.PROVINCES)));
        vehicle.country().set(retrieveNamed(Country.class, "Canada"));

        return vehicle;
    }

    private PotentialTenantInfo createPotentialTenantInfo(int index) {
        PotentialTenantInfo pti = EntityFactory.create(PotentialTenantInfo.class);

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
        pti.driversLicenseState().set(retrieveByMemeber(Province.class, pti.driversLicenseState().code(), RandomUtil.random(DemoData.PROVINCES)));

        pti.secureIdentifier().setValue("649 951 282");

        pti.notCanadianCitizen().setValue(RandomUtil.randomBoolean());

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
        log.info("Moving {} years back", years);
        log.info("Moving from {} to {}", moveOut, moveIn);
        previousAddress.moveOutDate().setValue(moveOut);
        previousAddress.moveInDate().setValue(moveIn);
        pti.previousAddress().set(previousAddress);

        for (int i = 0; i < RandomUtil.randomInt(3); i++) {
            Vehicle vehicle = createVehicle();
            //            PersistenceServicesFactory.getPersistenceService().persist(vehicle);
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

    //    @SuppressWarnings("unused")
    //    private PotentialTenant createPotentialTenant(int index) {
    //        PotentialTenant pt = EntityFactory.create(PotentialTenant.class);
    //
    //        populatePotentialTenant(pt);
    //
    //        PersistenceServicesFactory.getPersistenceService().persist(pt);
    //
    //        return pt;
    //    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PotentialTenantList.class, PotentialTenantFinancial.class, PotentialTenantInfo.class, Charges.class, ChargeLineList.class,
                    ChargeLine.class, TenantChargeList.class, TenantCharge.class, Application.class, UnitSelection.class, ApplicationProgress.class,
                    Pets.class, EmergencyContact.class, Summary.class, Address.class);
        } else {
            return "This is production";
        }
    }

    private User createUser() {
        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), DemoData.PRELOADED_USERNAME));
        user = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        //        user = EntityFactory.create(User.class);
        //        user.name().setValue("Gregory Holmes");
        //        user.email().setValue("gregory@221b.com");
        //
        //        PersistenceServicesFactory.getPersistenceService().persist(user);
        //
        //        UserCredential credential = EntityFactory.create(UserCredential.class);
        //        credential.setPrimaryKey(user.getPrimaryKey());
        //
        //        credential.user().set(user);
        //        credential.credential().setValue(VistaAuthenticationServicesImpl.encryptPassword("london"));
        //        credential.enabled().setValue(Boolean.TRUE);
        //        credential.behavior().setValue(VistaBehavior.POTENCIAL_TENANT);
        //
        //        PersistenceServicesFactory.getPersistenceService().persist(credential);
        return user;
    }

    private void loadUser(StringBuilder sb) {
        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), DemoData.PRELOADED_USERNAME));
        user = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        sb.append("User: ").append(user).append("\n");
    }

    private Application createApplication() {
        application = EntityFactory.create(Application.class);
        application.user().set(user);
        persist(application);
        return application;
    }

    private void loadApplication(StringBuilder sb) {
        EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
        assert (user != null);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));

        Application loadedApplication = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        sb.append("Application :").append(loadedApplication.rent().amount()).append("\n");
        sb.append("User: ").append(loadedApplication.user()).append("\n");
        ApplicationDebug.dumpApplicationSummary(loadedApplication);
    }

    private void createApplicationProgress() {
        ApplicationProgress progress = ApplicationServicesImpl.createApplicationProgress();
        progress.application().set(application);
        persist(progress);
    }

    private void loadApplicationProgress(StringBuilder sb) {
        EntityQueryCriteria<ApplicationProgress> criteria = EntityQueryCriteria.create(ApplicationProgress.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));

        ApplicationProgress progress = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        if (progress == null) {
            throw new IllegalStateException("Could not find progress for application");
        }

        sb.append(progress.steps().size()).append(" steps\n");
        for (ApplicationWizardStep step : progress.steps()) {
            sb.append("\t");
            sb.append(step.placeId().getStringView());
            sb.append("\n");
        }
    }

    private void createPotentialTenantList() {
        PotentialTenantList tenants = EntityFactory.create(PotentialTenantList.class);
        tenants.application().set(application);
        for (int i = 0; i < DemoData.NUM_POTENTIAL_TENANTS; i++) {
            PotentialTenantInfo tenantInfo = createPotentialTenantInfo(i);
            tenantInfo.application().set(application);
            tenants.tenants().add(tenantInfo);
        }
        persist(tenants);
        for (PotentialTenantInfo tenantInfo : tenants.tenants()) {
            persist(createFinancialInfo(tenantInfo));

            for (int i = 1; i <= 3; i++) {
                String fileName = "apartment" + i + ".jpg";
                ApplicationDocument applicationDocument = EntityFactory.create(ApplicationDocument.class);
                applicationDocument.application().set(application);
                applicationDocument.tenant().set(tenantInfo);
                applicationDocument.type().setValue(i == 1 ? ApplicationDocument.DocumentType.income : ApplicationDocument.DocumentType.securityInfo);
                applicationDocument.filename().setValue(fileName);
                try {
                    InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceFileName(fileName));
                    byte[] data = IOUtils.toByteArray(in);
                    applicationDocument.fileSize().setValue((long) data.length);
                    applicationDocument.data().setValue(data);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                persist(applicationDocument);
            }
        }
    }

    private void createPets() {
        pets = EntityFactory.create(Pets.class);
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

            persist(pet);
            pets.pets().add(pet);
        }

        persist(pets);
    }

    private void loadPets(StringBuilder sb) {
        EntityQueryCriteria<Pets> criteria = EntityQueryCriteria.create(Pets.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        Pets pets = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        sb.append("Pets\n");

        for (Pet pet : pets.pets()) {
            sb.append("\t");
            sb.append(pet.type().getValue());

            sb.append(" \t");
            sb.append(pet.name().getStringView());

            sb.append(" \t");
            sb.append(pet.color().getStringView());

            sb.append(" \t");
            sb.append(pet.breed().getStringView());

            sb.append(" \t");
            sb.append(pet.weight().getValue()).append(" ").append(pet.weightUnit().getValue());

            sb.append(" $");
            sb.append(pet.chargeLine().charge().amount().getValue());

            sb.append("\n");
        }
    }

    private void createUnitSelection() {
        unitSelection = EntityFactory.create(UnitSelection.class);
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

        ApartmentServicesImpl apartmentServices = new ApartmentServicesImpl();
        apartmentServices.loadAvailableUnits(unitSelection);

        // chose the first unit for demo
        ApartmentUnit selectedUnit = unitSelection.availableUnits().units().iterator().next();
        unitSelection.selectedUnit().set(selectedUnit);
        unitSelection.selectedUnitId().set(selectedUnit.id());
        unitSelection.markerRent().set(unitSelection.selectedUnit().marketRent().get(1)); // choose second lease
        unitSelection.rentStart().setValue(selectedUnit.avalableForRent().getValue());

        persist(unitSelection);
    }

    private void loadUnitSelection(StringBuilder sb) {
        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        UnitSelection unitSelection = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        ApartmentServicesImpl apartmentServices = new ApartmentServicesImpl();
        apartmentServices.loadAvailableUnits(unitSelection);

        //        building = unitSelection.selectedUnit().building();

        sb.append(PrintUtil.print(unitSelection));
    }

    private void loadTenants(StringBuilder sb) {
        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        PotentialTenantList tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        sb.append(tenantList.tenants().size()).append(" potential tenants");
        sb.append("\n");

        for (PotentialTenantInfo tenant : tenantList.tenants()) {

            sb.append("\n--- TENANT ---\n");
            sb.append(tenant.status().getStringView());
            sb.append(", ");

            sb.append(tenant.firstName().getStringView());
            sb.append(" ");
            if (tenant.middleName().getStringView().length() > 0) {
                sb.append(tenant.middleName().getStringView());
                sb.append(" ");
            }
            sb.append(tenant.lastName().getStringView());

            sb.append("\t\t Born on ");
            sb.append(tenant.birthDate().getValue());

            sb.append("\t");
            sb.append(tenant.homePhone().getStringView()).append(" | ").append(tenant.mobilePhone().getStringView());

            sb.append("\t");
            sb.append(tenant.email().getStringView());

            sb.append("\t");

            sb.append("\t Payment $").append(tenant.payment().getStringView());

            sb.append("\n\t");

            sb.append(tenant.driversLicense().getStringView()).append(" ").append(tenant.driversLicenseState().getStringView());

            sb.append("\t").append(tenant.secureIdentifier().getStringView());

            sb.append("Current address");
            sb.append(PrintUtil.print(tenant.currentAddress()));
            sb.append("Previous address");
            sb.append(PrintUtil.print(tenant.previousAddress()));

            sb.append("\nVehicles\n");
            // vehicles
            for (Vehicle vehicle : tenant.vehicles()) {
                sb.append("\n\t");
                sb.append(vehicle.year().getStringView()).append(" ");
                sb.append(vehicle.province().getStringView()).append(" ");
                sb.append(vehicle.make().getStringView()).append(" ").append(vehicle.model().getStringView()).append(" ");
                sb.append(vehicle.plateNumber().getStringView()).append(" ");
            }

            sb.append("\n");

            // Financial
            loadFinancialInfo(sb, tenant);
        }
    }

    private PotentialTenantFinancial createFinancialInfo(PotentialTenantInfo tenant) {
        PotentialTenantFinancial ptf = EntityFactory.create(PotentialTenantFinancial.class);

        ptf.application().set(application);
        ptf.id().set(tenant.id());

        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            TenantIncome income = EntityFactory.create(TenantIncome.class);

            income.incomeSource().setValue(IncomeSource.fulltime);
            income.employer().set(createEmployer());
            //            income. monthlyAmount().setValue(DomainUtil.createMoney(300d + RandomUtil.randomInt(4000)).getValue());

            //income.active().setValue(RandomUtil.randomBoolean());

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

    private void loadFinancialInfo(StringBuilder sb, PotentialTenantInfo tenant) {
        EntityQueryCriteria<PotentialTenantFinancial> criteria = EntityQueryCriteria.create(PotentialTenantFinancial.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), tenant.id().getValue()));
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        PotentialTenantFinancial financial = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        if (financial == null) {
            sb.append("No financial data\n");
            return;
        }

        sb.append("\nFinancial Info\n");

        sb.append("Incomes\n");
        for (TenantIncome income : financial.incomes()) {
            sb.append("\t");
            sb.append(income.incomeSource().getValue());
            sb.append(" $");
            // sb.append(income.monthlyAmount().getValue());

            loadEmployer(income.employer(), sb);

            //sb.append(" Active: ").append(income.active().getValue());

            sb.append("\n");
        }

        sb.append("Assets\n");
        for (TenantAsset asset : financial.assets()) {
            sb.append("\t");
            sb.append(asset.assetType().getValue());
            sb.append(" $");
            sb.append(asset.assetValue().getValue());
            sb.append("\n");
        }

        sb.append("Guarantor\n");
        for (TenantGuarantor guarantor : financial.guarantors()) {
            sb.append("\t");
            sb.append(guarantor.relationship().getValue());
            sb.append(", ");
            sb.append(guarantor.firstName().getStringView());
            sb.append(" ");
            sb.append(guarantor.lastName().getStringView());
            sb.append("\n");
        }

        sb.append("\n\n");
    }

    private void createCharges() {
        Charges charges = EntityFactory.create(Charges.class);
        charges.application().set(application);
        ChargesServerCalculation.updateChargesFromApplication(charges);

        persist(charges);
    }

    private void loadCharges(StringBuilder sb) {
        EntityQueryCriteria<Charges> criteria = EntityQueryCriteria.create(Charges.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        List<Charges> chargesList = PersistenceServicesFactory.getPersistenceService().query(criteria);
        for (Charges charges : chargesList) {
            sb.append(PrintUtil.print(charges));
        }
        sb.append("\n\n");
    }

    @Override
    public String create() {
        user = createUser();
        createApplication();
        createUnitSelection();
        createApplicationProgress();
        createPotentialTenantList();
        createPets();
        createCharges();

        load();

        StringBuilder b = new StringBuilder();
        b.append("Created potential tenant series of data");
        return b.toString();
    }

    public void load() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n---------------------------- USER -----------------------------------\n");
        loadUser(sb);

        sb.append("\n\n---------------------------- APPLICATION -----------------------------\n");
        loadApplication(sb);
        loadApplicationProgress(sb);

        sb.append("\n\n---------------------------- APARTMENT -------------------------------\n");
        loadUnitSelection(sb);

        sb.append("\n\n---------------------------- TENANTS ---------------------------------\n");
        loadTenants(sb);

        sb.append("\n\n---------------------------- PETS ------------------------------------\n");
        loadPets(sb);

        sb.append("\n\n---------------------------- CHARGES ---------------------------------\n");
        loadCharges(sb);

        //        List<PotentialTenant> pts = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<PotentialTenant>(PotentialTenant.class));
        //        StringBuilder sb = new StringBuilder();
        //        sb.append("\n\nLoaded " + pts.size() + " potential tenants\n\n");
        //        for (PotentialTenant pt : pts) {
        //            sb.append(pt.firstName().getStringView());
        //            sb.append(" ");
        //            if (pt.middleName().getStringView().length() > 0) {
        //                sb.append(pt.middleName().getStringView());
        //                sb.append(" ");
        //            }
        //            sb.append(pt.lastName().getStringView());
        //
        //            sb.append("\t\t");
        //            sb.append(pt.birthDate().getStringView());
        //
        //            sb.append("\t");
        //            sb.append(pt.homePhone().getStringView()).append(" | ").append(pt.mobilePhone().getStringView());
        //
        //            sb.append("\t");
        //            sb.append(pt.email().getStringView());
        //
        //            sb.append("\t");
        //            sb.append(pt.status().getStringView());
        //
        //            sb.append("\t$").append(pt.payment().getStringView());
        //
        //            sb.append("\n");
        //        }
        //        sb.append("\n\n");
        //

        log.info(sb.toString());
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }
}
