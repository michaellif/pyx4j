/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.BreadcrumbsBar;
import com.pyx4j.site.client.ui.crud.form.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.versioning.VersionSelectorDialog;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.rpc.services.breadcrumbs.BreadcrumbsService;

public class CrmViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    private static final I18n i18n = I18n.get(CrmViewerViewImplBase.class);

    private BreadcrumbsBar breadcrumbsBar;

    private BreadcrumbsService breadcumbsService;

    protected final String defaultCaption;

    private Button editButton;

    private Button selectVersion;

    private Button finalizeButton;

    private Button actionsButton;

    private Button.ButtonMenuBar actionsMenu;

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        this(placeClass, false);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, boolean viewOnly) {
        super();

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        setCaption(defaultCaption);

        // Notes button:
        addHeaderToolbarItem(new Button(i18n.tr("Notes"), new ClickHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(ClickEvent event) {
                ((CrmViewerActivity<E>) getPresenter()).getNotesAndAttachmentsController().show(CrmViewerViewImplBase.this);
            }
        }).asWidget());

        // Edit button:
        if (!viewOnly) {
            editButton = new Button(i18n.tr("Edit"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    getPresenter().edit();
                }
            });
            addHeaderToolbarItem(editButton);
        }

        // Actions button:
        actionsButton = new Button(i18n.tr("Actions"));
        actionsMenu = actionsButton.createMenu();
        actionsButton.setMenu(actionsMenu);
        addHeaderToolbarItem(actionsButton);
        actionsButton.setVisible(false);

        // Breadcrumb stuff:
        breadcumbsService = GWT.<BreadcrumbsService> create(BreadcrumbsService.class);
        breadcrumbsBar = new BreadcrumbsBar();
        setBreadcrumbsBar(breadcrumbsBar);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, CrmEntityForm<E> form) {
        this(placeClass);
        setForm(form);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, CrmEntityForm<E> form, boolean viewOnly) {
        this(placeClass, viewOnly);
        setForm(form);
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getSelectVersion() {
        return selectVersion;
    }

    public Button getFinalizeButton() {
        return finalizeButton;
    }

    public Button getActionsButton() {
        return actionsButton;
    }

    public void addAction(MenuItem action) {
        actionsMenu.addItem(action);
    }

    public void removeAction(MenuItem action) {
        actionsMenu.removeItem(action);
        actionsButton.setVisible(!actionsMenu.isMenuEmpty());
    }

    public void setActionVisible(MenuItem action, boolean visible) {
        action.setVisible(visible);
        actionsButton.setVisible(!actionsMenu.isMenuEmpty());
    }

    @Override
    public void reset() {
        if (finalizeButton != null) {
            finalizeButton.setVisible(false);
        }
        actionsButton.setVisible(false);
        super.reset();
    }

    @Override
    public void populate(E value) {
        super.populate(value);

        String caption = (defaultCaption + " " + value.getStringView());
        if (selectVersion != null) {
            if (((IVersionedEntity<?>) value).version().versionNumber().isNull()) {
                caption = caption + " (" + ((IVersionedEntity<?>) value).version().versionNumber().getStringView() + ")";
            } else {
                caption = caption + ", " + i18n.tr("version") + " #" + ((IVersionedEntity<?>) value).version().versionNumber().getStringView() + " ("
                        + ((IVersionedEntity<?>) value).version().fromDate().getStringView() + ")";
            }
        }
        setCaption(caption);

        if (editButton != null) {
            editButton.setEnabled(super.getPresenter().canEdit());
        }

        if (finalizeButton != null) {
            finalizeButton.setVisible(((IVersionedEntity<?>) value).version().versionNumber().isNull());
        }

        actionsButton.setVisible(!actionsMenu.isMenuEmpty());
        populateBreadcrumbs(value, caption);
    }

    protected void populateBreadcrumbs(E value, final String caption) {
        breadcumbsService.obtainBreadcrumbTrail(new AsyncCallback<Vector<IEntity>>() {

            @Override
            public void onSuccess(Vector<IEntity> result) {
                setCaption(caption);
                breadcrumbsBar.populate(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new Error(caught);
            }

        }, value.createIdentityStub());
    }

    protected <V extends IVersionData<?>> void enableVersioning(final Class<V> entityVersionClass, final AbstractVersionDataListService<V> entityVersionService) {

        selectVersion = new Button(i18n.tr("Select Version"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new VersionSelectorDialog<V>(entityVersionClass, getForm().getValue().getPrimaryKey()) {
                    @Override
                    public boolean onClickOk() {
                        getPresenter().view(getSelectedVersionId());
                        return true;
                    }

                    @Override
                    protected AbstractListService<V> getSelectService() {
                        return entityVersionService;
                    }
                }.show();
            }
        });
        addHeaderToolbarItem(selectVersion.asWidget());

        finalizeButton = new Button(i18n.tr("Finalize"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                getPresenter().approveFinal();
            }
        });
        addHeaderToolbarItem(finalizeButton.asWidget());

        if (editButton != null) {
            editButton.setCaption(i18n.tr("Edit Draft"));
        }
    }
}
