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
 * @version $Id$
 */
package com.pyx4j.tester.client.view;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.CComponentProperties;
import com.pyx4j.tester.client.view.form.EntityIFormWithoutLists;

public class CComponentViewForm extends CEntityForm<CComponentProperties> {

    private static final I18n i18n = I18n.get(EntityIFormWithoutLists.class);

    private CComponent component;

    public CComponentViewForm() {
        super(CComponentProperties.class);

        component = null;
    }

    @Override
    protected IsWidget createContent() {

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("CComponent Properties"));

        main.setWidget(++row, 0, inject(proto().title(), new WidgetDecorator.Builder().labelWidth("10em").build()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, inject(proto().componentValue(), new WidgetDecorator.Builder().labelWidth("10em").build()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        WidgetDecorator decorator = new WidgetDecorator.Builder().labelWidth("10em").build();
        decorator.getComponent().addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setMandatory(((Boolean) event.getValue()).booleanValue());
            }

        });
        main.setWidget(++row, 0, inject(proto().mandatory(), decorator));

        decorator = new WidgetDecorator.Builder().labelWidth("10em").build();
        decorator.getComponent().addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setEnabled(((Boolean) event.getValue()).booleanValue());
            }

        });
        main.setWidget(++row, 0, inject(proto().enabled(), decorator));

        decorator = new WidgetDecorator.Builder().labelWidth("10em").build();
        decorator.getComponent().addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setEditable(((Boolean) event.getValue()).booleanValue());
            }

        });
        main.setWidget(++row, 0, inject(proto().editable(), decorator));

        decorator = new WidgetDecorator.Builder().labelWidth("10em").build();
        decorator.getComponent().addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setVisible(((Boolean) event.getValue()).booleanValue());
            }

        });
        main.setWidget(++row, 0, inject(proto().visible(), decorator));

        decorator = new WidgetDecorator.Builder().labelWidth("10em").build();
        decorator.getComponent().addValueChangeHandler(new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {

                if (component != null)
                    component.setViewable(((Boolean) event.getValue()).booleanValue());
            }

        });
        main.setWidget(++row, 0, inject(proto().viewable(), decorator));

        decorator = new WidgetDecorator.Builder().labelWidth("10em").build();
        decorator.getComponent().setViewable(true);
        main.setWidget(++row, 0, inject(proto().valid(), decorator));

        main.setWidget(++row, 0, inject(proto().toolTip(), new WidgetDecorator.Builder().labelWidth("10em").build()));

        return main;
    }

    public void setComponent(CComponent component) {
        this.component = component;
    }
}
