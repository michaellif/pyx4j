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

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityII;
import com.pyx4j.tester.client.ui.FormDecoratorBuilder;

public class EntityIIFormWithVisibilityChange extends CEntityForm<EntityII> {

    private static final I18n i18n = I18n.get(EntityIIFormWithVisibilityChange.class);

    public EntityIIFormWithVisibilityChange() {
        super(EntityII.class);
    }

    @Override
    protected IsWidget createContent() {

        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Form component Visibility Test"));
        main.setWidget(++row, 0, inject(proto().optionalEnum(), new FormDecoratorBuilder().build()));
        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, inject(proto().optionalTextI(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().optionalTextII(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().mandatoryTextI(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().mandatoryTextII(), new FormDecoratorBuilder().build()));

        main.setWidget(++row, 0, inject(proto().checkBox(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().optionalPassword(), new FormDecoratorBuilder().build()));
        main.setWidget(++row, 0, inject(proto().mandatoryPassword(), new FormDecoratorBuilder().build()));

        return main;
    }

    @Override
    public void addValidations() {
        @SuppressWarnings("unchecked")
        CComboBox<EntityII.Enum1> type = (CComboBox<EntityII.Enum1>) get(proto().optionalEnum());
        type.addValueChangeHandler(new ValueChangeHandler<EntityII.Enum1>() {
            @Override
            public void onValueChange(ValueChangeEvent<EntityII.Enum1> event) {
                setVisibility(event.getValue());
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setVisibility(getValue().optionalEnum().getValue());
    }

    private void setVisibility(EntityII.Enum1 type) {

        if (type == null) {
            type = EntityII.Enum1.Value0;
        }

        get(proto().optionalTextI()).setVisible(false);
        get(proto().optionalTextII()).setVisible(false);
        get(proto().mandatoryTextI()).setVisible(false);
        get(proto().mandatoryTextII()).setVisible(false);
        get(proto().checkBox()).setVisible(false);
        get(proto().optionalPassword()).setVisible(false);
        get(proto().mandatoryPassword()).setVisible(false);

        switch (type) {
        case Value0:
            get(proto().optionalTextI()).setVisible(true);
            get(proto().optionalPassword()).setVisible(true);
            break;
        case Value1:
            get(proto().optionalTextII()).setVisible(true);
            get(proto().checkBox()).setVisible(true);
            break;
        case Value2:
            get(proto().mandatoryTextI()).setVisible(true);
            get(proto().mandatoryPassword()).setVisible(true);
            break;
        case Value3:
            get(proto().mandatoryTextII()).setVisible(true);
            get(proto().checkBox()).setVisible(true);
            break;

        default:
            break;
        }

    }
}
