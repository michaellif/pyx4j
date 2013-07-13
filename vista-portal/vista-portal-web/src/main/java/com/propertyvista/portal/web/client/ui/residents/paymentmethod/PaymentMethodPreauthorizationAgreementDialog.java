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
package com.propertyvista.portal.web.client.ui.residents.paymentmethod;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.resources.VistaResources;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.domain.payment.PaymentType;

public abstract class PaymentMethodPreauthorizationAgreementDialog extends OkCancelDialog {

    private static final I18n i18n = I18n.get(PaymentMethodPreauthorizationAgreementDialog.class);

    public PaymentMethodPreauthorizationAgreementDialog(PaymentType paymentType) {
        super(i18n.tr("Please Confirm"));
        setBody(createBody(paymentType));
        getOkButton().setTextLabel(i18n.tr("I Agree"));
    }

    private IsWidget createBody(PaymentType type) {
        CRichTextArea legalTerms = new CRichTextArea();
        legalTerms.setViewable(true);
        switch (type) {
        case Echeck:
            legalTerms.setValue(VistaResources.INSTANCE.paymentPreauthorisedPAD().getText());
            break;
        case CreditCard:
            legalTerms.setValue(VistaResources.INSTANCE.paymentPreauthorisedCC().getText());
            break;
        default:
            assert false : "Illegal payment method type!";
            break;
        }

        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        content.setH1(0, 0, 1, i18n.tr("Pre-Authorized Agreement"));
        content.setWidget(1, 0, new ScrollPanel(legalTerms.asWidget()));
        content.getWidget(1, 0).setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorLegalTerms.name());

        return content;
    }
}