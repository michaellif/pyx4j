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
 */
package com.pyx4j.site.client.backoffice.activity.prime;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.backoffice.ui.prime.wizard.IPrimeWizardView;
import com.pyx4j.site.client.backoffice.ui.prime.wizard.IPrimeWizardView.IPrimeWizardPresenter;
import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class AbstractPrimeWizardActivity<E extends IEntity> extends AbstractPrimeActivity<IPrimeWizardView<E>> implements IPrimeWizardPresenter {

    private static final I18n i18n = I18n.get(AbstractPrimeWizardActivity.class);

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    public AbstractPrimeWizardActivity(Class<E> entityClass, CrudAppPlace place, IPrimeWizardView<E> view, AbstractCrudService<E> service) {
        super(view, place);
        // development correctness checks:
        assert (entityClass != null);
        assert (view != null);
        assert (service != null);

        this.service = service;
        this.entityClass = entityClass;

    }

    @Override
    public CrudAppPlace getPlace() {
        return (CrudAppPlace) super.getPlace();
    }

    public AbstractCrudService<E> getService() {
        return service;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        getView().setPresenter(this);
        populate();
        panel.setWidget(getView());
    }

    protected void onDiscard() {
        getView().reset();
        getView().setPresenter(null);
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
    public void populate() {
        obtainInitializationData(new DefaultAsyncCallback<AbstractCrudService.InitializationData>() {
            @Override
            public void onSuccess(InitializationData result) {
                service.init(new DefaultAsyncCallback<E>() {
                    @Override
                    public void onSuccess(E result) {
                        onPopulateSuccess(result);
                    }
                }, result);
            }
        });
    }

    /**
     * Descendants may override this method to supply some initialization info.
     * 
     */
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        if (getPlace().getInitializationData() != null) {
            callback.onSuccess(getPlace().getInitializationData());
        } else {
            callback.onSuccess(null);
        }
    }

    public void onPopulateSuccess(E result) {
        populateView(result);
    }

    protected void populateView(E result) {
        getView().reset();
        getView().populate(result);
    }

    @Override
    public void finish() {
        trySave();
    }

    @Override
    public void cancel() {
        History.back();
    }

    public void trySave() {
        service.save(new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                ReferenceDataManager.invalidate(entityClass);
                onSaved(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                onSaveFail(caught);
            }
        }, getView().getValue());

    }

    protected void onSaved(Key result) {
        getView().reset();
        History.back();
    }

    protected void onSaveFail(Throwable caught) {
        if (!getView().onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    @Override
    public String mayStop() {
        if (getView().isDirty()) {
            String entityName = getView().getValue().getStringView();
            if (CommonsStringUtils.isEmpty(entityName)) {
                return i18n.tr("Changes to {0} were not saved", getView().getValue().getEntityMeta().getCaption());
            } else {
                return i18n.tr("Changes to {0} ''{1}'' were not saved", getView().getValue().getEntityMeta().getCaption(), entityName);
            }
        } else {
            return null;
        }
    }

    @Override
    @Deprecated
    public void refresh() {
    }

}
