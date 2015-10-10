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
import com.pyx4j.forms.client.ui.CComboBoxBoolean;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CRadioGroupBoolean;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.CRadioGroupInteger;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.CEntitySelectorHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.domain.test.EntityI.Enum1;
import com.pyx4j.tester.client.domain.test.EntityV;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.OptionGroup.Layout;

public class EntityIFormWithoutLists extends CForm<EntityI> {

    private static final I18n i18n = I18n.get(EntityIFormWithoutLists.class);

    public EntityIFormWithoutLists() {
        super(EntityI.class);
    }

    @Override
    protected IsWidget createContent() {

        TesterFormPanel formPanel = new TesterFormPanel(this);

        formPanel.h1(i18n.tr("Main Form"));

        // Personal Identity
        formPanel
                .append(Location.Left, proto().personalId(),
                        new CPersonalIdentityField<IPersonalIdentity>(IPersonalIdentity.class, new PersonalIdentityFormatter("XXX-XXX-xxx;XX-XX-xxxx")))
                .decorate();

        Anchor anchor = new Anchor(i18n.tr("Terms and Conditions"), new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }
        });

        formPanel.append(Location.Left, proto().signature1(), new CSignature(anchor)).decorate();

        formPanel.append(Location.Left, proto().hue()).decorate();
        formPanel.append(Location.Left, proto().color()).decorate();

        formPanel.append(Location.Left, proto().textBox()).decorate();
        formPanel.append(Location.Right, proto().integerBox()).decorate();

        formPanel.append(Location.Left, proto().enumBox()).decorate();

        Collection<String> options = new ArrayList<String>();
        for (int i = 0; i < 200; i++) {
            options.add("string" + i);
        }

        formPanel.append(Location.Left, proto().datePicker()).decorate();

        formPanel.append(Location.Right, proto().monthPicker()).decorate();

        formPanel.append(Location.Left, proto().optionalTimePicker()).decorate();

        formPanel.append(Location.Right, proto().phone()).decorate();

        formPanel.append(Location.Left, proto().email()).decorate();

        formPanel.append(Location.Right, proto().money()).decorate();

        formPanel.append(Location.Left, proto().percent1()).decorate();

        formPanel.append(Location.Right, proto().percent2()).decorate();

        formPanel.append(Location.Left, proto().booleanRadioGroupHorizontal()).decorate();
        formPanel.append(Location.Right, proto().enumRadioGroupHorizontal()).decorate();

        CRadioGroupBoolean rgb = new CRadioGroupBoolean(Layout.VERTICAL);
        formPanel.append(Location.Left, proto().booleanRadioGroupVertical(), rgb).decorate();

        CRadioGroupEnum<Enum1> rge = new CRadioGroupEnum<Enum1>(Enum1.class, Layout.VERTICAL);
        formPanel.append(Location.Right, proto().enumRadioGroupVertical(), rge).decorate();

        HashMap<Integer, String> rbgoptions = new HashMap<Integer, String>();
        for (int i = 0; i < 4; i++) {
            rbgoptions.put(i, "Value" + i);
        }

        CRadioGroupInteger rgi = new CRadioGroupInteger(Layout.HORIZONTAL, rbgoptions);
        formPanel.append(Location.Left, proto().intRadioGroupHorizontal(), rgi).decorate();

        rgi = new CRadioGroupInteger(Layout.VERTICAL, rbgoptions);
        formPanel.append(Location.Right, proto().intRadioGroupVertical(), rgi).decorate();

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
        formPanel.append(Location.Left, proto().entityComboBox(), cmbEntity).decorate();

        formPanel.append(Location.Left, proto().entitySelectorBox(), new CEntitySelectorHyperlink<EntityV>() {
            @Override
            protected AppPlace getTargetPlace() {
                return null;
            }

            @Override
            protected EntitySelectorTableDialog<EntityV> getSelectorDialog() {
                return null;
            }
        }).decorate();

        CComboBoxBoolean cmbBoolean = new CComboBoxBoolean();
        cmbBoolean.setOptions(Arrays.asList(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
        formPanel.append(Location.Right, proto().booleanComboBox(), cmbBoolean).decorate();

        formPanel.append(Location.Left, proto().checkBox()).decorate();

        formPanel.append(Location.Left, proto().enterPassword()).decorate();

        formPanel.append(Location.Left, proto().confirmPassword()).decorate();

        formPanel.append(Location.Left, proto().textArea()).decorate();

        formPanel.append(Location.Left, proto().richTextArea()).decorate();

        return formPanel;
    }

    @Override
    public void addValidations() {
        AbstractComponentValidator<String> passwordConfirmValidator = new AbstractComponentValidator<String>() {

            @Override
            public BasicValidationError isValid() {
                return CommonsStringUtils.equals(get(proto().enterPassword()).getValue(), get(proto().confirmPassword()).getValue()) ? null
                        : new BasicValidationError(getCComponent(), "Passwords do not match.");
            }

        };
        get(proto().confirmPassword()).addComponentValidator(passwordConfirmValidator);

        get(proto().enterPassword()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().confirmPassword())));

    }
}