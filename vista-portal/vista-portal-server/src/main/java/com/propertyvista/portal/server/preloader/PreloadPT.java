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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.IAddress;
import com.propertyvista.common.domain.User;
import com.propertyvista.portal.domain.ptapp.Address;
import com.propertyvista.portal.domain.ptapp.Application;
import com.propertyvista.portal.domain.ptapp.ApplicationDocument;
import com.propertyvista.portal.domain.ptapp.ApplicationProgress;
import com.propertyvista.portal.domain.ptapp.ChargeLine;
import com.propertyvista.portal.domain.ptapp.ChargeLineList;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.EmergencyContact;
import com.propertyvista.portal.domain.ptapp.Pets;
import com.propertyvista.portal.domain.ptapp.PotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.PotentialTenantList;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.SummaryPotentialTenantFinancial;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantChargeList;
import com.propertyvista.portal.domain.ptapp.TenantIncome;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.domain.util.VistaDataPrinter;
import com.propertyvista.portal.server.generator.PTGenerator;
import com.propertyvista.portal.server.ptapp.services.ApplicationDebug;
import com.propertyvista.server.domain.ApplicationDocumentData;

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
                    Pets.class, EmergencyContact.class, Summary.class, Address.class, ApplicationDocumentData.class);
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

        PTGenerator generator = new PTGenerator(DemoData.PT_GENERATION_SEED);
        Application application = generator.createApplication(user);
        ApplicationProgress progress = generator.createApplicationProgress(application);
        // TODO retrieve some unit
        Summary summary = generator.createSummary(application, null);
        persistFullApplication(summary, progress, generator);
        //List<ApplicationDocument> adocs = PersistenceServicesFactory.getPersistenceService().query(EntityQueryCriteria.create(ApplicationDocument.class));
        //for(ApplicationDocument adoc : adocs) {
        //    persist(generator.createApplicationDocumentData(adoc.filename().getValue(), adoc.id().getValue()));
        //}
        StringBuilder b = new StringBuilder();
        b.append("Created potential tenant series of data");
        return b.toString();
    }

    private void persistFullApplication(Summary summary, ApplicationProgress progress, PTGenerator generator) {
        persist(summary.application());
        persist(progress);
        persist(summary.unitSelection());
        persist(summary.tenantList());
        persist(summary.pets());

        log.debug("Charges: " + VistaDataPrinter.print(summary.charges()));
        persist(summary.charges());

        for (int i = 0; i < summary.tenantFinancials().size(); i++) {
            SummaryPotentialTenantFinancial financial = summary.tenantFinancials().get(i);
            PotentialTenantInfo tenant = summary.tenantList().tenants().get(i);

            financial.tenantFinancial().id().set(tenant.id());

            for (TenantIncome income : financial.tenantFinancial().incomes()) {
                for (ApplicationDocument applicationDocument : income.documents()) {
                    ApplicationDocumentData applicationDocumentData = generator
                            .createApplicationDocumentData(tenant, applicationDocument.filename().getValue());
                    persist(applicationDocumentData);
                    applicationDocument.dataId().set(applicationDocumentData.id());
                    persist(applicationDocument);
                }
            }

            persist(financial.tenantFinancial());

            if (tenant.notCanadianCitizen().isBooleanTrue()) {
                for (ApplicationDocument applicationDocument : tenant.documents()) {
                    ApplicationDocumentData applicationDocumentData = generator
                            .createApplicationDocumentData(tenant, applicationDocument.filename().getValue());
                    persist(applicationDocumentData);
                    applicationDocument.dataId().set(applicationDocumentData.id());
                    persist(applicationDocument);
                }
            }
        }
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();

        Application application = loadApplication();
        ApplicationDebug.dumpApplicationSummary(application);

        sb.append("\n\n---------------------------- USER -----------------------------------\n");
        sb.append(VistaDataPrinter.print(user));

        sb.append(ApplicationDebug.printApplicationSummary(application));

        return sb.toString();
    }

    private static void persist(IEntity entity) {
        PersistenceServicesFactory.getPersistenceService().persist(entity);
    }
}
