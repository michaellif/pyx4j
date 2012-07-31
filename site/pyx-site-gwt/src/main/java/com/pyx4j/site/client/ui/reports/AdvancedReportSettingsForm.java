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
 * Created on Jul 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;

public abstract class AdvancedReportSettingsForm<E extends ReportSettings & HasAdvancedSettings> extends CEntityForm<E> implements
        IAdvancedReportSettingsForm<E> {

    private Widget simpleSettingsPanel;

    private Widget advancedSettingsPanel;

    private HorizontalPanel contentPanel;

    public AdvancedReportSettingsForm(Class<E> clazz) {
        super(clazz);
    }

    @Override
    public final IsWidget createContent() {
        contentPanel = new HorizontalPanel();

        contentPanel.add(simpleSettingsPanel = createSimpleSettingsPanel());
        contentPanel.add(advancedSettingsPanel = createAdvancedSettingsPanel());
        contentPanel.setCellWidth(simpleSettingsPanel, "100%");
        contentPanel.setCellWidth(advancedSettingsPanel, "0%");
        advancedSettingsPanel.setVisible(false);

        return contentPanel;
    }

    @Override
    public void setAdvancedMode(boolean isAdvanced) {
        if (getValue() != null) {
            getValue().isInAdvancedMode().setValue(isAdvanced);
            updateView();
        }
    }

    public abstract Widget createSimpleSettingsPanel();

    public abstract Widget createAdvancedSettingsPanel();

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateView();
    }

    private void updateView() {
        advancedSettingsPanel.setVisible(getValue().isInAdvancedMode().isBooleanTrue());
        contentPanel.setCellWidth(advancedSettingsPanel, getValue().isInAdvancedMode().isBooleanTrue() ? "100%" : "0%");

        simpleSettingsPanel.setVisible(!getValue().isInAdvancedMode().isBooleanTrue());
        contentPanel.setCellWidth(simpleSettingsPanel, !getValue().isInAdvancedMode().isBooleanTrue() ? "100%" : "0%");
    }
}
