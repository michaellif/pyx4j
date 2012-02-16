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
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingCrudService;
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

    @Override
    protected void addItem() {
        new BuildingSelectorDialog().show();
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

    private class BuildingSelectorDialog extends EntitySelectorDialog<Building> {

        public BuildingSelectorDialog() {
            super(Building.class, true, getValue(), i18n.tr("Select Building"));
            addFilter(new DataTableFilterData(ComplexBuildingFolder.this.proto().complex().getPath(), Operators.is, null));
            setWidth("700px");
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (Building item : getSelectedItems()) {
                    item.complexPrimary().setValue(false);
                    addItem(item);
                }
                return true;
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().propertyCode(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().complex(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().externalId(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().propertyManager(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().info().name(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().info().type(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().info().shape(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().streetNumber(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().streetNumberSuffix(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().streetName(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().streetType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().streetDirection(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().city(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().province(), true).build(),
                    new MemberColumnDescriptor.Builder(proto().info().address().country(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().marketing().visibility(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().totalStoreys(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().residentialStoreys(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().structureType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().structureBuildYear(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().constructionType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().foundationType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().floorType(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().landArea(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().waterSupply(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().centralAir(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().info().centralHeat(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().contacts().website(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().contacts().email(), false).title(proto().contacts().email()).build(),
                    new MemberColumnDescriptor.Builder(proto().financial().dateAcquired(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().financial().purchasePrice(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().financial().marketPrice(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalDate(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().financial().lastAppraisalValue(), false).build(),
                    new MemberColumnDescriptor.Builder(proto().financial().currency().name(), false).title(proto().financial().currency()).build(),
                    new MemberColumnDescriptor.Builder(proto().marketing().name(), false).title(i18n.tr("Marketing Name")).build()
            );//@formatter:on
        }

        @Override
        protected AbstractListService<Building> getSelectService() {
            return GWT.<AbstractListService<Building>> create(SelectBuildingCrudService.class);
        }

    }
}