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
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.paymentmethod;

import java.util.Set;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.portal.rpc.portal.resident.dto.financial.PaymentMethodDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityEditor;
import com.propertyvista.portal.shared.ui.util.editors.PortalPaymentMethodEditor;

public class PaymentMethodViewForm extends CPortalEntityEditor<PaymentMethodDTO> {

    private static final I18n i18n = I18n.get(PaymentMethodViewForm.class);

    public PaymentMethodViewForm(PaymentMethodViewImpl view) {
        super(PaymentMethodDTO.class, view, i18n.tr("Payment Method"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().paymentMethod(), new PortalPaymentMethodEditor<LeasePaymentMethod>(LeasePaymentMethod.class) {

            @Override
            public Set<PaymentType> getPaymentTypes() {
                return PaymentMethodViewForm.this.getValue().allowedPaymentsSetup().allowedPaymentTypes();
            }

            @Override
            protected Set<CreditCardType> getAllowedCardTypes() {
                return PaymentMethodViewForm.this.getValue().allowedPaymentsSetup().allowedCardTypes();
            }

            @Override
            protected Set<CreditCardType> getConvienceFeeApplicableCardTypes() {
                return PaymentMethodViewForm.this.getValue().allowedPaymentsSetup().convenienceFeeApplicableCardTypes();
            }
        }));

        return content;
    }
}
