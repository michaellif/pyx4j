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

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.pmc.fee.AbstractPaymentFees;

public class PaymentFeesForm<E extends AbstractPaymentFees> extends CEntityForm<E> {

    public PaymentFeesForm(Class<E> clazz) {
        super(clazz);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, inject(proto().ccVisaFee(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().ccMasterCardFee(), new FormDecoratorBuilder().build()));

        panel.setWidget(++row, 0, inject(proto().visaDebitFee(), new FormDecoratorBuilder().build()));

        panel.setWidget(++row, 0, inject(proto().eChequeFee(), new CMoneyField(), new FormDecoratorBuilder().build()));
        panel.setWidget(++row, 0, inject(proto().directBankingFee(), new CMoneyField(), new FormDecoratorBuilder().build()));

        return panel;
    }
}
