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

import com.propertyvista.portal.domain.AddOn;
import com.propertyvista.portal.domain.Amenity;
import com.propertyvista.portal.domain.ApptUnit;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.Concession;
import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.MarketRent;
import com.propertyvista.portal.domain.UnitInfoItem;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.Utility;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.ChargeLineList;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.IncomeInfoEmployer;
import com.propertyvista.portal.domain.pt.IncomeSource;
import com.propertyvista.portal.domain.pt.LegalQuestions;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.Pet.WeightUnit;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenant;
import com.propertyvista.portal.domain.pt.PotentialTenant.Relationship;
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
import com.propertyvista.portal.domain.ref.Province;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.server.pt.ChargesServerCalculation;
import com.propertyvista.portal.server.pt.PotentialTenantServicesImpl;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

public class PreloadPT extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadPT.class);

    private User user;

    private Pets pets;

    private Building building;

    private UnitSelection unitSelection;

    private Application application;

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
        persist(address);
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
        vehicle.year().setValue(1990 + RandomUtil.randomInt(20));
        vehicle.make().setValue(RandomUtil.random(DemoData.CAR_MAKES));
        vehicle.model().setValue(RandomUtil.random(DemoData.CAR_MODELS));
        vehicle.province().set(retrieveByMemeber(Province.class, vehicle.province().code(), RandomUtil.random(DemoData.PROVINCES)));

        return vehicle;
    }

    private PotentialTenantInfo createPotentialTenantInfo(int index) {
        PotentialTenantInfo pti = EntityFactory.create(PotentialTenantInfo.class);

        // first tenant must always be an applicant
        if (index == 0) {
            pti.relationship().setValue(Relationship.Applicant);
        } else if (index == 1) {
            pti.relationship().setValue(Relationship.CoApplicant);
        } else if (index == 2) {
            pti.relationship().setValue(Relationship.CoApplicant);
        } else {
            pti.relationship().setValue(RandomUtil.random(DemoData.RELATIONSHIPS));
        }

        populatePotentialTenant(pti, pti.relationship().getValue());

        String driversLicense = "JTVMX" + RandomUtil.randomInt(10) + "VMIEK";
        pti.driversLicense().setValue(driversLicense);
        pti.driversLicenseState().set(retrieveByMemeber(Province.class, pti.driversLicenseState().code(), RandomUtil.random(DemoData.PROVINCES)));

        String si = RandomUtil.randomInt(1000) + " " + RandomUtil.randomInt(1000) + " " + RandomUtil.randomInt(1000);
        pti.secureIdentifier().setValue(si);

        pti.canadianCitizen().setValue(RandomUtil.randomBoolean());

        Address currentAddress = createAddress();
        persist(currentAddress);
        pti.currentAddress().set(currentAddress);

        Address previousAddress = createAddress();
        persist(previousAddress);
        pti.previousAddress().set(previousAddress);

        for (int i = 0; i < RandomUtil.randomInt(3); i++) {
            Vehicle vehicle = createVehicle();
            //            PersistenceServicesFactory.getPersistenceService().persist(vehicle);
            pti.vehicles().add(vehicle);
        }

        LegalQuestions legalQuestions = createLegalQuestions();
        PersistenceServicesFactory.getPersistenceService().persist(legalQuestions);
        pti.legalQuestions().set(legalQuestions);

        EmergencyContact ec1 = createEmergencyContact();
        PersistenceServicesFactory.getPersistenceService().persist(ec1);
        pti.emergencyContact1().set(ec1);

        EmergencyContact ec2 = createEmergencyContact();
        PersistenceServicesFactory.getPersistenceService().persist(ec2);
        pti.emergencyContact2().set(ec2);

        PersistenceServicesFactory.getPersistenceService().persist(pti);

        return pti;
    }

    private void populatePotentialTenant(PotentialTenant pt, Relationship relationship) {
        pt.firstName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
        pt.middleName().setValue(RandomUtil.randomInt(100) % 4 == 0 ? "M" : "");
        pt.lastName().setValue(RandomUtil.random(DemoData.LAST_NAMES));
        pt.birthDate().setValue(RandomUtil.randomDate(1930, 1980));
        pt.homePhone().setValue(RandomUtil.randomPhone());
        pt.mobilePhone().setValue(RandomUtil.randomPhone());
        pt.workPhone().setValue(RandomUtil.randomPhone());

        String email = pt.firstName().getStringView().toLowerCase() + "." + pt.lastName().getStringView().toLowerCase() + "@"
                + RandomUtil.random(DemoData.EMAIL_DOMAINS);
        pt.email().setValue(email);

        pt.payment().setValue(1.0d + RandomUtil.randomInt(3000));

        pt.dependant().setValue(false);
        if (relationship == Relationship.Daughter || relationship == Relationship.Son) {
            pt.dependant().setValue(true);
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
            return deleteAll(PotentialTenantList.class, PotentialTenant.class, PotentialTenantFinancial.class, PotentialTenantInfo.class, Charges.class,
                    ChargeLineList.class, ChargeLine.class, TenantChargeList.class, TenantCharge.class, Application.class, UnitSelection.class,
                    ApplicationProgress.class, Pets.class, EmergencyContact.class, Summary.class, Address.class);
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
    }

    private ApplicationWizardStep createWizardStep(AppPlace place, ApplicationWizardStep.Status status) {
        ApplicationWizardStep ws = EntityFactory.create(ApplicationWizardStep.class);
        ws.placeToken().setValue(AppPlaceInfo.getPlaceId(place.getClass()));
        ws.status().setValue(status);
        return ws;
    }

    private void createApplicationProgress() {
        ApplicationProgress progress = EntityFactory.create(ApplicationProgress.class);
        progress.steps().add(createWizardStep(new SiteMap.Apartment(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Tenants(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Info(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Financial(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Pets(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Charges(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Summary(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Payment(), ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(new SiteMap.Completion(), ApplicationWizardStep.Status.notVisited));
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
            sb.append(step.placeToken().getStringView());
            sb.append("\n");
        }
    }

    private void createPotentialTenantList() {
        PotentialTenantList tenants = EntityFactory.create(PotentialTenantList.class);
        tenants.application().set(application);
        for (int i = 0; i < DemoData.NUM_POTENTIAL_TENANTS; i++) {
            PotentialTenantInfo tenantInfo = createPotentialTenantInfo(i);
            tenantInfo.application().set(application);
            createFinancialInfo(tenantInfo);
            tenants.tenants().add(tenantInfo);
        }

        persist(tenants);
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

            pet.weight().setValue(20 + RandomUtil.randomInt(100));
            if (RandomUtil.randomBoolean()) {
                pet.weightUnit().setValue(WeightUnit.kg);
            } else {
                pet.weightUnit().setValue(WeightUnit.lb);
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

        PotentialTenantServicesImpl.loadAvailableUnits(unitSelection);

        // now chose the first unit
        if (!unitSelection.availableUnits().units().isEmpty()) {
            ApptUnit selectedUnit = unitSelection.availableUnits().units().iterator().next();
            unitSelection.selectedUnit().set(selectedUnit);
            unitSelection.building().set(unitSelection.selectedUnit().building());
            building = unitSelection.building();
            //            log.info("Created building {}", unitSelection.selectedUnit().building());
            unitSelection.markerRent().set(unitSelection.selectedUnit().marketRent().get(1)); // choose second lease
            unitSelection.rentStart().setValue(DateUtils.createDate(2011, 2, 17));
        }

        persist(unitSelection);
    }

    private void loadUnitSelection(StringBuilder sb) {
        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        UnitSelection unitSelection = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        PotentialTenantServicesImpl.loadAvailableUnits(unitSelection);

        sb.append("Criteria\n\t");
        sb.append(unitSelection.selectionCriteria());
        sb.append("\n\n");

        sb.append(unitSelection.availableUnits().units().size());
        sb.append(" available units\n");
        for (ApptUnit unit : unitSelection.availableUnits().units()) {
            sb.append("\t");
            sb.append(unit.suiteNumber().getStringView());
            sb.append(" ");
            sb.append(unit.bedrooms().getValue()).append(" beds, ");
            sb.append(unit.bathrooms().getValue()).append(" baths,");
            sb.append(" ");
            sb.append(unit.area().getValue()).append(", sq ft");

            sb.append(" available on ");
            sb.append(unit.avalableForRent().getStringView());

            log.info("Available {}", unit.building());

            sb.append(", status: ").append(unit.status().getStringView());

            sb.append("\n");

            // show rent
            for (MarketRent rent : unit.marketRent()) {
                sb.append("\t\t");
                sb.append(rent.leaseTerm().getValue()).append(" months $");
                sb.append(rent.rent().amount().getValue()).append("");
                sb.append("\n");
            }

            sb.append("\t\tDeposit: $").append(unit.requiredDeposit().getValue()).append("\n");
        }

        // selected unit
        ApptUnit unit = unitSelection.selectedUnit();
        sb.append("\n\n");
        sb.append("Selected: ").append(unit.suiteNumber().getStringView());
        sb.append("\n");

        // building
        log.info("Selected unit building {}", unit.building());
        building = unit.building();
        sb.append("Building: ").append(building).append("\n");
        sb.append("Property: ").append(building.propertyProfile()).append("\n");

        // amenities
        sb.append("\tAmenities:\n");
        for (Amenity amenity : unit.amenities()) {
            sb.append("\t\t");
            sb.append(amenity.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tUtilities:\n");
        for (Utility utility : unit.utilities()) {
            sb.append("\t\t");
            sb.append(utility.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tUnitInfoItem:\n");
        for (UnitInfoItem info : unit.infoDetails()) {
            sb.append("\t\t");
            sb.append(info.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tConcessions:\n");
        for (Concession concession : unit.concessions()) {
            sb.append("\t\t");
            sb.append(concession.name().getStringView());
            sb.append("\n");
        }

        // utilities
        sb.append("\tAdd-ons:\n");
        for (AddOn addOn : unit.addOns()) {
            sb.append("\t\t");
            sb.append(addOn.name().getStringView());
            sb.append(" $").append(addOn.monthlyCost().getValue());
            sb.append("\n");
        }

        // rent
        sb.append("\nStart rent:").append(unitSelection.rentStart().getStringView());
        sb.append(", Lease: ").append(unitSelection.markerRent().leaseTerm().getValue()).append(" months, $");
        sb.append(unitSelection.markerRent().rent().amount().getValue());
    }

    private void loadTenants(StringBuilder sb) {
        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        PotentialTenantList tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        sb.append(tenantList.tenants().size()).append(" potential tenants");
        sb.append("\n");

        for (PotentialTenantInfo tenant : tenantList.tenants()) {

            sb.append("\n--- TENANT ---\n");
            sb.append(tenant.relationship().getStringView());
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
        ptf.tenant().set(tenant);

        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            TenantIncome income = EntityFactory.create(TenantIncome.class);

            income.incomeSource().setValue(IncomeSource.fulltime);
            income.employer().set(createEmployer());
            //income.monthlyAmount().setValue(300d + RandomUtil.randomInt(4000));

            //income.active().setValue(RandomUtil.randomBoolean());

            ptf.incomes().add(income);
        }

        for (int i = 0; i < RandomUtil.randomInt(3); i++) {
            TenantAsset asset = EntityFactory.create(TenantAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.assetValue().setValue(100d + RandomUtil.randomInt(10000));

            persist(asset);
            ptf.assets().add(asset);
        }

        if (tenant.relationship().getValue() == Relationship.Son || tenant.relationship().getValue() == Relationship.Daughter) {
            for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
                TenantGuarantor guarantor = EntityFactory.create(TenantGuarantor.class);
                guarantor.firstName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
                guarantor.lastName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
                guarantor.relationship().setValue(RandomUtil.random(TenantGuarantor.Relationship.values()));
                persist(guarantor);
                ptf.guarantors().add(guarantor);
            }
        }

        persist(ptf);

        return ptf;
    }

    private void loadFinancialInfo(StringBuilder sb, PotentialTenantInfo tenant) {
        EntityQueryCriteria<PotentialTenantFinancial> criteria = EntityQueryCriteria.create(PotentialTenantFinancial.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenant));
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

            sb.append("Monthly\n");
            for (ChargeLine line : charges.monthlyCharges().charges()) {
                sb.append("\t$");
                sb.append(line.charge().amount().getStringView());
                sb.append(" \t");
                sb.append(line.type().getStringView());
                sb.append("\n");
            }

            sb.append("Upgrades\n");
            for (ChargeLineSelectable line : charges.monthlyCharges().upgradeCharges()) {
                sb.append("\t$");
                sb.append(line.charge().amount().getStringView());
                sb.append(" \t");
                sb.append(line.type().getStringView());
                if (line.selected().getValue()) {
                    sb.append(" YES");
                }
                sb.append("\n");
            }

            sb.append("Monthly + Upgrades Total \n\t$");
            sb.append(charges.monthlyCharges().total().amount().getStringView());
            sb.append("\n");

            sb.append("\nPro-Rated ").append(charges.proRatedCharges().total().amount().getStringView()).append("\n");
            for (ChargeLine line : charges.proRatedCharges().charges()) {
                sb.append("\t$");
                sb.append(line.charge().amount().getStringView());
                sb.append(" \t").append(line.label().getStringView());
                sb.append("\n");
            }

            sb.append("\nApplication Charges ").append(charges.applicationCharges().total().amount().getStringView()).append("\n");
            for (ChargeLine line : charges.applicationCharges().charges()) {
                sb.append("\t$");
                sb.append(line.charge().amount().getStringView());
                sb.append(" \t");
                sb.append(line.type().getStringView());
                sb.append("\n");
            }

            sb.append("\nTenants Payment Split ").append(charges.paymentSplitCharges().total().amount().getStringView()).append("\n");
            for (TenantCharge line : charges.paymentSplitCharges().charges()) {
                sb.append("\t").append(line.tenant().relationship().getStringView());
                sb.append(" ").append(line.tenant().firstName().getStringView()).append(" ").append(line.tenant().lastName().getStringView());
                sb.append(" \t").append(line.percentage().getValue()).append("% $");
                sb.append(line.charge().amount().getValue());
                sb.append("\n");
            }

            //            sb.append("\t").append(charges.monthlyCharges()).append("\n");
            //            sb.append("\t").append(charges.proRatedCharges()).append("\n");
            //            sb.append("\t").append(charges.applicationCharges()).append("\n");
            //            sb.append("\t").append(charges.paymentSplitCharges()).append("\n");
            sb.append("\n");
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
        //            sb.append(pt.relationship().getStringView());
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
