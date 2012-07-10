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
package com.propertyvista.portal.client.ui.residents.insurancemockup.forms;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.moveinwizardmockup.InsurancePurchaseForm;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.moveinwizardmockup.InsuranceDTO;
import com.propertyvista.portal.client.ui.residents.insurancemockup.components.InsuranceMessagePanel;
import com.propertyvista.portal.client.ui.residents.insurancemockup.resources.InsuranceMockupResources;

public class UnknownInsuranceForm extends CEntityDecoratableForm<InsuranceDTO> {

    private final static I18n i18n = I18n.get(UnknownInsuranceForm.class);

    private Command onPurchaseInsuranceConfirmedHandler;

    private Command onExistingInsuranceConfirmedHandler;

    private Button existingInsuranceConfirmationButton;

    public UnknownInsuranceForm() {
        super(InsuranceDTO.class);
    }

    public void setOnPurchaseInsuranceConfirmedCommand(Command command) {
        this.onPurchaseInsuranceConfirmedHandler = command;
    }

    public void setOnExistingInsuranceConfirmedCommand(Command command) {
        this.onExistingInsuranceConfirmedHandler = command;
    }

    @Override
    public IsWidget createContent() {

        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        content.setWidget(++row, 0, new InsuranceMessagePanel(new HTML(InsuranceMockupResources.INSTANCE.youMustObtainInsuranceMessage().getText())));

        content.getRowFormatter().getElement(row).getStyle().setPaddingBottom(10, Unit.PX);
        content.setWidget(++row, 0, inject(proto().purchaseInsurance(), new InsurancePurchaseForm(new Command() {

            @Override
            public void execute() {
                if (onExistingInsuranceConfirmedHandler != null) {
                    onPurchaseInsuranceConfirmedHandler.execute();
                }
            }
        })));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().alreadyHaveInsurance())).build());
        content.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(3, Unit.EM);
        get(proto().alreadyHaveInsurance()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                boolean alreadyHasInsurance = event.getValue();

                InsurancePurchaseForm purchaseInsuraceForm = (InsurancePurchaseForm) get(proto().purchaseInsurance());
                purchaseInsuraceForm.setVisible(!alreadyHasInsurance);

                InsuranceAlreadyAvailabileForm alreadyHasInsuranceForm = (InsuranceAlreadyAvailabileForm) get(proto().existingInsurance());
                alreadyHasInsuranceForm.setVisible(alreadyHasInsurance);
                existingInsuranceConfirmationButton.setVisible(alreadyHasInsurance);
            }
        });
        content.setWidget(++row, 0, inject(proto().existingInsurance(), new InsuranceAlreadyAvailabileForm()));
        get(proto().existingInsurance()).setVisible(false);

        content.setWidget(++row, 0, existingInsuranceConfirmationButton = new Button(i18n.tr("Save and Continue"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (onExistingInsuranceConfirmedHandler != null) {
                    onExistingInsuranceConfirmedHandler.execute();
                }
            }
        }));
        existingInsuranceConfirmationButton.setVisible(false);

        return content;
    }
}
