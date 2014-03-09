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
 * @version $Id$
 */
package com.pyx4j.tester.svg.client.ui;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.tester.svg.client.config.ChartTestConfiguration;
import com.pyx4j.tester.svg.client.config.ChartXYTestConfiguration;

public class ChartTestSelectorPanel extends SimplePanel {

    private final ChartXYTestForm form;

    public ChartTestSelectorPanel() {
        form = new ChartXYTestForm();
        form.initContent();
        form.populateNew();
        this.setWidget(form.asWidget());
    }

    public static Widget create() {
        VerticalPanel content = new VerticalPanel();

        return content;
    }

    public void setConfiguration(ChartTestConfiguration testConfiguration) {
        form.setValue((ChartXYTestConfiguration) testConfiguration);
    }

    @SuppressWarnings("unchecked")
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<? extends ChartTestConfiguration> handler) {
        return form.addValueChangeHandler((ValueChangeHandler<ChartXYTestConfiguration>) handler);
    }

}
