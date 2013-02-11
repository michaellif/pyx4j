/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.financial.yardi;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.TransactionHistoryViewerYardi;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.domain.dto.FinancialSummaryDTO;

public class FinancialSummaryForm extends CEntityDecoratableForm<FinancialSummaryDTO> {

    private static final I18n i18n = I18n.get(FinancialSummaryForm.class);

    private final Command payNowCommand;

    private Button payButton;

    public FinancialSummaryForm(Command payNowCommand) {
        super(FinancialSummaryDTO.class);
        setViewable(true);
        this.payNowCommand = payNowCommand;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setBR(++row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentBalance()), 10).build());
        content.setWidget(row, 1, payButton = new Button(i18n.tr("Pay Now"), new Command() {
            @Override
            public void execute() {
                payNowCommand.execute();
            }
        }));
        content.setWidget(++row, 0, inject(proto().transactionsHistory(), new TransactionHistoryViewerYardi()));

        return content;
    }

    public void setPayNowVisible(boolean visible) {
        payButton.setVisible(visible);
    }

}
