/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-12
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.resources.VistaResources;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;

public class LegalTermsDialog extends OkDialog {

    private static final I18n i18n = I18n.get(LegalTermsDialog.class);

    @com.pyx4j.i18n.annotations.I18n
    public enum TermsType {
        TermsOfUse, PrivacyPolicy, BillingAndRefundPolicy, PreauthorisedPAD, PreauthorisedCC;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    public LegalTermsDialog(TermsType paymentType) {
        super(i18n.tr("Review"));
        setBody(createBody(paymentType));
    }

    private IsWidget createBody(TermsType type) {
        HTML legalTerms = new HTML();
        switch (type) {

        case TermsOfUse:
            legalTerms.setHTML(VistaResources.INSTANCE.termsAndConditions().getText());
            break;
        case PrivacyPolicy:
            legalTerms.setHTML(VistaResources.INSTANCE.privacyPolicy().getText());
            break;
        case BillingAndRefundPolicy:
            legalTerms.setHTML(VistaResources.INSTANCE.billingAndRefundPolicy().getText());
            break;
        case PreauthorisedPAD:
            legalTerms.setHTML(VistaResources.INSTANCE.paymentPreauthorisedPAD().getText());
            break;
        case PreauthorisedCC:
            legalTerms.setHTML(VistaResources.INSTANCE.paymentPreauthorisedCC().getText());
            break;

        default:
            assert false : "Illegal term type!?";
            break;
        }

        FlowPanel content = new FlowPanel();
        content.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorLegalTerms.name());

        Widget w;
        content.add(w = new Label(type.toString()));
        w.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorLegalTermsHeader.name());

        content.add(w = new ScrollPanel(legalTerms));
        w.setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorLegalTermsContent.name());

        return content;
    }

    @Override
    public boolean onClickOk() {
        return true;
    }
}