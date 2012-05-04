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

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableEditor;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO;

public class InsuranceForm extends CEntityDecoratableEditor<InsuranceDTO> {

    private final static I18n i18n = I18n.get(InsuranceForm.class);

    private Command onPurchaseInsuranceConfirmedHandler;

    private Command onExistingInsuranceConfirmedHandler;

    public InsuranceForm() {
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
        content.setWidget(++row, 0, new HTML("&nbsp")); // separator
        content.setWidget(++row, 0, inject(proto().purchaseInsurance(), new InsurancePurchaseEditorForm()));
        content.setWidget(++row, 0, new Button(i18n.tr("Pay and Continue"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (onPurchaseInsuranceConfirmedHandler != null) {
                    onPurchaseInsuranceConfirmedHandler.execute();
                }
            }
        }));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().alreadyHaveInsurance())).build());
        content.getFlexCellFormatter().getElement(row, 0).getStyle().setPaddingTop(3, Unit.EM);
        get(proto().alreadyHaveInsurance()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                InsurancePurchaseEditorForm form = (InsurancePurchaseEditorForm) get(proto().purchaseInsurance());
                form.setEnabled(event.getValue() != true);
                form.setQuoteTotalPanelVisibility(event.getValue() != true);
            }
        });
        content.setWidget(++row, 0, inject(proto().existingInsurance(), new InsuranceAlreadyAvailabileEditorForm()));
        content.setWidget(++row, 0, new Button(i18n.tr("Save and Continue"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (onExistingInsuranceConfirmedHandler != null) {
                    onExistingInsuranceConfirmedHandler.execute();
                }
            }
        }));

        return content;
    }
}
