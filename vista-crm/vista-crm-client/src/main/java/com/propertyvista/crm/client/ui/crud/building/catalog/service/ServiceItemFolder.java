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
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.shared.config.VistaFeatures;

class ServiceItemFolder extends VistaTableFolder<ProductItem> {

    private final CrmEntityForm<Service> parent;

    public ServiceItemFolder(CrmEntityForm<Service> parent) {
        super(ProductItem.class, parent.isEditable());
        this.parent = parent;
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        ArrayList<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();

        columns.add(new FolderColumnDescriptor(proto().name(), "20em"));
        columns.add(new FolderColumnDescriptor(proto().price(), "8em"));
        columns.add(new FolderColumnDescriptor(proto().element(), "10em"));
        if (VistaFeatures.instance().yardiIntegration()) {
            columns.add(new FolderColumnDescriptor(proto().depositLMR(), "10em"));
        } else {
            columns.add(new FolderColumnDescriptor(proto().depositLMR(), "5em"));
            columns.add(new FolderColumnDescriptor(proto().depositMoveIn(), "5em"));
            columns.add(new FolderColumnDescriptor(proto().depositSecurity(), "5em"));
        }
        columns.add(new FolderColumnDescriptor(proto().description(), "25em"));

        return columns;
    }

    @Override
    protected CForm<ProductItem> createItemForm(IObject<?> member) {
        return new ServiceItemEditor();
    }

    @Override
    protected void addItem() {
        IShowable buildingElementSelectionBox = null;
        if (ARCode.Type.unitRelatedServices().contains(parent.getValue().code().type().getValue())) {
            Set<AptUnit> alreadySelected = new HashSet<AptUnit>(getValue().size());
            for (ProductItem item : getValue()) {
                alreadySelected.add((AptUnit) item.element().cast());
            }
            buildingElementSelectionBox = new UnitSelectorDialog(parent.getParentView(), alreadySelected) {
                @Override
                protected void setFilters(List<Criterion> filters) {
                    super.setFilters(filters);
                    addFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(AptUnit.class).building().productCatalog(), parent.getValue().catalog()));
                }

                @Override
                public void onClickOk() {
                    for (BuildingElement element : getSelectedItems()) {
                        ProductItem item = EntityFactory.create(ProductItem.class);
                        item.element().set(element);
                        addItem(item);
                    }
                }
            };
        }

        if (buildingElementSelectionBox != null) {
            buildingElementSelectionBox.show();
        } else {
            super.addItem();
        }
    }

    private class ServiceItemEditor extends CFolderRowEditor<ProductItem> {

        public ServiceItemEditor() {
            super(ProductItem.class, columns());
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected CField<?, ?> createCell(FolderColumnDescriptor column) {
            boolean isViewable = false;
            Class<? extends IEntity> buildingElementClass = null;

            if (ARCode.Type.unitRelatedServices().contains(parent.getValue().code().type().getValue())) {
                buildingElementClass = AptUnit.class;
                isViewable = true;
            }

            CField<?, ?> comp;
            if (column.getObject() == proto().element()) {
                if (buildingElementClass != null) {
                    if (parent.isEditable()) {
                        CEntityComboBox<BuildingElement> combo = new CEntityComboBox(buildingElementClass);
                        combo.addCriterion(PropertyCriterion.eq(combo.proto().building().productCatalog(), parent.getValue().catalog()));
                        comp = inject(column.getObject(), combo);
                        comp.setViewable(isViewable);
                    } else {
                        comp = inject(column.getObject(), new CEntityCrudHyperlink<BuildingElement>(AppPlaceEntityMapper.resolvePlace(buildingElementClass)));
                    }
                } else {
                    comp = new CLabel(); // there is no building element for this item!
                }
            } else {
                comp = super.createCell(column);
            }

            return comp;
        }
    }
}