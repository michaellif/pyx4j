/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FormDecoratorBuilder;

import com.propertyvista.domain.payment.AccountType;
import com.propertyvista.domain.payment.CheckInfo;

public class CheckInfoEditor extends CEntityForm<CheckInfo> {

    public CheckInfoEditor() {
        super(CheckInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        panel.setWidget(++row, 0, inject(proto().nameOn(), new FormDecoratorBuilder(20).build()));
        panel.setWidget(++row, 0, inject(proto().bankName(), new FormDecoratorBuilder(20).build()));
        panel.setWidget(++row, 0, inject(proto().accountType(), new FormDecoratorBuilder(10).build()));
        panel.setWidget(++row, 0, inject(proto().checkNo(), new FormDecoratorBuilder(5).build()));

        row = 0;
        panel.setWidget(++row, 1, inject(proto().transitNo(), new FormDecoratorBuilder(10).build()));
        panel.setWidget(++row, 1, inject(proto().institutionNo(), new FormDecoratorBuilder(5).build()));
        panel.setWidget(++row, 1, inject(proto().accountNo(), new FormDecoratorBuilder(15).build()));

        return panel;
    }

    @Override
    public void generateMockData() {
        get(proto().nameOn()).setMockValue("Dev");
        get(proto().bankName()).setMockValue("Nowhere Bank");
        get(proto().accountType()).setMockValue(AccountType.Chequing);
        get(proto().checkNo()).setMockValue("1");
        get(proto().institutionNo()).setMockValue("123");
        get(proto().transitNo()).setMockValue("12345");
        get(proto().accountNo()).setMockValueByString(String.valueOf(System.currentTimeMillis() % 10000000));
    }
}
