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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.CEntityListBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.test.EntityI;
import com.pyx4j.tester.client.domain.test.EntityIV;
import com.pyx4j.tester.client.ui.TesterWidgetDecorator;

public class EntityIFormWithoutLists extends CEntityEditor<EntityI> {

    private static I18n i18n = I18n.get(EntityIFormWithoutLists.class);

    public EntityIFormWithoutLists() {
        super(EntityI.class);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("Main Form"));

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextI())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextII())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextI())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextII())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextAreaI())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTextAreaII())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextAreaI())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTextAreaII())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalInteger())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryInteger())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().checkBox())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalPassword())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryPassword())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalEnum())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryEnum())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalDatePicker())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryDatePicker())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalTimePicker())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryTimePicker())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalSingleMonthDatePicker())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatorySingleMonthDatePicker())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalPhone())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryPhone())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalEmail())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryEmail())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().optionalMoney())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatoryMoney())));

        main.setHR(++row, 0, 1);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().entityIVListNotOwned(), new CEntityListBox<EntityIV>())));

        return main;
    }
}