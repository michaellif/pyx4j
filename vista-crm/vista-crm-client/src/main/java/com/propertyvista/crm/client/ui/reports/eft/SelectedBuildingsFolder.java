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
package com.propertyvista.crm.client.ui.reports.eft;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.domain.property.asset.building.Building;

public class SelectedBuildingsFolder extends VistaTableFolder<Building> {

    public enum Styles implements IStyleName {

        SelectedBuildingsFolder
    }

    private static List<FolderColumnDescriptor> COLUMNS;
    static {
        Building proto = EntityFactory.getEntityPrototype(Building.class);
        COLUMNS = Arrays.asList(//@formatter:off
                new FolderColumnDescriptor(proto.propertyCode(), "100px")
        );//@formatter:on
    }

    private final IPane parentView;

    public SelectedBuildingsFolder(IPane parentView) {
        super(Building.class);
        this.parentView = parentView;
        setOrderable(false);
        asWidget().addStyleName(Styles.SelectedBuildingsFolder.name());
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    protected CForm<Building> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<Building>(Building.class, COLUMNS) {
            @Override
            protected CField<?, ?> createCell(FolderColumnDescriptor column) {
                if (column.getObject() == proto().propertyCode()) {
                    return inject(proto().propertyCode());
                }
                return super.createCell(column);
            }
        };
    }

    @Override
    protected void addItem() {
        new BuildingSelectorDialog(parentView, new HashSet<>(getValue())) {
            @Override
            public void onClickOk() {
                for (Building building : getSelectedItems()) {
                    Building b = EntityFactory.create(Building.class);
                    b.setPrimaryKey(building.getPrimaryKey());
                    for (FolderColumnDescriptor c : COLUMNS) {
                        b.setValue(c.getObject().getPath(), building.getValue(c.getObject().getPath()));
                    }
                    addItem(b);
                }
            }
        }.show();
    }

}
