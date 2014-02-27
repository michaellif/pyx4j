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
 * Created on Jul 31, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.DefaultPaneTheme;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Toolbar;

public abstract class ReportSettingsFormControlBar extends Composite {

    private static final I18n i18n = I18n.get(ReportSettingsFormControlBar.class);

    private Button apply;

    private Anchor modeToggle;

    private final Toolbar controlPanelToolbar;

    private boolean isAdvanced;

    public ReportSettingsFormControlBar() {

        isAdvanced = false;

        controlPanelToolbar = new Toolbar();
        controlPanelToolbar.addItem(modeToggle = new Anchor(i18n.tr("advanced"), new Command() {
            @Override
            public void execute() {
                isAdvanced = !isAdvanced;
                updateModeToggleButtonLabel();
                onSettingsModeToggled(isAdvanced);
            }
        }));
        modeToggle.getElement().getStyle().setMarginRight(2, Unit.EM);

        controlPanelToolbar.addItem(apply = new Button(i18n.tr("Apply"), new Command() {
            @Override
            public void execute() {
                onApply();
            }
        }));

        SimplePanel toolbarHolder = new SimplePanel();
        toolbarHolder.setStyleName(DefaultPaneTheme.StyleName.FooterToolbar.name());
        toolbarHolder.setWidget(controlPanelToolbar);
        initWidget(toolbarHolder);
    }

    public final void enableSettingsModeToggle(boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
        modeToggle.setVisible(true);
        updateModeToggleButtonLabel();
    }

    public void disableModeToggle() {
        modeToggle.setVisible(false);
    }

    public void setEnabled(boolean isEnabled) {
        modeToggle.setEnabled(isEnabled);
        apply.setEnabled(isEnabled);
    }

    public abstract void onApply();

    public abstract void onSettingsModeToggled(boolean isAdvanced);

    private void updateModeToggleButtonLabel() {
        modeToggle.setHTML(new SafeHtmlBuilder().appendEscaped(isAdvanced ? i18n.tr("hide") : i18n.tr("advanced")).toSafeHtml());
    }
}
