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
package com.propertyvista.common.client.ui.components.folders;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalAsset.AssetType;

public class PersonalAssetFolder extends VistaTableFolder<PersonalAsset> {

    public PersonalAssetFolder(boolean modifyable) {
        super(PersonalAsset.class, modifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        List<EntityFolderColumnDescriptor> columns;
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().assetType(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().percent(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().assetValue(), "15em"));
        return columns;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof PersonalAsset) {
            return new PersonalAssetEditor();
        } else {
            return super.create(member);
        }
    }

    private class PersonalAssetEditor extends CEntityFolderRowEditor<PersonalAsset> {

        public PersonalAssetEditor() {
            super(PersonalAsset.class, columns());
        }

        @Override
        public void addValidations() {
            get(proto().percent()).addValueValidator(new EditableValueValidator<Double>() {
                @Override
                public boolean isValid(CComponent<Double, ?> component, Double value) {
                    return (value == null) || ((value >= 0) && (value <= 100));
                }

                @Override
                public String getValidationMessage(CComponent<Double, ?> component, Double value) {
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