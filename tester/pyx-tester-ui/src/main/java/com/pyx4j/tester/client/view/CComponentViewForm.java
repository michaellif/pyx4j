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
 * Created on Nov 3, 2011
 * @author michaellif
 */
package com.pyx4j.tester.client.view;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.CComponentProperties;
import com.pyx4j.tester.client.view.form.EntityIFormWithoutLists;

public class CComponentViewForm extends CForm<CComponentProperties> {

    private static final I18n i18n = I18n.get(EntityIFormWithoutLists.class);

    private CComponent<?, ?, ?, ?> component;

    public CComponentViewForm() {
        super(CComponentProperties.class);

        component = null;
    }

    @Override
    protected IsWidget createContent() {

        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("CComponent Properties"));

        formPanel.append(Location.Dual, proto().title()).decorate();

        formPanel.append(Location.Dual, proto().componentValue()).decorate();

        FieldDecorator decorator = new FieldDecorator.Builder().labelWidth("10em").build();
        formPanel.append(Location.Dual, inject(proto().mandatory(), decorator));
        get(proto().mandatory()).addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setMandatory(((Boolean) event.getValue()).booleanValue());
            }

        });

        decorator = new FieldDecorator.Builder().labelWidth("10em").build();
        formPanel.append(Location.Dual, inject(proto().enabled(), decorator));
        get(proto().enabled()).addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setEnabled(((Boolean) event.getValue()).booleanValue());
            }

        });

        decorator = new FieldDecorator.Builder().labelWidth("10em").build();
        formPanel.append(Location.Dual, inject(proto().editable(), decorator));
        get(proto().editable()).addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setEditable(((Boolean) event.getValue()).booleanValue());
            }

        });

        decorator = new FieldDecorator.Builder().labelWidth("10em").build();
        formPanel.append(Location.Dual, inject(proto().visible(), decorator));
        get(proto().visible()).addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setVisible(((Boolean) event.getValue()).booleanValue());
            }

        });

        decorator = new FieldDecorator.Builder().labelWidth("10em").build();
        formPanel.append(Location.Dual, inject(proto().viewable(), decorator));
        get(proto().viewable()).addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setViewable(((Boolean) event.getValue()).booleanValue());
            }

        });

        decorator = new FieldDecorator.Builder().labelWidth("10em").build();
        formPanel.append(Location.Dual, inject(proto().valid(), decorator));
        get(proto().valid()).setViewable(true);

        formPanel.append(Location.Left, proto().toolTip()).decorate();

        return formPanel;
    }

    public void setComponent(CComponent component) {
        this.component = component;
    }
}
