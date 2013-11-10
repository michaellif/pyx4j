/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.simulation;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.dev.CardServiceSimulatorConfig;
import com.propertyvista.operations.domain.dev.CardServiceSimulatorConfig.SimpulationType;
import com.propertyvista.operations.rpc.dto.SimulationDTO;

public class SimulationForm extends OperationsEntityForm<SimulationDTO> {

    private final static I18n i18n = I18n.get(SimulationForm.class);

    public SimulationForm(IForm<SimulationDTO> view) {
        super(SimulationDTO.class, view);

        selectTab(addTab(createGeneralTab()));
        addTab(createCaledonTab());
        addTab(createYardiTab());
        addTab(createEquifaxTab());
    }

    @Override
    protected void onValueSet(boolean populate) {
        updatecardServiceVisibility();
    }

    private TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;

        content.setH2(++row, 0, 2, i18n.tr("Cache"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().generalCacheEnabled()), 5, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().entityCacheServiceEnabled()), 5, true).build());

        content.setH2(++row, 0, 2, i18n.tr("Network Simulation"));

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().networkSimulation().enabled()), 5, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().networkSimulation().delay()), 10, true).build());

        content.setH2(++row, 0, 2, i18n.tr("New Session duration"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().devSessionDuration()), 10, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().applicationSessionDuration()), 10, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().containerSessionTimeout(), new CLabel<String>()), 10, true).build());

        return content;
    }

    private TwoColumnFlexFormPanel createCaledonTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Payments"));
        int row = -1;

        content.setH2(++row, 0, 2, i18n.tr("Funds Transfer"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().systems().useFundsTransferSimulator()), 5, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().systems().useDirectBankingSimulator()), 5, true).build());

        content.setH2(++row, 0, 2, i18n.tr("Credit Cards"));
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().systems().useCardServiceSimulator()), 5, true).build());
        // TODO This should be in separate server/separate forms

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardService().responseDelay()), 5, true).build());

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardService().responseType()), 15, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardService().responseCode()), 15, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardService().responseHttpCode()), 15, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardService().responseText()), 15, true).build());

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardService().acceptCardExpiryFrom()), 15, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().cardService().acceptCardExpiryTo()), 15, true).build());

        get(proto().cardService().responseType()).addValueChangeHandler(new ValueChangeHandler<CardServiceSimulatorConfig.SimpulationType>() {

            @Override
            public void onValueChange(ValueChangeEvent<SimpulationType> event) {
                updatecardServiceVisibility();
            }

        });

        return content;
    }

    private TwoColumnFlexFormPanel createYardiTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Yardi"));
        int row = -1;

        content.setH2(++row, 0, 2, i18n.tr("Yardi Network Simulation"));

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().systems().yardiInterfaceNetworkSimulation().enabled()), 5, true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().systems().yardiInterfaceNetworkSimulation().delay()), 10, true).build());

        return content;
    }

    private void updatecardServiceVisibility() {

        get(proto().systems().useFundsTransferSimulator()).setEditable(getValue().fundsTransferSimulationConfigurable().getValue());
        get(proto().systems().useDirectBankingSimulator()).setEditable(getValue().fundsTransferSimulationConfigurable().getValue());

        get(proto().cardService().responseCode()).setVisible(false);
        get(proto().cardService().responseText()).setVisible(false);
        get(proto().cardService().responseHttpCode()).setVisible(false);
        get(proto().cardService().acceptCardExpiryFrom()).setVisible(false);
        get(proto().cardService().acceptCardExpiryTo()).setVisible(false);

        switch (getValue().cardService().responseType().getValue()) {
        case RespondWithCode:
            get(proto().cardService().responseCode()).setVisible(true);
            break;
        case RespondWithText:
            get(proto().cardService().responseText()).setVisible(true);
            break;
        case RespondWithHttpCode:
            get(proto().cardService().responseHttpCode()).setVisible(true);
            break;
        case SimulateTransations:
            get(proto().cardService().acceptCardExpiryFrom()).setVisible(true);
            get(proto().cardService().acceptCardExpiryTo()).setVisible(true);
            break;
        default:
            break;
        }
    }

    private TwoColumnFlexFormPanel createEquifaxTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Equifax"));
        int row = -1;

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().systems().useEquifaxSimulator()), 5, true).build());

        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().equifax().approve().xml()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().equifax().decline().xml()), true).build());
        content.setWidget(++row, 0, 2, new FormDecoratorBuilder(inject(proto().equifax().moreInfo().xml()), true).build());

        return content;
    }

}
