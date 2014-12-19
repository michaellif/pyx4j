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
 */
package com.propertyvista.portal.resident.ui.financial.views;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class TransactionHistoryViewForm extends CPortalEntityForm<TransactionHistoryDTO> {

    private static final I18n i18n = I18n.get(TransactionHistoryViewForm.class);

    public TransactionHistoryViewForm() {
        super(TransactionHistoryDTO.class, null, i18n.tr("Transaction History"), ThemeColor.contrast4);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, proto().issueDate(), new CDateLabel()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().currentBalanceAmount(), new CMoneyLabel()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().lineItems(), new InvoiceLineItemFolder());
        return formPanel;
    }

    class InvoiceLineItemFolder extends PortalBoxFolder<InvoiceLineItem> {

        public InvoiceLineItemFolder() {
            super(InvoiceLineItem.class, false);
        }

        @Override
        protected CForm<InvoiceLineItem> createItemForm(IObject<?> member) {
            return new InvoiceLineItemViewer();
        }

        private class InvoiceLineItemViewer extends CForm<InvoiceLineItem> {

            public InvoiceLineItemViewer() {
                super(InvoiceLineItem.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);
                formPanel.append(Location.Left, proto().amount(), new CMoneyLabel()).decorate().componentWidth(100);
                formPanel.append(Location.Left, proto().postDate(), new CDateLabel()).decorate().componentWidth(100);
                formPanel.append(Location.Left, proto().description(), new CLabel<String>()).decorate().componentWidth(250);
                return formPanel;
            }
        }
    }
}