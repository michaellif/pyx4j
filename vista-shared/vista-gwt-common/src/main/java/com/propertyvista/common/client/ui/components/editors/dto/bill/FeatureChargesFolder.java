/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.dto.bill;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceSubLineItem;

public class FeatureChargesFolder extends VistaBoxFolder<InvoiceProductCharge> {

    public FeatureChargesFolder() {
        super(InvoiceProductCharge.class);
        this.setAddable(false);
    }

    @Override
    public IFolderItemDecorator<InvoiceProductCharge> createItemDecorator() {
        BoxFolderItemDecorator<InvoiceProductCharge> decor = (BoxFolderItemDecorator<InvoiceProductCharge>) super.createItemDecorator();
        decor.setExpended(false);
        return decor;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof InvoiceProductCharge) {
            return new ProductChargeEditor();
        }
        return super.create(member);
    }

    class ProductChargeEditor extends CEntityDecoratableForm<InvoiceProductCharge> {

        public ProductChargeEditor() {
            super(InvoiceProductCharge.class);
        }

        @Override
        public IsWidget createContent() {
            TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();
            main.setWidth("100%");

            int row = -1;
            main.setWidget(++row, 0, inject(proto().chargeSubLineItem().description()));
            main.setWidget(row, 1, inject(proto().chargeSubLineItem().amount()));

            main.getFlexCellFormatter().setColSpan(++row, 0, 2);
            main.setWidget(row, 0, inject(proto().adjustmentSubLineItems(), new SublineItemFolder()));

            main.getFlexCellFormatter().setColSpan(++row, 0, 2);
            main.setWidget(row, 0, inject(proto().concessionSubLineItems(), new SublineItemFolder()));
            return main;
        }
    }

    class SublineItemFolder extends VistaTableFolder<InvoiceSubLineItem> {

        public SublineItemFolder() {
            super(InvoiceSubLineItem.class);
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
            columns.add(new EntityFolderColumnDescriptor(proto().amount(), "15em"));
            return columns;
        }

        @Override
        protected IFolderDecorator<InvoiceSubLineItem> createFolderDecorator() {
            TableFolderDecorator<InvoiceSubLineItem> decor = (TableFolderDecorator<InvoiceSubLineItem>) super.createFolderDecorator();
            decor.setShowHeader(false);
            return decor;
        }
    }
}
