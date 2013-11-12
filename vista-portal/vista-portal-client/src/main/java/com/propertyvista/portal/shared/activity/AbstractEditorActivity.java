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
package com.propertyvista.portal.shared.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.IEditorView;
import com.propertyvista.portal.shared.ui.IEditorView.IEditorPresenter;

public abstract class AbstractEditorActivity<E extends IEntity> extends SecurityAwareActivity implements IEditorPresenter<E> {

    private static final I18n i18n = I18n.get(AbstractEditorActivity.class);

    private final IEditorView<E> view;

    private final AbstractCrudService<E> service;

    private final AppPlace place;

    private final Key entityId;

    public AbstractEditorActivity(Class<? extends IEditorView<E>> viewType, AbstractCrudService<E> service, AppPlace place) {
        this.service = service;
        this.place = place;
        entityId = place.getItemId();
        view = PortalSite.getViewFactory().getView(viewType);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);
        view.setPresenter(this);
        retreive();
    }

    public AbstractCrudService<E> getService() {
        return service;
    }

    public IEditorView<E> getView() {
        return view;
    }

    public Key getEntityId() {
        return entityId;
    }

    @Override
    public void cancel() {
        retreive();
    }

    public void retreive() {
        retreive(AbstractCrudService.RetrieveTarget.View);
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
        retreive(AbstractCrudService.RetrieveTarget.Edit);
    }

    @Override
    public void save() {
        AsyncCallback<Key> callback = new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                onSaved(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                onSaveFail(caught);
            }
        };
        service.save(callback, view.getValue());
    }

    protected void onSaved(Key result) {
        view.reset();
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(place.getClass()).formPlace(result));
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    private void retreive(final AbstractCrudService.RetrieveTarget target) {
        service.retrieve(new DefaultAsyncCallback<E>() {
            @Override
            public void onSuccess(E result) {
                view.reset();
                switch (target) {
                case Edit:
                    view.setEditable(true);
                    break;
                case View:
                    view.setEditable(false);
                    break;
                }
                view.populate(result);
            }
        }, entityId, target);
    }

    @Override
    public String mayStop() {
        if (view.isEditable() && view.isDirty()) {
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
