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
package com.pyx4j.site.client.backoffice.ui.prime.form;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.ILooseVersioning;
import com.pyx4j.entity.core.IVersionedEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.forms.client.ui.CrudDebugId;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeEditorView.IPrimeEditorPresenter;
import com.pyx4j.site.client.ui.layout.LayoutSystem;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.MenuBar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class AbstractPrimeEditorView<E extends IEntity> extends AbstractPrimeFormView<E, IPrimeEditorPresenter> implements IPrimeEditorView<E> {

    private static final I18n i18n = I18n.get(AbstractPrimeEditorView.class);

    private final Button btnApply;

    private final Button btnSave;

    private final Button btnCancel;

    protected EditMode mode;

    public AbstractPrimeEditorView() {
        this(LayoutSystem.LayoutPanels);
    }

    public AbstractPrimeEditorView(LayoutSystem layoutSystem) {
        super(layoutSystem);

        btnSave = new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {
                save();
            }
        });
        btnSave.ensureDebugId(CrudDebugId.Crud_Save.debugId());
        addHeaderToolbarItem(btnSave);

        btnApply = new Button(i18n.tr("Apply"), new Command() {
            @Override
            public void execute() {
                apply();
            }
        });
        addHeaderToolbarItem(btnApply);

        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                getPresenter().cancel();
            }
        });
        addHeaderToolbarItem(btnCancel);

        enableButtons(false);
    }

    private void apply() {
        getForm().setVisitedRecursive();
        if (!getForm().isValid()) {
            showValidationDialog();
        } else {
            getPresenter().apply();
        }
    }

    private void save() {
        getForm().setVisitedRecursive();
        if (!getForm().isValid()) {
            showValidationDialog();
        } else {
            getPresenter().save();
        }
    }

    private void saveAsNew() {
        getForm().setVisitedRecursive();
        if (!getForm().isValid()) {
            showValidationDialog();
        } else {
            getForm().setValue((E) VersionedEntityUtils.createNextVersion((IVersionedEntity<?>) getForm().getValue()));
            getPresenter().save();
        }
    }

    @Override
    protected void setForm(PrimeEntityForm<E> form) {
        super.setForm(form);

        this.getForm().addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                enableButtons(true);
            }
        });
    }

    @Override
    public void populate(E value) {
        super.populate(value);

        enableButtons(false);

        if (value instanceof ILooseVersioning && value.getPrimaryKey() != null) {
            MenuBar menu = new MenuBar();
            menu.addItem(i18n.tr("Save as New Version"), new Command() {
                @Override
                public void execute() {
                    saveAsNew();
                }
            });
            menu.addItem(i18n.tr("Save Current"), new Command() {
                @Override
                public void execute() {
                    save();
                }
            });
            btnSave.setMenu(menu);
        } else {
            btnSave.setMenu(null);
        }

        if (EditMode.newItem.equals(mode)) {
            getForm().setActiveFirstTab();
        }
    }

    @Override
    protected void updateCaption() {
        if (EditMode.newItem.equals(mode)) {
            setCaption(i18n.tr("New {0}", getEntityBaseName()));
        } else {
            super.updateCaption();
        }
    }

    @Override
    public void setEditMode(EditMode mode) {
        this.mode = mode;
    }

    protected void enableButtons(boolean enable) {
//
// TODO Currently buttons are enabled always - more precise form dirty-state mechanics should be implemented!..
//
//        btnApply.setEnabled(enable);
//        btnSave.setEnabled(enable);
    }

    @Override
    public IPrimeEditorView.IPrimeEditorPresenter getPresenter() {
        return super.getPresenter();
    }

    @Override
    public boolean onSaveFail(Throwable caught) {
        if (caught instanceof UniqueConstraintUserRuntimeException) {
            showErrorDialog(caught.getMessage());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isDirty() {
        return getForm().isDirty();
    }

    protected void showErrorDialog(String message) {
        MessageDialog.error(i18n.tr("Error"), message);
    }

    protected void showValidationDialog() {
        MessageDialog.error(i18n.tr("Error"), i18n.tr("There has been an error. Please check your data and try again."));
    }

    protected void setApplyButtonVisible(boolean enabled) {
        btnApply.setEnabled(enabled);
        btnApply.setVisible(enabled);
    }

    protected void setCancelButtonVisible(boolean enabled) {
        btnCancel.setEnabled(enabled);
        btnCancel.setVisible(enabled);
    }

    protected void setBtnCancelCaption(String caption) {
        btnCancel.setCaption(caption);
    }

    protected void setBtnApplyCaption(String caption) {
        btnApply.setCaption(caption);
    }

    protected void setBtnSaveCaption(String caption) {
        btnSave.setCaption(caption);
    }
}