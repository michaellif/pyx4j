/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Created on 2011-05-04
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.shared.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.prospect.ui.steps.UnitStepView;
import com.propertyvista.portal.rpc.portal.services.AbstractWizardStepService;
import com.propertyvista.portal.rpc.portal.services.UnitStepService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.IWizardStepView;
import com.propertyvista.portal.shared.ui.IWizardStepView.IWizardStepPresenter;

public abstract class AbstractWizardStepActivity<E extends IEntity> extends SecurityAwareActivity implements IWizardStepPresenter<E> {

    private static final I18n i18n = I18n.get(AbstractWizardStepActivity.class);

    private final IWizardStepView<E> view;

    private final AbstractWizardStepService<E> service;

    public AbstractWizardStepActivity(Class<? extends IWizardStepView<E>> viewType, AbstractWizardStepService<E> service) {
        view = PortalSite.getViewFactory().getView(viewType);
        this.service = service;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);
        service.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                view.reset();
                view.populate(result);
            }
        });
    }

    public abstract void navigateToNextStep();

    public abstract void navigateToPreviousStep();

    public abstract void navigateOut();

    public IWizardStepView<E> getView() {
        return view;
    }

    protected void onDiscard() {
        view.reset();
        view.setPresenter(null);
    }

    @Override
    public void onCancel() {
        onDiscard();
        super.onCancel();
    }

    @Override
    public void onStop() {
        onDiscard();
        super.onStop();
    }

    @Override
    public void cancel() {
        navigateOut();
    }

    @Override
    public void next() {
        service.submit(new AsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                navigateToNextStep();
            }

            @Override
            public void onFailure(Throwable caught) {
                onSubmittionFail(caught);
            }
        }, view.getValue());
    }

    protected void onSubmittionFail(Throwable caught) {
        if (!view.onSubmittionFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    @Override
    public void previous() {
        service.submit(new AsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                navigateToPreviousStep();
            }

            @Override
            public void onFailure(Throwable caught) {
                onSubmittionFail(caught);
            }
        }, view.getValue());
    }
}
