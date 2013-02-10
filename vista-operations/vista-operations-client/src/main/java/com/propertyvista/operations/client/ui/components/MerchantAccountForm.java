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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.financial.AbstractMerchantAccount;

public class MerchantAccountForm extends CEntityDecoratableForm<AbstractMerchantAccount> {

    public MerchantAccountForm() {
        super(AbstractMerchantAccount.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();
        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().merchantTerminalId()), 25).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bankId()), 5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().branchTransitNumber()), 5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accountNumber()), 15).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().chargeDescription()), 30).build());

        return panel;
    }

}
