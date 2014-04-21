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
import java.util.HashMap;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.PersonalIdentityFormatter;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.IPersonalIdentity;
import com.pyx4j.forms.client.ui.CAbstractSuggestBox;
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CEntityListBox;
import com.pyx4j.forms.client.ui.CListBox.SelectionMode;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CRadioGroupBoolean;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CRadioGroupInteger;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.CSuggestStringBox;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.client.ui.prime.misc.CEntitySelectorHyperlink;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.domain.test.EntityI.Enum1;
import com.pyx4j.tester.client.domain.test.EntityIII;
import com.pyx4j.tester.client.domain.test.EntityV;
import com.pyx4j.tester.client.ui.FormDecoratorBuilder;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.RadioGroup.Layout;

public class EntityIFormWithoutLists extends CEntityForm<EntityI> {

    private static final I18n i18n = I18n.get(EntityIFormWithoutLists.class);

    public EntityIFormWithoutLists() {
        super(EntityI.class);
    }

    @Override
    protected IsWidget createContent() {

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Main Form"));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        // list box
        CEntityListBox<EntityIII> listBox = new CEntityListBox<EntityIII>(SelectionMode.SINGLE_PANEL);
        EntityIII e1 = EntityFactory.create(EntityIII.class);
        e1.stringMember().setValue("OneOneOneOneOneOneOneOneOneOneOneOne");
        e1.integerMember().setValue(1);
        EntityIII e2 = EntityFactory.create(EntityIII.class);
        e2.stringMember().setValue("Two");
        e2.integerMember().setValue(2);
        EntityIII e3 = EntityFactory.create(EntityIII.class);
        e3.stringMember().setValue("Three");
        e3.integerMember().setValue(3);
        listBox.setOptions(Arrays.asList(e1, e2, e3));
        listBox.setDecorator(new FormDecoratorBuilder().build());

        main.setWidget(++row, 0, inject(proto().entityIIIList(), listBox));

        CEntityListBox<EntityIII> setBox = new CEntityListBox<EntityIII>(SelectionMode.TWO_PANEL);
        setBox.setOptions(Arrays.asList(e1, e2, e3));
        setBox.setDecorator(new FormDecoratorBuilder().build());

        main.setWidget(++row, 0, inject(proto().entityIIIList2(), setBox));

        // Personal Identity
        main.setWidget(
                ++row,
                0,
                inject(proto().personalId(), new CPersonalIdentityField<IPersonalIdentity>(IPersonalIdentity.class, new PersonalIdentityFormatter(
                        "XXX-XXX-xxx;XX-XX-xxxx")), new FormDecoratorBuilder().build()));

        Anchor anchor = new Anchor(i18n.tr("Terms and Conditions"), new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }
        });

        main.setWidget(++row, 0, inject(proto().signature1(), new CSignature(anchor), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().hue(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().color(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().textBox(), new FormDecoratorBuilder().build()));
        main.setWidget(row, 1, inject(proto().integerBox(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().enumBox(), new FormDecoratorBuilder().build()));

        Collection<String> options = new ArrayList<String>();
        for (int i = 0; i < 200; i++) {
            options.add("string" + i);
        }

        CAbstractSuggestBox<String> box = new CSuggestStringBox();
        box.setOptions(options);
        main.setWidget(row, 1, inject(proto().suggest(), box, new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().datePicker(), new FormDecoratorBuilder().build()));

        main.setWidget(row, 1, inject(proto().monthPicker(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().optionalTimePicker(), new FormDecoratorBuilder().build()));

        main.setWidget(row, 1, inject(proto().phone(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().email(), new FormDecoratorBuilder().build()));

        main.setWidget(row, 1, inject(proto().money(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().percent1(), new FormDecoratorBuilder().build()));

        main.setWidget(row, 1, inject(proto().percent2(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().booleanRadioGroupHorizontal(), new FormDecoratorBuilder().build()));
        main.setWidget(row, 1, inject(proto().enumRadioGroupHorizontal(), new FormDecoratorBuilder().build()));

        CRadioGroupBoolean rgb = new CRadioGroupBoolean(Layout.VERTICAL);
        main.setWidget(++row, 0, inject(proto().booleanRadioGroupVertical(), rgb, new FormDecoratorBuilder().build()));

        CRadioGroupEnum<Enum1> rge = new CRadioGroupEnum<Enum1>(Enum1.class, Layout.VERTICAL);
        main.setWidget(row, 1, inject(proto().enumRadioGroupVertical(), rge, new FormDecoratorBuilder().build()));

        HashMap<Integer, String> rbgoptions = new HashMap<Integer, String>();
        for (int i = 0; i < 4; i++) {
            rbgoptions.put(i, "Value" + i);
        }

        CRadioGroupInteger rgi = new CRadioGroupInteger(Layout.HORISONTAL, rbgoptions);
        main.setWidget(++row, 0, inject(proto().intRadioGroupHorizontal(), rgi, new FormDecoratorBuilder().build()));

        rgi = new CRadioGroupInteger(Layout.VERTICAL, rbgoptions);
        main.setWidget(row, 1, inject(proto().intRadioGroupVertical(), rgi, new FormDecoratorBuilder().build()));

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
        main.setWidget(++row, 0, inject(proto().entityComboBox(), cmbEntity, new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().entitySelectorBox(), new CEntitySelectorHyperlink<EntityV>() {
            @Override
            protected AppPlace getTargetPlace() {
                return null;
            }

            @Override
            protected EntitySelectorTableDialog<EntityV> getSelectorDialog() {
                return null;
            }
        }, new FormDecoratorBuilder().build()));

        CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
        cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
        main.setWidget(row, 1, inject(proto().booleanComboBox(), cmbBoolean, new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().checkBox(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().enterPassword(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().confirmPassword(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().textArea(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().richTextArea(), new FormDecoratorBuilder().build()));

        return main;
    }

    @Override
    public void addValidations() {
        AbstractComponentValidator<String> passwordConfirmValidator = new AbstractComponentValidator<String>() {

            @Override
            public FieldValidationError isValid() {
                return CommonsStringUtils.equals(get(proto().enterPassword()).getValue(), get(proto().confirmPassword()).getValue()) ? null
                        : new FieldValidationError(getComponent(), "Passwords do not match.");
            }

        };
        get(proto().confirmPassword()).addComponentValidator(passwordConfirmValidator);

        get(proto().enterPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().confirmPassword())));

    }
}