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
package com.propertyvista.admin.client.ui.crud.simulation;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.admin.client.ui.crud.AdminEntityForm;
import com.propertyvista.admin.domain.dev.CardServiceSimulatorConfig;
import com.propertyvista.admin.domain.dev.CardServiceSimulatorConfig.SimpulationType;
import com.propertyvista.admin.rpc.SimulationDTO;

public class SimulationForm extends AdminEntityForm<SimulationDTO> {

    private final static I18n i18n = I18n.get(SimulationForm.class);

    public SimulationForm(IFormView<SimulationDTO> view) {
        super(SimulationDTO.class, view);

        selectTab(addTab(createGeneralTab()));
        addTab(createCaledonTab());
        addTab(createEquifaxTab());
        addTab(createOnboardingTab());
    }

    @Override
    protected void onValueSet(boolean populate) {
        updatecardServiceVisibility();
    }

    private FormFlexPanel createGeneralTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));
        int row = -1;

        content.setH2(++row, 0, 1, i18n.tr("Cache"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().generalCacheEnabled()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().entityCacheServiceEnabled()), 5).build());

        content.setH2(++row, 0, 1, i18n.tr("Network Simulation"));

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().networkSimulation().enabled()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().networkSimulation().delay()), 10).build());

        return content;
    }

    private FormFlexPanel createCaledonTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Caledon (Payments)"));
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().systems().usePadSimulator()), 5).build());

        content.setH2(++row, 0, 1, i18n.tr("Credit Cards"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().systems().useCardServiceSimulator()), 5).build());
        // TODO This should be in separate server/separate forms
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cardService().responseType()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cardService().responseCode()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cardService().responseHttpCode()), 15).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().cardService().responseText()), 15).build());

        get(proto().cardService().responseType()).addValueChangeHandler(new ValueChangeHandler<CardServiceSimulatorConfig.SimpulationType>() {

            @Override
            public void onValueChange(ValueChangeEvent<SimpulationType> event) {
                updatecardServiceVisibility();
            }

        });

        return content;
    }

    private void updatecardServiceVisibility() {
        get(proto().cardService().responseCode()).setVisible(false);
        get(proto().cardService().responseText()).setVisible(false);
        get(proto().cardService().responseHttpCode()).setVisible(false);

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
        default:
            break;
        }
    }

    private FormFlexPanel createEquifaxTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("Equifax"));
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().systems().useEquifaxSimulator()), 5).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifax().approveXml()), 50).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifax().declineXml()), 50).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().equifax().moreInfoXml()), 50).build());

        return content;
    }

    private FormFlexPanel createOnboardingTab() {
        FormFlexPanel content = new FormFlexPanel(i18n.tr("On-Boarding"));
        int row = -1;

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().systems().onboarding().enabled()), 5).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().systems().onboarding().simpulationType()), 15).build());

        return content;
    }
}
