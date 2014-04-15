/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.financial.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class TransactionHistoryViewForm extends CPortalEntityForm<TransactionHistoryDTO> {

    private static final I18n i18n = I18n.get(TransactionHistoryViewForm.class);

    public TransactionHistoryViewForm() {
        super(TransactionHistoryDTO.class, null, i18n.tr("Transaction History"), ThemeColor.contrast4);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, inject(proto().issueDate(), new CDateLabel(), new FormWidgetDecoratorBuilder(100).build()));
        content.setWidget(++row, 0, inject(proto().currentBalanceAmount(), new CMoneyLabel(), new FormWidgetDecoratorBuilder(100).build()));

        content.setWidget(++row, 0, inject(proto().lineItems(), new InvoiceLineItemFolder()));

        return content;
    }

    class InvoiceLineItemFolder extends PortalBoxFolder<InvoiceLineItem> {

        public InvoiceLineItemFolder() {
            super(InvoiceLineItem.class, false);
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof InvoiceLineItem) {
                return new InvoiceLineItemViewer();
            }
            return super.create(member);
        }

        private class InvoiceLineItemViewer extends CEntityForm<InvoiceLineItem> {

            public InvoiceLineItemViewer() {
                super(InvoiceLineItem.class);
            }

            @Override
            protected IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, inject(proto().amount(), new CMoneyLabel(), new FormWidgetDecoratorBuilder(100).build()));
                content.setWidget(++row, 0, inject(proto().postDate(), new CDateLabel(), new FormWidgetDecoratorBuilder(100).build()));
                content.setWidget(++row, 0, inject(proto().description(), new CLabel<String>(), new FormWidgetDecoratorBuilder(250).build()));

                return content;
            }
        }
    }
}