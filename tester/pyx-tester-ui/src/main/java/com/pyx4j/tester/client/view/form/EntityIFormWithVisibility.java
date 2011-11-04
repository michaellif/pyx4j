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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.ui.TesterWidgetDecorator;

public class EntityIFormWithVisibility extends CEntityEditor<EntityI> {

    private static I18n i18n = I18n.get(EntityIFormWithVisibility.class);

    public EntityIFormWithVisibility() {
        super(EntityI.class);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Visibility Test"));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalEnum())));
        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextI())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextII())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextI())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextII())));

        return main;
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CComboBox<EntityI.Enum1> type = (CComboBox<EntityI.Enum1>) get(proto().optionalEnum());
        type.addValueChangeHandler(new ValueChangeHandler<EntityI.Enum1>() {
            @Override
            public void onValueChange(ValueChangeEvent<EntityI.Enum1> event) {
                setVisibility(event.getValue());
            }
        });
    }

    @Override
    public void populate(EntityI value) {
        super.populate(value);
        setVisibility(value.optionalEnum().getValue());
    }

    private void setVisibility(EntityI.Enum1 type) {
        get(proto().optionalTextI()).setVisible(false);
        get(proto().optionalTextII()).setVisible(false);
        get(proto().mandatoryTextI()).setVisible(false);
        get(proto().mandatoryTextII()).setVisible(false);
        switch (type) {
        case Value0:
            get(proto().optionalTextI()).setVisible(true);
            break;
        case Value1:
            get(proto().optionalTextII()).setVisible(true);
            break;
        case Value2:
            get(proto().mandatoryTextI()).setVisible(true);
            break;
        case Value3:
            get(proto().mandatoryTextII()).setVisible(true);
            break;

        default:
            break;
        }

    }
}
