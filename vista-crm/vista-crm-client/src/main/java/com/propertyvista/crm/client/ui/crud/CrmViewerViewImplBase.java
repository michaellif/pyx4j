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
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.entity.rpc.AbstractVersionDataListService;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.AccessControlContext;
import com.pyx4j.site.client.ui.BreadcrumbsBar;
import com.pyx4j.site.client.ui.PaneTheme;
import com.pyx4j.site.client.ui.prime.IPrimePane;
import com.pyx4j.site.client.ui.prime.form.AbstractViewer;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.versioning.VersionSelectorDialog;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.visor.notes.NotesAndAttachmentsVisorController;
import com.propertyvista.crm.rpc.VistaCrmDebugId;
import com.propertyvista.crm.rpc.services.breadcrumbs.BreadcrumbsService;
import com.propertyvista.domain.note.HasNotesAndAttachments;

public class CrmViewerViewImplBase<E extends IEntity> extends AbstractViewer<E> {

    private static final I18n i18n = I18n.get(CrmViewerViewImplBase.class);

    private final boolean viewOnly;

    private final BreadcrumbsBar breadcrumbsBar;

    private final BreadcrumbsService breadcumbsService;

    private final Button notesButton;

    private Button editButton;

    private Button versioningButton;

    private Button revisionsButton;

    private MenuItem editDraft;

    private MenuItem selectVersion;

    private MenuItem finalizeMenu;

    private final Button viewsButton;

    private Button.ButtonMenuBar viewsMenu;

    private final Button actionsButton;

    private Button.ButtonMenuBar actionsMenu;

    private Class<? extends IEntity> notesPermissionClass = null;

    private AccessControlContext notesSecurityContexts = null;

    public CrmViewerViewImplBase() {
        this(false);
    }

    public CrmViewerViewImplBase(boolean viewOnly) {
        this.viewOnly = viewOnly;

        // Notes button:
        notesButton = new Button(i18n.tr("Notes"), new Command() {
            @SuppressWarnings("unchecked")
            @Override
            public void execute() {
                NotesAndAttachmentsVisorController notesController = ((CrmViewerActivity<E>) getPresenter()).getNotesAndAttachmentsController();
                notesController.setSecurityData(notesPermissionClass, notesSecurityContexts);
                notesController.show();
            }
        }) {
            @Override
            public void setSecurityContext(AccessControlContext context) {
                super.setSecurityContext(context);
                notesSecurityContexts = context;
            }
        };
        addHeaderToolbarItem(notesButton);

        // Edit button:
        if (!this.viewOnly) {
            editButton = new Button(i18n.tr("Edit"), new Command() {
                @Override
                public void execute() {
                    getPresenter().edit();
                }
            });
            editButton.ensureDebugId(VistaCrmDebugId.View.Edit.debugId());
            addHeaderToolbarItem(editButton);
        }

        // Views button:
        viewsButton = new Button(i18n.tr("Views"));
        viewsButton.ensureDebugId(VistaCrmDebugId.View.Views.debugId());
        viewsMenu = viewsButton.createMenu();
        viewsButton.setMenu(viewsMenu);
        addHeaderToolbarItem(viewsButton);
        viewsButton.setVisible(false);
        addSecureConcern(viewsButton);

        // Actions button:
        actionsButton = new Button(i18n.tr("Actions"));
        actionsButton.ensureDebugId(VistaCrmDebugId.View.Actions.debugId());
        actionsMenu = actionsButton.createMenu();
        actionsButton.setMenu(actionsMenu);
        addHeaderToolbarItem(actionsButton);
        actionsButton.setVisible(false);
        addSecureConcern(actionsMenu);

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

    protected void setForm(CrmEntityForm<E> form) {
        super.setForm(form);

        // set default notes permission class (root entity one):
        if (notesPermissionClass == null) {
            setNotesPermissionClass(form.getRootClass());
        }
    }

    /*
     * overrides default notes permission class (root entity one)
     */
    public void setNotesPermissionClass(Class<? extends IEntity> permissionClass) {
        notesButton.setPermission(DataModelPermission.permissionRead(permissionClass));
        notesPermissionClass = permissionClass;
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
            action.addStyleName(PaneTheme.StyleName.HighlightedAction.name());
            actionsButton.addStyleName(PaneTheme.StyleName.HighlightedButton.name());
        } else {
            action.removeStyleName(PaneTheme.StyleName.HighlightedAction.name());

            boolean highlighted = false;
            for (MenuItem a : actionsMenu.getItems()) {
                highlighted = a.getStyleName().contains(PaneTheme.StyleName.HighlightedAction.name());
                if (highlighted) {
                    break;
                }
            }
            if (!highlighted) {
                actionsButton.removeStyleName(PaneTheme.StyleName.HighlightedButton.name());
            }
        }
    }

    public void resetActionHighlighting() {
        for (MenuItem action : actionsMenu.getItems()) {
            action.removeStyleName(PaneTheme.StyleName.HighlightedAction.name());
        }
        actionsButton.removeStyleName(PaneTheme.StyleName.HighlightedButton.name());
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

    public final void setEditingVisible(boolean visible) {
        if (editButton != null) {
            editButton.setVisible(visible && super.getPresenter().canEdit());
        } else if (editDraft != null) {
            editDraft.setVisible(visible && super.getPresenter().canEdit());
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
                    finalizeMenu.addStyleName(PaneTheme.StyleName.HighlightedAction.name());
                    versioningButton.addStyleName(PaneTheme.StyleName.HighlightedButton.name());
                } else {
                    finalizeMenu.removeStyleName(PaneTheme.StyleName.HighlightedAction.name());
                    versioningButton.removeStyleName(PaneTheme.StyleName.HighlightedButton.name());
                }
            }
        }
    }

    protected boolean isFinalizationVisible() {
        return (finalizeMenu != null ? finalizeMenu.isVisible() : false);
    }

    @Override
    public void setPresenter(IPrimePane.Presenter presenter) {
        super.setPresenter(presenter);
        if (presenter != null) {
            reset(); // initialize the view!..
        }
    }

    @Override
    public void reset() {
        setFinalizationVisible(false);

        viewsButton.setVisible(false);
        actionsButton.setVisible(false);
        notesButton.setVisible(false);

        resetActionHighlighting();

        super.reset();
    }

    @Override
    public void populate(E value) {
        reset();

        super.populate(value);

        if (value instanceof IVersionedEntity) {
            setFinalizationVisible(((IVersionedEntity<?>) value).version().versionNumber().isNull());
        }

        setEditingVisible(true);

        viewsButton.setVisible(!viewsMenu.isMenuEmpty());
        actionsButton.setVisible(!actionsMenu.isMenuEmpty());
        notesButton.setVisible(value instanceof HasNotesAndAttachments);

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
                new VersionSelectorDialog<V>(CrmViewerViewImplBase.this, entityVersionClass, getForm().getValue().getPrimaryKey()) {
                    @Override
                    public void onClickOk() {
                        getPresenter().view(getSelectedVersionId());
                    }

                    @Override
                    protected AbstractListCrudService<V> getSelectService() {
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
                new VersionSelectorDialog<V>(CrmViewerViewImplBase.this, entityVersionClass, getForm().getValue().getPrimaryKey()) {
                    @Override
                    public void onClickOk() {
                        getPresenter().view(getSelectedVersionId());
                    }

                    @Override
                    protected AbstractListCrudService<V> getSelectService() {
                        return entityVersionService;
                    }
                }.show();
            }
        });
    }
}
