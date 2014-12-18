/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Mar 9, 2014
 * @author vlads
 */
package com.pyx4j.tester.svg.client.ui;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.tester.svg.client.config.ChartXYTestConfiguration;

public class ChartXYTestForm extends CForm<ChartXYTestConfiguration> {

    public ChartXYTestForm() {
        super(ChartXYTestConfiguration.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();

        String labelWidth = "7em";
        int row = 0;
        main.setWidget(row, 0, inject(proto().chartType(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 1, inject(proto().pointsType(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 2, inject(proto().points(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));

        row++;
        main.setWidget(row, 0, inject(proto().xValuesType(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 1, inject(proto().xFrom(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 2, inject(proto().xTo(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 3, inject(proto().xMultiplication(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));

        row++;
        main.setWidget(row, 0, inject(proto().yValuesType(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 1, inject(proto().yFrom(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 2, inject(proto().yTo(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));
        main.setWidget(row, 3, inject(proto().yMultiplication(), new FieldDecorator.Builder().componentWidth("8em").labelWidth(labelWidth).build()));

        return main;
    }
}
