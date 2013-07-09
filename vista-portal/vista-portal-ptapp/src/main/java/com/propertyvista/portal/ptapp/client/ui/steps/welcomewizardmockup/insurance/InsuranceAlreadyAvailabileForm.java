/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup.insurance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.moveinwizardmockup.ExistingInsurance;

public class InsuranceAlreadyAvailabileForm extends CEntityDecoratableForm<ExistingInsurance> {

    private final static I18n i18n = I18n.get(InsuranceAlreadyAvailabileForm.class);

    public InsuranceAlreadyAvailabileForm() {
        super(ExistingInsurance.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("Proof of Insurance"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceProvider()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceCertificateNumber()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().personalLiability()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceStartDate()), 10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().insuranceExpirationDate()), 10).build());
        content.setH2(++row, 0, 1, i18n.tr("Attach Insurance Certificate"));
        content.setWidget(++row, 0, inject(proto().documents(), new InsuranceUploaderFolder()));

        addValueValidator(new EditableValueValidator<ExistingInsurance>() {

            @Override
            public ValidationError isValid(CComponent<ExistingInsurance> component, ExistingInsurance value) {
                if (!component.isValid()) {
                    return new ValidationError(component, i18n.tr("Valid Proof of Insurance is Required prior to Move-In"));
                } else {
                    return null;
                }
            }
        });
        return content;
    }

}