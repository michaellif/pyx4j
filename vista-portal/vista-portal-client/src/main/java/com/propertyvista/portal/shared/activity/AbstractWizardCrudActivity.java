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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.shared.ui.IWizardView;

public abstract class AbstractWizardCrudActivity<E extends IEntity> extends AbstractWizardActivity<E> {

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    public AbstractWizardCrudActivity(Class<? extends IWizardView<E>> viewType, AbstractCrudService<E> service, Class<E> entityClass) {
        super(viewType);

        this.service = service;
        this.entityClass = entityClass;

    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        super.start(panel, eventBus);

        obtainInitializationData(new DefaultAsyncCallback<AbstractCrudService.InitializationData>() {
            @Override
            public void onSuccess(InitializationData result) {
                service.init(new DefaultAsyncCallback<E>() {
                    @Override
                    public void onSuccess(E result) {
                        getView().populate(result);
                    }
                }, result);
            }
        });
    }

    public AbstractCrudService<E> getService() {
        return service;
    }

    @Override
    public void finish() {
        assert service != null : "Service shouldn't be null or method finish() has to be implemented in subclass.";
        service.create(new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                ReferenceDataManager.invalidate(entityClass);
                AbstractWizardCrudActivity.super.finish();
                onFinish(result);
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
    protected final void onFinish() {
    }

    protected void onFinish(Key result) {

    }
}
