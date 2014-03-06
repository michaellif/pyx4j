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
package com.propertyvista.portal.prospect.ui.application.editors;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset.AssetType;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FormWidgetDecoratorBuilder;

public class PersonalAssetFolder extends PortalBoxFolder<CustomerScreeningPersonalAsset> {

    private static final I18n i18n = I18n.get(PersonalAssetFolder.class);

    public PersonalAssetFolder() {
        super(CustomerScreeningPersonalAsset.class, i18n.tr("Personal Asset"));
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

            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().assetType()), 180).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().percent()), 60).build());
            main.setWidget(++row, 0, new FormWidgetDecoratorBuilder(inject(proto().assetValue()), 100).build());
            main.setWidget(++row, 0, inject(proto().documents(), new ProofOfAssetUploaderFolder()));

            return main;
        }

        @Override
        public void addValidations() {
            get(proto().percent()).addComponentValidator(new AbstractComponentValidator<Double>() {
                @Override
                public FieldValidationError isValid() {
                    return (getComponent().getValue() == null) || ((getComponent().getValue() >= 0) && (getComponent().getValue() <= 100)) ? null
                            : new FieldValidationError(getComponent(), i18n.tr("Value Should Be In Range Of 0-100%"));
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