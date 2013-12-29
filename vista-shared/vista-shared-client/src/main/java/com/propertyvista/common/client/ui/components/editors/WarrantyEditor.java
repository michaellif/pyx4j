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
package com.propertyvista.common.client.ui.components.editors;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.property.vendor.Warranty;
import com.propertyvista.domain.property.vendor.WarrantyItem;

public class WarrantyEditor extends CEntityDecoratableForm<Warranty> {

    private static final I18n i18n = I18n.get(WarrantyEditor.class);

    public WarrantyEditor() {
        super(Warranty.class);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Information"));
        main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().title()), 20).build());
        main.setWidget(row, 1, new FormDecoratorBuilder(inject(proto().type()), 11).build());

        main.setH1(++row, 0, 2, proto().contract().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().contract(), new ContractEditor()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        main.setH1(++row, 0, 2, proto().items().getMeta().getCaption());
        main.setWidget(++row, 0, inject(proto().items(), new WarrantyItemFolder()));
        main.getFlexCellFormatter().setColSpan(row, 0, 2);

        return main;
    }

    private class WarrantyItemFolder extends VistaTableFolder<WarrantyItem> {

        public WarrantyItemFolder() {
            super(WarrantyItem.class, WarrantyEditor.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            List<EntityFolderColumnDescriptor> columns;
            columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "50em"));
            return columns;
        }

        @Override
        protected IFolderDecorator<WarrantyItem> createFolderDecorator() {
            TableFolderDecorator<WarrantyItem> decor = (TableFolderDecorator<WarrantyItem>) super.createFolderDecorator();
            decor.setShowHeader(false);
            return decor;
        }
    }
}
