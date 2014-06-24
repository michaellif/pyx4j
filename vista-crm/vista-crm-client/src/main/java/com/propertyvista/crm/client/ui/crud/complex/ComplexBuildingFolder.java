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
import java.util.HashSet;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;

public class ComplexBuildingFolder extends VistaTableFolder<Building> {

    private static final I18n i18n = I18n.get(ComplexBuildingFolder.class);

    private final CrmEntityForm<?> parentForm;

    public ComplexBuildingFolder(CrmEntityForm<?> parentForm) {
        super(Building.class, parentForm.isEditable());
        this.parentForm = parentForm;
    }

    @Override
    public List<FolderColumnDescriptor> columns() {
        List<FolderColumnDescriptor> columns;
        columns = new ArrayList<FolderColumnDescriptor>();
        columns.add(new FolderColumnDescriptor(proto().propertyCode(), "10em"));
        columns.add(new FolderColumnDescriptor(proto().info(), "20em"));
        columns.add(new FolderColumnDescriptor(proto().complexPrimary(), "7em"));
        return columns;
    }

    @Override
    protected CForm<Building> createItemForm(IObject<?> member) {
        return new ComplexBuildingEditor();
    }

    @Override
    protected void addItem() {
        new BuildingSelectorDialog(parentForm.getParentView(), new HashSet<>(getValue())) {
            @Override
            protected void setFilters(List<Criterion> filters) {
                super.setFilters(filters);
                addFilter(PropertyCriterion.eq(ComplexBuildingFolder.this.proto().complex(), (Serializable) null));
            }

            @Override
            public void onClickOk() {
                for (Building selected : getSelectedItems()) {
                    selected.complexPrimary().setValue(false);
                    addItem(selected);
                }
            }
        }.show();
    }

    private class ComplexBuildingEditor extends CFolderRowEditor<Building> {

        public ComplexBuildingEditor() {
            super(Building.class, columns());
            setViewable(true);
        }

        @SuppressWarnings("rawtypes")
        @Override
        protected CField<?, ?> createCell(FolderColumnDescriptor column) {
            if (proto().propertyCode() == column.getObject()) {
                CField<?, ?> comp = inject(proto().propertyCode());
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
                CField<?, ?> comp = inject(column.getObject());
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
                            for (CComponent<?, ?, ?> comp : ComplexBuildingFolder.this.getItem(i).getComponents()) {
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

        this.addComponentValidator(new AbstractComponentValidator<IList<Building>>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && !getComponent().getValue().isEmpty()) {
                    boolean primaryFound = false;
                    for (Building item : getComponent().getValue()) {
                        if (item.complexPrimary().getValue(false)) {
                            primaryFound = true;
                            break;
                        }
                    }
                    if (!primaryFound) {
                        return new BasicValidationError(getComponent(), i18n.tr("Primary building should be selected"));
                    }
                }
                return null;
            }
        });
    }
}