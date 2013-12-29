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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IVersionData;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.BreadcrumbsBar;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.site.client.ui.prime.form.AbstractViewer;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.versioning.VersionSelectorDialog;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.rpc.services.breadcrumbs.BreadcrumbsService;

public class CrmViewerViewImplBase<E extends IEntity> extends AbstractViewer<E> {

    private static final I18n i18n = I18n.get(CrmViewerViewImplBase.class);

    private final boolean viewOnly;

    private BreadcrumbsBar breadcrumbsBar;

    private BreadcrumbsService breadcumbsService;

    private Button notesButton;

    private Button editButton;

    private Button versioningButton;

    private Button revisionsButton;

    private MenuItem editDraft;

    private MenuItem selectVersion;

    private MenuItem finalizeMenu;

    private Button viewsButton;

    private Button.ButtonMenuBar viewsMenu;

    private Button actionsButton;

    private Button.ButtonMenuBar actionsMenu;

    public CrmViewerViewImplBase() {
        this(false);
    }

    public CrmViewerViewImplBase(boolean viewOnly) {
        this.viewOnly = viewOnly;

        // Notes button:
        // TODO VISTA-3708 create when HasNotesAndAttachments
        addHeaderToolbarItem((notesButton = new Button(i18n.tr("Notes"), new Command() {
            @SuppressWarnings("unchecked")
            @Override
            public void execute() {
                if (!isVisorShown()) {
                    ((CrmViewerActivity<E>) getPresenter()).getNotesAndAttachmentsController().show();
                }
            }
        })).asWidget());

        // Edit button:
        if (!this.viewOnly) {
            editButton = new Button(i18n.tr("Edit"), new Command() {
                @Override
                public void execute() {
                    getPresenter().edit();
                }
            });
            addHeaderToolbarItem(editButton);
        }

        // Views button:
        viewsButton = new Button(i18n.tr("Views"));
        viewsMenu = viewsButton.createMenu();
        viewsButton.setMenu(viewsMenu);
        addHeaderToolbarItem(viewsButton);
        viewsButton.setVisible(false);

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

    public CrmViewerViewImplBase(CrmEntityForm<E> form) {
        this(false);
        setForm(form);
    }

    public CrmViewerViewImplBase(CrmEntityForm<E> form, boolean viewOnly) {
        this(viewOnly);
        setForm(form);
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
            action.addStyleName(DefaultPaneTheme.StyleName.HighlightedAction.name());
            actionsButton.addStyleName(DefaultPaneTheme.StyleName.HighlightedButton.name());
        } else {
            action.removeStyleName(DefaultPaneTheme.StyleName.HighlightedAction.name());

            boolean highlighted = false;
            for (MenuItem a : actionsMenu.getItems()) {
                highlighted = a.getStyleName().contains(DefaultPaneTheme.StyleName.HighlightedAction.name());
                if (highlighted) {
                    break;
                }
            }
            if (!highlighted) {
                actionsButton.removeStyleName(DefaultPaneTheme.StyleName.HighlightedButton.name());
            }
        }
    }

    public void resetActionHighlighting() {
        for (MenuItem action : actionsMenu.getItems()) {
            action.removeStyleName(DefaultPaneTheme.StyleName.HighlightedAction.name());
        }
        actionsButton.removeStyleName(DefaultPaneTheme.StyleName.HighlightedButton.name());
    }

    public MenuItemSeparator addViewsSeparator() {
        return viewsMenu.addSeparator();
    }

    public void addView(MenuItem view) {
        viewsMenu.addItem(view);
    }

    public void removeView(MenuItem view) {
        viewsMenu.removeItem(view);
        viewsButton.setVisible(!viewsMenu.isMenuEmpty());
    }

    public void setViewVisible(MenuItem view, boolean visible) {
        view.setVisible(visible);
        viewsButton.setVisible(!viewsMenu.isMenuEmpty());
    }

    public void setNotesEnabled(boolean enabled) {
        notesButton.setEnabled(enabled);
    }

    public void setNotesVisible(boolean visible) {
        notesButton.setVisible(visible);
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
                    finalizeMenu.addStyleName(DefaultPaneTheme.StyleName.HighlightedAction.name());
                    versioningButton.addStyleName(DefaultPaneTheme.StyleName.HighlightedButton.name());
                } else {
                    finalizeMenu.removeStyleName(DefaultPaneTheme.StyleName.HighlightedAction.name());
                    versioningButton.removeStyleName(DefaultPaneTheme.StyleName.HighlightedButton.name());
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

        viewsButton.setVisible(false);
        actionsButton.setVisible(false);
        resetActionHighlighting();

        super.reset();
    }

    @Override
    public void populate(E value) {
        super.populate(value);

        if (value instanceof IVersionedEntity) {
            setFinalizationVisible(((IVersionedEntity<?>) value).version().versionNumber().isNull());
        }

        setEditingEnabled(super.getPresenter().canEdit());

        viewsButton.setVisible(!viewsMenu.isMenuEmpty());
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
        assert revisionsButton == null : "Loose Versioning is enabled already!?";

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

    protected <V extends IVersionData<?>> void enableLooseVersioning(final Class<V> entityVersionClass,
            final AbstractVersionDataListService<V> entityVersionService) {
        assert versioningButton == null : "Versioning is enabled already!?";

        revisionsButton = new Button(i18n.tr("Revisions"));
        addHeaderToolbarItem(revisionsButton);

        revisionsButton.setCommand(new Command() {
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
    }
}
