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
package com.propertyvista.portal.server.ptapp.services;

import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.EntityServicesImpl;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLeaseFragment;
import com.propertyvista.portal.domain.ptapp.ApplicationProgress;
import com.propertyvista.portal.domain.ptapp.ApplicationWizardStep;
import com.propertyvista.portal.domain.ptapp.ApplicationWizardSubstep;
import com.propertyvista.portal.domain.ptapp.PotentialTenant.Status;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.dto.TenantEditorDTO;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.server.generator.dto.TenantSummaryDTO;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApplicationProgressMgr {

    private static ApplicationWizardStep createWizardStep(Class<? extends AppPlace> place, ApplicationWizardStep.Status status) {
        ApplicationWizardStep ws = EntityFactory.create(ApplicationWizardStep.class);
        ws.placeId().setValue(AppPlaceInfo.getPlaceId(place));
        ws.status().setValue(status);
        return ws;
    }

    public static ApplicationProgress createApplicationProgress() {
        ApplicationProgress progress = EntityFactory.create(ApplicationProgress.class);
        progress.steps().add(createWizardStep(PtSiteMap.Apartment.class, ApplicationWizardStep.Status.latest));
        progress.steps().add(createWizardStep(PtSiteMap.Tenants.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(PtSiteMap.Info.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(PtSiteMap.Financial.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(PtSiteMap.Pets.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(PtSiteMap.Charges.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(PtSiteMap.Summary.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(PtSiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(PtSiteMap.Completion.class, ApplicationWizardStep.Status.notVisited));
        return progress;
    }

    private static ApplicationWizardStep findWizardStep(ApplicationProgress progress, Class<? extends AppPlace> place) {
        for (ApplicationWizardStep step : progress.steps()) {
            if (step.placeId().getValue().equals(AppPlaceInfo.getPlaceId(place))) {
                return step;
            }
        }
        throw new Error("Step '" + place.getName() + "' not found");
    }

    public static boolean shouldEnterInformation(PotentialTenantInfo tenant) {
        //@see http://propertyvista.jira.com/browse/VISTA-235?focusedCommentId=10332
        if (tenant.status().getValue() == Status.Applicant) {
            return true;
        }
        if (!tenant.takeOwnership().isBooleanTrue()) {
            return false;
        }
        return (TimeUtils.isOlderThen(tenant.person().birthDate().getValue(), 18));
    }

    public static boolean shouldEnterInformation(TenantSummaryDTO tenantSummary) {
        return shouldEnterInformation(tenantSummary.tenantInLease(), tenantSummary.tenant().person().birthDate().getValue());
    }

    public static boolean shouldEnterInformation(TenantInLeaseFragment tenant, LogicalDate birthDate) {
        //@see http://propertyvista.jira.com/browse/VISTA-235?focusedCommentId=10332
        if (tenant.status().getValue() == TenantInLease.Status.Applicant) {
            return true;
        }
        if (!tenant.takeOwnership().isBooleanTrue()) {
            return false;
        }
        return (TimeUtils.isOlderThen(birthDate, 18));
    }

    public static void invalidateChargesStep() {
        EntityQueryCriteria<ApplicationProgress> applicationProgressCriteria = EntityQueryCriteria.create(ApplicationProgress.class);
        applicationProgressCriteria.add(PropertyCriterion.eq(applicationProgressCriteria.proto().lease(), PtAppContext.getCurrentLease()));
        ApplicationProgress progress = EntityServicesImpl.secureRetrieve(applicationProgressCriteria);
        ApplicationWizardStep chargesStep = findWizardStep(progress, PtSiteMap.Charges.class);
        switch (chargesStep.status().getValue()) {
        case latest:
        case complete:
            chargesStep.status().setValue(ApplicationWizardStep.Status.invalid);
            PersistenceServicesFactory.getPersistenceService().persist(chargesStep);
        }
    }

    public static void syncroizeApplicationProgress(List<TenantEditorDTO> tenants) {
        EntityQueryCriteria<ApplicationProgress> applicationProgressCriteria = EntityQueryCriteria.create(ApplicationProgress.class);
        applicationProgressCriteria.add(PropertyCriterion.eq(applicationProgressCriteria.proto().lease(), PtAppContext.getCurrentLease()));

        ApplicationProgress progress = EntityServicesImpl.secureRetrieve(applicationProgressCriteria);

        ApplicationWizardStep infoStep = findWizardStep(progress, PtSiteMap.Info.class);
        ApplicationWizardStep financialStep = findWizardStep(progress, PtSiteMap.Financial.class);
        //Keep original values to be able to merge Steps
        ApplicationWizardSubstep[] infoSubSteps = infoStep.substeps().toArray(new ApplicationWizardSubstep[infoStep.substeps().size()]);
        ApplicationWizardSubstep[] financialSubSteps = financialStep.substeps().toArray(new ApplicationWizardSubstep[financialStep.substeps().size()]);

        infoStep.substeps().clear();
        financialStep.substeps().clear();
        for (TenantEditorDTO tenant : tenants) {
            if (shouldEnterInformation(tenant, tenant.person().birthDate().getValue())) {
                ApplicationWizardSubstep infoSubstep = merge(tenant, infoSubSteps);
                infoStep.substeps().add(infoSubstep);
                updateParentStepStatus(infoStep, infoSubstep);

                ApplicationWizardSubstep financialSubstep = merge(tenant, financialSubSteps);
                financialStep.substeps().add(financialSubstep);
                updateParentStepStatus(financialStep, financialSubstep);
            }
        }
        updateStepCompletion(infoStep);
        updateStepCompletion(financialStep);
        PersistenceServicesFactory.getPersistenceService().merge(infoStep);
        PersistenceServicesFactory.getPersistenceService().merge(financialStep);
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

    @SuppressWarnings("unchecked")
    private static ApplicationWizardSubstep merge(TenantEditorDTO tenant, ApplicationWizardSubstep[] origSubSteps) {

        ApplicationWizardSubstep step = EntityFactory.create(ApplicationWizardSubstep.class);
        //TODO serialize key.
        step.placeArgument().setValue(tenant.getPrimaryKey().toString());
        step.name().setValue(
                EntityFromatUtils.nvl_concat(" ", tenant.person().name().firstName(), tenant.person().name().middleName(), tenant.person().name().lastName()));
        step.status().setValue(ApplicationWizardStep.Status.notVisited);

        // find original
        for (ApplicationWizardSubstep origStep : origSubSteps) {
            if (origStep.placeArgument().getValue().equals(tenant.getPrimaryKey())) {
                step.id().set(origStep.id());
                step.status().set(origStep.status());

                // see if something changed between tenantNew and tenantOrig
                if ((tenant.changeStatus().getValue() == TenantEditorDTO.ChangeStatus.Updated)
                        && (step.status().getValue() == ApplicationWizardStep.Status.complete)) {
                    step.status().setValue(ApplicationWizardStep.Status.invalid);
                }
            }
        }

        return step;
    }
}
