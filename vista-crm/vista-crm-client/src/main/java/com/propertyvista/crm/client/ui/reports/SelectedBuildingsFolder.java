/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-14
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
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectionDialog;
import com.propertyvista.domain.property.asset.building.Building;

public class SelectedBuildingsFolder extends VistaTableFolder<Building> {

    public SelectedBuildingsFolder() {
        super(Building.class, true);
        setOrderable(false);
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return Arrays.asList(new FolderColumnDescriptor(proto().propertyCode(), "15em"));
    }

    @Override
    protected CForm<Building> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<Building>(Building.class, columns()) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column.getObject() == proto().propertyCode()) {
                    return inject(proto().propertyCode(), new CLabel<String>());
                }
                return super.createCell(column);
            }
        };
    }

    @Override
    protected IFolderDecorator<Building> createFolderDecorator() {
        return new VistaTableFolderDecorator<Building>(this, this.isEditable()) {
            {
                setShowHeader(false);
            }
        };
    }

    @Override
    protected void addItem() {
        new BuildingSelectionDialog(new HashSet<>(getValue())) {
            @Override
            public boolean onClickOk() {
                for (Building building : getSelectedItems()) {
                    addItem(building);
                }
                return true;
            }
        }.show();
    }

}
