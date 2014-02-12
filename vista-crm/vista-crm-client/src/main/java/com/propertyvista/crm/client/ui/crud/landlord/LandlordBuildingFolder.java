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
package com.propertyvista.crm.client.ui.crud.landlord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;

public class LandlordBuildingFolder extends VistaTableFolder<Building> {

    private static final I18n i18n = I18n.get(LandlordBuildingFolder.class);

    private final CrmEntityForm<?> parentForm;

    public LandlordBuildingFolder(CrmEntityForm<?> parentForm) {
        super(Building.class, parentForm.isEditable());
        this.parentForm = parentForm;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        List<EntityFolderColumnDescriptor> columns;
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().propertyCode(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().info(), "20em"));
        return columns;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Building) {
            return new LandlordBuildingEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new BuildingSelectorDialog(parentForm.getParentView(), getValue()) {
            @Override
            protected void setFilters(List<Criterion> filters) {
                super.setFilters(filters);
                addFilter(PropertyCriterion.eq(LandlordBuildingFolder.this.proto().landlord(), (Serializable) null));
            }

            @Override
            public void onClickOk() {
                for (Building selected : getSelectedItems()) {
                    addItem(selected);
                }
            }
        }.show();
    }

    private class LandlordBuildingEditor extends CEntityFolderRowEditor<Building> {

        public LandlordBuildingEditor() {
            super(Building.class, columns());
            setViewable(true);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            if (proto().propertyCode() == column.getObject()) {
                CComponent<?> comp = inject(proto().propertyCode());
                ((CField) comp).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Building.class).formViewerPlace(getValue().getPrimaryKey()));
                    }
                });
                return comp;
            } else if (proto().info() == column.getObject()) {
                return inject(proto().info(), new CEntityLabel<BuildingInfo>());
            }
            return super.createCell(column);
        }

    }

}