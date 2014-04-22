/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.components;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.common.client.ui.validators.EcheckAccountNumberStringValidator;
import com.propertyvista.common.client.ui.validators.EcheckBankIdValidator;
import com.propertyvista.common.client.ui.validators.EcheckBranchTransitValidator;
import com.propertyvista.domain.financial.AbstractMerchantAccount;
import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount;
import com.propertyvista.operations.domain.vista2pmc.VistaMerchantAccount.AccountType;

public class MerchantAccountForm extends CForm<AbstractMerchantAccount> {

    public MerchantAccountForm() {
        super(AbstractMerchantAccount.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, inject(proto().merchantTerminalId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(row, 1, inject(proto().bankId(), new FieldDecoratorBuilder(5).build()));

        panel.setWidget(++row, 0, inject(proto().accountNumber(), new FieldDecoratorBuilder(15).build()));
        panel.setWidget(row, 1, inject(proto().branchTransitNumber(), new FieldDecoratorBuilder(5).build()));

        panel.setWidget(++row, 0, 2, inject(proto().chargeDescription(), new FieldDecoratorBuilder(true).build()));

        return panel;
    }

    @Override
    public void addValidations() {
        get(proto().accountNumber()).addComponentValidator(new EcheckAccountNumberStringValidator());
        get(proto().branchTransitNumber()).addComponentValidator(new EcheckBranchTransitValidator());
        get(proto().bankId()).addComponentValidator(new EcheckBankIdValidator());
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updateVisibility();
    }

    private void updateVisibility() {
        if ((getValue().isInstanceOf(VistaMerchantAccount.class) && (getValue().duplicate(VistaMerchantAccount.class).accountType().getValue() == AccountType.PaymentAggregation))) {
            get(proto().merchantTerminalId()).setVisible(false);
        } else {
            get(proto().merchantTerminalId()).setVisible(true);
        }
    }
}
