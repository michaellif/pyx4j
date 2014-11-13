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

import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulatorConfig;
import com.propertyvista.operations.domain.eft.cards.simulator.CardServiceSimulatorConfig.SimulationType;
import com.propertyvista.operations.rpc.dto.CardServiceSimulatorConfigDTO;

public class CardServiceSimulatorConfigForm extends OperationsEntityForm<CardServiceSimulatorConfigDTO> {

    public CardServiceSimulatorConfigForm(IFormView<CardServiceSimulatorConfigDTO> view) {
        super(CardServiceSimulatorConfigDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().responseDelay()).decorate().componentWidth(80);

        formPanel.append(Location.Left, proto().responseType()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().responseCode()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().responseHttpCode()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().responseText()).decorate().componentWidth(180);

        formPanel.append(Location.Left, proto().acceptCardExpiryFrom()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().acceptCardExpiryTo()).decorate().componentWidth(180);

        get(proto().responseType()).addValueChangeHandler(new ValueChangeHandler<CardServiceSimulatorConfig.SimulationType>() {

            @Override
            public void onValueChange(ValueChangeEvent<SimulationType> event) {
                updatecardServiceVisibility();
            }

        });

        selectTab(addTab(formPanel, "Card Service Simulator"));
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
