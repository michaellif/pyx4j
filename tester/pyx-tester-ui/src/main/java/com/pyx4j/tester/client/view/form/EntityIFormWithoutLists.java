/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.client.view.form;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.forms.client.ui.CAbstractSuggestBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CSuggestStringBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.ui.TesterWidgetDecorator;

public class EntityIFormWithoutLists extends CEntityEditor<EntityI> {

    private static final I18n i18n = I18n.get(EntityIFormWithoutLists.class);

    public EntityIFormWithoutLists() {
        super(EntityI.class);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Main Form"));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextII())));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatoryTextI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextII())));

        //main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextAreaI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextAreaII())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextAreaI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextAreaII())));

        //main.setHR(++row, 0, 1);

        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().optionalIntegerI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryIntegerI())));

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalIntegerII())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryIntegerII())));

        //main.setHR(++row, 0, 1);

        //main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryPasswordI())));

        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatoryPasswordII())));

        //main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalEnumI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryEnumI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalEnumII())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryEnumII())));

        //main.setHR(++row, 0, 1);

        Collection<String> options = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            options.add("string" + i);
        }

        CAbstractSuggestBox<String> box = new CSuggestStringBox();
        box.setOptions(options);
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalSuggest(), box)));

        box = new CSuggestStringBox();
        box.setOptions(options);
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatorySuggest(), box)));

        //main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalDatePicker())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryDatePicker())));

        //main.setHR(++row, 0, 1);

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTimePicker())));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatoryTimePicker())));

        //main.setHR(++row, 0, 1);

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalSingleMonthDatePicker())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatorySingleMonthDatePicker())));

        //main.setHR(++row, 0, 1);

        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().optionalPhone())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryPhone())));

//        main.setHR(++row, 0, 1);

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalEmail())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryEmail())));

//        main.setHR(++row, 0, 1);

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalMoney())));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatoryMoney())));

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().checkBox())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        //main.setHR(++row, 0, 1);

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().entityIVListNotOwned(), new CEntityListBox<EntityIV>())));

        return main;
    }

    @Override
    public void addValidations() {
        EditableValueValidator<String> passwordConfirmValidator = new EditableValueValidator<String>() {

            @Override
            public ValidationFailure isValid(CComponent<String, ?> component, String value) {
                return CommonsStringUtils.equals(get(proto().mandatoryPasswordI()).getValue(), get(proto().mandatoryPasswordII()).getValue()) ? null
                        : new ValidationFailure("Passwords do not match.");
            }

        };
        get(proto().mandatoryPasswordII()).addValueValidator(passwordConfirmValidator);

        get(proto().mandatoryPasswordI()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().mandatoryPasswordII())));

    }
}