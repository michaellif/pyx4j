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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.portal.client.ui.residents.insurancemockup.resources.InsuranceMockupResources;
import com.propertyvista.portal.rpc.ptapp.dto.welcomewizard.InsuranceDTO;

public class InsuranceForm extends CEntityDecoratableForm<InsuranceDTO> {

    private final static I18n i18n = I18n.get(InsuranceForm.class);

    private Command onPurchaseInsuranceConfirmedHandler;

    private Command onExistingInsuranceConfirmedHandler;

    private Button existingInsuranceConfirmationButton;

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

        HorizontalPanel obtainInsuranceMessagePanel = new HorizontalPanel();
        VerticalPanel logoPanel = new VerticalPanel();
        logoPanel.add(new Image(InsuranceMockupResources.INSTANCE.logoTenantSure()));
        logoPanel.add(new Image(InsuranceMockupResources.INSTANCE.logoHighcourt()));
        logoPanel.add(new HTML("1-888-123-4567"));
        logoPanel.getElement().getStyle().setPadding(1, Unit.EM);
        logoPanel.getElement().getStyle().setMargin(1, Unit.EM);
        obtainInsuranceMessagePanel.add(logoPanel);
        obtainInsuranceMessagePanel.setCellWidth(logoPanel, "5em");
        obtainInsuranceMessagePanel.setCellHorizontalAlignment(logoPanel, HasHorizontalAlignment.ALIGN_CENTER);
        obtainInsuranceMessagePanel.setCellVerticalAlignment(logoPanel, HasVerticalAlignment.ALIGN_MIDDLE);
        obtainInsuranceMessagePanel.add(new HTML(InsuranceMockupResources.INSTANCE.youMustObtainInsuranceMessage().getText()));
        content.setWidget(++row, 0, obtainInsuranceMessagePanel);

        content.getRowFormatter().getElement(row).getStyle().setPaddingBottom(10, Unit.PX);
        content.setWidget(++row, 0, inject(proto().purchaseInsurance(), new InsurancePurchaseEditorForm(new Command() {

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

                InsurancePurchaseEditorForm purchaseInsuraceForm = (InsurancePurchaseEditorForm) get(proto().purchaseInsurance());
                purchaseInsuraceForm.setVisible(!alreadyHasInsurance);

                InsuranceAlreadyAvailabileEditorForm alreadyHasInsuranceForm = (InsuranceAlreadyAvailabileEditorForm) get(proto().existingInsurance());
                alreadyHasInsuranceForm.setVisible(alreadyHasInsurance);
                existingInsuranceConfirmationButton.setVisible(alreadyHasInsurance);
            }
        });
        content.setWidget(++row, 0, inject(proto().existingInsurance(), new InsuranceAlreadyAvailabileEditorForm()));
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
