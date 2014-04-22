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

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset.AssetType;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;
import com.propertyvista.portal.shared.ui.util.decorators.FieldDecoratorBuilder;

public class PersonalAssetFolder extends PortalBoxFolder<CustomerScreeningPersonalAsset> {

    private static final I18n i18n = I18n.get(PersonalAssetFolder.class);

    public PersonalAssetFolder() {
        this(true);
    }

    public PersonalAssetFolder(boolean modifiable) {
        super(CustomerScreeningPersonalAsset.class, i18n.tr("Personal Asset"), modifiable);
    }

    @Override
    public void addValidations() {
        super.addValidations();

        this.addComponentValidator(new AbstractComponentValidator<IList<CustomerScreeningPersonalAsset>>() {
            @Override
            public AbstractValidationError isValid() {
                if (getComponent().getValue() != null) {
                    if (getComponent().getValue().size() > 3) {
                        return new FieldValidationError(getComponent(), i18n.tr("No need to supply more than 3 items"));
                    }
                }
                return null;
            }
        });
    }

    @Override
    protected CForm<CustomerScreeningPersonalAsset> createItemForm(IObject<?> member) {
        return new PersonalAssetEditor();
    }

    private class PersonalAssetEditor extends CForm<CustomerScreeningPersonalAsset> {

        public PersonalAssetEditor() {
            super(CustomerScreeningPersonalAsset.class);
        }

        @Override
        protected IsWidget createContent() {
            BasicFlexFormPanel main = new BasicFlexFormPanel();
            int row = -1;

            main.setWidget(++row, 0, inject(proto().assetType(), new FieldDecoratorBuilder(180).build()));
            main.setWidget(++row, 0, inject(proto().percent(), new FieldDecoratorBuilder(60).build()));
            main.setWidget(++row, 0, inject(proto().assetValue(), new FieldDecoratorBuilder(100).build()));
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