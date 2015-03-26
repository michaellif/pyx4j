/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author VladL
 */
package com.propertyvista.portal.resident.ui.financial.paymentmethod;

import java.util.Collections;
import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.form.EditableFormDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityEditor;
import com.propertyvista.portal.shared.ui.util.editors.PortalPaymentMethodEditor;

public class PaymentMethodViewForm extends CPortalEntityEditor<PaymentMethodDTO> {

    private static final I18n i18n = I18n.get(PaymentMethodViewForm.class);

    private Button editButton;

    public PaymentMethodViewForm(PaymentMethodViewImpl view) {
        super(PaymentMethodDTO.class, view, i18n.tr("Payment Method"), ThemeColor.contrast4);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().paymentMethod(), new PortalPaymentMethodEditor<LeasePaymentMethod>(LeasePaymentMethod.class) {

            @Override
            public Set<PaymentType> getDefaultPaymentTypes() {
                if (PaymentMethodViewForm.this.getValue() != null) {
                    return PaymentMethodViewForm.this.getValue().allowedPaymentsSetup().allowedPaymentTypes();
                }
                return Collections.emptySet();
            }

            @Override
            protected Set<CreditCardType> getAllowedCardTypes() {
                if (PaymentMethodViewForm.this.getValue() != null) {
                    return PaymentMethodViewForm.this.getValue().allowedPaymentsSetup().allowedCardTypes();
                }
                return Collections.emptySet();
            }

            @Override
            protected Set<CreditCardType> getConvienceFeeApplicableCardTypes() {
                if (PaymentMethodViewForm.this.getValue() != null) {
                    return PaymentMethodViewForm.this.getValue().allowedPaymentsSetup().convenienceFeeApplicableCardTypes();
                }
                return Collections.emptySet();
            }
        });

        return formPanel;
    }

    @Override
    protected EditableFormDecorator<PaymentMethodDTO> createDecorator() {
        EditableFormDecorator<PaymentMethodDTO> decor = super.createDecorator();
        editButton = decor.getBtnEdit();
        return decor;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        editButton.setVisible(editButton.isVisible() && SecurityController.check(VistaCustomerPaymentTypeBehavior.forPaymentMethodSetup()));
    }
}
