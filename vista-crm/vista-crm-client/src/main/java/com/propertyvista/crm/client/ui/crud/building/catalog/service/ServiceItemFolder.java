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

import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.crm.client.mvp.MainActivityMapper;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.unit.AptUnit;

class ServiceItemFolder extends VistaTableFolder<ServiceItem> {

    private final CEntityEditor<Service> parent;

    public ServiceItemFolder(CEntityEditor<Service> parent) {
        super(ServiceItem.class, parent.isEditable());
        this.parent = parent;
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().price(), "8em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().element(), "15em"));
        return columns;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ServiceItem) {
            return new ServiceItemEditor();
        }
        return super.create(member);
    }

    class ServiceItemEditor extends CEntityFolderRowEditor<ServiceItem> {

        public ServiceItemEditor() {
            super(ServiceItem.class, columns());
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            Class<? extends IEntity> buildingElementClass = null;
            switch (parent.getValue().type().getValue()) {
            case residentialUnit:
            case residentialShortTermUnit:
            case commercialUnit:
                buildingElementClass = AptUnit.class;
                break;
            case garage:
                buildingElementClass = Parking.class;
                break;
            case storage:
                buildingElementClass = LockerArea.class;
                break;
            case roof:
                buildingElementClass = Roof.class;
                break;
            }

            CComponent<?> comp;
            if (column.getObject() == proto().element()) {
                if (parent.isEditable()) {
                    comp = inject(column.getObject(), new CEntityComboBox(buildingElementClass));
                    CEntityComboBox<BuildingElement> combo = (CEntityComboBox) comp;
                    combo.addCriterion(PropertyCriterion.eq(combo.proto().belongsTo(), parent.getValue().catalog().belongsTo().detach()));
                } else {
                    comp = inject(column.getObject(), new CEntityCrudHyperlink<AptUnit>(MainActivityMapper.getCrudAppPlace(buildingElementClass)));
                }
            } else {
                comp = super.createCell(column);
            }

            if (column.getObject() == proto().type()) {
                if (comp instanceof CEntityComboBox<?>) {
                    CEntityComboBox<ServiceItemType> combo = (CEntityComboBox<ServiceItemType>) comp;
                    combo.addCriterion(PropertyCriterion.eq(combo.proto().serviceType(), parent.getValue().type().getValue()));
                }
            }

            return comp;

        }
    }
}