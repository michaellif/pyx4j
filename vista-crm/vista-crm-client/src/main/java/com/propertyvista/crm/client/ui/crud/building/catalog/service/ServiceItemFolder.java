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
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.dialogs.EntitySelectorTableDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.UnitSelectorDialog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.unit.AptUnit;

class ServiceItemFolder extends VistaTableFolder<ProductItem> {

    private final CEntityForm<Service> parent;

    public ServiceItemFolder(CEntityForm<Service> parent) {
        super(ProductItem.class, parent.isEditable());
        this.parent = parent;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().price(), "8em"));
        columns.add(new EntityFolderColumnDescriptor(proto().element(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ProductItem) {
            return new ServiceItemEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        EntitySelectorTableDialog<?> buildingElementSelectionBox = null;
        switch (parent.getValue().serviceType().getValue()) {
        case residentialUnit:
// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//        case residentialShortTermUnit:
        case commercialUnit:
            List<AptUnit> alreadySelected = new ArrayList<AptUnit>(getValue().size());
            for (ProductItem item : getValue()) {
                alreadySelected.add((AptUnit) item.element().cast());
            }
            buildingElementSelectionBox = new UnitSelectorDialog(true, alreadySelected) {
                @Override
                protected void setFilters(List<Criterion> filters) {
                    super.setFilters(filters);
                    addFilter(PropertyCriterion.eq(EntityFactory.getEntityPrototype(AptUnit.class).building().productCatalog(), parent.getValue().catalog()));
                }

                @Override
                public boolean onClickOk() {
                    return processSelectedItems(getSelectedItems());
                }
            };
            break;

// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//        case garage:
//            break;
//        case storage:
//            break;
//        case roof:
//            break;
        }

        if (buildingElementSelectionBox != null) {
            buildingElementSelectionBox.show();
        } else {
            super.addItem();
        }
    }

    private boolean processSelectedItems(List<? extends BuildingElement> selectedItems) {
        if (selectedItems.isEmpty()) {
            return false;
        } else {
            for (BuildingElement element : selectedItems) {
                ProductItem item = EntityFactory.create(ProductItem.class);
                item.element().set(element);
                addItem(item);
            }
            return true;
        }
    }

    private class ServiceItemEditor extends CEntityFolderRowEditor<ProductItem> {

        public ServiceItemEditor() {
            super(ProductItem.class, columns());
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            boolean isViewable = false;
            Class<? extends IEntity> buildingElementClass = null;
            switch (parent.getValue().serviceType().getValue()) {
            case residentialUnit:
// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//            case residentialShortTermUnit:
            case commercialUnit:
                buildingElementClass = AptUnit.class;
                isViewable = true;
                break;
// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//            case garage:
//                buildingElementClass = Parking.class;
//                break;
//            case storage:
//                buildingElementClass = LockerArea.class;
//                break;
//            case roof:
//                buildingElementClass = Roof.class;
//                break;
            }

            CComponent<?, ?> comp;
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
                    comp = new CLabel(""); // there is no building element for this item!
                }
            } else if (column.getObject() == proto().type()) {
                comp = inject(column.getObject(), new CEntityComboBox<ServiceItemType>(ServiceItemType.class));
            } else {
                comp = super.createCell(column);
            }

            if (column.getObject() == proto().type()) {
                if (parent.isEditable() && comp instanceof CEntityComboBox<?>) {
                    final CEntityComboBox<ServiceItemType> combo = (CEntityComboBox<ServiceItemType>) comp;
                    combo.addCriterion(PropertyCriterion.eq(combo.proto().serviceType(), parent.getValue().serviceType()));
// TODO : preselect if single option:                    
//                    combo.addCriterion(PropertyCriterion.eq(combo.proto().serviceType(), parent.getValue().version().type()));
//                    combo.addOptionsChangeHandler(new OptionsChangeHandler<List<ProductItemType>>() {
//                        @Override
//                        public void onOptionsChange(OptionsChangeEvent<List<ProductItemType>> event) {
//                            if (event.getOptions().size() == 1) {
//                                combo.setValue(event.getOptions().get(0), false);
//                                combo.setViewable(true);
//                            }
//                        }
//                    });
                }
            }

            return comp;
        }
    }
}