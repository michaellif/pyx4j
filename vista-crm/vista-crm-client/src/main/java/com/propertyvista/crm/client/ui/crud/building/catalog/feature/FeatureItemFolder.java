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
package com.propertyvista.crm.client.ui.crud.building.catalog.feature;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.CEntityForm;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.BuildingElement;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;

class FeatureItemFolder extends VistaTableFolder<ProductItem> {

    private static final I18n i18n = I18n.get(FeatureItemFolder.class);

    private final CEntityForm<Feature> parent;

    public FeatureItemFolder(CEntityForm<Feature> parent) {
        super(ProductItem.class, parent.isEditable());
        this.parent = parent;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().price(), "8em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().element(), "15em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof ProductItem) {
            return new FeatureItemEditor();
        }
        return super.create(member);
    }

    private class FeatureItemEditor extends CEntityFolderRowEditor<ProductItem> {

        public FeatureItemEditor() {
            super(ProductItem.class, columns());
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            Class<? extends IEntity> buildingElementClass = null;
            switch (parent.getValue().version().type().getValue()) {
            case parking:
                buildingElementClass = Parking.class;
                break;
            case locker:
                buildingElementClass = LockerArea.class;
                break;
            }

            CComponent<?, ?> comp;
            if (column.getObject() == proto().element()) {
                if (buildingElementClass != null) {
                    if (parent.isEditable()) {
                        CEntityComboBox<BuildingElement> combo = new CEntityComboBox(buildingElementClass);
                        combo.addCriterion(PropertyCriterion.eq(combo.proto().belongsTo().productCatalog(), parent.getValue().catalog()));
                        comp = inject(column.getObject(), combo);
                    } else {
                        comp = inject(column.getObject(), new CEntityCrudHyperlink<BuildingElement>(AppPlaceEntityMapper.resolvePlace(buildingElementClass)));
                    }
                } else {
                    comp = new CLabel(""); // there is no building element for this item!
                }
            } else if (column.getObject() == proto().type()) {
                comp = inject(column.getObject(), new CEntityComboBox<FeatureItemType>(FeatureItemType.class));
            } else {
                comp = super.createCell(column);
            }

            if (column.getObject() == proto().type()) {
                if (parent.isEditable() && comp instanceof CEntityComboBox<?>) {
                    final CEntityComboBox<FeatureItemType> combo = (CEntityComboBox<FeatureItemType>) comp;
                    combo.addCriterion(PropertyCriterion.eq(combo.proto().featureType(), parent.getValue().version().type()));
                    combo.addValueChangeHandler(new ValueChangeHandler<FeatureItemType>() {
                        @Override
                        public void onValueChange(ValueChangeEvent<FeatureItemType> event) {
                            for (FeatureItemType item : parent.getValue().catalog().includedUtilities()) {
                                if (item.equals(event.getValue())) {
                                    MessageDialog.warn(i18n.tr("Note"), i18n.tr("The Selected Utility Type Is Included In The Price"));
                                }
                            }
                        }
                    });

// TODO : preselect if single option:                    
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