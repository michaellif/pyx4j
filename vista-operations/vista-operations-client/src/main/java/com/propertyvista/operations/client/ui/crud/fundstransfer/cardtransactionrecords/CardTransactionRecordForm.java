/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.fundstransfer.cardtransactionrecords;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.cards.CardTransactionRecord;

public class CardTransactionRecordForm extends OperationsEntityForm<CardTransactionRecord> {

    private static final I18n i18n = I18n.get(CardTransactionRecordForm.class);

    public CardTransactionRecordForm(IForm<CardTransactionRecord> view) {
        super(CardTransactionRecord.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 1, inject(proto().pmc().name(), new FieldDecoratorBuilder().customLabel("PMC:").build()));
        panel.setWidget(++row, 0, 1, inject(proto().merchantTerminalId(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().paymentTransactionId(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().cardType(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().amount(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().feeAmount(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().saleResponseCode(), new FieldDecoratorBuilder().build()));
        panel.setWidget(++row, 0, 1, inject(proto().saleResponseText(), new FieldDecoratorBuilder().build()));

        panel.setWidget(++row, 0, 1, inject(proto().feeResponseCode(), new FieldDecoratorBuilder().build()));

        selectTab(addTab(panel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}
