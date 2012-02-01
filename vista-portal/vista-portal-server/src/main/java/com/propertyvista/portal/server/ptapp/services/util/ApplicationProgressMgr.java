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
package com.propertyvista.portal.server.ptapp.services.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardSubstep;
import com.propertyvista.dto.TenantInLeaseDTO;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.ptapp.ApplicationManager;

public class ApplicationProgressMgr extends ApplicationManager {

    private final static Logger log = LoggerFactory.getLogger(ApplicationProgressMgr.class);

    public static boolean shouldEnterInformation(TenantInLease tenant) {
        if (tenant.isNull()) {
            log.info("Received a null tenant when checking for eligibility");
            return false;
        }

        if (SecurityController.checkBehavior(VistaTenantBehavior.ProspectiveApplicant)) {
            //@see http://jira.birchwoodsoftwaregroup.com/browse/VISTA-235
            if (tenant.role().getValue() == TenantInLease.Role.Applicant) {
                return true;
            }
            if (!tenant.takeOwnership().isBooleanTrue()) {
                return false;
            }
            return (TimeUtils.isOlderThan(tenant.tenant().person().birthDate().getValue(), 18));
        } else if (tenant.tenant().equals(PtAppContext.getCurrentUserTenant())) {
            return true; // allow just his/her data...
        }
        return false;
    }

    public static void syncroizeApplicationProgress(Application application, List<TenantInLeaseDTO> tenants) {
        ApplicationWizardStep infoStep = findWizardStep(application, PtSiteMap.Info.class);
        ApplicationWizardStep financialStep = findWizardStep(application, PtSiteMap.Financial.class);

        // keep original values to be able to merge Steps
        ApplicationWizardSubstep[] infoSubSteps = infoStep.substeps().toArray(new ApplicationWizardSubstep[infoStep.substeps().size()]);
        ApplicationWizardSubstep[] financialSubSteps = financialStep.substeps().toArray(new ApplicationWizardSubstep[financialStep.substeps().size()]);

        // process sub-steps:
        infoStep.substeps().clear();
        financialStep.substeps().clear();
        for (TenantInLeaseDTO tenant : tenants) {
            if (shouldEnterInformation(tenant)) {
                ApplicationWizardSubstep infoSubstep = mergeSubStep(tenant, infoSubSteps);
                infoStep.substeps().add(infoSubstep);
                updateParentStepStatus(infoStep, infoSubstep);

                ApplicationWizardSubstep financialSubstep = mergeSubStep(tenant, financialSubSteps);
                financialStep.substeps().add(financialSubstep);
                updateParentStepStatus(financialStep, financialSubstep);
            }
        }

        updateStepCompletion(infoStep);
        updateStepCompletion(financialStep);

        Persistence.service().merge(infoStep);
        Persistence.service().merge(financialStep);
    }

    public static void createGurantorDataSteps(Application application, Guarantor guarantor) {

        // create an new sub-step:
        ApplicationWizardSubstep subStep = EntityFactory.create(ApplicationWizardSubstep.class);
        subStep.placeArgument().setValue(guarantor.getPrimaryKey().toString());
        subStep.name().setValue(guarantor.person().name().getStringView());
        subStep.status().setValue(ApplicationWizardStep.Status.notVisited);

        ApplicationWizardStep infoStep = findWizardStep(application, PtSiteMap.Info.class);
        if (infoStep.substeps().isEmpty()) {
            infoStep.substeps().add((ApplicationWizardSubstep) subStep.duplicate());
            updateParentStepStatus(infoStep, subStep);
            updateStepCompletion(infoStep);
            Persistence.service().merge(infoStep);
        }

        ApplicationWizardStep financialStep = findWizardStep(application, PtSiteMap.Financial.class);
        if (financialStep.substeps().isEmpty()) {
            financialStep.substeps().add((ApplicationWizardSubstep) subStep.duplicate());
            updateParentStepStatus(financialStep, subStep);
            updateStepCompletion(financialStep);
            Persistence.service().merge(financialStep);
        }
    }

    public static void invalidateChargesStep(Application application) {
        ApplicationWizardStep chargesStep = findWizardStep(application, PtSiteMap.Charges.class);
        switch (chargesStep.status().getValue()) {
        case latest:
        case complete:
            chargesStep.status().setValue(ApplicationWizardStep.Status.invalid);
            Persistence.service().merge(chargesStep);
        }
    }

    public static void invalidateSummaryStep(Application application) {
        ApplicationWizardStep summaryStep = findWizardStep(application, PtSiteMap.Summary.class);
        switch (summaryStep.status().getValue()) {
        case latest:
        case complete:
            summaryStep.status().setValue(ApplicationWizardStep.Status.invalid);
            Persistence.service().merge(summaryStep);
            break;
        }
    }

    // internals:

    private static ApplicationWizardSubstep mergeSubStep(TenantInLeaseDTO tenant, ApplicationWizardSubstep[] origSubSteps) {

        // try to find original:
        ApplicationWizardSubstep subStep = null;
        for (ApplicationWizardSubstep origStep : origSubSteps) {
            if (origStep.placeArgument().getValue().equals(tenant.getPrimaryKey().toString())) {
                // copy step data if found:
                subStep = origStep;
                // invalidate status if there are changes:
                if ((tenant.changeStatus().getValue() == TenantInLeaseDTO.ChangeStatus.Updated)
                        && (subStep.status().getValue() == ApplicationWizardStep.Status.complete)) {
                    subStep.status().setValue(ApplicationWizardStep.Status.invalid);
                }

                break;
            }
        }

        if (subStep == null) {
            // create an new sub-step:
            subStep = EntityFactory.create(ApplicationWizardSubstep.class);
            subStep.placeArgument().setValue(tenant.getPrimaryKey().toString());
            subStep.name().setValue(tenant.tenant().person().name().getStringView());
            subStep.status().setValue(ApplicationWizardStep.Status.notVisited);
        }

        return subStep;
    }

    private static void updateParentStepStatus(ApplicationWizardStep step, ApplicationWizardSubstep substep) {
        switch (substep.status().getValue()) {
        case invalid:
            step.status().setValue(ApplicationWizardStep.Status.invalid);
            break;
        case notVisited:
            if (step.status().getValue() == ApplicationWizardStep.Status.complete) {
                step.status().setValue(ApplicationWizardStep.Status.invalid);
            }
            break;
        }
    }

    private static void updateStepCompletion(ApplicationWizardStep step) {
        boolean hasNotComplete = false;
        boolean hasInvalid = false;
        for (ApplicationWizardSubstep substep : step.substeps()) {
            if ((hasInvalid) && (substep.status().getValue() == ApplicationWizardStep.Status.latest)) {
                substep.status().setValue(ApplicationWizardStep.Status.notVisited);
            }
            if (substep.status().getValue() == ApplicationWizardStep.Status.invalid) {
                hasInvalid = true;
            }
            if (substep.status().getValue() != ApplicationWizardStep.Status.complete) {
                hasNotComplete = true;
            }
        }

        if (!hasNotComplete) {
            step.status().setValue(ApplicationWizardStep.Status.complete);
        }
    }

    private static ApplicationWizardStep findWizardStep(Application application, Class<? extends AppPlace> place) {
        for (ApplicationWizardStep step : application.steps()) {
            if (step.placeId().getValue().equals(AppPlaceInfo.getPlaceId(place))) {
                return step;
            }
        }
        throw new Error("Step '" + place.getName() + "' not found");
    }

}
