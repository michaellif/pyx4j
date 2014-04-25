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

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.dto.SimulationDTO;

public class SimulationForm extends OperationsEntityForm<SimulationDTO> {

    private final static I18n i18n = I18n.get(SimulationForm.class);

    public SimulationForm(IForm<SimulationDTO> view) {
        super(SimulationDTO.class, view);

        selectTab(addTab(createGeneralTab(), i18n.tr("General")));
        addTab(createCaledonTab(), i18n.tr("Payments"));
        addTab(createYardiTab(), i18n.tr("Yardi"));
        addTab(createEquifaxTab(), i18n.tr("Equifax"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        updatecardServiceVisibility();
    }

    private void updatecardServiceVisibility() {
        get(proto().systems().useFundsTransferSimulator()).setEditable(getValue().fundsTransferSimulationConfigurable().getValue());
        get(proto().systems().useDirectBankingSimulator()).setEditable(getValue().fundsTransferSimulationConfigurable().getValue());
    }

    private TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setH2(++row, 0, 2, i18n.tr("Cache"));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().generalCacheEnabled(), 5, true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().entityCacheServiceEnabled(), 5, true));

        content.setH2(++row, 0, 2, i18n.tr("Network Simulation"));

        content.setWidget(++row, 0, 2, injectAndDecorate(proto().networkSimulation().enabled(), 5, true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().networkSimulation().delay(), 10, true));

        content.setH2(++row, 0, 2, i18n.tr("New Session duration"));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().devSessionDuration(), 10, true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().applicationSessionDuration(), 10, true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().containerSessionTimeout(), new CLabel<String>(), 10, true));

        return content;
    }

    private TwoColumnFlexFormPanel createCaledonTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setH2(++row, 0, 2, i18n.tr("Funds Transfer"));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().systems().useFundsTransferSimulator(), 5, true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().systems().useDirectBankingSimulator(), 5, true));

        content.setH2(++row, 0, 2, i18n.tr("Credit Cards"));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().systems().useCardServiceSimulator(), 5, true));

        return content;
    }

    private TwoColumnFlexFormPanel createYardiTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, 2, injectAndDecorate(proto().systems().yardiAllTenantsToHaveEmails(), 5, true));

        content.setH2(++row, 0, 2, i18n.tr("Yardi Network Simulation"));

        content.setWidget(++row, 0, 2, injectAndDecorate(proto().systems().yardiInterfaceNetworkSimulation().enabled(), 5, true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().systems().yardiInterfaceNetworkSimulation().delay(), 10, true));

        return content;
    }

    private TwoColumnFlexFormPanel createEquifaxTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();
        int row = -1;

        content.setWidget(++row, 0, 2, injectAndDecorate(proto().systems().useEquifaxSimulator(), 5, true));

        content.setWidget(++row, 0, 2, injectAndDecorate(proto().equifax().approve().xml(), true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().equifax().decline().xml(), true));
        content.setWidget(++row, 0, 2, injectAndDecorate(proto().equifax().moreInfo().xml(), true));

        return content;
    }

}
