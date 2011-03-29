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

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.DemoData;
import com.propertyvista.portal.domain.User;
import com.propertyvista.portal.domain.pt.Address;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineList;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.EmergencyContact;
import com.propertyvista.portal.domain.pt.IAddress;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.Summary;
import com.propertyvista.portal.domain.pt.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.pt.TenantChargeList;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.util.VistaDataPrinter;
import com.propertyvista.portal.server.generator.VistaDataGenerator;
import com.propertyvista.portal.server.pt.services.ApplicationDebug;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class PreloadPT extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadPT.class);

    private User user;

//    private Application application;

//    private void loadEmployer(IncomeInfoEmployer employer, StringBuilder sb) {
//        sb.append(" Employer: ").append(employer.name().getStringView());
//        sb.append(" \t").append(employer.starts().getStringView()).append(" - ").append(employer.ends().getStringView());
//
//        sb.append(" Supervisor: ").append(employer.supervisorName().getStringView());
//        sb.append(" at ").append(employer.supervisorPhone().getStringView());
//
//        sb.append(", Monthly salary ").append(employer.monthlyAmount().getValue());
//        sb.append(", Poisiton ").append(employer.position().getStringView());
//
//        sb.append(", \tAddress: ");
//        loadAddress(employer, sb);
//    }

    public static void loadAddress(IAddress address, StringBuilder sb) {
        sb.append(address.street1().getValue());
        sb.append(", ").append(address.city().getStringView());
        sb.append(", ").append(address.province().getStringView());
        sb.append(" ").append(address.postalCode().getStringView());
    }

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

    private User loadUser(String name) {
        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), name));
        user = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        return user;
    }

    private Application loadApplication() {
        EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
        assert (user != null);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));

        Application application = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        return application;
    }

//    private void loadApplicationProgress(StringBuilder sb) {
//        EntityQueryCriteria<ApplicationProgress> criteria = EntityQueryCriteria.create(ApplicationProgress.class);
//        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
//
//        ApplicationProgress progress = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
//        if (progress == null) {
//            throw new IllegalStateException("Could not find progress for application");
//        }
//
//        sb.append(progress.steps().size()).append(" steps\n");
//        for (ApplicationWizardStep step : progress.steps()) {
//            sb.append("\t");
//            sb.append(step.placeId().getStringView());
//            sb.append("\n");
//        }
//    }

//    private void loadUnitSelection(StringBuilder sb) {
//        EntityQueryCriteria<UnitSelection> criteria = EntityQueryCriteria.create(UnitSelection.class);
//        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
//        UnitSelection unitSelection = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
//
//        ApartmentServicesImpl apartmentServices = new ApartmentServicesImpl();
//        apartmentServices.loadAvailableUnits(unitSelection);
//
//        //        building = unitSelection.selectedUnit().building();
//
//        sb.append(VistaDataPrinter.print(unitSelection));
//    }

    @Override
    public String create() {
        user = loadUser(DemoData.PRELOADED_USERNAME);

        try {
            VistaDataGenerator generator = new VistaDataGenerator();
            Application application = generator.createApplication(user);
            ApplicationProgress progress = generator.createApplicationProgress(application);
            Summary summary = generator.createSummary(application);
            persistFullApplication(summary, progress, generator);

            load();

            StringBuilder b = new StringBuilder();
            b.append("Created potential tenant series of data");
            return b.toString();
        } catch(IOException e) {
            log.error(e.getMessage(), e);
            return "FAILED to create potential tenant series of data. ERROR: "+e.getClass().getName()+": "+e.getMessage();
        }
    }

    private void persistFullApplication(Summary summary, ApplicationProgress progress, VistaDataGenerator generator) throws IOException {
        persist(summary.application());
        persist(progress);
        persist(summary.unitSelection());
        persist(summary.tenantList());
        persist(summary.pets());

        log.info("Charges: " + VistaDataPrinter.print(summary.charges()));
        persist(summary.charges());

        for (int i = 0; i < summary.tenantFinancials().size(); i++) {
            SummaryPotentialTenantFinancial financial = summary.tenantFinancials().get(i);
            PotentialTenantInfo tenant = summary.tenantList().tenants().get(i);

            financial.tenantFinancial().id().set(tenant.id());

            for (TenantIncome income : financial.tenantFinancial().incomes()) {
                for (ApplicationDocument document : income.documents()) {
                    persist(document);
                }
            }

            persist(financial.tenantFinancial());

            //using apartment1.jpg as income doc, and apartment2.jpg and apartment3.jpg as securityInfo doc
            ApplicationDocument.DocumentType documentType = ApplicationDocument.DocumentType.securityInfo;
            persist(generator.createApplicationDocument(tenant, "apartment2.jpg", documentType));
            persist(generator.createApplicationDocument(tenant, "apartment3.jpg", documentType));
        }
    }

    public void load() {
        StringBuilder sb = new StringBuilder();

        Application application = loadApplication();
        ApplicationDebug.dumpApplicationSummary(application);

        sb.append("\n\n---------------------------- USER -----------------------------------\n");
        sb.append(VistaDataPrinter.print(user));

        sb.append(ApplicationDebug.printApplicationSummary(application));

        log.info(sb.toString());
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }
}
