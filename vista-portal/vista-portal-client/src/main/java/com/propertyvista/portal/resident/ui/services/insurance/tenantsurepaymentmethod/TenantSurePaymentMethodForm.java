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
package com.propertyvista.portal.resident.ui.services.insurance.tenantsurepaymentmethod;

import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.ISignature.SignatureType;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.CustomerSignature;
import com.propertyvista.portal.resident.themes.TenantSureTheme;
import com.propertyvista.portal.shared.ui.util.editors.PaymentMethodEditor;

public class TenantSurePaymentMethodForm extends PaymentMethodEditor<InsurancePaymentMethod> {

    private static final I18n i18n = I18n.get(TenantSurePaymentMethodForm.class);

    private final Command onSameAsCurrentAddressSelected;

    private final HTML legalTerms = new HTML();

    public TenantSurePaymentMethodForm() {
        this(null);
    }

    public TenantSurePaymentMethodForm(Command onSameAsCurrentAddressSelected) {
        super(InsurancePaymentMethod.class);
        this.onSameAsCurrentAddressSelected = onSameAsCurrentAddressSelected;
    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().preAuthorizedAgreementSignature()).addValueValidator(new EditableValueValidator<CustomerSignature>() {
            @Override
            public ValidationError isValid(CComponent<CustomerSignature> component, CustomerSignature value) {
                if (value != null && !value.agree().isBooleanTrue()) {
                    return new ValidationError(component, i18n.tr("You must agree to preauthorized payment agreement to continue"));
                }
                return null;
            }
        });
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = (BasicFlexFormPanel) super.createContent();
        int row = content.getRowCount() + 1;
        content.setBR(row, 0, 2);
        content.setWidget(++row, 0, 2, createPapAgreementTermsPanel());
        content.setBR(++row, 0, 2);
        content.setWidget(
                ++row,
                0,
                2,
                inject(proto().preAuthorizedAgreementSignature(),
                        new CSignature(SignatureType.AgreeBox, i18n.tr("I agree to the preauthorized payments agreement"))));
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        return content;
    }

    private Widget createPapAgreementTermsPanel() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        panel.addStyleName(TenantSureTheme.StyleName.TenantSurePapAgreementPanel.name());
        panel.setH1(0, 0, 1, i18n.tr("Pre-Authorized Agreement"));
        legalTerms.getElement().getStyle().setTextAlign(TextAlign.JUSTIFY);
        panel.setWidget(1, 0, legalTerms);
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        setBillingAddressAsCurrentEnabled(onSameAsCurrentAddressSelected != null);
        super.onValueSet(populate);
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

    @Override
    protected String getNameOn() {
        return ClientContext.getUserVisit().getName();
    }

}
