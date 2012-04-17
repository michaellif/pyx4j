/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 16, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizard.insurance;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.widgets.client.RadioGroup.Layout;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.common.client.ui.components.c.NewPaymentMethodForm;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.ExisitingInsuranceDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO.InsuranceOptions;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.PurchaseInsuranceDTO;

public class InsuranceForm extends CEntityDecoratableEditor<InsuranceDTO> {

    public InsuranceForm() {
        super(InsuranceDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        CRadioGroupEnum<InsuranceDTO.InsuranceOptions> insuranceOption = new CRadioGroupEnum<InsuranceDTO.InsuranceOptions>(
                InsuranceDTO.InsuranceOptions.class, Layout.VERTICAL);

        int row = -1;
        content.setWidget(++row, 0, inject(proto().insuranceType(), insuranceOption));
        get(proto().insuranceType()).addValueChangeHandler(new ValueChangeHandler<InsuranceDTO.InsuranceOptions>() {
            @Override
            public void onValueChange(ValueChangeEvent<InsuranceOptions> event) {
                InsuranceDTO.InsuranceOptions insuranceOption = event.getValue();
                if (insuranceOption != null) {
                    get(proto().purchaseInsurance()).setVisible(insuranceOption == InsuranceOptions.wantToBuyInsurance);
                    get(proto().existingInsurance()).setVisible(insuranceOption == InsuranceOptions.alreadyHaveInsurance);
                }
            }
        });
        content.setWidget(++row, 0, new HTML("&nbsp")); // separator
        content.setWidget(++row, 0, inject(proto().purchaseInsurance(), new PurchaseInsuranceForm()));
        get(proto().purchaseInsurance()).setVisible(false);
        content.setWidget(++row, 0, inject(proto().existingInsurance(), new ExistingInsuranceForm()));
        get(proto().existingInsurance()).setVisible(false);

        return content;
    }

    public static class PurchaseInsuranceForm extends CEntityDecoratableEditor<PurchaseInsuranceDTO> {

        public PurchaseInsuranceForm() {
            super(PurchaseInsuranceDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;
            content.setWidget(++row, 0, inject(proto().paymentMethod(), new NewPaymentMethodForm()));
            return content;
        }
    }

    public static class ExistingInsuranceForm extends CEntityDecoratableEditor<ExisitingInsuranceDTO> {

        public ExistingInsuranceForm() {
            super(ExisitingInsuranceDTO.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();
            int row = -1;
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().company()), 10).build());
            content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().policyNumber()), 10).build());
            return content;
        }

    }
}
