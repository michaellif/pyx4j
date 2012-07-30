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
 * Created on Jul 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

public abstract class SimpleReportSettingsForm<E extends ReportSettings> extends CEntityForm<E> implements IReportSettingsForm<E> {

    private static final I18n i18n = I18n.get(SimpleReportSettingsForm.class);

    private ApplyCallback<E> applyCallback;

    private IsWidget simpleSettingsPanel;

    public SimpleReportSettingsForm(Class<E> clazz, boolean hasAdvancedSettings) {
        super(clazz);
    }

    @Override
    public final IsWidget createContent() {
        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(simpleSettingsPanel = createSimpleSettingsPanel());
        contentPanel.add(new Button(i18n.tr("Apply", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (applyCallback != null) {
                    applyCallback.apply(getValue());
                }
            }
        })));
        return contentPanel;
    }

    public abstract IsWidget createSimpleSettingsPanel();

    @Override
    public void setOnApplySettingsCallback(ApplyCallback<E> applyCallback) {
        this.applyCallback = applyCallback;
    }

}
