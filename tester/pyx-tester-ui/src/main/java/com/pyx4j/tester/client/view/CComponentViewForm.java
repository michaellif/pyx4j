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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.tester.client.domain.CComponentProperties;
import com.pyx4j.tester.client.ui.TesterWidgetDecorator;
import com.pyx4j.tester.client.view.form.EntityIFormWithoutLists;

public class CComponentViewForm extends CEntityEditor<CComponentProperties> {

    private static I18n i18n = I18n.get(EntityIFormWithoutLists.class);

    public CComponentViewForm() {
        super(CComponentProperties.class);
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 1, i18n.tr("CComponent Properties"));

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().title())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().componentValue())));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().mandatory())));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().enabled())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().editable())));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().visible())));
        main.setWidget(++row, 0, new TesterWidgetDecorator(inject(proto().valid())));
        main.setWidget(row, 1, new TesterWidgetDecorator(inject(proto().toolTip())));

        return main;
    }
}
