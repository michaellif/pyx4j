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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Address.OwnedRented;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineList;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.Employer;
import com.propertyvista.portal.domain.pt.IncomeSource;
import com.propertyvista.portal.domain.pt.LegalQuestions;
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
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.Vehicle;
import com.propertyvista.portal.server.pt.ChargesServerCalculation;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

public class PreloadPT extends AbstractDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadPT.class);

    private static Employer createEmployer() {
        Employer employer = EntityFactory.create(Employer.class);

        employer.name().setValue(RandomUtil.random(DemoData.EMPLOYER_NAMES));
        employer.supervisorName().setValue("Mr. " + RandomUtil.random(DemoData.LAST_NAMES));
        employer.supervisorPhone().setValue(RandomUtil.randomPhone());
        employer.monthlySalary().setValue(1000d + RandomUtil.randomInt(4000));
        employer.position().setValue(RandomUtil.random(DemoData.OCCUPATIONS));
        employer.jobStart().setValue(RandomUtil.randomDate(1980, 2020));
        employer.jobEnd().setValue(RandomUtil.randomDate(1980, 2020));

        return employer;
    }

    public static PotentialTenantFinancial createFinancial() {
        PotentialTenantFinancial ptf = EntityFactory.create(PotentialTenantFinancial.class);

        for (int i = 0; i < RandomUtil.randomInt(3); i++) {
            TenantIncome income = EntityFactory.create(TenantIncome.class);

            income.incomeSource().setValue(IncomeSource.fulltime);
            income.employer().set(createEmployer());
            income.monthlyAmount().setValue(10d + RandomUtil.randomInt(5000));

            ptf.incomes().add(income);
        }

        for (int i = 0; i < RandomUtil.randomInt(5); i++) {
            TenantAsset asset = EntityFactory.create(TenantAsset.class);

            asset.assetType().setValue(RandomUtil.random(AssetType.values()));
            asset.assetValue().setValue(100d + RandomUtil.randomInt(10000));

            PersistenceServicesFactory.getPersistenceService().persist(asset);
            ptf.assets().add(asset);
        }

        persist(ptf);

        return ptf;
    }

    public static EmergencyContact createEmergencyContact() {
        EmergencyContact contact = EntityFactory.create(EmergencyContact.class);

        contact.firstName().setValue(RandomUtil.random(DemoData.FIRST_NAMES));
        contact.middleName().setValue("");
        contact.lastName().setValue(RandomUtil.random(DemoData.LAST_NAMES));

        contact.homePhone().setValue(RandomUtil.randomPhone());
        contact.mobilePhone().setValue(RandomUtil.randomPhone());

        String email = contact.firstName().getStringView().toLowerCase() + "." + contact.lastName().getStringView().toLowerCase() + "@"
                + RandomUtil.random(DemoData.EMAIL_DOMAINS);
        contact.email().setValue(email);

        // TODO not going to save address - address already exists, will talk about this 
        //        Address address = createAddress("500 Yonge St", "M2C 98C");
        //        persist(address);
        //        contact.address().set(address);

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

        return lq;
    }

    public static Address createAddress(String line1, String zip) {
        Address address = EntityFactory.create(Address.class);

        address.street1().setValue(line1);
        address.street2().setValue("");
        address.city().setValue(RandomUtil.random(DemoData.CITIES));
        address.province().setValue(RandomUtil.random(DemoData.PROVINCES));
        address.postalCode().setValue(zip);

        address.moveInDate().setValue(RandomUtil.randomDate(2008, 2010));
        address.moveOutDate().setValue(RandomUtil.randomDate(2010, 2012));

        address.payment().setValue(1000d + RandomUtil.randomInt(1000));

        address.phone().setValue(RandomUtil.randomPhone());
        address.rented().setValue((OwnedRented) RandomUtil.random(Address.OwnedRented.class));
        address.managerName().setValue("Mr. " + RandomUtil.random(DemoData.LAST_NAMES));

        return address;
    }

    private Vehicle createVehicle() {
        Vehicle vehicle = EntityFactory.create(Vehicle.class);

        vehicle.plateNumber().setValue("ML" + RandomUtil.randomInt(9999) + "K");
        vehicle.year().setValue(1990 + RandomUtil.randomInt(20));
        vehicle.make().setValue(RandomUtil.random(DemoData.CAR_MAKES));
        vehicle.model().setValue(RandomUtil.random(DemoData.CAR_MODELS));
        vehicle.province().setValue(RandomUtil.random(DemoData.PROVINCES));

        return vehicle;
    }

    private PotentialTenantInfo createPotentialTenantInfo(int index) {
        PotentialTenantInfo pti = EntityFactory.create(PotentialTenantInfo.class);

        // first tenant must always be an applicant
        if (index == 0) {
            pti.relationship().setValue(Relationship.Applicant);
        } else {
            pti.relationship().setValue(RandomUtil.random(DemoData.RELATIONSHIPS));
        }

        populatePotentialTenant(pti);

        String driversLicense = "JTVMX" + RandomUtil.randomInt(10) + "VMIEK";
        pti.driversLicense().setValue(driversLicense);
        pti.driversLicenseState().setValue(RandomUtil.random(DemoData.PROVINCES));

        String si = RandomUtil.randomInt(1000) + " " + RandomUtil.randomInt(1000) + " " + RandomUtil.randomInt(1000);
        pti.secureIdentifier().setValue(si);

        pti.canadianCitizen().setValue(RandomUtil.randomBoolean());

        // there are problems saving this for now
        //        Address currentAddress = createAddress(RandomUtil.randomInt(15000) + " Yonge St", "M2C 1J2");
        //        PersistenceServicesFactory.getPersistenceService().persist(currentAddress);
        //        pti.currentAddress().set(currentAddress);
        //        pti.previousAddress().set(PreloadUtil.createAddress(RandomUtil.randomInt(15000) + " Yonge St", "H82 6K3"));

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
        pti.emergencyContact1().set(ec2);

        PersistenceServicesFactory.getPersistenceService().persist(pti);

        return pti;
    }

    private void populatePotentialTenant(PotentialTenant pt) {
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

        pt.dependant().setValue(RandomUtil.randomBoolean());
        pt.takeOwnership().setValue(RandomUtil.randomBoolean());
    }

    @SuppressWarnings("unused")
    private PotentialTenant createPotentialTenant(int index) {
        PotentialTenant pt = EntityFactory.create(PotentialTenant.class);

        populatePotentialTenant(pt);

        PersistenceServicesFactory.getPersistenceService().persist(pt);

        return pt;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(PotentialTenantList.class, PotentialTenant.class, PotentialTenantFinancial.class, PotentialTenantInfo.class, Charges.class,
                    ChargeLineList.class, ChargeLine.class, TenantChargeList.class, TenantCharge.class, Application.class, UnitSelection.class,
                    ApplicationProgress.class, Pets.class, Address.class, EmergencyContact.class, Summary.class);
        } else {
            return "This is production";
        }
    }

    private void createCharges(Application application) {
        Charges charges = EntityFactory.create(Charges.class);

        ChargesServerCalculation.dummyPopulate(charges, application);

        persist(charges);
    }

    @Override
    public String create() {
        User user = EntityFactory.create(User.class);
        user.name().setValue("z");
        user.email().setValue("x");
        PersistenceServicesFactory.getPersistenceService().persist(user);

        Application application = EntityFactory.create(Application.class);
        // TODO Dima. We need a use to bind this application to.
        application.user().set(user);
        persist(application);

        UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
        //unitSelection.selectionCriteria().propertyCode().set(request.propertyCode());
        //unitSelection.selectionCriteria().floorplanName().set(request.floorplanName());
        //unitSelection.building().set(building);
        unitSelection.application().set(application);
        persist(unitSelection);

        // TODO Dima. initialize ApplicationProgress here. Create a new class for this and MOVE code from GetCurrentApplicationImpl... have no idea how to name it.

        PotentialTenantList tenants = EntityFactory.create(PotentialTenantList.class);
        tenants.application().set(application);
        for (int i = 0; i < DemoData.NUM_POTENTIAL_TENANTS; i++) {
            PotentialTenantInfo tenantInfo = createPotentialTenantInfo(i);
            tenantInfo.application().set(application);
            createFinancial();
            tenants.tenants().add(tenantInfo);
        }

        persist(tenants);

        createCharges(application);

        load();

        StringBuilder b = new StringBuilder();
        b.append("Created potential tenant series of data");
        return b.toString();
    }

    public void load() {
        List<PotentialTenantList> tenantLists = PersistenceServicesFactory.getPersistenceService().query(
                new EntityQueryCriteria<PotentialTenantList>(PotentialTenantList.class));
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nLoaded " + tenantLists.size() + " potential tenant lists\n\n");

        for (PotentialTenantList tenants : tenantLists) {

            sb.append(tenants.application().getStringView());
            sb.append("\n");

            for (PotentialTenant tenant : tenants.tenants()) {
                sb.append("\t").append(tenant.getStringView());
                sb.append("\n");
            }
        }
        sb.append("\n\n");

        // Charges
        sb.append("------------------------ CHARGES -----------------------\n");
        List<Charges> chargesList = PersistenceServicesFactory.getPersistenceService().query(new EntityQueryCriteria<Charges>(Charges.class));
        for (Charges charges : chargesList) {
            sb.append("Charges\n");

            sb.append("Monthly\n");
            for (ChargeLine line : charges.monthlyCharges().charges()) {
                sb.append("\t");
                sb.append(line.charge().amount().getStringView());
                sb.append(" ");
                sb.append(line.type().getStringView());
                if (line.selected().getValue()) {
                    sb.append(" YES");
                }
                sb.append("\n");
            }

            sb.append("Upgrades\n");
            for (ChargeLine line : charges.monthlyCharges().upgradeCharges()) {
                sb.append("\t");
                sb.append(line.charge().amount().getStringView());
                sb.append(" ");
                sb.append(line.type().getStringView());
                if (line.selected().getValue()) {
                    sb.append(" YES");
                }
                sb.append("\n");
            }

            sb.append("Monthly + Upgrades Total ");
            sb.append(charges.monthlyCharges().total().amount().getStringView());
            sb.append("\n\n");

            sb.append("Pro-Rated ").append(charges.proRatedCharges().total().amount().getStringView()).append("\n");
            for (ChargeLine line : charges.proRatedCharges().charges()) {
                sb.append("\t").append(line.label().getStringView());
                sb.append(" ").append(line.charge().amount().getStringView());
                sb.append("\n");
            }

            //            sb.append("\t").append(charges.monthlyCharges()).append("\n");
            //            sb.append("\t").append(charges.proRatedCharges()).append("\n");
            //            sb.append("\t").append(charges.applicationCharges()).append("\n");
            //            sb.append("\t").append(charges.paymentSplitCharges()).append("\n");
            sb.append("\n");
        }
        sb.append("\n----------------------- END OF CHARGES ------------------\n\n");

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
        //        List<PotentialTenantInfo> ptis = PersistenceServicesFactory.getPersistenceService().query(
        //                new EntityQueryCriteria<PotentialTenantInfo>(PotentialTenantInfo.class));
        //        sb.append("Loaded " + ptis.size() + " potential tenant infos\n\n");
        //        for (PotentialTenantInfo pti : ptis) {
        //
        //            sb.append(pti.firstName().getStringView());
        //            sb.append(" ");
        //            if (pti.middleName().getStringView().length() > 0) {
        //                sb.append(pti.middleName().getStringView());
        //                sb.append(" ");
        //            }
        //            sb.append(pti.lastName().getStringView());
        //
        //            sb.append("\t\t");
        //            sb.append(pti.birthDate().getStringView());
        //
        //            sb.append("\t");
        //            sb.append(pti.homePhone().getStringView()).append(" | ").append(pti.mobilePhone().getStringView());
        //
        //            sb.append("\t");
        //            sb.append(pti.email().getStringView());
        //
        //            sb.append("\t");
        //            sb.append(pti.relationship().getStringView());
        //
        //            sb.append("\t$").append(pti.payment().getStringView());
        //
        //            sb.append("\n");
        //
        //            sb.append(pti.driversLicense().getStringView()).append(" ").append(pti.driversLicenseState().getStringView());
        //
        //            sb.append("\t").append(pti.secureIdentifier().getStringView());
        //
        //            // vehicles
        //            for (Vehicle vehicle : pti.vehicles()) {
        //                sb.append("\n\t");
        //                sb.append(vehicle.year().getStringView()).append(" ");
        //                sb.append(vehicle.province().getStringView()).append(" ");
        //                sb.append(vehicle.make().getStringView()).append(" ").append(vehicle.model().getStringView()).append(" ");
        //                sb.append(vehicle.plateNumber().getStringView()).append(" ");
        //            }
        //
        //            sb.append("\n");
        //        }
        //        sb.append("\n\n");
        //
        //        List<PotentialTenantFinancial> ptfs = PersistenceServicesFactory.getPersistenceService().query(
        //                new EntityQueryCriteria<PotentialTenantFinancial>(PotentialTenantFinancial.class));
        //        sb.append("Loaded " + ptis.size() + " potential tenant financials\n\n");
        //        for (PotentialTenantFinancial ptf : ptfs) {
        //            sb.append(ptf);
        //            sb.append("\n");
        //        }
        //        sb.append("\n\n");

        log.info(sb.toString());
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }
}
