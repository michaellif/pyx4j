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
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.media.ApplicationDocumentFile;
import com.propertyvista.domain.media.IdentificationDocument;
import com.propertyvista.domain.media.ProofOfEmploymentDocument;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonGuarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication.Status;
import com.propertyvista.misc.EquifaxApproval.Decision;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.Summary;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantChargeList;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;
import com.propertyvista.portal.server.preloader.util.LeaseLifecycleSim;
import com.propertyvista.server.common.util.LeaseManager;
import com.propertyvista.server.domain.ApplicationDocumentBlob;

public class PtPreloader extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PtPreloader.class);

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Charges.class, ChargeLineList.class, ChargeLine.class, TenantChargeList.class, TenantCharge.class, OnlineApplication.class,
                    EmergencyContact.class, Summary.class, ApplicationDocumentBlob.class, Guarantor.class, PersonScreening.class);
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
            TenantUser user = UserPreloader.createTenantUser("", email, email, VistaTenantBehavior.ProspectiveApplicant);
            ApplicationSummaryGDO summary = generator.createSummary(user, units.get(i - 1));
            if (summary != null) {
                // Update user name
                Persistence.service().persist(user);
                persistFullApplication(summary, generator, i);
                numCreated++;
            }
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + numCreated + " potential tenants");
        return b.toString();
    }

    private void persistFullApplication(ApplicationSummaryGDO summary, PTGenerator generator, int cnt) {
        LeaseLifecycleSim.updateLease(summary.lease());
        Double overalPercentageApproval = 0.0, maxPercentageApproval = 0.0;

        for (TenantSummaryGDO tenantSummary : summary.tenants()) {
            Persistence.service().persist(tenantSummary.tenant());
            summary.lease().version().tenants().add(tenantSummary.tenantInLease());

            Persistence.service().persist(tenantSummary.tenantScreening());
            for (PersonGuarantor pg : tenantSummary.tenantScreening().guarantors()) {
                // TODO remove this set, it should be automatic
                pg.guarantee().set(tenantSummary.tenantScreening());
                Persistence.service().persist(pg.guarantor());
            }
            Persistence.service().persist(tenantSummary.tenantScreening().guarantors());
            Persistence.service().persist(tenantSummary.guarantorScreening());

            for (IdentificationDocument id : tenantSummary.tenantScreening().documents()) {
                for (ApplicationDocumentFile page : id.documentPages()) {
                    generator.attachDocumentData(page);
                }
            }
            for (PersonalIncome income : tenantSummary.tenantScreening().incomes()) {
                for (ProofOfEmploymentDocument doc : income.documents()) {
                    for (ApplicationDocumentFile page : doc.documentPages()) {
                        generator.attachDocumentData(page);
                    }
                }
            }

            if (PTGenerator.equifaxDemo) {
                if (!tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().isNull()) {
                    overalPercentageApproval += tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().getValue();

                    if (maxPercentageApproval < tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().getValue()) {
                        maxPercentageApproval = tenantSummary.tenantScreening().equifaxApproval().percenrtageApproved().getValue();
                    }
                }

                Persistence.service().persist(tenantSummary.tenantScreening().equifaxApproval().checkResultDetails());
                Persistence.service().persist(tenantSummary.tenantScreening().equifaxApproval());
            }
        }

        // save lease (tenants):
        new LeaseManager().save(summary.lease());

        LeaseLifecycleSim.startApplication(summary.lease().getPrimaryKey());

        // Create working appl. only for first half 
        if (cnt <= DemoData.UserType.PTENANT.getDefaultMax() / 2) {
            MasterOnlineApplication ma = summary.lease().application();
            if (PTGenerator.equifaxDemo) {
                ma.equifaxApproval().percenrtageApproved().setValue(overalPercentageApproval);

                if (maxPercentageApproval > 80) {
                    //ma.status().setValue(Status.Approved);
                    ma.equifaxApproval().suggestedDecision().setValue(Decision.Approve);
                } else if (overalPercentageApproval > 20) {
                    ma.status().setValue(Status.InformationRequested);
                    ma.equifaxApproval().suggestedDecision().setValue(Decision.RequestInfo);
                } else if (overalPercentageApproval > 50) {
                    //ma.status().setValue(Status.Declined);
                    ma.equifaxApproval().suggestedDecision().setValue(Decision.Decline);
                } else {
                    ma.equifaxApproval().suggestedDecision().setValue(Decision.Pending);
                }

                switch (ma.status().getValue()) {
                //case Approved:
                case InformationRequested:
                    //case Declined:
                case Cancelled:
                    EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
                    summary.lease().leaseApplication().decidedBy().set(RandomUtil.random(Persistence.service().query(criteria)));
                    summary.lease().leaseApplication().decisionDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
                    summary.lease().leaseApplication().decisionReason().setValue("Decided according current application state and Equifax check results");
                }

                Persistence.service().persist(ma.equifaxApproval().checkResultDetails());
                Persistence.service().persist(ma.equifaxApproval());
                Persistence.service().persist(ma);
            }
        }

//TODO
//        log.debug("Charges: " + VistaDataPrinter.print(summary.charges()));
//        Persistence.service().persist(summary.charges());
    }
}
