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
package com.pyx4j.site.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractWizardService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.wizard.IWizard;
import com.pyx4j.site.rpc.AppPlace;

public abstract class AbstractWizardActivity<E extends IEntity> extends AbstractActivity implements IWizard.Presenter {

    private static final I18n i18n = I18n.get(AbstractWizardActivity.class);

    private final IWizard<E> view;

    private final AbstractWizardService<E> service;

    private final AppPlace place;

    private final Class<E> entityClass;

    public AbstractWizardActivity(AppPlace place, IWizard<E> view, AbstractWizardService<E> service, Class<E> entityClass) {
        // development correctness checks:
        assert (view != null);
        assert (service != null);
        assert (entityClass != null);

        this.place = place;
        this.view = view;
        this.service = service;
        this.entityClass = entityClass;

        view.getMemento().setCurrentPlace(place);
    }

    public IWizard<E> getView() {
        return view;
    }

    public AbstractWizardService<E> getService() {
        return service;
    }

    public AppPlace getPlace() {
        return place;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        populate();
        panel.setWidget(view);
    }

    @Override
    public void onStop() {
        view.reset();
        view.setPresenter(null);
        super.onStop();
    }

    @Override
    public void populate() {
        service.create(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                onPopulateSuccess(result);
            }
        });
    }

    public void onPopulateSuccess(E result) {
        populateView(result);
    }

    protected void populateView(E result) {
        view.reset();
        view.populate(result);
    }

    @Override
    public void finish() {
        trySave();
    }

    @Override
    public void cancel() {
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }

    public void trySave() {
        service.finish(new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                ReferenceDataManager.invalidate(entityClass);
                onSaved(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                onSaveFail(caught);
            }
        }, view.getValue());

    }

    protected void onSaved(Key result) {
        view.reset();
        AppSite.getPlaceController().goTo(AppSite.getPlaceController().getForwardedFrom());
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    @Override
    public String mayStop() {
        if (view.isDirty()) {
            String entityName = view.getValue().getStringView();
            if (CommonsStringUtils.isEmpty(entityName)) {
                return i18n.tr("Changes to {0} were not saved", view.getValue().getEntityMeta().getCaption());
            } else {
                return i18n.tr("Changes to {0} ''{1}'' were not saved", view.getValue().getEntityMeta().getCaption(), entityName);
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
