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
package com.propertyvista.crm.client.ui.crud.financial;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.AggregatedTransfer;

public class MerchantTransactionForm extends CrmEntityForm<AggregatedTransfer> {

    public MerchantTransactionForm() {
        this(false);
    }

    public MerchantTransactionForm(boolean viewMode) {
        super(AggregatedTransfer.class, viewMode);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().paymentDate()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().status()), 10).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().merchantAccount()), 20).build());

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().grossPaymentAmount()), 10).build());

        FormFlexPanel amount = new FormFlexPanel();
        int row2 = -1;
        amount.setWidget(++row2, 0, new DecoratorBuilder(inject(proto().rejectItemsAmount()), 10).build());
        amount.setWidget(++row2, 0, new DecoratorBuilder(inject(proto().rejectItemsFee()), 10).build());
        amount.setWidget(++row2, 0, new DecoratorBuilder(inject(proto().rejectItemsCount()), 5).build());
        row2 = -1;
        amount.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().returnItemsAmount()), 10).build());
        amount.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().returnItemsFee()), 10).build());
        amount.setWidget(++row2, 1, new DecoratorBuilder(inject(proto().returnItemsCount()), 5).build());

        main.setWidget(++row, 0, amount);

        main.setH3(++row, 0, 1, proto().payments().getMeta().getCaption());
        main.setWidget(++row, 0, ((MerchantTransactionViewerView) getParentView()).getPaymentsListerView().asWidget());
        main.setH3(++row, 0, 1, proto().returnedPayments().getMeta().getCaption());
        main.setWidget(++row, 0, ((MerchantTransactionViewerView) getParentView()).getReturnedPaymentsListerView().asWidget());

        return new ScrollPanel(main);
    }
}