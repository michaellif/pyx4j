/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.invoice;

import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.client.CEntityViewer;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.Invoice;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;

public class InvoiceViewer extends CEntityViewer<Invoice> {

    private final static I18n i18n = I18n.get(InvoiceViewer.class);

    @Override
    public IsWidget createContent(Invoice value) {
        FormFlexPanel content = new FormFlexPanel();

        content.setWidget(0, 0, createLineItems(value.lineItems()));

        return content;
    }

    private IsWidget createLineItems(List<InvoiceLineItem> items) {
        FormFlexPanel lineItemsView = new FormFlexPanel();
        // TODO add style
        int row = -1;
        lineItemsView.setWidget(++row, 0, new HTML(new SafeHtmlBuilder().appendEscaped(i18n.tr("Item")).toSafeHtml()));
        lineItemsView.setWidget(row, 1, new HTML(i18n.tr("Debit")));
        lineItemsView.setWidget(row, 2, new HTML(i18n.tr("Credit")));
        lineItemsView.setWidget(row, 3, new HTML(i18n.tr("Balance")));

        for (InvoiceLineItem item : items) {
            lineItemsView.setWidget(++row, 0, new HTML(new SafeHtmlBuilder().appendEscaped(item.description().getValue()).toSafeHtml()));

            if (item.isInstanceOf(InvoiceDebit.class)) {
                lineItemsView.setWidget(row, 1, new HTML(new SafeHtmlBuilder().appendEscaped(item.amount().toString()).toSafeHtml()));
            } else if (item.isInstanceOf(InvoiceCredit.class)) {
                lineItemsView.setWidget(row, 2, new HTML(new SafeHtmlBuilder().appendEscaped(item.amount().toString()).toSafeHtml()));
            } else if (ApplicationMode.isDevelopment()) {
                lineItemsView.setWidget(row, 1, new HTML("error"));
                lineItemsView.setWidget(row, 2, new HTML("error"));
                continue;
            }
            // TODO 
            lineItemsView.setWidget(row, 3, new HTML("here comes balance"));
        }

        return lineItemsView;

    }

}
