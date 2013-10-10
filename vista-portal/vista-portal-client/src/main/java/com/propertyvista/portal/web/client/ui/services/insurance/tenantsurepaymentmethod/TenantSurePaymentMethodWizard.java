/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.services.insurance.tenantsurepaymentmethod;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.wizard.WizardStep;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.InsurancePaymentMethodDTO;
import com.propertyvista.portal.web.client.ui.CPortalEntityWizard;
import com.propertyvista.portal.web.client.ui.IWizardView;
import com.propertyvista.portal.web.client.ui.LegalTermsDialog;
import com.propertyvista.portal.web.client.ui.LegalTermsDialog.TermsType;
import com.propertyvista.portal.web.client.ui.services.insurance.TenantSurePaymentMethodForm;

public class TenantSurePaymentMethodWizard extends CPortalEntityWizard<InsurancePaymentMethodDTO> {

    private static final I18n i18n = I18n.get(TenantSurePaymentMethodWizard.class);

    private final WizardStep comfirmationStep;

    private final SimplePanel confirmationDetailsHolder = new SimplePanel();

    public TenantSurePaymentMethodWizard(IWizardView<InsurancePaymentMethodDTO> view) {
        super(InsurancePaymentMethodDTO.class, view, i18n.tr("TenantSure Payment Setup"), i18n.tr("Submit"), ThemeColor.contrast4);

        addStep(createPaymentMethodStep());
        comfirmationStep = addStep(createConfirmationStep());
    }

    private BasicFlexFormPanel createPaymentMethodStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Payment Method"));

        panel.setWidget(0, 0, inject(proto().paymentMethod(), new TenantSurePaymentMethodForm()));

        return panel;
    }

    private BasicFlexFormPanel createConfirmationStep() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel(i18n.tr("Confirmation"));
        int row = -1;

        panel.setWidget(++row, 0, confirmationDetailsHolder);
        panel.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        panel.setBR(++row, 0, 1);
        panel.setHR(++row, 0, 1);

        panel.setWidget(++row, 0, createLegalTermsPanel());
        panel.getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);

        return panel;
    }

    @Override
    protected void onStepChange(SelectionEvent<WizardStep> event) {
        super.onStepChange(event);
        if (event.getSelectedItem().equals(comfirmationStep)) {
            confirmationDetailsHolder.clear();
            confirmationDetailsHolder.setWidget(createConfirmationDetailsPanel());
        }
    }

    @Override
    protected void onValueSet(final boolean populate) {
        super.onValueSet(populate);
    }

    private Widget createConfirmationDetailsPanel() {
        VerticalPanel panel = new VerticalPanel();
        Widget w;

        panel.add(new HTML("<br/>"));

        HorizontalPanel pm = new HorizontalPanel();
        pm.add(w = new HTML(i18n.tr("Payment Method:")));
        w.setWidth("10em");
        pm.add(w = new HTML(get(proto().paymentMethod()).getValue().getStringView()));
        w.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        panel.add(pm);

        return panel;
    }

    private Widget createLegalTermsPanel() {
        FlowPanel panel = new FlowPanel();
        Widget w;

        panel.add(new HTML(i18n.tr("Be informed that you are acknowledging our")));
        panel.add(w = new Anchor(i18n.tr("Terms Of Use"), new Command() {
            @Override
            public void execute() {
                // TODO change this to TenantSure Preauthorized Agreement terms.
                new LegalTermsDialog(TermsType.TermsOfUse).show();
            }
        }));

        panel.add(w = new HTML("&nbsp" + i18n.tr("and") + "&nbsp"));
        w.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        panel.add(w = new Anchor(i18n.tr("Privacy Policy"), new Command() {
            @Override
            public void execute() {
                new LegalTermsDialog(TermsType.PrivacyPolicy).show();
            }
        }));

        return panel;
    }
}
