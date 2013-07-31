/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;

public class PaymentFeesForm<E extends AbstractPaymentFees> extends CEntityDecoratableForm<E> {

    public PaymentFeesForm(Class<E> clazz) {
        super(clazz);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().ccVisaFee()), true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().ccMasterCardFee()), true).build());

        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().visaDebitFee()), true).build());

        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().eChequeFee(), new CMoneyField()), true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().directBankingFee(), new CMoneyField()), true).build());

        return panel;
    }
}
