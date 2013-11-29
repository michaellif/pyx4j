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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.events.OptionsChangeEvent;
import com.pyx4j.forms.client.events.OptionsChangeHandler;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityComboBox;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.prime.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
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
        columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().price(), "8em"));
        columns.add(new EntityFolderColumnDescriptor(proto().element(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "25em"));
        return columns;
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
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
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            Class<? extends IEntity> buildingElementClass = null;
            switch (parent.getValue().code().type().getValue()) {
            case Parking:
                buildingElementClass = Parking.class;
                break;
            case Locker:
                buildingElementClass = LockerArea.class;
                break;
            }

            CComponent<?> comp;
            if (column.getObject() == proto().element()) {
                if (buildingElementClass != null) {
                    if (parent.isEditable()) {
                        CEntityComboBox<BuildingElement> combo = new CEntityComboBox(buildingElementClass);
                        combo.addCriterion(PropertyCriterion.eq(combo.proto().building().productCatalog(), parent.getValue().catalog()));
                        comp = inject(column.getObject(), combo);
                    } else {
                        comp = inject(column.getObject(), new CEntityCrudHyperlink<BuildingElement>(AppPlaceEntityMapper.resolvePlace(buildingElementClass)));
                    }
                } else {
                    comp = new CLabel(""); // there is no building element for this item!
                }
            } else if (column.getObject() == proto().product().holder().code()) {
                comp = inject(column.getObject(), new CEntityComboBox<ARCode>(ARCode.class));
            } else {
                comp = super.createCell(column);
            }

            if (column.getObject() == proto().name()) {
                if (parent.isEditable() && comp instanceof CEntityComboBox<?>) {
                    final CEntityComboBox<ARCode> combo = (CEntityComboBox<ARCode>) comp;
//TODO                    combo.addCriterion(PropertyCriterion.eq(combo.proto().type(), parent.getValue().type()));
                    // preselect if single option:                    
                    combo.addOptionsChangeHandler(new OptionsChangeHandler<List<ARCode>>() {
                        @Override
                        public void onOptionsChange(OptionsChangeEvent<List<ARCode>> event) {
                            if (event.getOptions().size() == 1) {
                                if (combo.getValue() == null) {
                                    combo.setValue(event.getOptions().get(0), false);
                                }
                                combo.setEditable(false);
                            }
                        }
                    });
                }
            }
            return comp;
        }

        @Override
        public void addValidations() {
            super.addValidations();

            get(proto().isDefault()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue() != null && event.getValue().booleanValue()) {
                        for (int i = 0; i < FeatureItemFolder.this.getItemCount(); ++i) {
                            for (CComponent<?> comp : FeatureItemFolder.this.getItem(i).getComponents()) {
                                if (comp instanceof FeatureItemEditor && !comp.equals(FeatureItemEditor.this)) {
                                    ((FeatureItemEditor) comp).get(proto().isDefault()).setValue(false);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        this.addValueValidator(new EditableValueValidator<IList<ProductItem>>() {
            @Override
            public ValidationError isValid(CComponent<IList<ProductItem>> component, IList<ProductItem> value) {
                if (value != null && !value.isEmpty()) {
                    CComponent<Boolean> comp = parent.get(parent.proto().version().mandatory());
                    if (comp != null && comp.getValue() != null && comp.getValue()) {
                        boolean defaultFound = false;
                        for (ProductItem item : value) {
                            if (item.isDefault().isBooleanTrue()) {
                                defaultFound = true;
                                break;
                            }
                        }
                        if (!defaultFound) {
                            return new ValidationError(component, i18n.tr("Default item should be selected"));
                        }
                    }
                }

                return null;
            }
        });
    }
}