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
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
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
        super.onValueSet(populate);
        updatecardServiceVisibility();
    }

    private void updatecardServiceVisibility() {
        get(proto().systems().useFundsTransferSimulator()).setEditable(getValue().fundsTransferSimulationConfigurable().getValue());
        get(proto().systems().useDirectBankingSimulator()).setEditable(getValue().fundsTransferSimulationConfigurable().getValue());
    }

    private TwoColumnFlexFormPanel createGeneralTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));
        int row = -1;

        content.setH2(++row, 0, 2, i18n.tr("Cache"));
        content.setWidget(++row, 0, 2, inject(proto().generalCacheEnabled(), new FieldDecoratorBuilder(5, true).build()));
        content.setWidget(++row, 0, 2, inject(proto().entityCacheServiceEnabled(), new FieldDecoratorBuilder(5, true).build()));

        content.setH2(++row, 0, 2, i18n.tr("Network Simulation"));

        content.setWidget(++row, 0, 2, inject(proto().networkSimulation().enabled(), new FieldDecoratorBuilder(5, true).build()));
        content.setWidget(++row, 0, 2, inject(proto().networkSimulation().delay(), new FieldDecoratorBuilder(10, true).build()));

        content.setH2(++row, 0, 2, i18n.tr("New Session duration"));
        content.setWidget(++row, 0, 2, inject(proto().devSessionDuration(), new FieldDecoratorBuilder(10, true).build()));
        content.setWidget(++row, 0, 2, inject(proto().applicationSessionDuration(), new FieldDecoratorBuilder(10, true).build()));
        content.setWidget(++row, 0, 2, inject(proto().containerSessionTimeout(), new CLabel<String>(), new FieldDecoratorBuilder(10, true).build()));

        return content;
    }

    private TwoColumnFlexFormPanel createCaledonTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Payments"));
        int row = -1;

        content.setH2(++row, 0, 2, i18n.tr("Funds Transfer"));
        content.setWidget(++row, 0, 2, inject(proto().systems().useFundsTransferSimulator(), new FieldDecoratorBuilder(5, true).build()));
        content.setWidget(++row, 0, 2, inject(proto().systems().useDirectBankingSimulator(), new FieldDecoratorBuilder(5, true).build()));

        content.setH2(++row, 0, 2, i18n.tr("Credit Cards"));
        content.setWidget(++row, 0, 2, inject(proto().systems().useCardServiceSimulator(), new FieldDecoratorBuilder(5, true).build()));

        return content;
    }

    private TwoColumnFlexFormPanel createYardiTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Yardi"));
        int row = -1;

        content.setWidget(++row, 0, 2, inject(proto().systems().yardiAllTenantsToHaveEmails(), new FieldDecoratorBuilder(5, true).build()));

        content.setH2(++row, 0, 2, i18n.tr("Yardi Network Simulation"));

        content.setWidget(++row, 0, 2, inject(proto().systems().yardiInterfaceNetworkSimulation().enabled(), new FieldDecoratorBuilder(5, true).build()));
        content.setWidget(++row, 0, 2, inject(proto().systems().yardiInterfaceNetworkSimulation().delay(), new FieldDecoratorBuilder(10, true).build()));

        return content;
    }

    private TwoColumnFlexFormPanel createEquifaxTab() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("Equifax"));
        int row = -1;

        content.setWidget(++row, 0, 2, inject(proto().systems().useEquifaxSimulator(), new FieldDecoratorBuilder(5, true).build()));

        content.setWidget(++row, 0, 2, inject(proto().equifax().approve().xml(), new FieldDecoratorBuilder(true).build()));
        content.setWidget(++row, 0, 2, inject(proto().equifax().decline().xml(), new FieldDecoratorBuilder(true).build()));
        content.setWidget(++row, 0, 2, inject(proto().equifax().moreInfo().xml(), new FieldDecoratorBuilder(true).build()));

        return content;
    }

}
