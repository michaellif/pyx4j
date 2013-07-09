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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
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
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof Building) {
            return new ComplexBuildingEditor();
        }
        return super.create(member);
    }

    @Override
    protected void addItem() {
        new BuildingSelectorDialog(true, getValue()) {
            @Override
            protected void setFilters(List<Criterion> filters) {
                super.setFilters(filters);
                addFilter(PropertyCriterion.eq(ComplexBuildingFolder.this.proto().complex(), (Serializable) null));
            }

            @Override
            public boolean onClickOk() {
                if (getSelectedItems().isEmpty()) {
                    return false;
                } else {
                    for (Building selected : getSelectedItems()) {
                        selected.complexPrimary().setValue(false);
                        addItem(selected);
                    }
                    return true;
                }
            }
        }.show();
    }

    private class ComplexBuildingEditor extends CEntityFolderRowEditor<Building> {

        public ComplexBuildingEditor() {
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
            } else if (column.getObject() == proto().complexPrimary() && isEditable()) {
                CComponent<?> comp = inject(column.getObject());
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
                            for (CComponent<?> comp : ComplexBuildingFolder.this.getItem(i).getComponents()) {
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

    @Override
    public void addValidations() {
        super.addValidations();

        this.addValueValidator(new EditableValueValidator<IList<Building>>() {
            @Override
            public ValidationError isValid(CComponent<IList<Building>> component, IList<Building> value) {
                if (value != null && !value.isEmpty()) {
                    boolean primaryFound = false;
                    for (Building item : value) {
                        if (item.complexPrimary().isBooleanTrue()) {
                            primaryFound = true;
                            break;
                        }
                    }
                    if (!primaryFound) {
                        return new ValidationError(component, i18n.tr("Primary building should be selected"));
                    }
                }
                return null;
            }
        });
    }
}