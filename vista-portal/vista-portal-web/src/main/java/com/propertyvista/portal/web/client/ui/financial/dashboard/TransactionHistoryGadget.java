/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.financial.dashboard;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CMoneyLabel;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeHandler;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.util.decorators.FormDecoratorBuilder;

public class TransactionHistoryGadget extends AbstractGadget<FinancialDashboardViewImpl> {

    private static final I18n i18n = I18n.get(TransactionHistoryGadget.class);

    private final TransactionHistoryView view;

    TransactionHistoryGadget(FinancialDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.billingIcon(), i18n.tr("Transaction History"), ThemeColor.contrast4);

        view = new TransactionHistoryView();
        view.setViewable(true);
        view.initContent();

        setContent(view);
    }

    protected void populate(TransactionHistoryDTO value) {
        view.populate(value);
    }

    class TransactionHistoryView extends CEntityForm<TransactionHistoryDTO> {

        public TransactionHistoryView() {
            super(TransactionHistoryDTO.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel content = new BasicFlexFormPanel();
            int row = -1;

            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().issueDate(), new CDateLabel()), 100).build());
            content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().currentBalanceAmount(), new CMoneyLabel()), 100).build());

            content.setWidget(++row, 0, inject(proto().lineItems(), new InvoiceLineItemFolder()));

            return content;
        }
    }

    private class InvoiceLineItemFolder extends VistaBoxFolder<InvoiceLineItem> {

        public InvoiceLineItemFolder() {
            super(InvoiceLineItem.class, false);
        }

        @Override
        public IFolderItemDecorator<InvoiceLineItem> createItemDecorator() {
            BoxFolderItemDecorator<InvoiceLineItem> decor = (BoxFolderItemDecorator<InvoiceLineItem>) super.createItemDecorator();
            decor.setExpended(false);
            return decor;
        }

        @Override
        public CComponent<?> create(IObject<?> member) {
            if (member instanceof InvoiceLineItem) {
                return new InvoiceLineItemViewer();
            }
            return super.create(member);
        }

        private class InvoiceLineItemViewer extends CEntityDecoratableForm<InvoiceLineItem> {

            public InvoiceLineItemViewer() {
                super(InvoiceLineItem.class);

                setViewable(true);
                inheritViewable(false);
            }

            @Override
            public IsWidget createContent() {
                BasicFlexFormPanel content = new BasicFlexFormPanel();
                int row = -1;

                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().amount(), new CMoneyLabel()), 100).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().postDate(), new CDateLabel()), 100).build());
                content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().description(), new CLabel<String>()), 250).build());

                return content;
            }
        }
    }

}
