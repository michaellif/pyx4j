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
package com.propertyvista.portal.server.ptapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.User;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardSubstep;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApplicationServiceImpl extends ApplicationEntityServiceImpl implements ApplicationService {

    private final static Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Override
    public void getApplication(AsyncCallback<Application> callback) {

        User currentUser = PtAppContext.getCurrentUser();
        log.debug("Asking for current application for current user {}", currentUser);

        Lease lease;
        Tenant tenant;
        {
            EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
            tenant = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
            if (tenant == null) {
                tenant = EntityFactory.create(Tenant.class);
                tenant.user().set(currentUser);
                tenant.type().setValue(Tenant.Type.person);
                tenant.person().email().address().set(currentUser.email());
                PersistenceServicesFactory.getPersistenceService().persist(tenant);
            }
        }

        TenantInLease tenantInLease;
        {
            EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenant));
            tenantInLease = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
            if (tenantInLease == null) {
                throw new Error("Invalid application");
            }
        }
        lease = PersistenceServicesFactory.getPersistenceService().retrieve(Lease.class, tenantInLease.lease().getPrimaryKey());

        Application application;
        {
            EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
            application = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        }

        PtAppContext.setCurrentUserApplication(application);
        PtAppContext.setCurrentLease(lease);

        log.debug("Application {}", application);
        callback.onSuccess(application);
    }

    @Override
    public void getApplicationProgress(AsyncCallback<Application> callback, ApplicationWizardStep currentStep, ApplicationWizardSubstep currentSubstep) {
        Application progress = PersistenceServicesFactory.getPersistenceService().retrieve(Application.class,
                PtAppContext.getCurrentUserApplicationPrimaryKey());

        if (currentStep != null) {
            int idx = progress.steps().indexOf(currentStep);
            if (idx == -1) {
                throw new Error("Invalid Step recived");
            }

            currentStep = progress.steps().get(idx);
            //TODO hasAlert ?
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
                selectSubStep(currentStep, idxSub);
            }

            currentStep.status().setValue(getStepAggeratedStatus(currentStep));
            PersistenceServicesFactory.getPersistenceService().persist(currentStep);
            boolean currentStepCompleated = (currentStep.status().getValue() == ApplicationWizardStep.Status.complete);

            if (currentStepCompleated) {
                // Move to next regular step
                idx++;
                iterOverSteps: while (idx < progress.steps().size()) {
                    ApplicationWizardStep nextStep = progress.steps().get(idx);
                    // navigate to next invalid step
                    switch (nextStep.status().getValue()) {
                    case latest:
                    case notVisited:
                        nextStep.status().setValue(ApplicationWizardStep.Status.latest);
                        PersistenceServicesFactory.getPersistenceService().persist(nextStep);
                        currentStep = nextStep;
                        break iterOverSteps;
                    case invalid:
                        currentStep = nextStep;
                        break iterOverSteps;
                    }
                    idx++;
                }
                // Navigate to first subStep
                selectSubStep(currentStep, 0);
            }

        }
        callback.onSuccess(progress);
    }

    private boolean selectSubStep(ApplicationWizardStep currentStep, int startWithSubIndex) {
        // navigate to next invalid or notVisited step
        int idxSub = startWithSubIndex;
        while (idxSub < currentStep.substeps().size()) {
            ApplicationWizardSubstep nextSubstep = currentStep.substeps().get(idxSub);
            switch (nextSubstep.status().getValue()) {
            case latest:
            case notVisited:
                nextSubstep.status().setValue(ApplicationWizardStep.Status.latest);
                PersistenceServicesFactory.getPersistenceService().persist(nextSubstep);
                return true;
            case invalid:
                return true;
            }
            idxSub++;
        }
        return false;
    }

    private static ApplicationWizardStep.Status getStepAggeratedStatus(ApplicationWizardStep step) {
        ApplicationWizardStep.Status aggrStatus = ApplicationWizardStep.Status.complete;
        for (ApplicationWizardSubstep substep : step.substeps()) {
            switch (substep.status().getValue()) {
            case complete:
                break;
            case invalid:
                return ApplicationWizardStep.Status.invalid;
            case latest:
                aggrStatus = ApplicationWizardStep.Status.latest;
                break;
            case notVisited:
                if (aggrStatus != ApplicationWizardStep.Status.latest) {
                    aggrStatus = ApplicationWizardStep.Status.notVisited;
                }
                break;
            }
        }
        return aggrStatus;
    }
}
