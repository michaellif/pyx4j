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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

public abstract class AdvancedReportSettingsForm<E extends ReportSettings & HasAdvancedSettings> extends CEntityForm<E> implements IReportSettingsForm<E> {

    private static final I18n i18n = I18n.get(AdvancedReportSettingsForm.class);

    private ApplyCallback<E> applyCallback = null;

    private Widget simpleSettingsPanel;

    private Widget advancedSettingsPanel;

    private Anchor advancedModeToggle;

    private Button applyButton;

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
        contentPanel.setCellWidth(advancedSettingsPanel, "1%");
        advancedSettingsPanel.setVisible(false);

        VerticalPanel controlPanel = new VerticalPanel();
        controlPanel.getElement().getStyle().setMarginLeft(2, Unit.EM);
        controlPanel.getElement().getStyle().setMarginRight(2, Unit.EM);

        controlPanel.add(applyButton = new Button(i18n.tr("Apply"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (applyCallback != null) {
                    applyCallback.apply(getValue());
                }
            }
        }));

        contentPanel.setCellHorizontalAlignment(applyButton, HasHorizontalAlignment.ALIGN_CENTER);

        applyButton.getElement().getStyle().setPaddingTop(0.5, Unit.EM);
        applyButton.getElement().getStyle().setPaddingBottom(0.5, Unit.EM);
        applyButton.getElement().getStyle().setPaddingLeft(4, Unit.EM);
        applyButton.getElement().getStyle().setPaddingRight(4, Unit.EM);

        applyButton.getElement().getStyle().setMarginTop(0.5, Unit.EM);
        applyButton.getElement().getStyle().setMarginBottom(0.5, Unit.EM);

        controlPanel.add(advancedModeToggle = new Anchor(i18n.tr("advanced..."), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (getValue() != null) {
                    getValue().isInAdvancedMode().setValue(!getValue().isInAdvancedMode().isBooleanTrue());
                    updateView();
                }
            }
        }));
        controlPanel.setCellHorizontalAlignment(advancedModeToggle, HasHorizontalAlignment.ALIGN_CENTER);

        contentPanel.add(controlPanel);

        return contentPanel;
    }

    @Override
    public void setOnApplySettingsCallback(ApplyCallback<E> applyCallback) {
        this.applyCallback = applyCallback;
    }

    public abstract Widget createSimpleSettingsPanel();

    public abstract Widget createAdvancedSettingsPanel();

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateView();
    }

    private void updateView() {
        advancedModeToggle.setHTML(new SafeHtmlBuilder().appendEscaped(
                getValue().isInAdvancedMode().isBooleanTrue() ? i18n.tr("hide...") : i18n.tr("advanced...")).toSafeHtml());
        advancedSettingsPanel.setVisible(getValue().isInAdvancedMode().isBooleanTrue());
        contentPanel.setCellWidth(advancedSettingsPanel, getValue().isInAdvancedMode().isBooleanTrue() ? "100%" : "0%");

        simpleSettingsPanel.setVisible(!getValue().isInAdvancedMode().isBooleanTrue());
        contentPanel.setCellWidth(simpleSettingsPanel, !getValue().isInAdvancedMode().isBooleanTrue() ? "100%" : "0%");

    }
}
