/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.complex;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;

public class ComplexBuildingFolder extends VistaTableFolder<Building> {

    private static final I18n i18n = I18n.get(ComplexBuildingFolder.class);

    public ComplexBuildingFolder(boolean modifyable) {
        super(Building.class, modifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        List<EntityFolderColumnDescriptor> columns;
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().propertyCode(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().info(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().complexPrimary(), "7em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Building) {
            return new ComplexBuildingEditor();
        }
        return super.create(member);
    }

    private class ComplexBuildingEditor extends CEntityFolderRowEditor<Building> {

        public ComplexBuildingEditor() {
            super(Building.class, columns());
            setViewable(true);
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof BuildingInfo) {
                return new CEntityLabel<BuildingInfo>();
            }
            return super.create(member);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().complexPrimary() && isEditable()) {
                CComponent<?, ?> comp = inject(column.getObject());
                comp.inheritViewable(false); // always not viewable!
                return comp;
            }
            return super.createCell(column);
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().complexPrimary()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue().booleanValue()) {
                        for (int i = 0; i < ComplexBuildingFolder.this.getItemCount(); ++i) {
                            for (CComponent<?, ?> comp : ComplexBuildingFolder.this.getItem(i).getComponents()) {
                                if (comp instanceof ComplexBuildingEditor && !comp.equals(ComplexBuildingEditor.this)) {
                                    ((ComplexBuildingEditor) comp).get(proto().complexPrimary()).setValue(false);
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}