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
 */
package com.propertyvista.common.client.ui.components.editors;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.TableFolderDecorator;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.property.vendor.Warranty;
import com.propertyvista.domain.property.vendor.WarrantyItem;

public class WarrantyEditor extends CForm<Warranty> {

    private static final I18n i18n = I18n.get(WarrantyEditor.class);

    public WarrantyEditor() {
        super(Warranty.class);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Information"));
        formPanel.append(Location.Left, proto().title()).decorate();
        formPanel.append(Location.Right, proto().type()).decorate().componentWidth(140);

        formPanel.h1(proto().contract().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().contract(), new ContractEditor());

        formPanel.h1(proto().items().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().items(), new WarrantyItemFolder());

        return formPanel;
    }

    private class WarrantyItemFolder extends VistaTableFolder<WarrantyItem> {

        public WarrantyItemFolder() {
            super(WarrantyItem.class, WarrantyEditor.this.isEditable());
        }

        @Override
        public List<FolderColumnDescriptor> columns() {
            List<FolderColumnDescriptor> columns;
            columns = new ArrayList<FolderColumnDescriptor>();
            columns.add(new FolderColumnDescriptor(proto().name(), "50em"));
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
