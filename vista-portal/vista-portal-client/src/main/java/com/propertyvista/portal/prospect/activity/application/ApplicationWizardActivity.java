/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 15, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity.application;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.rpc.report.ReportRequest;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ReportDialog;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepStatus;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitSelectionDTO.UnitTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.rpc.portal.resident.services.movein.LeaseTermBlankAgreementDocumentDownloadService;
import com.propertyvista.portal.shared.activity.AbstractWizardActivity;

public class ApplicationWizardActivity extends AbstractWizardActivity<OnlineApplicationDTO, ApplicationWizardView> implements ApplicationWizardPresenter {

    private static final Logger log = LoggerFactory.getLogger(ApplicationWizardActivity.class);

    private static final I18n i18n = I18n.get(ApplicationWizardActivity.class);

    private final ApplicationWizardService service;

    public ApplicationWizardActivity(AppPlace place) {
        super(ApplicationWizardView.class);

        this.service = GWT.<ApplicationWizardService> create(ApplicationWizardService.class);

        Window.addCloseHandler(new CloseHandler<Window>() {

            @Override
            public void onClose(CloseEvent<Window> event) {
                onDiscard();
            }
        });
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        super.start(panel, eventBus);

        service.init(new DefaultAsyncCallback<OnlineApplicationDTO>() {
            @Override
            public void onSuccess(OnlineApplicationDTO result) {
                getView().populate(result);
                eventBus.fireEvent(new ApplicationWizardStateChangeEvent(getView().getApplicationWizard(), ApplicationWizardStateChangeEvent.ChangeType.init));
            }
        });
    }

    @Override
    public void finish() {
        updateStepProgressInfo();
        service.submit(new DefaultAsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                ApplicationWizardActivity.super.finish();
                AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.ApplicationConfirmation());
            }
        }, getView().getValue());
    }

    @Override
    protected void onDiscard() {
        OnlineApplicationDTO currentValue = getView().getValue();
        if (currentValue != null) {
            updateStepProgressInfo();
            // save current value state:
            service.save(new DefaultAsyncCallback<Key>() {
                @Override
                public void onSuccess(Key result) {
                    log.info("application was saved on server");
                }

                @Override
                public void onFailure(Throwable caught) {
                    log.error("failed to save application on server");
                    super.onFailure(caught);
                }

            }, currentValue);
        }

        // call super AFTER! 
        super.onDiscard();
        ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(null, ApplicationWizardStateChangeEvent.ChangeType.discard));
    }

    private void updateStepProgressInfo() {
        getView().getValue().stepsStatuses().clear();
        for (WizardStep step : getView().getApplicationWizard().getAllSteps()) {
            ApplicationWizardStep appStep = (ApplicationWizardStep) step;
            OnlineApplicationWizardStepStatus status = EntityFactory.create(OnlineApplicationWizardStepStatus.class);
            status.step().setValue(appStep.getOnlineApplicationWizardStepMeta());
            status.complete().setValue(appStep.isStepComplete());
            status.visited().setValue(appStep.isStepVisited());
            getView().getValue().stepsStatuses().add(status);
        }
    }

    @Override
    public String mayStop() {
        if (getView().isDirty()) {
            return i18n
                    .tr("Leaving this page will SAVE all your latest changes. You can continue from the point you left anytime by Login into the application again.");
        }
        return null;
    }

    @Override
    public void getAvailableUnits(AsyncCallback<UnitSelectionDTO> callback, UnitSelectionDTO unitSelection) {
        service.getAvailableUnits(callback, unitSelection);
    }

    @Override
    public void getAvailableUnitOptions(AsyncCallback<UnitOptionsSelectionDTO> callback, UnitTO unit) {
        service.getAvailableUnitOptions(callback, unit/* .<AptUnit> createIdentityStub() */);
    }

    @Override
    public void getProfiledPaymentMethods(final AsyncCallback<List<LeasePaymentMethod>> callback) {
        service.getProfiledPaymentMethods(new DefaultAsyncCallback<Vector<LeasePaymentMethod>>() {
            @Override
            public void onSuccess(Vector<LeasePaymentMethod> result) {
                callback.onSuccess(result);
            }
        });
    }

    @Override
    public void downloadLeaseAgreementDraft() {
        ReportDialog reportDialog = new ReportDialog(i18n.tr("Creating Draft of Lease Agreement"), "");
        reportDialog.setDownloadServletPath(GWT.getModuleBaseURL() + DeploymentConsts.downloadServletMapping);

        HashMap<String, Serializable> requestParameters = new HashMap<>();
        requestParameters.put(LeaseTermBlankAgreementDocumentDownloadService.IS_DRAFT_KEY, Boolean.TRUE);

        ReportRequest request = new ReportRequest();
        request.setParameters(requestParameters);
        reportDialog.start(GWT.<LeaseTermBlankAgreementDocumentDownloadService> create(LeaseTermBlankAgreementDocumentDownloadService.class), request);

    }
}