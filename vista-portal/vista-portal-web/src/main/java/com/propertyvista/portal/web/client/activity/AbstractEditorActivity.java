/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.ReferenceDataManager;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.IEditorView;
import com.propertyvista.portal.web.client.ui.IEditorView.IEditorPresenter;

public abstract class AbstractEditorActivity<E extends IEntity> extends SecurityAwareActivity implements IEditorPresenter<E> {

    private static final I18n i18n = I18n.get(AbstractEditorActivity.class);

    private final IEditorView<E> view;

    private final AbstractCrudService<E> service;

    private final Class<E> entityClass;

    private E value;

    public AbstractEditorActivity(Class<? extends IEditorView<E>> viewType, Class<? extends AbstractCrudService<E>> serviceType, Class<E> entityClass) {
        view = PortalWebSite.getViewFactory().instantiate(viewType);
        view.setPresenter(this);

        this.service = GWT.create(serviceType);
        this.entityClass = entityClass;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(getView());

        service.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                value = result;
                view.reset();
                view.populate(result);
            }
        }, null, AbstractCrudService.RetrieveTarget.View);
    }

    public AbstractCrudService<E> getService() {
        return service;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public IEditorView<E> getView() {
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
    public void edit() {
        getView().setEditable(true);
    }

    @Override
    public void save() {
        AsyncCallback<Key> callback = new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                ReferenceDataManager.invalidate(entityClass);
                onSaved(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                onSaveFail(caught);
            }
        };
        service.save(callback, view.getValue());
        view.setEditable(false);
    }

    protected void onSaved(Key result) {
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    @Override
    public void cancel() {
        getView().setEditable(false);
        view.reset();
        view.populate(value);
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
}
