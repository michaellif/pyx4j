/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.property.vendor.Warranty;
import com.propertyvista.domain.property.vendor.WarrantyItem;

public class CWarranty extends CDecoratableEntityEditor<Warranty> {

    public CWarranty() {
        super(Warranty.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel main = new FormFlexPanel();

        int row = 0;
        main.setWidget(row, 0, decorate(inject(proto().title()), 15));
        main.setWidget(row, 1, decorate(inject(proto().type()), 11));

        main.setHeader(++row, 0, 2, i18n.tr("Contract details"));
//        main.setWidget(++row, 0, inject(proto().cast(), new CContract()));
//      main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setHeader(++row, 0, 2, proto().items().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().items(), new WarrantyItemFolder()));

        main.getColumnFormatter().setWidth(0, "50%");
        main.getColumnFormatter().setWidth(1, "50%");

        return main;
    }

    private class WarrantyItemFolder extends VistaTableFolder<WarrantyItem> {

        public WarrantyItemFolder() {
            super(WarrantyItem.class, CWarranty.this.isEditable());
        }

        @Override
        protected List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns;
            columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "30em"));
            return columns;
        }

        @Override
        protected IFolderDecorator<WarrantyItem> createDecorator() {
            TableFolderDecorator<WarrantyItem> decor = (TableFolderDecorator<WarrantyItem>) super.createDecorator();
            decor.setShowHeader(false);
            return decor;
        }
    }
}
