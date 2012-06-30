/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.deposit;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.tenant.lease.Deposit;

public class DepositForm extends CrmEntityForm<Deposit> {

    private static final I18n i18n = I18n.get(DepositForm.class);

    public DepositForm() {
        this(false);
    }

    public DepositForm(boolean viewMode) {
        super(Deposit.class, viewMode);
    }

    @Override
    public void createTabs() {

        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 12).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().status()), 8).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositDate()), 9).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().refundDate()), 9).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().initialAmount()), 7).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().currentAmount()), 7).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 25).build());

        // tweaking:
        get(proto().type()).setEditable(false);
        get(proto().status()).setEditable(false);
        get(proto().depositDate()).setEditable(false);
        get(proto().refundDate()).setEditable(false);
        get(proto().currentAmount()).setEditable(false);

        content.getColumnFormatter().setWidth(0, "50%");
        content.getColumnFormatter().setWidth(1, "50%");

        selectTab(addTab(content, i18n.tr("General")));
    }
}