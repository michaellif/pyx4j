/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.wizard.creditcheck.components;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.css.IStyleDependent;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.domain.pmc.CreditCheckReportType;

public class CreditCheckReportTypeSelector extends Composite implements IFocusWidget, HasValueChangeHandlers<CreditCheckReportType> {

    private static final I18n i18n = I18n.get(CreditCheckReportTypeSelector.class);

    public enum Styles implements IStyleName {
        CreditCheckReportTypePanel, CreditCheckReportTypeLabel, CreditCheckReportTypeSetupFee, CreditCheckReportTypePerApplicantFee, CreditCheckReportDetailsLabel, CreditCheckPoweredByLabel, CreditCheckPoweredByLogo;
    }

    public enum StyleDependent implements IStyleDependent {
        Selected;
    }

    private class ReportTypeConditionsPanel extends Composite {

        private final Label setupFeeLabel;

        private final Label perApplicantFeeLabel;

        private final FlowPanel conditionsPanel;

        private final CreditCheckReportType reportType;

        private boolean selected;

        public ReportTypeConditionsPanel(CreditCheckReportType reportType, SafeHtml details, ImageResource providerLogo) {
            this.reportType = reportType;
            this.selected = false;
            FocusPanel wrapper = new FocusPanel();
            wrapper.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    CreditCheckReportTypeSelector.this.setCreditCheckReportType(ReportTypeConditionsPanel.this.reportType);
                }
            });
            conditionsPanel = new FlowPanel();
            conditionsPanel.setStyleName(Styles.CreditCheckReportTypePanel.name());
            wrapper.add(conditionsPanel);

            Label reportTypeLabel = new Label();
            reportTypeLabel.setStyleName(Styles.CreditCheckReportTypeLabel.name());
            reportTypeLabel.setText(reportType.toString());
            conditionsPanel.add(reportTypeLabel);

            Label poweredByLabel = new Label();
            poweredByLabel.setStyleName(Styles.CreditCheckPoweredByLabel.name());
            poweredByLabel.setText(i18n.tr("Powered By"));
            conditionsPanel.add(poweredByLabel);

            Image providerLogoImage = new Image(providerLogo);
            providerLogoImage.setStyleName(Styles.CreditCheckPoweredByLogo.name());
            conditionsPanel.add(providerLogoImage);

            perApplicantFeeLabel = new Label();
            perApplicantFeeLabel.setStyleName(Styles.CreditCheckReportTypePerApplicantFee.name());
            conditionsPanel.add(perApplicantFeeLabel);

            setupFeeLabel = new Label();
            setupFeeLabel.setStyleName(Styles.CreditCheckReportTypeSetupFee.name());
            conditionsPanel.add(setupFeeLabel);

            Label detailsLabel = new Label();
            detailsLabel.setStyleName(Styles.CreditCheckReportDetailsLabel.name());
            detailsLabel.setHTML(details);

            conditionsPanel.add(detailsLabel);

            initWidget(wrapper);
        }

        public void setSelected(boolean isSelected) {
            this.selected = isSelected;
            conditionsPanel.setStyleDependentName(StyleDependent.Selected.name(), isSelected);
        }

        public boolean isSelected() {
            return selected;
        }

        public void setFees(BigDecimal setupFee, BigDecimal perApplicantFee) {
            perApplicantFeeLabel.setText(i18n.tr("${0,number,#,##0.##} per Applicant", perApplicantFee));
            setupFeeLabel.setText(setupFee == null || setupFee.compareTo(BigDecimal.ZERO) == 0 ? i18n.tr("No Set Up Fee!") : i18n.tr(
                    "${0,number,#,##0.##} one-time Set-up fee", setupFee));
        }

    }

    private final Map<CreditCheckReportType, ReportTypeConditionsPanel> reportTypePanels;

    public CreditCheckReportTypeSelector(ReportTypeDetailsResources reportDetailsResources) {
        reportTypePanels = new HashMap<CreditCheckReportType, CreditCheckReportTypeSelector.ReportTypeConditionsPanel>();
        HorizontalPanel typeSelectorPanel = new HorizontalPanel();
        typeSelectorPanel.setSpacing(5);

        ReportTypeConditionsPanel panel = makePanel(CreditCheckReportType.RecomendationReport, reportDetailsResources.recommendationReportDescription()
                .getText(), reportDetailsResources.equifaxLogo());
        typeSelectorPanel.add(panel);
        typeSelectorPanel.setCellWidth(panel, "50%");

        panel = makePanel(CreditCheckReportType.FullCreditReport, reportDetailsResources.fullCreditReportDescription().getText(),
                reportDetailsResources.equifaxLogo());
        typeSelectorPanel.add(panel);
        typeSelectorPanel.setCellWidth(panel, "50%");

        initWidget(typeSelectorPanel);
    }

    public void setCreditCheckReportType(CreditCheckReportType reportType) {
        for (Map.Entry<CreditCheckReportType, ReportTypeConditionsPanel> entry : reportTypePanels.entrySet()) {
            entry.getValue().setSelected(entry.getKey() == reportType);
        }
        // fire event from here if the widget was reset
        ValueChangeEvent.<CreditCheckReportType> fire(CreditCheckReportTypeSelector.this, (CreditCheckReportType) null);
    }

    public CreditCheckReportType getCreditCheckReportType() {
        CreditCheckReportType selected = null;
        for (Map.Entry<CreditCheckReportType, ReportTypeConditionsPanel> entry : reportTypePanels.entrySet()) {
            if (entry.getValue().isSelected()) {
                selected = entry.getKey();
                break;
            }
        }
        return selected;
    }

    public void setFees(CreditCheckReportType reportType, BigDecimal setupFee, BigDecimal perApplicantFee) {
        reportTypePanels.get(reportType).setFees(setupFee, perApplicantFee);
    }

    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEditable(boolean editable) {

    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public int getTabIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTabIndex(int index) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDebugId(IDebugId debugId) {
        ensureDebugId(debugId.debugId());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CreditCheckReportType> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler focusHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler blurHandler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return null;
    }

    private ReportTypeConditionsPanel makePanel(CreditCheckReportType reportType, String detailsHtml, ImageResource providerLogo) {
        ReportTypeConditionsPanel panel = new ReportTypeConditionsPanel(reportType, new SafeHtmlBuilder().appendHtmlConstant(detailsHtml).toSafeHtml(),
                providerLogo);
        reportTypePanels.put(reportType, panel);
        return panel;
    }

}
