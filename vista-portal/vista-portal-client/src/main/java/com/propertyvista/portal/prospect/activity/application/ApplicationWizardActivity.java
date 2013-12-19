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

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.prospect.events.ApplicationWizardStateChangeEvent;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardView.ApplicationWizardPresenter;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.prospect.dto.UnitOptionsSelectionDTO;
import com.propertyvista.portal.rpc.portal.prospect.services.ApplicationWizardService;
import com.propertyvista.portal.shared.activity.AbstractWizardActivity;

public class ApplicationWizardActivity extends AbstractWizardActivity<OnlineApplicationDTO> implements ApplicationWizardPresenter {

    private final ApplicationWizardService service;

    public ApplicationWizardActivity(AppPlace place) {
        super(ApplicationWizardView.class);

        this.service = GWT.<ApplicationWizardService> create(ApplicationWizardService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        super.start(panel, eventBus);
        service.init(new DefaultAsyncCallback<OnlineApplicationDTO>() {
            @Override
            public void onSuccess(OnlineApplicationDTO result) {
                getView().populate(result);
                eventBus.fireEvent(new ApplicationWizardStateChangeEvent(((ApplicationWizardView) getView()).getApplicationWizard(),
                        ApplicationWizardStateChangeEvent.ChangeType.init));
            }
        });
    }

    @Override
    public void finish() {
        assert service != null : "Service shouldn't be null or method finish() has to be implemented in subclass.";
        service.submit(new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                ApplicationWizardActivity.super.finish();
                AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.ApplicationConfirmation());
            }

            @Override
            public void onFailure(Throwable caught) {
                if (!getView().manageSubmissionFailure(caught)) {
                    throw new UnrecoverableClientError(caught);
                }
            }
        }, getView().getValue());

    }

    @Override
    public void onStepSelected(WizardStep selectedStep) {
        super.onStepSelected(selectedStep);

        OnlineApplicationDTO currentValue = getView().getValue();
        if (currentValue != null) {
            // save current value state:
            service.save(new DefaultAsyncCallback<Key>() {
                @Override
                public void onSuccess(Key result) {
                    // TODO Auto-generated method stub
                }
            }, currentValue);
        }
    }

    @Override
    protected void onDiscard() {
        super.onDiscard();
        ClientEventBus.instance.fireEvent(new ApplicationWizardStateChangeEvent(null, ApplicationWizardStateChangeEvent.ChangeType.discard));
    }

    @Override
    public void getAvailableUnits(AsyncCallback<Vector<AptUnit>> callback, Floorplan floorplan, LogicalDate moveIn) {
        service.getAvailableUnits(callback, floorplan, moveIn);
    }

    @Override
    public void getAvailableUnitOptions(AsyncCallback<UnitOptionsSelectionDTO> callback, AptUnit unit) {
        service.getAvailableUnitOptions(callback, unit);
    }
}