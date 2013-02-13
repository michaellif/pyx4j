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

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.TransactionHistoryViewerYardi;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.portal.client.ui.components.CurrentBalanceFormat;
import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;

public class FinancialSummaryForm extends CEntityDecoratableForm<YardiFinancialSummaryDTO> {

    private static final I18n i18n = I18n.get(FinancialSummaryForm.class);

    private final Command payNowCommand;

    private Button payButton;

    public FinancialSummaryForm(Command payNowCommand) {
        super(YardiFinancialSummaryDTO.class);
        setViewable(true);
        this.payNowCommand = payNowCommand;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;
        content.setBR(++row, 0, 2);
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().currentBalance(), new CMoneyField()), 10).build());
        ((CMoneyField) get(proto().currentBalance())).setFormat(new CurrentBalanceFormat());

        content.setWidget(row, 1, payButton = new Button(i18n.tr("Pay Now"), new Command() {
            @Override
            public void execute() {
                payNowCommand.execute();
            }
        }));

        content.setBR(++row, 0, 2);
        content.setH3(++row, 0, 2, proto().latestActivities().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().latestActivities(), new InvoiceLineItemFolder()));

        content.setBR(++row, 0, 2);
        content.setH3(++row, 0, 2, i18n.tr("Charges and Payments"));
        content.setWidget(++row, 0, inject(proto().transactionsHistory(), new TransactionHistoryViewerYardi()));

        return content;
    }

    public void setPayNowVisible(boolean visible) {
        payButton.setVisible(visible);
    }

    // TODO - duplicate of BillSummaryForm.InvoiceLineItemFolder
    class InvoiceLineItemFolder extends VistaTableFolder<InvoiceLineItem> {

        public InvoiceLineItemFolder() {
            super(InvoiceLineItem.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(// @formatter:off
                    new EntityFolderColumnDescriptor(proto().amount(), "10em"),
                    new EntityFolderColumnDescriptor(proto().postDate(), "10em"),
                    new EntityFolderColumnDescriptor(proto().description(), "20em")
            ); // formatter:on
        }
    }
}
