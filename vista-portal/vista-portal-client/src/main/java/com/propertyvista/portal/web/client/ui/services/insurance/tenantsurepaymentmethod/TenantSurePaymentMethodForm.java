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
package com.propertyvista.portal.web.client.ui.services.insurance.tenantsurepaymentmethod;

import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.web.client.ui.financial.paymentmethod.PaymentMethodEditor;

public class TenantSurePaymentMethodForm extends PaymentMethodEditor<InsurancePaymentMethod> {

    private static final I18n i18n = I18n.get(TenantSurePaymentMethodForm.class);

    private final Command onSameAsCurrentAddressSelected;

    private final CCheckBox iAgreeBox = new CCheckBox();

    private final HTML legalTerms = new HTML();

    private boolean isAgreedToPreauthorizedPayments;

    public TenantSurePaymentMethodForm() {
        this(null);
    }

    public TenantSurePaymentMethodForm(Command onSameAsCurrentAddressSelected) {
        super(InsurancePaymentMethod.class);
        this.onSameAsCurrentAddressSelected = onSameAsCurrentAddressSelected;
        this.isAgreedToPreauthorizedPayments = false;
    }

    @Override
    protected String getNameOn() {
        return ClientContext.getUserVisit().getName();
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = (BasicFlexFormPanel) super.createContent();

        content.setBR(content.getRowCount(), 0, 1);

        content.setWidget(content.getRowCount() + 1, 0, createLegalTermsPanel());

        return content;
    }

    private Widget createLegalTermsPanel() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        panel.setH1(0, 0, 1, i18n.tr("Pre-Authorized Agreement"));
        panel.setWidget(1, 0, legalTerms);
        panel.setWidget(2, 0, new FormDecoratorBuilder(iAgreeBox, FormDecoratorBuilder.LABEL_WIDTH, 5, 15).customLabel(i18n.tr("I Agree")).build());

        iAgreeBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                onIAgree(event.getValue());
            }
        });

        addValueValidator(new EditableValueValidator<InsurancePaymentMethod>() {
            @Override
            public ValidationError isValid(CComponent<InsurancePaymentMethod> component, InsurancePaymentMethod value) {
                if (!isAgreedToPreauthorizedPayments()) {
                    return new ValidationError(component, i18n.tr("You must agree to Preauthorized Payments Agreement in order to continue!"));
                } else {
                    return null;
                }
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
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (iAgreeBox != null) {
            iAgreeBox.setValue(false);
        }
    }

    @Override
    public Set<PaymentType> defaultPaymentTypes() {
        return EnumSet.of(PaymentType.CreditCard);
    }

    public void setPreAuthorizedAgreement(String agreementHtml) {
        legalTerms.setHTML(agreementHtml);
    }

    @Override
    protected void onBillingAddressSameAsCurrentOne(boolean set, CComponent<AddressSimple> comp) {
        if (set) {
            onSameAsCurrentAddressSelected.execute();
        }
    }

    protected void onIAgree(boolean set) {
        isAgreedToPreauthorizedPayments = set;
        revalidate();
    }

    public boolean isAgreedToPreauthorizedPayments() {
        return isAgreedToPreauthorizedPayments;
    }

}
