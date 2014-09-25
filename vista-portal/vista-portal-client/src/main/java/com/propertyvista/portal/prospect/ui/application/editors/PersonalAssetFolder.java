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

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.AbstractValidationError;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset;
import com.propertyvista.domain.tenant.income.CustomerScreeningPersonalAsset.AssetType;
import com.propertyvista.portal.shared.ui.util.PortalBoxFolder;

public class PersonalAssetFolder extends PortalBoxFolder<CustomerScreeningPersonalAsset> {

    private static final I18n i18n = I18n.get(PersonalAssetFolder.class);

    public PersonalAssetFolder() {
        this(true);
    }

    public PersonalAssetFolder(boolean editable) {
        super(CustomerScreeningPersonalAsset.class, i18n.tr("Personal Asset"), editable);

        if (editable) {
            setNoDataLabel(i18n.tr("Please enter your asset(s) if present"));
        }
    }

    @Override
    public void addValidations() {
        super.addValidations();

        this.addComponentValidator(new AbstractComponentValidator<IList<CustomerScreeningPersonalAsset>>() {
            @Override
            public AbstractValidationError isValid() {
                if (getComponent().getValue() != null) {
                    if (getComponent().getValue().size() > 3) {
                        return new BasicValidationError(getComponent(), i18n.tr("No need to supply more than 3 items"));
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
            FormPanel formPanel = new FormPanel(this);

            formPanel.append(Location.Left, proto().assetType()).decorate().componentWidth(180);
            formPanel.append(Location.Left, proto().ownership()).decorate().componentWidth(60);
            formPanel.append(Location.Left, proto().assetValue()).decorate().componentWidth(100);
            formPanel.append(Location.Left, proto().documents(), new ProofOfAssetUploaderFolder());

            return formPanel;
        }

        @Override
        public void addValidations() {
            get(proto().assetType()).addValueChangeHandler(new ValueChangeHandler<CustomerScreeningPersonalAsset.AssetType>() {
                @Override
                public void onValueChange(ValueChangeEvent<AssetType> event) {
                    if (get(proto().ownership()).getValue() == null) {
                        get(proto().ownership()).setValue(BigDecimal.ONE);
                    }
                }
            });
        }
    }
}