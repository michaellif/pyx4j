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
package com.propertyvista.crm.client.ui.crud.tenant.screening;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalAsset.AssetType;

class PersonalAssetFolder extends VistaTableFolder<PersonalAsset> {

    public PersonalAssetFolder() {
        super(PersonalAsset.class);
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        List<EntityFolderColumnDescriptor> columns;
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().assetType(), "15em"));
        columns.add(new EntityFolderColumnDescriptor(proto().percent(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().assetValue(), "15em"));
        return columns;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PersonalAsset) {
            return new PersonalAssetEditor();
        }
        return super.create(member);
    }

    class PersonalAssetEditor extends CEntityFolderRowEditor<PersonalAsset> {

        public PersonalAssetEditor() {
            super(PersonalAsset.class, columns());
        }

        @Override
        public void addValidations() {
            get(proto().percent()).addValueValidator(new EditableValueValidator<Double>() {
                @Override
                public boolean isValid(CEditableComponent<Double, ?> component, Double value) {
                    return (value == null) || ((value >= 0) && (value <= 100));
                }

                @Override
                public String getValidationMessage(CEditableComponent<Double, ?> component, Double value) {
                    return VistaTableFolder.i18n.tr("Value should be in range 0-100%");
                }

            });

            get(proto().assetType()).addValueChangeHandler(new ValueChangeHandler<PersonalAsset.AssetType>() {
                @Override
                public void onValueChange(ValueChangeEvent<AssetType> event) {
                    if (get(proto().percent()).getValue() == null) {
                        get(proto().percent()).setValue(100d);
                    }
                }
            });
        }
    }
}