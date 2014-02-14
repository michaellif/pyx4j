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
package com.propertyvista.crm.client.ui.crud.lease.application.components;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset.AssetType;

public class PersonalAssetFolder extends VistaBoxFolder<CustomerScreeningPersonalAsset> {

    private static final I18n i18n = I18n.get(PersonalAssetFolder.class);

    public PersonalAssetFolder(boolean modifyable) {
        super(CustomerScreeningPersonalAsset.class, modifyable);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof CustomerScreeningPersonalAsset) {
            return new PersonalAssetEditor();
        }
        return super.create(member);
    }

    private class PersonalAssetEditor extends CEntityForm<CustomerScreeningPersonalAsset> {

        public PersonalAssetEditor() {
            super(CustomerScreeningPersonalAsset.class);
        }

        @Override
        public IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();
            int row = -1;

            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().assetType()), 15).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().percent()), 5).build());
            main.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().assetValue()), 10).build());
            main.setWidget(++row, 0, inject(proto().documents(), new ProofOfAssetUploaderFolder()));

            return main;
        }

        @Override
        public void addValidations() {
            get(proto().percent()).addValueValidator(new EditableValueValidator<Double>() {
                @Override
                public FieldValidationError isValid(CComponent<Double> component, Double value) {
                    return (value == null) || ((value >= 0) && (value <= 100)) ? null : new FieldValidationError(component, i18n
                            .tr("Value Should Be In Range Of 0-100%"));
                }

            });

            get(proto().assetType()).addValueChangeHandler(new ValueChangeHandler<CustomerScreeningPersonalAsset.AssetType>() {
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