/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Sep 11, 2013
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.form;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.wizard.WizardDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

public class EditableFormDecorator<E extends IEntity> extends FormDecorator<E, CEntityForm<E>> {

    private static final I18n i18n = I18n.get(WizardDecorator.class);

    private final Button btnEdit;

    private final Button btnSave;

    private final Button btnCancel;

    public EditableFormDecorator() {

        btnEdit = new Button(i18n.tr("Edit"), new Command() {
            @Override
            public void execute() {
                onEdit();
            }
        });
        addHeaderToolbarWidget(btnEdit);

        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                onCancel();
            }
        });
        addFooterToolbarWidget(btnCancel);

        btnSave = new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {
                onSave();
            }
        });
        addFooterToolbarWidget(btnSave);

        setWidth("100%");

    }

    @Override
    public void setComponent(CEntityForm<E> component) {
        super.setComponent(component);
        component.addPropertyChangeHandler(new PropertyChangeHandler() {

            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName() == PropertyChangeEvent.PropertyName.viewable ||

                event.getPropertyName() == PropertyChangeEvent.PropertyName.editable ||

                event.getPropertyName() == PropertyChangeEvent.PropertyName.enabled) {

                    calculateButtonsState();
                }

            }

        });
        calculateButtonsState();

    }

    private void calculateButtonsState() {
        if (!getComponent().isViewable() && getComponent().isEditable() && getComponent().isEnabled()) {
            btnEdit.setVisible(false);
            getFooterPanel().setVisible(true);
        } else {
            btnEdit.setVisible(true);
            getFooterPanel().setVisible(false);
        }
    }

    protected void onEdit() {
    }

    protected void onCancel() {
    }

    protected void onSave() {
    }

    public Button getBtnEdit() {
        return btnEdit;
    }

    public Button getBtnSave() {
        return btnSave;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

}
