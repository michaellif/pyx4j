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
package com.propertyvista.crm.client.ui.crud.administration.profile.paymentmethods;

import java.util.List;

import com.pyx4j.forms.client.ui.panels.FluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
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
        FormPanel formPanel = new FormPanel(this);

        noPaymentMethodsMessage = new Label();
        noPaymentMethodsMessage.setText(i18n.tr("There are no payment methods. Click 'Edit' to add a new payment method"));
        noPaymentMethodsMessage.setVisible(false);

        formPanel.append(Location.Full, noPaymentMethodsMessage);
        formPanel.append(Location.Full, proto().paymentMethods(), new PmcPaymentMethodFolder());

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);

    }

    @Override
    public void addValidations() {
        super.addValidations();
        get(proto().paymentMethods()).addComponentValidator(new AbstractComponentValidator<List<PmcPaymentMethod>>() {
            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null && getComponent().getValue().isEmpty()) {
                    return new FieldValidationError(getComponent(), i18n.tr("At least one payment method is required"));
                } else {
                    return null;
                }
            }
        });
        get(proto().paymentMethods()).addComponentValidator(new AbstractComponentValidator<List<PmcPaymentMethod>>() {

            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null) {
                    boolean hasEquifaxMethod = false;
                    for (PmcPaymentMethod pmcPaymentMethod : getComponent().getValue()) {
                        if (pmcPaymentMethod.selectForEquifaxPayments().getValue(false)) {
                            hasEquifaxMethod = true;
                            break;
                        }
                    }
                    if (!hasEquifaxMethod) {
                        return new FieldValidationError(getComponent(), i18n.tr("Please select a payment method for Equifax"));
                    } else {
                        return null;
                    }
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
