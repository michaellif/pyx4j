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
package com.propertyvista.portal.client.ui.residents.payment;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.widgets.client.dialog.OkDialog;

import com.propertyvista.common.client.resources.VistaResources;
import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;

public class LegalTermsDialog extends OkDialog {

    private static final I18n i18n = I18n.get(LegalTermsDialog.class);

    @com.pyx4j.i18n.annotations.I18n
    public enum TermsType {
        TermsAndConditions, PrivacyPolicy, BillingAndRefundPolicy;

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
        CLabel<String> legalTerms = new CLabel<String>();
        legalTerms.setEscapeHTML(false);
        switch (type) {
        case TermsAndConditions:
            legalTerms.setValue(VistaResources.INSTANCE.termsAndConditions().getText());
            break;
        case PrivacyPolicy:
            legalTerms.setValue(VistaResources.INSTANCE.privacyPolicy().getText());
            break;
        case BillingAndRefundPolicy:
            legalTerms.setValue(VistaResources.INSTANCE.billingAndRefundPolicy().getText());
            break;
        default:
            assert false : "Illegal term type!?";
            break;
        }

        FormFlexPanel content = new FormFlexPanel();

        content.setH1(0, 0, 1, type.toString());
        content.setWidget(1, 0, new ScrollPanel(legalTerms.asWidget()));
        content.getWidget(1, 0).setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorLegalTerms.name());

        return content;
    }

    @Override
    public boolean onClickOk() {
        return true;
    }
}