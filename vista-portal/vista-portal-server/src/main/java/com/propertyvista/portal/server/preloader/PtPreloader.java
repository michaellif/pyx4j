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

import com.propertvista.generator.PTGenerator;
import com.propertvista.generator.gdo.ApplicationSummaryGDO;
import com.propertvista.generator.gdo.TenantSummaryGDO;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.PriorAddress;
import com.propertyvista.domain.User;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.media.ApplicationDocument;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.domain.tenant.ptapp.MasterApplication.Status;
import com.propertyvista.misc.EquifaxApproval.Decision;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantChargeList;
import com.propertyvista.server.common.ptapp.ApplicationMgr;
import com.propertyvista.server.domain.ApplicationDocumentData;

public class PtPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PtPreloader.class);

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

    @Override
    public String create() {

        PTGenerator generator = new PTGenerator(config());

        // Get the fist building
        EntityListCriteria<Building> bcriteria = EntityListCriteria.create(Building.class);
        bcriteria.asc(bcriteria.proto().propertyCode());
        bcriteria.setPageSize(1);
        bcriteria.setPageNumber(0);
        Building building = Persistence.service().retrieve(bcriteria);

        EntityQueryCriteria<AptUnit> ucriteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        ucriteria.add(PropertyCriterion.eq(ucriteria.proto().belongsTo(), building));
        List<AptUnit> units = Persistence.service().query(ucriteria);

        int numCreated = 0;
        for (int i = 1; i <= config().numPotentialTenants; i++) {
            if (units.size() <= i) {
                log.warn("No more units available for PotentialTenants. Change configuration!");
                break;
            }

            String email = DemoData.UserType.PTENANT.getEmail(i);
            User user = UserPreloader.createUser(email, email, VistaBehavior.PROSPECTIVE_TENANT);
            ApplicationSummaryGDO summary = generator.createSummary(user, units.get(i - 1));

            // Update user name
            Persistence.service().persist(user);
            //TODO create users for CoApplicants
            persistFullApplication(summary, generator);
            numCreated++;
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + numCreated + " potential tenants");
        return b.toString();
    }

    private void persistFullApplication(ApplicationSummaryGDO summary, PTGenerator generator) {

        LeaseHelper.updateLease(summary.lease());

        Persistence.service().persist(summary.lease());

        Double overalPercentageApproval = 0.0, maxPercentageApproval = 0.0;

        for (TenantSummaryGDO tenantSummary : summary.tenants()) {
            Persistence.service().persist(tenantSummary.tenant());

            tenantSummary.tenantInLease().lease().set(summary.lease());
            Persistence.service().persist(tenantSummary.tenantInLease());

            for (ApplicationDocument applicationDocument : tenantSummary.tenantScreening().documents()) {
                generator.attachDocumentData(applicationDocument);
            }
            for (PersonalIncome income : tenantSummary.tenantScreening().incomes()) {
                for (ApplicationDocument applicationDocument : income.documents()) {
                    generator.attachDocumentData(applicationDocument);
                }
            }

            if (!tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().isNull()) {
                overalPercentageApproval += tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().getValue();

                if (maxPercentageApproval < tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().getValue()) {
                    maxPercentageApproval = tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().getValue();
                }
            }

            Persistence.service().persist(tenantSummary.tenantScreening().equifaxApproval());
            Persistence.service().persist(tenantSummary.tenantScreening());

            summary.lease().tenants().add(tenantSummary.tenantInLease());
        }

        MasterApplication ma = ApplicationMgr.createMasterApplication(summary.lease());
        ma.equifaxApproval().percenrtageApproved().setValue(overalPercentageApproval);

        if (maxPercentageApproval > 80) {
            ma.status().setValue(Status.Approved);
            ma.equifaxApproval().suggestedDecision().setValue(Decision.Approve);
        } else if (overalPercentageApproval > 20) {
            ma.status().setValue(Status.InformationRequested);
            ma.equifaxApproval().suggestedDecision().setValue(Decision.RequestInfo);
        } else if (overalPercentageApproval > 50) {
            ma.status().setValue(Status.Declined);
            ma.equifaxApproval().suggestedDecision().setValue(Decision.Decline);
        } else {
            ma.equifaxApproval().suggestedDecision().setValue(Decision.Pending);
        }

        switch (ma.status().getValue()) {
        case Approved:
        case InformationRequested:
        case Declined:
        case Cancelled:
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            ma.decidedBy().set(RandomUtil.random(Persistence.service().query(criteria)));
            ma.decisionDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
            ma.decisionReason().setValue("Decided according current application state and Equifax check results");
        }

        Persistence.service().persist(summary.lease());
        Persistence.service().persist(ma.equifaxApproval());
        Persistence.service().persist(ma);

//TODO
//        log.debug("Charges: " + VistaDataPrinter.print(summary.charges()));
//        Persistence.service().persist(summary.charges());
    }
}
