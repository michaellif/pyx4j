/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.portal.prospect;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.security.VistaCustomerBehavior;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.prospect.ApplicationWizardStep;
import com.propertyvista.domain.tenant.prospect.OnlineApplication;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class ApplicationProgressMgr {

    private final static Logger log = LoggerFactory.getLogger(ApplicationProgressMgr.class);

    public static boolean shouldEnterInformation(LeaseTermTenant tenant) {
        if (tenant.isNull()) {
            log.info("Received a null tenant when checking for eligibility");
            return false;
        }

        if (SecurityController.checkBehavior(VistaCustomerBehavior.ProspectiveApplicant)) {
            //@see http://jira.birchwoodsoftwaregroup.com/browse/VISTA-235
            if (tenant.role().getValue() == LeaseTermParticipant.Role.Applicant) {
                return true;
            }
            if (!tenant.takeOwnership().isBooleanTrue()) {
                return false;
            }
            return (TimeUtils.isOlderThan(tenant.leaseParticipant().customer().person().birthDate().getValue(), 18));
        } else if (tenant.leaseParticipant().customer().equals(ProspectApplicationContext.retrieveCurrentUserCustomer())) {
            return true; // allow just his/her data...
        }
        return false;
    }

    public static void syncronizeApplicationProgress(OnlineApplication application, List<TenantInLeaseDTO> tenants) {
        ApplicationWizardStep infoStep = findWizardStep(application, PtSiteMap.Info.class);
        ApplicationWizardStep financialStep = findWizardStep(application, PtSiteMap.Financial.class);

//        // keep original values to be able to merge Steps
//        ApplicationWizardSubstep[] infoSubSteps = infoStep.substeps().toArray(new ApplicationWizardSubstep[infoStep.substeps().size()]);
//        ApplicationWizardSubstep[] financialSubSteps = financialStep.substeps().toArray(new ApplicationWizardSubstep[financialStep.substeps().size()]);
//
//        // process sub-steps:
//        infoStep.substeps().clear();
//        financialStep.substeps().clear();
//        for (TenantInLeaseDTO tenant : tenants) {
//            if (shouldEnterInformation(tenant)) {
//                ApplicationWizardSubstep infoSubstep = mergeSubStep(tenant, infoSubSteps);
//                infoStep.substeps().add(infoSubstep);
//                updateParentStepStatus(infoStep, infoSubstep);
//
//                ApplicationWizardSubstep financialSubstep = mergeSubStep(tenant, financialSubSteps);
//                financialStep.substeps().add(financialSubstep);
//                updateParentStepStatus(financialStep, financialSubstep);
//            }
//        }

        Persistence.service().merge(infoStep);
        Persistence.service().merge(financialStep);
    }

    public static void createTenantDataSteps(OnlineApplication application, Customer tenant) {

        EntityQueryCriteria<LeaseTermTenant> criteria = EntityQueryCriteria.create(LeaseTermTenant.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant().customer(), tenant));
        LeaseTermTenant outer = Persistence.service().retrieve(criteria);
        if (outer == null) {
            throw new Error("TenantInLease for '" + tenant.getStringView() + "' not found");
        }

        createPersonDataSteps(application, tenant.person(), outer.getPrimaryKey());
    }

    public static void createGurantorDataSteps(OnlineApplication application, Customer guarantor) {

        EntityQueryCriteria<LeaseTermGuarantor> criteria = EntityQueryCriteria.create(LeaseTermGuarantor.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseParticipant().customer(), guarantor));
        LeaseTermGuarantor outer = Persistence.service().retrieve(criteria);
        if (outer == null) {
            throw new Error("PersonGuarantor for '" + guarantor.getStringView() + "' not found");
        }

        createPersonDataSteps(application, guarantor.person(), outer.getPrimaryKey());
    }

    public static void createPersonDataSteps(OnlineApplication application, Person person, Key stepID) {

//        // create an new sub-step:
//        ApplicationWizardSubstep subStep = EntityFactory.create(ApplicationWizardSubstep.class);
//        subStep.placeArgument().setValue(stepID.toString());
//        subStep.name().setValue(person.name().getStringView());
//        subStep.status().setValue(ApplicationWizardStep.Status.notVisited);
//
//        ApplicationWizardStep infoStep = findWizardStep(application, PtSiteMap.Info.class);
//        if (infoStep.substeps().isEmpty()) {
//            infoStep.substeps().add((ApplicationWizardSubstep) subStep.duplicate());
//            updateParentStepStatus(infoStep, subStep);
//            updateStepCompletion(infoStep);
//            Persistence.service().merge(infoStep);
//        }
//
//        ApplicationWizardStep financialStep = findWizardStep(application, PtSiteMap.Financial.class);
//        if (financialStep.substeps().isEmpty()) {
//            financialStep.substeps().add((ApplicationWizardSubstep) subStep.duplicate());
//            updateParentStepStatus(financialStep, subStep);
//            updateStepCompletion(financialStep);
//            Persistence.service().merge(financialStep);
//        }
    }

    public static void invalidateChargesStep(OnlineApplication application) {
// TODO : Charges and Payment steps are closed (removed) so far...
        if (false) {
            ApplicationWizardStep chargesStep = findWizardStep(application, PtSiteMap.Charges.class);
            switch (chargesStep.status().getValue()) {
            case latest:
            case complete:
                chargesStep.status().setValue(ApplicationWizardStep.Status.invalid);
                Persistence.service().merge(chargesStep);
            }
        }
    }

    public static void invalidateSummaryStep(OnlineApplication application) {
        if (!VistaTODO.enableWelcomeWizardDemoMode) {
            ApplicationWizardStep summaryStep = findWizardStep(application, PtSiteMap.Summary.class);
            switch (summaryStep.status().getValue()) {
            case latest:
            case complete:
                summaryStep.status().setValue(ApplicationWizardStep.Status.invalid);
                Persistence.service().merge(summaryStep);
                break;
            }
        }
    }

    // internals:

    private static ApplicationWizardStep findWizardStep(OnlineApplication application, Class<? extends AppPlace> place) {
        for (ApplicationWizardStep step : application.steps()) {
            if (step.placeId().getValue().equals(AppPlaceInfo.getPlaceId(place))) {
                return step;
            }
        }
        throw new Error("Step '" + place.getName() + "' not found");
    }

}
