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

import com.propertvista.generator.PTGenerator;
import com.propertvista.generator.gdo.ApplicationSummaryGDO;
import com.propertvista.generator.gdo.TenantSummaryGDO;

import com.pyx4j.commons.Key;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PreloadConfig;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.User;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.contact.IAddress;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantChargeList;
import com.propertyvista.portal.rpc.ptapp.VistaDataPrinter;
import com.propertyvista.portal.server.ptapp.services.ApplicationDebug;
import com.propertyvista.server.common.ptapp.ApplicationMgr;
import com.propertyvista.server.domain.ApplicationDocumentData;

public class PtPreloader extends BaseVistaDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PtPreloader.class);

    private User user;

    public PtPreloader(PreloadConfig config) {
        super(config);
    }

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
            return deleteAll(Charges.class, ChargeLineList.class, ChargeLine.class, TenantChargeList.class, TenantCharge.class, Application.class, Pet.class,
                    EmergencyContact.class, Summary.class, PriorAddress.class, ApplicationDocumentData.class);
        } else {
            return "This is production";
        }
    }

    private User loadUser(String name) {
        EntityQueryCriteria<User> criteria = EntityQueryCriteria.create(User.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().name(), name));
        user = Persistence.service().retrieve(criteria);
        return user;
    }

    private Application loadApplication() {
        EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
        assert (user != null);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));

        Application application = Persistence.service().retrieve(criteria);
        return application;
    }

    //    private void loadApplicationProgress(StringBuilder sb) {
    //        EntityQueryCriteria<ApplicationProgress> criteria = EntityQueryCriteria.create(ApplicationProgress.class);
    //        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
    //
    //        ApplicationProgress progress = Persistence.service().retrieve(criteria);
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
    //        UnitSelection unitSelection = Persistence.service().retrieve(criteria);
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

        PTGenerator generator = new PTGenerator(DemoData.PT_GENERATION_SEED, PreloadConfig.createTest());

        ApplicationSummaryGDO summary = generator.createSummary(user, Persistence.service().retrieve(AptUnit.class, new Key(1)));

        persistFullApplication(summary, generator);
        //List<ApplicationDocument> adocs = Persistence.service().query(EntityQueryCriteria.create(ApplicationDocument.class));
        //for(ApplicationDocument adoc : adocs) {
        //    Persistence.service().persist(generator.createApplicationDocumentData(adoc.filename().getValue(), adoc.id().getValue()));
        //}
        StringBuilder b = new StringBuilder();
        b.append("Created 1 potential tenant series of data");
        return b.toString();
    }

    private void persistFullApplication(ApplicationSummaryGDO summary, PTGenerator generator) {

        Persistence.service().persist(summary.lease());

        for (TenantSummaryGDO tenantSummary : summary.tenants()) {
            Persistence.service().persist(tenantSummary.tenant());

            tenantSummary.tenantInLease().lease().set(summary.lease());
            Persistence.service().persist(tenantSummary.tenantInLease());
            Persistence.service().persist(tenantSummary.tenantScreening());

            summary.lease().tenants().add(tenantSummary.tenantInLease());
        }

        Persistence.service().persist(summary.lease());

        MasterApplication ma = EntityFactory.create(MasterApplication.class);
        ma.lease().set(summary.lease());

        Application a = EntityFactory.create(Application.class);
        a.steps().addAll(ApplicationMgr.createApplicationProgress());
        a.user().set(user);
        a.lease().set(ma.lease());
        ma.applications().add(a);

        Persistence.service().persist(ma);

//TODO
//        log.debug("Charges: " + VistaDataPrinter.print(summary.charges()));
//        Persistence.service().persist(summary.charges());

//        for (int i = 0; i < summary.tenantFinancials().size(); i++) {
//            SummaryPotentialTenantFinancial financial = summary.tenantFinancials().get(i);
//            PotentialTenantInfo tenant = summary.tenantList().tenants().get(i);
//
//            financial.tenantFinancial().id().set(tenant.id());
//
//            for (TenantIncome income : financial.tenantFinancial().incomes()) {
//                for (ApplicationDocument applicationDocument : income.documents()) {
//                    ApplicationDocumentData applicationDocumentData = generator
//                            .createApplicationDocumentData(tenant, applicationDocument.filename().getValue());
//                    Persistence.service().persist(applicationDocumentData);
//                    applicationDocument.dataId().set(applicationDocumentData.id());
//                    Persistence.service().persist(applicationDocument);
//                }
//            }
//
//            Persistence.service().persist(financial.tenantFinancial());
//
//            if (tenant.notCanadianCitizen().isBooleanTrue()) {
//                for (ApplicationDocument applicationDocument : tenant.documents()) {
//                    ApplicationDocumentData applicationDocumentData = generator
//                            .createApplicationDocumentData(tenant, applicationDocument.filename().getValue());
//                    Persistence.service().persist(applicationDocumentData);
//                    applicationDocument.dataId().set(applicationDocumentData.id());
//                    Persistence.service().persist(applicationDocument);
//                }
//            }
//        }
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
}
