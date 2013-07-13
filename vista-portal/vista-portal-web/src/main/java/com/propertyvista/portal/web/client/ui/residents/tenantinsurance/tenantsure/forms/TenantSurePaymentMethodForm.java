/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.forms;

import java.util.Collection;
import java.util.EnumSet;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CRichTextArea;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.theme.NewPaymentMethodEditorTheme;
import com.propertyvista.common.client.ui.components.editors.payments.PaymentMethodForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;

public class TenantSurePaymentMethodForm extends PaymentMethodForm<InsurancePaymentMethod> {

    private static final I18n i18n = I18n.get(TenantSurePaymentMethodForm.class);

    private final Command onSameAsCurrentAddressSelected;

    private final CCheckBox iAgreeBox = new CCheckBox();

    protected final CRichTextArea legalTerms = new CRichTextArea();

    private boolean isAgreedToPreauthorizedPayments;

    public TenantSurePaymentMethodForm() {
        this(null);
    }

    public TenantSurePaymentMethodForm(Command onSameAsCurrentAddressSelected) {
        super(InsurancePaymentMethod.class);
        this.onSameAsCurrentAddressSelected = onSameAsCurrentAddressSelected;
        this.isAgreedToPreauthorizedPayments = false;
        legalTerms.setViewable(true);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        content.setWidget(0, 0, super.createContent());
        content.setBR(1, 0, 1);
        content.setWidget(2, 0, createLegalTermsPanel());

        return content;
    }

    private Widget createLegalTermsPanel() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        panel.setH1(0, 0, 3, i18n.tr("Pre-Authorized Agreement"));

        panel.setWidget(1, 0, new ScrollPanel(legalTerms.asWidget()));
        panel.getWidget(1, 0).setStyleName(NewPaymentMethodEditorTheme.StyleName.PaymentEditorLegalTerms.name());

        panel.setWidget(2, 0, new FormDecoratorBuilder(iAgreeBox, 5).customLabel(i18n.tr("I Agree")).build());
        iAgreeBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onIAgree(event.getValue());
            }
        });

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        setBillingAddressAsCurrentEnabled(onSameAsCurrentAddressSelected != null);
        super.onValueSet(populate);
    }

    @Override
    public Collection<PaymentType> defaultPaymentTypes() {
        return EnumSet.of(PaymentType.CreditCard);
    }

    public void setPreAuthorizedAgreement(String agreementHtml) {
        legalTerms.setValue(agreementHtml);
    }

    @Override
    protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressStructured> comp) {
        if (set) {
            onSameAsCurrentAddressSelected.execute();
        }
    }

    protected void onIAgree(boolean set) {
        isAgreedToPreauthorizedPayments = set;
    }

    public boolean isAgreedToPreauthorizedPayments() {
        return isAgreedToPreauthorizedPayments;
    }

}
