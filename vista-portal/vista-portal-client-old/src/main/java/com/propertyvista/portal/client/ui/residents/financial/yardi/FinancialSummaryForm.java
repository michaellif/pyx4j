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

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.CTextFieldBase;
import com.pyx4j.forms.client.ui.IFormat;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.TransactionHistoryViewerYardi;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.portal.client.ui.components.CurrentBalanceFormat;
import com.propertyvista.portal.client.ui.residents.billing.PaymentInfoFolder;
import com.propertyvista.portal.domain.dto.financial.YardiFinancialSummaryDTO;

public class FinancialSummaryForm extends CEntityDecoratableForm<YardiFinancialSummaryDTO> {

    private static final I18n i18n = I18n.get(FinancialSummaryForm.class);

    private final Command payNowCommand;

    private Button payButton;

    private Widget currentAutoPaymentCaption;

    private Widget transactionsHistoryCaption;

    public FinancialSummaryForm(Command payNowCommand) {
        super(YardiFinancialSummaryDTO.class);
        setViewable(true);
        this.payNowCommand = payNowCommand;
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();

        int row = -1;
        content.setBR(++row, 0, 2);
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().currentBalance(), new CMoneyField()), 10).build());
        ((CMoneyField) get(proto().currentBalance())).setFormat(new CurrentBalanceFormat());

        content.setWidget(row, 1, payButton = new Button(i18n.tr("Pay Now"), new Command() {
            @Override
            public void execute() {
                payNowCommand.execute();
            }
        }));

        content.setH3(++row, 0, 2, proto().currentAutoPayments().getMeta().getCaption());
        currentAutoPaymentCaption = content.getWidget(row, 0);
        content.setWidget(++row, 0, 2, inject(proto().currentAutoPayments(), new PaymentInfoFolder()));

        content.setBR(++row, 0, 2);

        content.setH3(++row, 0, 2, i18n.tr("Charges and Payments"));
        transactionsHistoryCaption = content.getWidget(row, 0);
        content.setWidget(++row, 0, inject(proto().transactionsHistory(), new TransactionHistoryViewerYardi()));

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        currentAutoPaymentCaption.setVisible(!getValue().currentAutoPayments().isEmpty());
        transactionsHistoryCaption.setVisible(!getValue().transactionsHistory().isEmpty());
    }

    public void setPayNowVisible(boolean visible) {
        payButton.setVisible(visible);
    }

    public static class LatestActivitiesFolder extends VistaTableFolder<InvoiceLineItem> {

        public LatestActivitiesFolder() {
            super(InvoiceLineItem.class, false);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().postDate(), "10em"),
                new EntityFolderColumnDescriptor(proto().description(), "20em"),
                new EntityFolderColumnDescriptor(proto().amount(), "10em")                                        
            ); // formatter:on 
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member.getObjectClass() == InvoiceLineItem.class) {
                return new CEntityFolderRowEditor<InvoiceLineItem>(InvoiceLineItem.class, columns()) {
                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = super.createCell(column);
                        
                        if (column.getObject() == proto().amount()) {
                            @SuppressWarnings("unchecked")
                            CTextFieldBase<BigDecimal, ?> amountField = (CTextFieldBase<BigDecimal, ?>)comp;
                            final IFormat<BigDecimal> defaultFormat = amountField.getFormat();
                            amountField.setFormat(new IFormat<BigDecimal>() {
                                // show payments (negative amount) as credits
                                private final NumberFormat paymentFormat = NumberFormat.getFormat(i18n.tr("$#,##0.00;$# CR"));
                                // yardi may have negative charges that are actually credits, so we show CR in these cases
                                private final NumberFormat chargeFormat = NumberFormat.getFormat(i18n.tr("$#,##0.00;$# CR"));
                                @Override
                                public String format(BigDecimal value) {
                                    BigDecimal amount = getValue().amount().getValue();
                                    if (getValue() instanceof InvoiceDebit) {
                                        return chargeFormat.format(amount);
                                    } else if (getValue() instanceof InvoiceCredit) {
                                        return paymentFormat.format(amount);
                                    } else if (defaultFormat != null) {
                                        return defaultFormat.format(amount);
                                    } else {
                                        return null;
                                    }
                                }

                                @Override
                                public BigDecimal parse(String string) throws ParseException {
                                    return null;
                                }
                            });
                        }
                        return comp;
                    }
                };
            } else { 
                return super.create(member);
            }
        }
    }
    
}
