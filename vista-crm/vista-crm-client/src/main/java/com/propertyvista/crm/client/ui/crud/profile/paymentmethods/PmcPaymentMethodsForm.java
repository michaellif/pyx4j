/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-08
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.profile.paymentmethods;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.admin.PmcPaymentMethodsDTO;
import com.propertyvista.domain.pmc.PmcPaymentMethod;

public class PmcPaymentMethodsForm extends CrmEntityForm<PmcPaymentMethodsDTO> {

    private static final I18n i18n = I18n.get(PmcPaymentMethod.class);

    private final Label noPaymentMethodsMessage;

    public PmcPaymentMethodsForm(IForm<PmcPaymentMethodsDTO> view) {
        super(PmcPaymentMethodsDTO.class, view);
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Payment Methods"));
        int row = -1;

        noPaymentMethodsMessage = new Label();
        noPaymentMethodsMessage.setText(i18n.tr("There are no payment methods. Click 'Edit' to add a new payment method"));
        noPaymentMethodsMessage.setVisible(false);
        content.setWidget(++row, 0, noPaymentMethodsMessage);
        content.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

        content.setWidget(++row, 0, inject(proto().paymentMethods(), new PmcPaymentMethodFolder()));
        selectTab(addTab(content));

        addValueValidator(new EditableValueValidator<PmcPaymentMethodsDTO>() {
            @Override
            public ValidationError isValid(CComponent<PmcPaymentMethodsDTO> component, PmcPaymentMethodsDTO paymentMethodsHolder) {
                if (paymentMethodsHolder != null) {
                    boolean hasEquifaxMethod = false;
                    for (PmcPaymentMethod pmcPaymentMethod : paymentMethodsHolder.paymentMethods()) {
                        if (pmcPaymentMethod.selectForEquifaxPayments().isBooleanTrue()) {
                            hasEquifaxMethod = true;
                            break;
                        }
                    }
                    if (!hasEquifaxMethod) {
                        return new ValidationError(component, i18n.tr("Please select a payment method for Equifax"));
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        });
        addValueValidator(new EditableValueValidator<PmcPaymentMethodsDTO>() {
            @Override
            public ValidationError isValid(CComponent<PmcPaymentMethodsDTO> component, PmcPaymentMethodsDTO value) {
                if (value != null && value.paymentMethods().isEmpty()) {
                    return new ValidationError(component, i18n.tr("At least one payment method is required"));
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        noPaymentMethodsMessage.setVisible(!isEditable() & getValue().paymentMethods().isEmpty());
    }

}
