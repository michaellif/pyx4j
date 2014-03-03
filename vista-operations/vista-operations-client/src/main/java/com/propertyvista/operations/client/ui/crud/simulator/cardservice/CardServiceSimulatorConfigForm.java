/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-03-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulator.cardservice;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulatorConfig;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulatorConfig.SimpulationType;
import com.propertyvista.operations.rpc.dto.CardServiceSimulatorConfigDTO;

public class CardServiceSimulatorConfigForm extends OperationsEntityForm<CardServiceSimulatorConfigDTO> {

    public CardServiceSimulatorConfigForm(IForm<CardServiceSimulatorConfigDTO> view) {
        super(CardServiceSimulatorConfigDTO.class, view);

        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();
        int row = -1;

        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().responseDelay()), 5, true).build());

        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().responseType()), 15, true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().responseCode()), 15, true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().responseHttpCode()), 15, true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().responseText()), 15, true).build());

        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().acceptCardExpiryFrom()), 15, true).build());
        panel.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().acceptCardExpiryTo()), 15, true).build());

        get(proto().responseType()).addValueChangeHandler(new ValueChangeHandler<CardServiceSimulatorConfig.SimpulationType>() {

            @Override
            public void onValueChange(ValueChangeEvent<SimpulationType> event) {
                updatecardServiceVisibility();
            }

        });

        selectTab(addTab(panel));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updatecardServiceVisibility();
    }

    private void updatecardServiceVisibility() {

        get(proto().responseCode()).setVisible(false);
        get(proto().responseText()).setVisible(false);
        get(proto().responseHttpCode()).setVisible(false);
        get(proto().acceptCardExpiryFrom()).setVisible(false);
        get(proto().acceptCardExpiryTo()).setVisible(false);

        switch (getValue().responseType().getValue()) {
        case RespondWithCode:
            get(proto().responseCode()).setVisible(true);
            break;
        case RespondWithText:
            get(proto().responseText()).setVisible(true);
            break;
        case RespondWithHttpCode:
            get(proto().responseHttpCode()).setVisible(true);
            break;
        case SimulateTransations:
            get(proto().acceptCardExpiryFrom()).setVisible(true);
            get(proto().acceptCardExpiryTo()).setVisible(true);
            break;
        default:
            break;
        }
    }
}
