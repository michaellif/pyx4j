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
package com.propertyvista.crm.client.ui.crud.tenant.lease.payment;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.financial.billing.Payment;

public class PaymentEditorForm extends CrmEntityForm<Payment> {

    public PaymentEditorForm() {
        this(false);
    }

    public PaymentEditorForm(boolean viewMode) {
        super(Payment.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositDate()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amount()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().type()), 10).build());

        return new CrmScrollPanel(main);
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        get(proto().id()).setVisible(!getValue().id().isNull());
    }
}