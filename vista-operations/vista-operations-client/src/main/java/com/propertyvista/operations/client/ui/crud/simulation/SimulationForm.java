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

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;

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

    private IsWidget createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h2(i18n.tr("Cache"));
        formPanel.append(Location.Left, proto().generalCacheEnabled()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().entityCacheServiceEnabled()).decorate().componentWidth(80);

        formPanel.h2(i18n.tr("Network Simulation"));

        formPanel.append(Location.Left, proto().networkSimulation().enabled()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().networkSimulation().delay()).decorate().componentWidth(120);

        formPanel.h2(i18n.tr("New Session duration"));
        formPanel.append(Location.Left, proto().devSessionDuration()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().applicationSessionDuration()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().containerSessionTimeout(), new CLabel<String>()).decorate().componentWidth(120);

        return formPanel;
    }

    private IsWidget createCaledonTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h2(i18n.tr("Funds Transfer"));
        formPanel.append(Location.Left, proto().systems().useFundsTransferSimulator()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().systems().useDirectBankingSimulator()).decorate().componentWidth(80);

        formPanel.h2(i18n.tr("Credit Cards"));
        formPanel.append(Location.Left, proto().systems().useCardServiceSimulator()).decorate().componentWidth(80);

        return formPanel;
    }

    private IsWidget createYardiTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().systems().yardiAllTenantsToHaveEmails()).decorate().componentWidth(80);

        formPanel.h2(i18n.tr("Yardi Network Simulation"));

        formPanel.append(Location.Left, proto().systems().yardiInterfaceNetworkSimulation().enabled()).decorate().componentWidth(80);
        formPanel.append(Location.Left, proto().systems().yardiInterfaceNetworkSimulation().delay()).decorate().componentWidth(120);

        return formPanel;
    }

    private IsWidget createEquifaxTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().systems().useEquifaxSimulator()).decorate().componentWidth(80);

        formPanel.append(Location.Left, proto().equifax().forceResultRiskCode()).decorate();

        formPanel.append(Location.Dual, proto().equifax().approve().xml()).decorate().customLabel("Approve");
        formPanel.append(Location.Dual, proto().equifax().decline().xml()).decorate().customLabel("Decline");
        formPanel.append(Location.Dual, proto().equifax().moreInfo().xml()).decorate().customLabel("MoreInfo");

        return formPanel;
    }
}
