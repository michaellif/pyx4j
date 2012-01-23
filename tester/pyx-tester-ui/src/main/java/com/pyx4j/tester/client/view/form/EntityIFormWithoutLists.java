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
import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CAbstractSuggestBox;
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CSuggestStringBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationFailure;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.domain.test.EntityV;
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

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().textBox())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextII())));
//        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatoryTextI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextII())));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().integerBox())));

        //main.setHR(++row, 0, 1);

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextAreaII())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextAreaI())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextAreaII())));

        //main.setHR(++row, 0, 1);

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryIntegerI())));

//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalIntegerII())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryIntegerII())));

        //main.setHR(++row, 0, 1);

        //main.setHR(++row, 0, 1);

        //main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().enumBox())));
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
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().suggest(), box)));

        box = new CSuggestStringBox();
        box.setOptions(options);
//        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatorySuggest(), box)));

        //main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().datePicker())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryDatePicker())));

        //main.setHR(++row, 0, 1);
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().singleMonthdatePicker())));
//      main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatorySingleMonthDatePicker())));

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTimePicker())));
//        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatoryTimePicker())));

        //main.setHR(++row, 0, 1);

        //main.setHR(++row, 0, 1);

        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().phone())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryPhone())));

//        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().email())));
//        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryEmail())));

//        main.setHR(++row, 0, 1);

        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().money())));
//        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().mandatoryMoney())));

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().booleanRadioGroup())));

        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().enumRadioGroup())));

        CEntityComboBox<EntityV> cmbEntity = new CEntityComboBox<EntityV>(EntityV.class);
        Collection<EntityV> entityoptions = new ArrayList<EntityV>();
        EntityV retVal;
        for (int i = 0; i < 10; i++) {
            retVal = EntityFactory.create(EntityV.class);

            retVal.name().setValue("Name" + i);
            retVal.stringValue().setValue("Value" + i);
            entityoptions.add(retVal);
        }

        cmbEntity.setOptions(entityoptions);
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().entityComboBox(), cmbEntity)));

        CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
        cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().booleanComboBox(), cmbBoolean)));

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().checkBox())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().enterPassword())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().confirmPassword())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().textArea())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().richTextArea())));
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
                return CommonsStringUtils.equals(get(proto().enterPassword()).getValue(), get(proto().confirmPassword()).getValue()) ? null
                        : new ValidationFailure("Passwords do not match.");
            }

        };
        get(proto().confirmPassword()).addValueValidator(passwordConfirmValidator);

        get(proto().enterPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().confirmPassword())));

    }
}