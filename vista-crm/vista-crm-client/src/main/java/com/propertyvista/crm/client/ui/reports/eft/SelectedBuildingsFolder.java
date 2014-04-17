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
import java.util.List;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.domain.property.asset.building.Building;

public class SelectedBuildingsFolder extends VistaTableFolder<Building> {

    public enum Styles implements IStyleName {

        SelectedBuildingsFolder
    }

    private static List<EntityFolderColumnDescriptor> COLUMNS;
    static {
        Building proto = EntityFactory.getEntityPrototype(Building.class);
        COLUMNS = Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto.propertyCode(), "100px")
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
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof Building) {
            return (T) new CEntityFolderRowEditor<Building>(Building.class, COLUMNS) {
                @Override
                protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                    if (column.getObject() == proto().propertyCode()) {
                        return inject(proto().propertyCode());
                    }
                    return super.createCell(column);
                }
            };
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new BuildingSelectorDialog(parentView, getValue()) {
            @Override
            public void onClickOk() {
                for (Building building : getSelectedItems()) {
                    Building b = EntityFactory.create(Building.class);
                    b.setPrimaryKey(building.getPrimaryKey());
                    for (EntityFolderColumnDescriptor c : COLUMNS) {
                        b.setValue(c.getObject().getPath(), building.getValue(c.getObject().getPath()));
                    }
                    addItem(b);
                }
            }
        }.show();
    }

}
