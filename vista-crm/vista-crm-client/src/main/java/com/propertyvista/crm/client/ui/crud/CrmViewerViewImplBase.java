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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;

import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IVersionData;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.BreadcrumbsBar;
import com.pyx4j.site.client.ui.crud.DefaultSiteCrudPanelsTheme;
import com.pyx4j.site.client.ui.crud.form.ViewerViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.versioning.VersionSelectorDialog;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.rpc.services.breadcrumbs.BreadcrumbsService;

public class CrmViewerViewImplBase<E extends IEntity> extends ViewerViewImplBase<E> {

    private static final I18n i18n = I18n.get(CrmViewerViewImplBase.class);

    private final boolean viewOnly;

    private BreadcrumbsBar breadcrumbsBar;

    private BreadcrumbsService breadcumbsService;

    protected final String defaultCaption;

    private Button editButton;

    private Button versioningButton;

    private Button historyingButton;

    private MenuItem editDraft;

    private MenuItem selectVersion;

    private MenuItem finalizeMenu;

    private Button actionsButton;

    private Button.ButtonMenuBar actionsMenu;

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        this(placeClass, false);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, CrmEntityForm<E> form) {
        this(placeClass);
        setForm(form);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, CrmEntityForm<E> form, boolean viewOnly) {
        this(placeClass, viewOnly);
        setForm(form);
    }

    public CrmViewerViewImplBase(Class<? extends CrudAppPlace> placeClass, boolean viewOnly) {
        this.viewOnly = viewOnly;

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");
        setCaption(defaultCaption);

        // Notes button:
        addHeaderToolbarItem(new Button(i18n.tr("Notes"), new Command() {
            @SuppressWarnings("unchecked")
            @Override
            public void execute() {
                if (!isVisorShown()) {
                    ((CrmViewerActivity<E>) getPresenter()).getNotesAndAttachmentsController().show(CrmViewerViewImplBase.this);
                }
            }
        }).asWidget());

        // Edit button:
        if (!viewOnly) {
            editButton = new Button(i18n.tr("Edit"), new Command() {
                @Override
                public void execute() {
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

    public MenuItemSeparator addActionSeparator() {
        return actionsMenu.addSeparator();
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

    public void setActionHighlighted(MenuItem action, boolean highlight) {
        if (highlight) {
            action.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedAction.name());
            actionsButton.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
        } else {
            action.removeStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedAction.name());

            boolean highlighted = false;
            for (MenuItem a : actionsMenu.getItems()) {
                highlighted = a.getStyleName().contains(DefaultSiteCrudPanelsTheme.StyleName.HighlightedAction.name());
                if (highlighted) {
                    break;
                }
            }
            if (!highlighted) {
                actionsButton.removeStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
            }
        }
    }

    public void resetActionHighlighting() {
        for (MenuItem action : actionsMenu.getItems()) {
            action.removeStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedAction.name());
        }
        actionsButton.removeStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
    }

    public void setEditingVisible(boolean visible) {
        if (editButton != null) {
            editButton.setVisible(visible);
        } else if (editDraft != null) {
            editDraft.setVisible(visible);
        }
    }

    public void setEditingEnabled(boolean enabled) {
        if (editButton != null) {
            editButton.setEnabled(enabled);
        } else if (editDraft != null) {
            editDraft.setEnabled(enabled);
        }
    }

    protected void setFinalizationVisible(boolean visible) {
        if (finalizeMenu != null) {
            if (visible != isFinalizationVisible()) {
                finalizeMenu.setVisible(visible);

                if (visible) {
                    finalizeMenu.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedAction.name());
                    versioningButton.addStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
                } else {
                    finalizeMenu.removeStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedAction.name());
                    versioningButton.removeStyleName(DefaultSiteCrudPanelsTheme.StyleName.HighlightedButton.name());
                }
            }
        }
    }

    protected boolean isFinalizationVisible() {
        return (finalizeMenu != null ? finalizeMenu.isVisible() : false);
    }

    @Override
    public void reset() {
        setFinalizationVisible(false);
        actionsButton.setVisible(false);
        resetActionHighlighting();
        super.reset();
    }

    @Override
    public void populate(E value) {
        super.populate(value);

        String caption = (defaultCaption + " " + value.getStringView());
        if (value instanceof IVersionedEntity) {
            IVersionData<?> version = ((IVersionedEntity<?>) value).version();

            caption = caption + " (";
            if (version.versionNumber().isNull()) { // draft case:
                setFinalizationVisible(true);
                caption = caption + i18n.tr("Draft Version");
            } else {
                setFinalizationVisible(false);
                if (VersionedEntityUtils.isCurrent((IVersionedEntity<?>) value)) {
                    caption = caption + i18n.tr("Current Version");
                } else {
                    caption = caption + i18n.tr("Version") + " #" + version.versionNumber().getStringView() + " - " + version.fromDate().getStringView();
                }
            }
            caption = caption + ")";
        }
        setCaption(caption); // update caption

        setEditingEnabled(super.getPresenter().canEdit());

        actionsButton.setVisible(!actionsMenu.isMenuEmpty());
        populateBreadcrumbs(value);
    }

    protected void populateBreadcrumbs(E value) {
        breadcumbsService.obtainBreadcrumbTrail(new DefaultAsyncCallback<Vector<IEntity>>() {
            @Override
            public void onSuccess(Vector<IEntity> result) {
                breadcrumbsBar.populate(result);
            }
        }, value.createIdentityStub());
    }

    protected <V extends IVersionData<?>> void enableVersioning(final Class<V> entityVersionClass, final AbstractVersionDataListService<V> entityVersionService) {
        assert historyingButton == null : "Historying is enabled already!?";

        if (editButton != null) {
            editButton.removeFromParent();
            editButton = null;
        }

        versioningButton = new Button(i18n.tr("Versioning"));
        Button.ButtonMenuBar versioningMenu = versioningButton.createMenu();
        versioningButton.setMenu(versioningMenu);
        addHeaderToolbarItem(versioningButton);

        if (!viewOnly) {
            editDraft = new MenuItem(i18n.tr("Edit Draft"), new Command() {
                @Override
                public void execute() {
                    getPresenter().edit();
                }
            });
            versioningMenu.addItem(editDraft);
        }

        finalizeMenu = new MenuItem(i18n.tr("Finalize"), new Command() {
            @Override
            public void execute() {
                getPresenter().approveFinal();
            }
        });
        versioningMenu.addItem(finalizeMenu);

        selectVersion = new MenuItem(i18n.tr("Select Version"), new Command() {
            @Override
            public void execute() {
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
        versioningMenu.addItem(selectVersion);
    }

    protected <V extends IVersionData<?>> void enableHistorying(final Class<V> entityVersionClass, final AbstractVersionDataListService<V> entityVersionService) {
        assert versioningButton == null : "Versioning is enabled already!?";

        historyingButton = new Button(i18n.tr("History"));
        addHeaderToolbarItem(historyingButton);

        historyingButton.addClickHandler(new ClickHandler() {
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
    }
}
