/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Jul 25, 2013
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.wizard;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

public class WizardDecorator<E extends IEntity> extends FormDecorator<E> {

    private static final I18n i18n = I18n.get(WizardDecorator.class);

    public static enum WizardDebugIds implements IDebugId {

        WizardPrevious, WizardNext, WizardSave, WizardCancel;

        @Override
        public String debugId() {
            return name();
        }
    }

    private final Button btnPrevious;

    private final Button btnNext;

    private final Button btnSave;

    private final Button btnCancel;

    public WizardDecorator() {

        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                getComponent().cancel();
            }
        });
        btnCancel.setDebugId(WizardDebugIds.WizardCancel);
        addFooterToolbarWidget(btnCancel);

        btnSave = new Button(i18n.tr("Save"), new Command() {
            @Override
            public void execute() {
                getComponent().save();
                calculateButtonsState();
            }
        });
        btnSave.setDebugId(WizardDebugIds.WizardSave);
        addFooterToolbarWidget(btnSave);
        btnSave.setVisible(false); // invisible by default!..

        btnPrevious = new Button(i18n.tr("Previous"), new Command() {
            @Override
            public void execute() {
                getComponent().previousStep();
                calculateButtonsState();
            }
        });
        btnPrevious.setDebugId(WizardDebugIds.WizardPrevious);
        addFooterToolbarWidget(btnPrevious);

        // This name will change in each step.
        btnNext = new Button(i18n.tr("Next"), new Command() {
            @Override
            public void execute() {
                if (getComponent().isLast()) {
                    getComponent().finish();
                } else {
                    getComponent().nextStep();
                    calculateButtonsState();
                }
            }
        });
        btnNext.setDebugId(WizardDebugIds.WizardNext);
        addFooterToolbarWidget(btnNext);

        setWidth("100%");
    }

    @Override
    public CEntityWizard<E> getComponent() {
        return (CEntityWizard<E>) super.getComponent();
    }

    public Button getBtnPrevious() {
        return btnPrevious;
    }

    // Can't change the name, use WizardStep constructor.
    // TODO The same button used in final submit. maybe need to changed
    public Button getBtnNext() {
        return btnNext;
    }

    public Button getBtnSave() {
        return btnSave;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    @Override
    public void init(CForm<E> component) {
        super.init(component);
        calculateButtonsState();
    }

    public void calculateButtonsState() {
        //If Wizard already initiated.
        if (getComponent() != null && getComponent().getSelectedStep() != null) {
            btnNext.setCaption(getComponent().getSelectedStep().getNextButtonCaption());
            btnPrevious.setEnabled(!getComponent().isFirst());
        }
    }

}
