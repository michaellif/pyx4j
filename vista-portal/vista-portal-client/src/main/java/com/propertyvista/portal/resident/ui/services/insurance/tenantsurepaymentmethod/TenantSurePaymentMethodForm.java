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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CSignature;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.domain.contact.AddressSimple;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.ResidentPortalTerms;
import com.propertyvista.portal.shared.ui.TermsAnchor;
import com.propertyvista.portal.shared.ui.util.decorators.SignatureDecorator;
import com.propertyvista.portal.shared.ui.util.editors.PortalPaymentMethodEditor;

public class TenantSurePaymentMethodForm extends PortalPaymentMethodEditor<InsurancePaymentMethod> {

    private static final I18n i18n = I18n.get(TenantSurePaymentMethodForm.class);

    private final Command onSameAsCurrentAddressSelected;

    public TenantSurePaymentMethodForm() {
        this(null);
    }

    public TenantSurePaymentMethodForm(Command onSameAsCurrentAddressSelected) {
        super(InsurancePaymentMethod.class);
        this.onSameAsCurrentAddressSelected = onSameAsCurrentAddressSelected;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = (BasicFlexFormPanel) super.createContent();
        int row = content.getRowCount() + 1;
        content.setBR(row, 0, 2);

        SafeHtmlBuilder signatureDescriptionBuilder = new SafeHtmlBuilder();
        String anchorId = HTMLPanel.createUniqueId();
        signatureDescriptionBuilder.appendHtmlConstant(i18n.tr("I agree to the {0}.", "<span id=\"" + anchorId + "\"></span>"));

        HTMLPanel signatureDescriptionPanel = new HTMLPanel(signatureDescriptionBuilder.toSafeHtml());
        Anchor termsAnchor = new TermsAnchor(i18n.tr("TenantSure Pre-Authorized Payment Terms"), ResidentPortalTerms.TenantSurePreAuthorizedPaymentTerms.class);
        signatureDescriptionPanel.addAndReplaceElement(termsAnchor, anchorId);

        content.setWidget(++row, 0, 2, new SignatureDecorator(inject(proto().preAuthorizedAgreementSignature(), new CSignature(signatureDescriptionPanel))));
        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        setBillingAddressAsCurrentEnabled(onSameAsCurrentAddressSelected != null);
        super.onValueSet(populate);
    }

    @Override
    public Set<PaymentType> getPaymentTypes() {
        return EnumSet.of(PaymentType.CreditCard);
    }

    @Override
    protected Set<CreditCardType> getAllowedCardTypes() {
        return EnumSet.allOf(CreditCardType.class);
    }

    @Override
    protected Set<CreditCardType> getConvienceFeeApplicableCardTypes() {
        return Collections.emptySet();
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
