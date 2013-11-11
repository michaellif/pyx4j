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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.wizardstep;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.form.FormDecorator;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

public class WizardStepDecorator<E extends IEntity> extends FormDecorator<E, CEntityForm<E>> {

    private static final I18n i18n = I18n.get(WizardStepDecorator.class);

    private final Button btnPrevious;

    private final Button btnNext;

    private final Button btnCancel;

    public WizardStepDecorator(String btnNextCaption) {

        btnCancel = new Button(i18n.tr("Cancel"), new Command() {
            @Override
            public void execute() {
                onCancel();
            }
        });
        addFooterToolbarButton(btnCancel);

        btnPrevious = new Button(i18n.tr("Previous"), new Command() {
            @Override
            public void execute() {
                onPrevious();
            }
        });
        addFooterToolbarButton(btnPrevious);

        btnNext = new Button(btnNextCaption, new Command() {
            @Override
            public void execute() {
                onNext();
            }
        });
        addFooterToolbarButton(btnNext);

        setWidth("100%");
    }

    public Button getBtnPrevious() {
        return btnPrevious;
    }

    public Button getBtnNext() {
        return btnNext;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    protected void onNext() {
    }

    protected void onPrevious() {
    }

    protected void onCancel() {
    }
}
