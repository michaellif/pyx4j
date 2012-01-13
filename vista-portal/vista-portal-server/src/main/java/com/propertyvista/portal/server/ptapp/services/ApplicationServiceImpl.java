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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardStep;
import com.propertyvista.domain.tenant.ptapp.ApplicationWizardSubstep;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;
import com.propertyvista.portal.rpc.ptapp.services.ApplicationService;
import com.propertyvista.portal.server.ptapp.PtAppContext;
import com.propertyvista.server.common.ptapp.ApplicationMgr;

public class ApplicationServiceImpl extends ApplicationEntityServiceImpl implements ApplicationService {

    private static I18n i18n = I18n.get(ApplicationServiceImpl.class);

    private final static Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Override
    public void getApplication(AsyncCallback<Application> callback) {

        TenantUser currentUser = PtAppContext.getCurrentUser();
        log.debug("Asking for current application for current user {}", currentUser);

        Tenant tenant;
        {
            EntityQueryCriteria<Tenant> criteria = EntityQueryCriteria.create(Tenant.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
            tenant = Persistence.service().retrieve(criteria);
            if (tenant == null) {
                tenant = EntityFactory.create(Tenant.class);
                tenant.user().set(currentUser);
                tenant.type().setValue(Tenant.Type.person);
                tenant.person().email().set(currentUser.email());
                Persistence.service().persist(tenant);
            }
        }

        TenantInLease tenantInLease;
        {
            EntityQueryCriteria<TenantInLease> criteria = EntityQueryCriteria.create(TenantInLease.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().tenant(), tenant));
            tenantInLease = Persistence.service().retrieve(criteria);
            if (tenantInLease == null) {
                throw new Error("Invalid application");
            }
        }

        Application application;
        {
            // TODO verify application status
            EntityQueryCriteria<Application> criteria = EntityQueryCriteria.create(Application.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), currentUser));
            application = Persistence.service().retrieve(criteria);
            if (application == null) {
                throw new UserRuntimeException(i18n.tr("You have no applications assigned"));
            }
        }

        PtAppContext.setCurrentUserApplication(application);

        log.debug("Application {}", application);
        callback.onSuccess(application);
    }

    @Override
    public void getApplicationProgress(AsyncCallback<Application> callback, ApplicationWizardStep currentStep, ApplicationWizardSubstep currentSubstep) {
        Application application = PtAppContext.getCurrentUserApplication();

        if (currentStep != null) {
            int idx = application.steps().indexOf(currentStep);
            if (idx == -1) {
                throw new Error("Invalid Step received");
            }

            currentStep = application.steps().get(idx);
            ApplicationWizardStep.Status origStatus = currentStep.status().getValue();
            //TODO hasAlert ?
            if (currentSubstep != null) {
                int idxSub = currentStep.substeps().indexOf(currentSubstep);
                if (idxSub == -1) {
                    throw new Error("Invalid Substep received");
                }
                currentSubstep = currentStep.substeps().get(idxSub);
                currentSubstep.status().setValue(ApplicationWizardStep.Status.complete);
                Persistence.service().persist(currentSubstep);
                // navigate to next invalid or notVisited step
                idxSub++;
                selectSubStep(currentStep, idxSub);
            }

            currentStep.status().setValue(getStepAggeratedStatus(currentStep));
            Persistence.service().persist(currentStep);
            boolean currentStepCompleated = (currentStep.status().getValue() == ApplicationWizardStep.Status.complete);

            if (currentStepCompleated) {
                if (origStatus != ApplicationWizardStep.Status.complete) {
                    onStepCompleted(application, currentStep);
                }

                // Move to next regular step
                idx++;
                iterOverSteps: while (idx < application.steps().size()) {
                    ApplicationWizardStep nextStep = application.steps().get(idx);
                    // navigate to next invalid step
                    switch (nextStep.status().getValue()) {
                    case latest:
                    case notVisited:
                        nextStep.status().setValue(ApplicationWizardStep.Status.latest);
                        Persistence.service().persist(nextStep);
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
        callback.onSuccess(application);
    }

    private void onStepCompleted(Application application, ApplicationWizardStep currentStep) {
        if (currentStep.placeId().getValue().equals(AppPlaceInfo.getPlaceId(PtSiteMap.Payment.class))) {
            ApplicationMgr.makeApplicationCompleted(application);
        }

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
                Persistence.service().persist(nextSubstep);
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
