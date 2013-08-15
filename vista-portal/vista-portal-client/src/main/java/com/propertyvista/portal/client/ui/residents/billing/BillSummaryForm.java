/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 29, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.billing;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CMoneyField;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;
import com.propertyvista.portal.client.ui.components.CurrentBalanceFormat;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryView.Presenter;
import com.propertyvista.portal.domain.dto.financial.PvBillingFinancialSummaryDTO;

public class BillSummaryForm extends CEntityDecoratableForm<PvBillingFinancialSummaryDTO> {

    private static final I18n i18n = I18n.get(BillSummaryForm.class);

    private Presenter presenter;

    private Button payButton;

    public BillSummaryForm() {
        super(PvBillingFinancialSummaryDTO.class, new VistaViewersComponentFactory());
        setViewable(true);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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
                presenter.payNow();
            }
        }));
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().currentBill().dueDate()), 10).build());
        content.setWidget(row, 1, new Anchor(i18n.tr("View Current Bill"), new Command() {
            @Override
            public void execute() {
                presenter.viewCurrentBill();
            }
        }));

        content.setBR(++row, 0, 2);
        content.setH3(++row, 0, 2, proto().currentAutoPayments().getMeta().getCaption());
        content.setWidget(++row, 0, 2, inject(proto().currentAutoPayments(), new PaymentInfoFolder()));

        content.setBR(++row, 0, 2);

        content.setH3(++row, 0, 2, proto().latestActivities().getMeta().getCaption());
        content.setWidget(++row, 0, 2, inject(proto().latestActivities(), new InvoiceLineItemFolder()));

        // tweak UI:
        payButton.getElement().getStyle().setFloat(Float.NONE);

        return content;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        payButton.setVisible(SecurityController.checkAnyBehavior(VistaCustomerPaymentTypeBehavior.values()));
    }

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
