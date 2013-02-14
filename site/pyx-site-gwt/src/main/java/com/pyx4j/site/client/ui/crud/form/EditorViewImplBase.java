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
package com.pyx4j.site.client.ui.crud.form;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.ILooseVersioning;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.CrudEntityForm;
import com.pyx4j.site.client.ui.crud.FormViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class EditorViewImplBase<E extends IEntity> extends FormViewImplBase<E> implements IEditorView<E> {

    private static final I18n i18n = I18n.get(EditorViewImplBase.class);

    private IEditorView.Presenter presenter;

    protected String defaultCaption;

    protected final Button btnApply;

    protected final Button btnSave;

    protected EditMode mode;

    public EditorViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super();

        defaultCaption = (placeClass != null ? AppSite.getHistoryMapper().getPlaceInfo(placeClass).getCaption() : "");

        btnSave = new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {
                save();
            }
        });
        addFooterToolbarItem(btnSave);

        btnApply = new Button(i18n.tr("Apply"), new Command() {
            @Override
            public void execute() {
                apply();
            }
        });
        addFooterToolbarItem(btnApply);

        Anchor btnCancel = new Anchor(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                getPresenter().cancel();
            }
        });
        addFooterToolbarItem(btnCancel);

        enableButtons(false);
    }

    private void apply() {
        if (!getForm().isValid()) {
            getForm().setUnconditionalValidationErrorRendering(true);
            showValidationDialog();
        } else {
            getPresenter().apply();
        }
    }

    private void save() {
        if (!getForm().isValid()) {
            getForm().setUnconditionalValidationErrorRendering(true);
            showValidationDialog();
        } else {
            getPresenter().save();
        }
    }

    private void saveAsNew() {
        if (!getForm().isValid()) {
            getForm().setUnconditionalValidationErrorRendering(true);
            showValidationDialog();
        } else {
            getForm().setValue((E) VersionedEntityUtils.createNextVersion((IVersionedEntity<?>) getForm().getValue()));
            getPresenter().save();
        }
    }

    @Override
    protected void setForm(CrudEntityForm<? extends E> form) {
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
        enableButtons(false);
        if (value instanceof ILooseVersioning && value.getPrimaryKey() != null) {
            ButtonMenuBar menu = btnSave.createMenu();
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
            setCaption(defaultCaption + " " + i18n.tr("New Item..."));
            getForm().setActiveTab(0);
        } else {
            setCaption(defaultCaption + " " + (value == null ? "" : value.getStringView()));
        }

        super.populate(value);
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
    public void setPresenter(IEditorView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IEditorView.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public E getValue() {
        return getForm().getValue();
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
        MessageDialog.error(i18n.tr("Error"), getForm().getValidationResults().getValidationMessage(true, true));
    }
}