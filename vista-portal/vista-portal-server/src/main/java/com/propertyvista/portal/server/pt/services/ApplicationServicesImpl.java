/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ApplicationProgress;
import com.propertyvista.portal.domain.pt.ApplicationWizardStep;
import com.propertyvista.portal.domain.pt.ApplicationWizardSubstep;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.pt.UnitSelectionCriteria;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.pt.CurrentApplication;
import com.propertyvista.portal.rpc.pt.SiteMap;
import com.propertyvista.portal.rpc.pt.services.ApplicationServices;
import com.propertyvista.portal.server.pt.PtUserDataAccess;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityFromatUtils;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.AppPlaceInfo;

public class ApplicationServicesImpl extends ApplicationEntityServicesImpl implements ApplicationServices {

    private final static Logger log = LoggerFactory.getLogger(ApplicationServicesImpl.class);

    @Override
    public void getCurrentApplication(AsyncCallback<CurrentApplication> callback, UnitSelectionCriteria request) {
        // find application by user
        EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), PtUserDataAccess.getCurrentUser()));
        Application application = secureRetrieve(criteria);

        CurrentApplication currentApplication = new CurrentApplication();

        if (application == null) {
            UnitSelection unitSelection = EntityFactory.create(UnitSelection.class);
            unitSelection.selectionCriteria().set(request);
            new ApartmentServicesImpl().loadAvailableUnits(unitSelection);

            if (unitSelection.building().isNull()) {
                log.info("Could not find building with propertyCode {}", request.propertyCode());
                throw new UserRuntimeException("Selected building not found");
            }

            application = EntityFactory.create(Application.class);
            application.user().set(PtUserDataAccess.getCurrentUser());
            secureSave(application);

            ApplicationProgress progress = createApplicationProgress();
            progress.application().set(application);
            secureSave(progress);

            currentApplication.progress = progress;

            unitSelection.application().set(application);
            secureSave(unitSelection);

        } else {
            //Verify if buildingName and floorplanName are the same
            EntityQueryCriteria<UnitSelection> unitSelectionCriteria = EntityQueryCriteria.create(UnitSelection.class);
            unitSelectionCriteria.add(PropertyCriterion.eq(unitSelectionCriteria.proto().application(), application));
            UnitSelection unitSelection = secureRetrieve(unitSelectionCriteria);

            if ((unitSelection != null) && (request != null)) {
                if ((!unitSelection.selectionCriteria().propertyCode().equals(request.propertyCode()))
                        || (!unitSelection.selectionCriteria().floorplanName().equals(request.floorplanName()))) {
                    //TODO What if they are diferent ?  We need to discard some part of application flow.
                }
            }

            EntityQueryCriteria<ApplicationProgress> applicationProgressCriteria = EntityQueryCriteria.create(ApplicationProgress.class);
            applicationProgressCriteria.add(PropertyCriterion.eq(applicationProgressCriteria.proto().application(), application));
            currentApplication.progress = secureRetrieve(applicationProgressCriteria);
        }
        PtUserDataAccess.setCurrentUserApplication(application);
        currentApplication.application = application;
        log.info("Start application {}", application);
        log.info("  progress {}", currentApplication.progress);
        callback.onSuccess(currentApplication);
    }

    @Override
    public void getApplicationProgress(AsyncCallback<ApplicationProgress> callback, ApplicationWizardStep currentStep, ApplicationWizardSubstep currentSubstep) {
        EntityQueryCriteria<ApplicationProgress> applicationProgressCriteria = EntityQueryCriteria.create(ApplicationProgress.class);
        applicationProgressCriteria.add(PropertyCriterion.eq(applicationProgressCriteria.proto().application(), PtUserDataAccess.getCurrentUserApplication()));
        ApplicationProgress progress = secureRetrieve(applicationProgressCriteria);

        if (currentStep != null) {
            int idx = progress.steps().indexOf(currentStep);
            if (idx == -1) {
                throw new Error("Invalid Step recived");
            }

            currentStep = progress.steps().get(idx);
            //TODO hasAlert ?
            boolean currentStepCompleated = true;
            if (currentSubstep != null) {
                int idxSub = currentStep.substeps().indexOf(currentSubstep);
                if (idxSub == -1) {
                    throw new Error("Invalid Substep recived");
                }
                currentSubstep = currentStep.substeps().get(idxSub);
                currentSubstep.status().setValue(ApplicationWizardStep.Status.complete);
                PersistenceServicesFactory.getPersistenceService().persist(currentSubstep);
                // navigate to next invalid or notVisited step
                idxSub++;
                itrOverSubSteps: while (idxSub < currentStep.substeps().size()) {
                    ApplicationWizardSubstep nextSubstep = currentStep.substeps().get(idxSub);
                    switch (nextSubstep.status().getValue()) {
                    case latest:
                    case notVisited:
                        nextSubstep.status().setValue(ApplicationWizardStep.Status.latest);
                        PersistenceServicesFactory.getPersistenceService().persist(nextSubstep);
                        currentStepCompleated = false;
                        break itrOverSubSteps;
                    case invalid:
                        currentStepCompleated = false;
                        break itrOverSubSteps;
                    }
                    idxSub++;
                }
            }

            if (currentStepCompleated) {
                // Move to next regular step
                currentStep.status().setValue(ApplicationWizardStep.Status.complete);
                PersistenceServicesFactory.getPersistenceService().persist(currentStep);
                idx++;
                iterOverSteps: while (idx < progress.steps().size()) {
                    ApplicationWizardStep nextStep = progress.steps().get(idx);
                    // navigate to next invalid step
                    switch (nextStep.status().getValue()) {
                    case latest:
                    case notVisited:
                        nextStep.status().setValue(ApplicationWizardStep.Status.latest);
                        PersistenceServicesFactory.getPersistenceService().persist(nextStep);
                        break iterOverSteps;
                    case invalid:
                        break iterOverSteps;
                    }
                    idx++;
                }
                //TODO Navigate to first subStep
            }

        }
        callback.onSuccess(progress);
    }

    private static ApplicationWizardStep createWizardStep(Class<? extends AppPlace> place, ApplicationWizardStep.Status status) {
        ApplicationWizardStep ws = EntityFactory.create(ApplicationWizardStep.class);
        ws.placeId().setValue(AppPlaceInfo.getPlaceId(place));
        ws.status().setValue(status);
        return ws;
    }

    public static ApplicationProgress createApplicationProgress() {
        ApplicationProgress progress = EntityFactory.create(ApplicationProgress.class);
        progress.steps().add(createWizardStep(SiteMap.Apartment.class, ApplicationWizardStep.Status.latest));
        progress.steps().add(createWizardStep(SiteMap.Tenants.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(SiteMap.Info.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(SiteMap.Financial.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(SiteMap.Pets.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(SiteMap.Charges.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(SiteMap.Summary.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(SiteMap.Payment.class, ApplicationWizardStep.Status.notVisited));
        progress.steps().add(createWizardStep(SiteMap.Completion.class, ApplicationWizardStep.Status.notVisited));
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

    //TODO move this to IList
    private static PotentialTenantInfo findTenant(PotentialTenantList tenantsNew, PotentialTenantInfo tenantOrig) {
        if (tenantsNew == null) {
            return null;
        }
        for (PotentialTenantInfo tenant : tenantsNew.tenants()) {
            if (tenant.equals(tenantOrig)) {
                return tenant;
            }
        }
        return null;
    }

    public static void syncroizeApplicationProgress(PotentialTenantList tenantsOrig, PotentialTenantList tenantsNew) {
        EntityQueryCriteria<ApplicationProgress> applicationProgressCriteria = EntityQueryCriteria.create(ApplicationProgress.class);
        applicationProgressCriteria.add(PropertyCriterion.eq(applicationProgressCriteria.proto().application(), tenantsNew.application()));
        ApplicationProgress progress = secureRetrieve(applicationProgressCriteria);

        ApplicationWizardStep infoStep = findWizardStep(progress, SiteMap.Info.class);
        ApplicationWizardSubstep[] infoSubSteps = infoStep.substeps().toArray(new ApplicationWizardSubstep[infoStep.substeps().size()]);

        ApplicationWizardStep financialStep = findWizardStep(progress, SiteMap.Financial.class);
        ApplicationWizardSubstep[] financialSubSteps = financialStep.substeps().toArray(new ApplicationWizardSubstep[financialStep.substeps().size()]);

        infoStep.substeps().clear();
        financialStep.substeps().clear();
        for (PotentialTenantInfo tenant : tenantsNew.tenants()) {
            // if not 18y Old continue;
            // TODO vlads By the way, I do not think that this is correct, since this is moving domain-specific logic for tenants to such a generic method as this
            if (!ChargesSharedCalculation.isEligibleForPaymentSplit(tenant)) {
                continue;
            }
            infoStep.substeps().add(merge(tenant, findTenant(tenantsOrig, tenant), infoSubSteps));
            financialStep.substeps().add(merge(tenant, findTenant(tenantsOrig, tenant), financialSubSteps));
        }
        PersistenceServicesFactory.getPersistenceService().merge(infoStep);
        PersistenceServicesFactory.getPersistenceService().merge(financialStep);
    }

    @SuppressWarnings("unchecked")
    private static ApplicationWizardSubstep merge(PotentialTenantInfo tenantNew, PotentialTenantInfo tenantOrig, ApplicationWizardSubstep[] origSubSteps) {

        ApplicationWizardSubstep step = EntityFactory.create(ApplicationWizardSubstep.class);
        step.placeArgument().setValue(tenantNew.getPrimaryKey());
        step.name().setValue(EntityFromatUtils.nvl_concat(" ", tenantNew.firstName(), tenantNew.middleName(), tenantNew.lastName()));
        step.status().setValue(ApplicationWizardStep.Status.notVisited);

        // find original
        for (ApplicationWizardSubstep origStep : origSubSteps) {
            if (origStep.placeArgument().getValue().equals(tenantNew.getPrimaryKey())) {
                step.id().set(origStep.id());
                step.status().set(origStep.status());

                // see if something changed between tenantNew and tenantOrig
                if ((tenantOrig != null) && (step.status().getValue() == ApplicationWizardStep.Status.complete)
                        && !EntityGraph.memebersEquals(tenantNew, tenantOrig, tenantNew.firstName(), tenantNew.middleName(), tenantNew.lastName())) {
                    step.status().setValue(ApplicationWizardStep.Status.invalid);
                }
            }
        }

        return step;
    }
}
