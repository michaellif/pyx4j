/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.reports;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.ui.components.boxes.PortfolioSelectionDialog;
import com.propertyvista.domain.company.Portfolio;

public class SelectPortfolioFolder extends VistaTableFolder<Portfolio> {

    public SelectPortfolioFolder() {
        super(Portfolio.class, true);
        setOrderable(false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(new FolderColumnDescriptor(proto().name(), "15em"));
    }

    @Override
    protected CForm<Portfolio> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<Portfolio>(Portfolio.class, columns()) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column.getObject() == proto().name()) {
                    return inject(proto().name(), new CLabel<String>());
                }
                return super.createCell(column);
            }
        };
    }

    @Override
    protected IFolderDecorator<Portfolio> createFolderDecorator() {
        return new VistaTableFolderDecorator<Portfolio>(this, this.isEditable()) {
            {
                setShowHeader(false);
            }
        };
    }

    @Override
    protected void addItem() {
        new PortfolioSelectionDialog(new HashSet<>(getValue())) {
            @Override
            public boolean onClickOk() {
                for (Portfolio selected : getSelectedItems()) {
                    addItem(selected);
                }
                return true;
            }
        }.show();
    }
}
